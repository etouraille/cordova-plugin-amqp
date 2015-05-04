package org.amqp.notification;

import org.apache.cordova.LOG;
import org.json.JSONException;
import org.json.JSONObject;

import org.amqp.notification.PushNotification;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

public class PushHandlerActivity extends Activity {

	/*
	 * this activity will be started if we receive/open push notification
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			boolean isCordovaWebViewActive = Push.isActive();
			processPushBundle(isCordovaWebViewActive);

			Log.d(Push.TAG, "MM--- isCordovaWebViewActive = " + isCordovaWebViewActive);
			if (!isCordovaWebViewActive) {
				forceMainActivityReload();
			}

			finish();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Forces the main activity to re-launch if it's unloaded.
	 */
	public void forceMainActivityReload() {
		PackageManager pm = getPackageManager();
		Intent launch = pm.getLaunchIntentForPackage(getApplicationContext().getPackageName());
		startActivity(launch);
	}

	/**
	 * Takes the push extras from the intent, and sends it through to the
	 * Push for processing.
	 *
	 * @throws JSONException
	 */
	private void processPushBundle(boolean isPushPluginActive) throws JSONException {
		Bundle extras = getIntent().getExtras();

		if (null != extras) {
			String message =extras.getString("pushMessage");
			Push.proceedNotification(new PushNotification(message));

			LOG.d(Push.TAG, message);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration arg0) {
		super.onConfigurationChanged(arg0);
	}
}
