package org.amqp.notification;

import org.amqp.notification.PushManager;

class PushManagerObserver{
    
    private PushManager _pushManager;

    public PushManagerObserver(PushManager pushManager){
        _pushManager = pushManager;
    }
    
    public void enablePushManager(){
        _pushManager.setEnabled();
    }
    
    public void setError(String error){
        _pushManager.addError(error);
        _pushManager.setDisabled();
    }
}
