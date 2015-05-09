var notification = {
    listener : function(event, data){}, // we define a signature for the methode
    
    listenerCallback : function(event, data){ // te callback call that signature, probably impossible to redefine a function is it already defined see redefinition method in js
        notification.listener(event, data);
    },
    
    register : function(listener){
        
        notification.listener = listener;
        
        function successCb(){
        
        }
        function errorCb(){
            console.log('Error while registration');
        }
        
        cordova.exec(
            successCb,
            errorCb,
            'Push',
            'initialize',
            [{'notificationListener' : 'window.push.listenerCallback'}]
        );
        
    },
};
module.exports = notification;
    

