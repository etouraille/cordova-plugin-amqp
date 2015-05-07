var notification = {
    listener : function(event, data){}, // we define a signature for the methode
    
    listenerCallback : function(event, data){ // te callback call that signature, probably impossible to redefine a function is it already defined see redefinition method in js
        amqp.listener(event, data);
    },
    
    register : function(listener){
        
        function successCb(){
            notification.listener = listener;
        }
        function errorCb(){
            console.log('Error while registration');
        }
        
        cordova.exec(
            successCb,
            errorCb,
            'Push',
            'initialize',
            [{'notificationListener' : 'notification.listenerCallback'}]
        );
        
    },
};
module.exports(notification);
    

