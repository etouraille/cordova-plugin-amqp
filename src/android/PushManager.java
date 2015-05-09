package org.amqp.notification;

//org.amqp
import org.amqp.notification.PushService;
import org.amqp.notification.Push;

//android 
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.util.Log;

class PushManager  {

    public PushManager( Activity activity , Push push) {
        
        Intent intent = new Intent( activity, PushService.class);
        Log.e("BEFORE START SERVICE","1");
        //Intent intent = new Intent("org.amqp.notification.PushService");
        activity.startService(intent);
    }
}
