var notification = {
    listener : function(event, data){}, // we define a signature for the methode
    
    listenerCallback : function(event, data){ // te callback call that signature, probably impossible to redefine a function is it already defined see redefinition method in js
        notification.listener(event, data);
    },
    
    register : function(listener,  configuration ){
        
        notification.listener = listener;
        
        function successCb(){
        
        }
        function errorCb(data){
            console.log('Error while registration' + data);
        }
        
        cordova.exec(
            successCb,
            errorCb,
            'Push',
            'initialize',
            [
                {
                'notificationListener' : 'window.push.listenerCallback',
                'configuration' : configuration 
                }
            ]
        );
        
    },
};
module.exports = notification;
    

