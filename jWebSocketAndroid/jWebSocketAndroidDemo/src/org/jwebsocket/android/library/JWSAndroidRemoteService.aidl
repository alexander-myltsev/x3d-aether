//Since JWSAndroidService runs in a different process from client Application's UI, and to pass objects 
//between processes the Android platform, one process can not normally access the memory of another process. 
//So to talk, they need to decompose their objects into primitives that the operating system can understand, 
//and "marshall" the object across that boundary for you. So we provide the AIDL tool to generate code that 
//enables the client processes on an Android-powered device to talk using interprocess communication (IPC). 
//to call methods on an object in another process running as JWSAndroidService, you would use this AIDL to
//generate code to marshall the parameters.

package org.jwebsocket.android.library;

import org.jwebsocket.android.library.JWSAndroidRemoteServiceCallback;
import org.jwebsocket.android.library.ParcelableToken;

// Declare the interface.
interface JWSAndroidRemoteService {

	void open();

    void close();
    
    void disconnect();
 
    void send(in String data);
    
    void sendText(in String target, in String data);

	void broadcastText(in String data);
	
	void sendToken(in ParcelableToken token);

    void saveFile(in String fileName, in String scope, in boolean notify, in byte[] data);
    
    String getUsername();
    
    void login(in String aUsername, in String aPassword);
    
    void logout();
    
    void ping(in boolean echo);
    
    void getConnections(); 
    
    boolean isAuthenticated();
    
    /**
     * Register a service to call back to the clients using this remote service
     */
    void registerCallback(in JWSAndroidRemoteServiceCallback cb);
    
    /**
     * Remove a previously registered callback interface.
     */
    void unregisterCallback(in JWSAndroidRemoteServiceCallback cb);

}