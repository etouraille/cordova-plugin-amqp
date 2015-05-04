package org.amqp.notification;

import org.amqp.notification.PushNotification;
import org.amqp.notification.Push;
import android.content.Context;
import android.content.Intent;

public class PushReceiver {
    
    public static void onNotificationReceived(PushNotification notification, Context context ) {
        if(Push.isActive() && !(Push.inPause)){
            Intent intent = new Intent(context, org.amqp.notification.PushHandlerActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("push", notification.toString());
            
            context.startActivity(intent);
        } 
    }
}
