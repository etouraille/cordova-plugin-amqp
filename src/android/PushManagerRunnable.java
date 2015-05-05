package org.amqp.notification;
//java
import java.lang.Runnable;

//rabbit
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;
//amqp

import org.amqp.notification.PushManagerObserver;
//android
import android.content.Context;
import android.util.Log;
import android.content.Context;


class PushManagerRunnable implements Runnable {
    
    private PushManagerObserver observer;
    private Context context;

    public PushManagerRunnable(PushManagerObserver observer, Context context){
        this.observer = observer;
        this.context = context;
    
    }

    public void run() {
      try{
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
        observer.enablePushManager();
        while (true) {
            Delivery delivery = consumer.nextDelivery();
            String message = new String(delivery.getBody());
            Log.e("MESSAGE",message);
            PushNotification notification = new PushNotification(message);
            PushReceiver.onNotificationReceived(notification, this.context );
        }
      } catch (Exception e){
            observer.setError(e.getMessage());
      }
    } 
}
