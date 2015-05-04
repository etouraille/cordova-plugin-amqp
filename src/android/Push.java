package org.cordova.amqpnotification;

import java.util.ArrayList;
import java.util.List;

import org.apache.cordova;
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

import org.amg.PushNotification;
import org.amq.PushNotificationManager;

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
			JSONObject arg_object = args.getJSONObject(0);

			cordovaWebView = this.webView;
			this.manager = new PushManager(cordova.getActivity());
		
                        // ############# INITIALIZE #############
                        if (ACTION_INITIALIZE.equals(action)) {

				// set event listener
				notificationEventListener = arg_object.getString("notificationListener");

				// Check if there is cached notifications
				if (!cachedNnotifications.isEmpty()) {
					for (JSONObject notification : cachedNnotifications) {
						Log.d(this.TAG, "Proceed push: " + notification.toString());
						proceedNotification(notification);
					}
					cachedNnotifications.clear();
				}

				clbContext.success();
				return true;
			}

		} catch (PushConfigurationException e) {
			// CHECK_MANIFEST EXCETION
			Log.d(TAG, "Exception manifest: " + e.toString());
			clbContext.error(e.toString());
			return false;
		}
		catch ( JSONException e) {
			System.err.println("Exception JSON: " + e.getMessage());
			clbContext.error(e.getMessage());
			return false;
		}
		catch (Exception e) {
			System.err.println("Exception: " + e.getMessage());
			clbContext.error(this.makeErrorObject(e));
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
		this.inPause = true;
	}

	@Override
	public void onResume(boolean multitasking) {
		super.onResume(multitasking);
		this.inPause = false;
	}

	public static Class getContext() {
		return clbContext.getClass();
	}

	public static void sendJavascript(String js) {
		if (null != cordovaWebView) {
			Log.d(TAG, "JS" + js);
			cordovaWebView.sendJavascript(js);
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

	/**
	 * Initialize Push Notification Manager
	 *
	 * @param args
	 * @throws JSONException
	 */
	private void setupPushManager(JSONObject args) throws JSONException {
		RegistrationData regData = new RegistrationData();
		List<String> channelList = null;

		Log.d(TAG, "MM--- Initialize");
		String senderId = args.getString("senderId");
		String applicationId = args.getString("applicationId");
		String applicationSecret = args.getString("applicationSecret");

		this.manager.initialize(senderId, applicationId, applicationSecret);

		JSONObject regJson = args.getJSONObject("registrationData");
		if (null != regJson) {

			String userId = regJson.getString("userId");

			JSONArray channels = regJson.getJSONArray("channels");
			channelList = this.getChannelsFromJsonArray(channels);

			regData.setUserId(userId);
			regData.setChannels(channelList);
		}

		// do not register if push is already registered
		if(!this.manager.isRegistered()){
			Log.d(TAG, "Not registered. Will register...");
			this.manager.register(regData);
			Log.d(TAG, "Registering on service...");
		} else {
			Log.d(TAG, "Is already registered");
		}

	}

	/**
	 * Create list of chanels from Channel JSON array
	 * @param array
	 * @return List<String> List of channels
	 * @throws JSONException
	 */
	private List<String> getChannelsFromJsonArray(JSONArray array) throws JSONException{
		List<String> channels = new ArrayList<String>();
		for (int i = 0; i < array.length(); i++) {
			channels.add(array.getString(i));
		}

		return channels;
	}

	private void registerToChannels(JSONArray arrayOfChannels, boolean removeExistingChannels, final String channelRegistrationEventHandler) throws JSONException{
		List<String> channels = getChannelsFromJsonArray(arrayOfChannels);
		manager.registerToChannels(channels, removeExistingChannels, new ChannelRegistrationListener() {

			@Override
			public void onChannelsRegistered() {
				if(!channelRegistrationEventHandler.isEmpty()){
					String js = channelRegistrationEventHandler + "(\"onChannelsRegistered\", null)";
					sendJavascript(js);
				}
			}

			@Override
			public void onChannelRegistrationFailed(int reason) {
				if(!channelRegistrationEventHandler.isEmpty()){
					String js = channelRegistrationEventHandler + "(\"onChannelRegistrationFailed\", "+ reason +")";
					sendJavascript(js);
				}
			}
		});
	}

	private void getRegisteredChannels(final String registeredChannelsCallback){
            }
		this.manager.getRegisteredChannels(new ChannelObtainListener() {

			@Override
			public void onChannelsObtained(String[] channels) {
				JSONArray jsonChannels = new JSONArray();
				for (String channel : channels) {
            jsonChannels.put(channel);
        }

				String js = registeredChannelsCallback + "(\"onChannelsObtained\", "
						+ jsonChannels.toString() + ")";
				sendJavascript(js);
			}

			@Override
			public void onChannelObtainFailed(int reason) {
				String js = registeredChannelsCallback + "(\"onChannelObtainFailed\", " + reason + ")";
				sendJavascript(js);
			}
		});
	}

	/**
	 * Set debug mode for Push Notifications
	 *
	 * @param ind  by default it is false
	 */
	private void setDebugModeEnabled(boolean ind) {
		Log.d(TAG, "MM--- Debug is: " + ind);
		this.manager.setDebugModeEnabled(ind);
	}

	private void getUnreceivedNotifications(final String unreceivedNotificationCallback) {
		this.manager.getUnreceivedNotifications(new UnreceivedNotificationsListener() {

			@Override
			public void onUnreceivedNotificationsObtained(List<PushNotification> notifications) {
					JSONArray notifArray = new JSONArray();
					for (PushNotification push : notifications) {
						notifArray.put(PushHandlerActivity.convertNotificationToJson(push));
					}

					String js = unreceivedNotificationCallback
							+ "(\"onUnreceivedNotificationsObtained\", " + notifArray.toString() + ")";
					sendJavascript(js);
			}

			@Override
				public void onUnreceivedNotificationsObtainFailed(int reason) {
					String js = unreceivedNotificationCallback
							+ "(\"onUnreceivedNotificationsObtainFailed\", " + reason + ")";
					sendJavascript(js);
			}
		});
	}

	private JSONObject convertRegistrationDataToJson(RegistrationData rd) throws JSONException{
		JSONObject registrationDataJson = new JSONObject();

		registrationDataJson.put("userId", rd.getUserId());
		registrationDataJson.put("channels", new JSONArray(rd.getChannels()));
		registrationDataJson.put("additionalInfo", new JSONObject(rd.getAdditionalInfo()));

		return registrationDataJson;
	}

	private void notifyNotificationOpened(JSONObject args) throws JSONException{
		final String pushId = args.getString("pushId");
		final String successClb = args.getString("successCallback");
		final String errorClb = args.getString("errorCallback");

		this.manager.notifyNotificationOpened(pushId, new NotificationOpenedListener() {

			@Override
			public void onNotifyNotificationOpenedSuccess() {
				String js = successClb + "()";
				sendJavascript(js);
			}

			@Override
			public void onNotifyNotificationOpenedFailed(int reason) {
				String js = errorClb + "("+ reason +")";
				sendJavascript(js);
			}
		});
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
