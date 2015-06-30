package org.amqp.notification;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.String;
import java.lang.Integer;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;

import android.content.Context;

class Config {

    protected static String configFileName = "config";
    
    public String host;
    public int port;
    public String username;
    public String password;
    public String virtualHost;
    public String routingKey;

    public static void init( JSONObject configuration , Context context ) {
        writeInConfigFile(
                configuration, 
                configFileName, 
                context
                ); 
    }

    protected static void writeInConfigFile(JSONObject config , String configFileName, Context context ) {
        try {
           FileWriter file = new FileWriter("/data/data/" + context.getPackageName() + "/" 
                   + configFileName );
           file.write(config.toString());
           file.flush();
           file.close();
        } catch(IOException e) {
            e.printStackTrace();

        }
        
    }

    public Config( Context context ) {
        try {
            File file = new File("/data/data/" + context.getPackageName() + "/" + configFileName) ;
            FileInputStream is = new FileInputStream(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String content = new String(buffer);
            JSONObject configJson = new JSONObject(content);
            initVarFromJson(configJson);

        } catch (IOException e ) {
            e.printStackTrace();
        } catch (JSONException e ) {
            e.printStackTrace();
        }
    }

    protected void initVarFromJson( JSONObject json ) {
    
        try {
            this.host = json.getString("host");
            this.port = Integer.parseInt(json.getString("port"));
            this.username = json.getString("username");
            this.password = json.getString("password");
            this.virtualHost = json.getString("virtualHost");
            this.routingKey = json.getString("routingKey");
        } catch (JSONException e ) {
           e.printStackTrace(); 
        }
    }
}

/*The config json has the form
 * { 
 *      host : host 
 *      port : port 
 *      virtualhost : virtualhost 
 *      login : login 
 *      password : password
 *      routingKey : key 
 *  }
 **/

