package org.amqp.notification;

import java.util.ArrayList;
import java.util.List;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import org.amqp.notification.PushNotification;
import org.amqp.notification.PushManager;

public class Push extends CordovaPlugin {
	private static CallbackContext clbContext;
	private PushManager manager;
	public static boolean inPause;
	private static String notificationEventListener;
	private static CordovaWebView cordovaWebView;
	private static List<PushNotification> cachedNnotifications = new ArrayList<PushNotification>();

	public static final String TAG = "Push";

	public static final String ACTION_INITIALIZE = "initialize";


	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
		try {
			clbContext = callbackContext;
			notificationEventListener = args.getJSONObject(0).getString("notificationListener");

cordovaWebView = this.webView;
			this.manager = new PushManager(cordova.getActivity());
                        // ############# INITIALIZE #############
                        if (ACTION_INITIALIZE.equals(action)) {
				// Check if there is cached notifications
				if (!cachedNnotifications.isEmpty()) {
					for (PushNotification notification : cachedNnotifications) {
						Log.d(Push.TAG, "Proceed push: " + notification.toString());
						proceedNotification(notification);
					}
					cachedNnotifications.clear();
				}

				clbContext.success();
				return true;
			}

		} catch ( JSONException e) {
			System.err.println("Exception JSON: " + e.getMessage());
			clbContext.error(e.getMessage());
			return false;
		}
		catch (Exception e) {
			System.err.println("Exception: " + e.getMessage());
			clbContext.error(e.getMessage());
			return false;
		}

	}


	public static boolean isActive() {

		if (cordovaWebView != null) {
			return true;
		}
		return false;
	}

	@Override
	public void onPause(boolean multitasking) {
		super.onPause(multitasking);
		Push.inPause = true;
	}

	@Override
	public void onResume(boolean multitasking) {
		super.onResume(multitasking);
		Push.inPause = false;
	}

	public static Context getContext() {
		return CordovaWebView;
	}

	public static void sendJavascript(String js) {
		if (null != cordovaWebView) {
			Log.d("js", "JS" + js);
			//cordovaWebView.sendJavascript(js);
                        cordovaWebView.loadUrl("javascript:"+js);
                        
		}
	}

	public static void proceedNotification(PushNotification extras) {
		if (null != extras) {
			if (null != cordovaWebView) {
				try {
					String js = notificationEventListener + "(\""
							+ extras.getId() + "\", "
							+ extras.toString() + ")";
					Log.d(TAG, js);
					sendJavascript(js);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Log.v(TAG, "proceedNotification: caching extras to proceed at a later time.");
				cachedNnotifications.add(extras);
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		cordovaWebView = null;
	}

	@Override
	public boolean onOverrideUrlLoading(String url) {
		return false;
	}
}
