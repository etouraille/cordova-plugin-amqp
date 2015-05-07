package org.amqp.notification;

import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;

import org.amqp.notification.Push;
import org.amqp.notification.PushNotification;

import java.lang.String;


class PushReceiver extends BroadcastReceiver {
    public final static String PUSH_INTENT_EXTRA = "org.amqp.notification.push.intent.extra";
    public final static String PUSH_INTENT_ACTION = "org.amqp.notification.push.intent.action";   
    
    @Override
    public void onReceive(Context context, Intent intent) {
        if(PUSH_INTENT_ACTION.equals(intent.getAction())) {
            String message = intent.getStringExtra(PUSH_INTENT_EXTRA);
            Push.proceedNotification(new PushNotification(message));
        }   
    }

}
