package org.amqp.notification;

import org.apache.cordova.CordovaActivity;

import java.lang.Runnable;
import java.lang.Exception;

//rabbit
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;
//org.amqp
import org.amqp.notification.PushNotifiaction;
import org.amqp.notification.PushReceiver;
//android 
import android.util.Log;

class PushManager  {

    public PushManager( CordovaActivity activity ) throws Exception {
        activity.runOnUiThread(new Runnable() {
            public void run(){
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
                    Delivery delivery = consumer.nextDelivery();
                    String message = new String(delivery.getBody());
                    Log.d("MESSAGE",message);
                    PushNotification notification = new PushNotification(message);
                    PushReceiver.onNotificationReceived(notification, Push.getContext());
                }
            }        
        });
    }

}
