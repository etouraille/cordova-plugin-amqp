package org.amqp.notification;

//org.amqp
import org.amqp.notification.NotificationService;

//android 
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.util.Log;

class PushManager  {

    public PushManager( Activity activity , Push push) {
        
        Intent intent = new Intent( activity, NotificationService.class);
        Log.e("BEFORE START SERVICE","1");
        activity.startService(intent);
    }
}
