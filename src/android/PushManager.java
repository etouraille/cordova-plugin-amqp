package org.amqp.notification;

import org.apache.cordova.CordovaActivity;

import java.lang.Runnable;
import java.lang.Exception;
import java.lang.String;

//org.amqp
import org.amqp.notification.PushManagerObserver;
import org.amqp.notification.PushManagerRunnable;
//android 
import android.util.Log;
import android.app.Activity;
import java.util.List;
import java.util.ArrayList;
class PushManager  {

    private List<String> errors = new ArrayList<String>();
    private Boolean enabled = false;

    public PushManager( Activity activity ) throws Exception {
        activity.runOnUiThread(new PushManagerRunnable(new PushManagerObserver(this), activity));
    }

    public void setEnabled(){
        this.enabled = true;
    }

    public void setDisabled(){
        this.enabled = false;
    }

    public void addError(String error){
        errors.add(error);   
    }

}
