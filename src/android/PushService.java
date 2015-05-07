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

import org.amqp.notification.PushReceiver;


class PushService extends Service{
    
    protected Thread amqpThread;
    
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        proceed(intent); 
        return START_REDELIVER_INTENT; // must be able to get the intent
            // because in the first intent there are the connexion and user information.
            // what is the solution to stop the servi   ce without calling stop self
    }
    
    //The thread listen to rabbit MQ
    //Received message are broadcasted
    //The message are proccessed in the PushManager and send to the view there.
    protected void proceed(Intent intent) {
         amqpThread = new Thread(new Runnable() {
            public void run() {
                try {
                    ConnectionFactory factory = new ConnectionFactory();
                    factory.setHost("objetspartages.org");
                    factory.setUsername("toto");
                    factory.setPassword("toto");
                    factory.setVirtualHost("toto");
                    factory.setPort(5672);

                    Connection connection = factory.newConnection();
                    Channel channel = connection.createChannel();
                    channel.queueDeclare("hello", false, false, false, null);
                    QueueingConsumer consumer = new QueueingConsumer(channel);
                    channel.basicConsume("hello", true, consumer);
                        while (true) {
                            //Put some weak references to delivery and messages
                            Delivery delivery = consumer.nextDelivery();
                            String message = new String(delivery.getBody());
                            Log.e("MESSAGE",message);
                            //send the message with broadcast
                            Intent intent = new Intent();
                            intent.setAction(PushReceiver.PUSH_INTENT_ACTION);
                            intent.putExtra(PushReceiver.PUSH_INTENT_EXTRA,message); 
                            sendBroadcast(intent);
                        } 
                    } catch(IOException e){
                      // must be none bloking do not detroy the service 
                    } catch(Exception e ){
                        //return the thread is dead
                        //check documentation for rabbitmq client exception
                    }
            }
         });
    
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
                case "MSG_SAY_HELLO" :
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
