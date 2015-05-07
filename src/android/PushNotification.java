package org.amqp.notification ;

import java.lang.String;

class PushNotification {
    
    private String content;

    public PushNotification(String content ) {
        
       this.content = content;
    
    }

    public String toString(){
        return content;
    }

    public String getMessage(){
        return content;
    }

    public String getId(){
        return new String("BEEP");
    }

    //todo rendre parselable
    //tod faire un mapper avec les données reçues par le service avant de l'envoyer

}
