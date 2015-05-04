package org.amqp.notification;

import org.amqp.PushNotification;
import org.amqp.Push;
import android.content.Context;
import android.content.Intent;
public class PushReceiver {
    
    public static void onNotificationReceived(PushNotification notification, Context context ) {
        if(Push.isActive() && !(Push.inPause)){
            Intent intent = new Intent(context, org.amqp.PushHandlerActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("push", notification.toString());
            
            context.startActivity(intent);
        } 
    }
}
