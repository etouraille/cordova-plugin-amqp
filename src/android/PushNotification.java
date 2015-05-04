package org.cordova.amqpnotification;

import java.lang.String;

class PushNotification {
    
    private String content;

    public void PushNotification(String content ) {
        
       content = content;
    
    }

    public String toString(){
        return content;
    }

    public String getMessage(){
        return content;
    }

    public String getId(){
        return 'BEEP';
    }

}
