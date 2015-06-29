#Notification systeme

 * Notification are send via a brocker, and can adress a certain device 
  - the device is identified via a unique identifier
  - the brocker listener must restart when the network is available
  - the brocker listener must retain the message in the queue
  - the brocker listener must restart automatically when it stops

 * The sended message must raise a branded notification.

 * The notification have a json content, the json format

 * The plugin is available for cordova.
