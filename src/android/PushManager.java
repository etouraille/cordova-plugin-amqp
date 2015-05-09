package org.amqp.notification;

//org.amqp
import org.amqp.notification.PushService;
//android 
import android.app.Activity;
import android.content.Intent;

import android.util.Log;

class PushManager  {

    public PushManager( Activity activity ) {
        
        Intent intent = new Intent( this, PushService.class);
        Log.e("BEFORE START SERVICE","1");
        //Intent intent = new Intent("org.amqp.notification.PushService");
        activity.startService(intent);
    }
}
