package org.amqp.notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;

import java.lang.Runnable;
import java.lang.Thread;
import java.lang.Exception;
import java.io.IOException;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;
import com.rabbitmq.client.DefaultConsumer;

import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP;

import org.amqp.notification.PushReceiver;
import org.amqp.notification.Config;


public class NotificationService extends Service{
    
    protected Thread amqpThread;
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        proceed(); 
        return START_REDELIVER_INTENT; // must be able to get the intent
            // because in the first intent there are the connexion and user information.
            // what is the solution to stop the servi   ce without calling stop self
    }
    
    //The thread listen to rabbit MQ
    //Received message are broadcasted
    //The message are proccessed in the PushManager and send to the view there.
    protected void proceed() {
        Log.e("IN PROCEED", "IN PROCEED");
        amqpThread = new Thread(new Runnable() {
            public void run() {
                try {
                    ConnectionFactory factory = new ConnectionFactory();
                    
                    Config configuration = new Config(getApplicationContext());
                    
                    factory.setHost(configuration.host);
                    factory.setUsername(configuration.username);
                    factory.setPassword(configuration.password);
                    factory.setVirtualHost(configuration.virtualHost);
                    factory.setPort(configuration.port);

                    Connection connection = factory.newConnection();
                    final Channel channel = connection.createChannel();
                    //#
                    
                    //#QUEUE DECLARATION
                    //the queue is a pile that dispatch message between worker
                    //if two queues have the same name, then the message will be ballanced between the queue 
                    //when we define a routingKey, the purpose migth be to corresponding queue
                    //or to use a specific mapping, for instance in the direct messaging.
                    //The choice we have to examine, is declaring a single queue for all the application
                    //or declaring a new queue for each application
                    //if i declare a single queue, it can be reload on and on again when restarting the notification service, 
                    String queueName = "NotificationQueue";
                    boolean durable = true; // if the rabbitMQ server stops, the queue is stile available
                    boolean exclusive = false; //the queue can be consume by other connexion, not dedicated to that connection only.
                    boolean autoDelete = false; //the queue must not be deleted, because it miht be use eventually by other apps
                    channel.queueDeclare(queueName, durable, exclusive, autoDelete, null);
                    QueueingConsumer consumer = new QueueingConsumer(channel);

                    String exchangeName = "NotificationExchange";
                    String exchangeType = "direct";
                    
                    channel.exchangeDeclare(exchangeName, exchangeType,durable, autoDelete, null);
                    
                    channel.queueBind(queueName, exchangeName, configuration.routingKey ); //bind the queue with the exchange
                    
                    boolean autoAck = false;

                    channel.basicConsume(queueName, autoAck, "myConsumerTag",
                        new DefaultConsumer(channel) {
                            @Override
                            public void handleDelivery(String consumerTag,
                                    Envelope envelope,
                                    AMQP.BasicProperties properties,
                                    byte[] body) throws IOException
                            {
                                 String routingKey = envelope.getRoutingKey();
                                 long deliveryTag = envelope.getDeliveryTag();
                                 String message = new String(body);
                                 Intent intent = new Intent();
                                 intent.setAction(PushReceiver.PUSH_INTENT_ACTION);
                                 intent.putExtra(PushReceiver.PUSH_INTENT_EXTRA,message); 
                                 sendBroadcast(intent);

                                 channel.basicAck(deliveryTag, false);
                                 
                             }
                    });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        });
        amqpThread.start();
    
    }
    //broadcast an error message if the service is unable to connext.
    //ferme le service must be able to restart after connection fail.
    @Override
    public void onDestroy(){
        amqpThread = null; //We destroy the thread.
        super.onDestroy();   
    }

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1 :
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}
