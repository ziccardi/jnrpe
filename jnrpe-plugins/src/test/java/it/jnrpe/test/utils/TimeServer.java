/*******************************************************************************
 * Copyright (c) 2007, 2014 Massimiliano Ziccardi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package it.jnrpe.test.utils;

import it.jnrpe.utils.TimeUnit;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import javax.net.ServerSocketFactory;

/**
 * A simple TimeServer implementation.
 * Since it is used for testing, a method is provided to force the next time response.
 * 
 * @author Massimiliano Ziccardi
 */
public class TimeServer extends Thread {

    /**
     * The server socket.
     */
    private ServerSocket serverSocket = null;
    
    /**
     * The port where the server listens to.
     */
    private final int port;
    
    /**
     * A delegate object that will receive the timeserver events.
     */
    private final TimeServerDelegate delegate;
    
    /**
     * The to be provided to the next request.
     * Used for testing purposes. 
     */
    private Date nextTime = null;
    
    /**
     * Used to know if the server should be stopped.
     */
    private boolean keepRunning = true;
    
    /**
     * TimeServer delegate interface.
     * 
     * @author Massimiliano Ziccardi
     */
    public interface TimeServerDelegate {
        /**
         * Called when the server is just started.
         */
        void serverDidStart();
        
        /**
         * Called after the server has stopped.
         */
        void serverDidStop();
        
        /**
         * Called if an exception has been thrown during server stop.
         * @param thr the exception
         */
        void serverStoppedWithError(Throwable thr);
    }
    
    /**
     * Sets the time to be returned to the next request.
     * 
     * @param date the time to be returned to the next request
     */
    public final void setNextTime(final Date date) {
        nextTime = date;
    }
    
    /**
     * Builds and initializes the server.
     * 
     * @param listeningPort the server port
     * @param serverDelegate the delegate
     */
    public TimeServer(final int listeningPort, final TimeServerDelegate serverDelegate) {
        this.port = listeningPort;
        this.delegate = serverDelegate;
    }
    
    @Override
    public final void run() {
        try {
            serverSocket = ServerSocketFactory.getDefault().createServerSocket(port);
            while (keepRunning) {
                delegate.serverDidStart();
                Socket s = serverSocket.accept();
        
                DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                
                if (nextTime != null) {
                    // Next answer has been forced.
                    dout.writeInt((int) (TimeUnit.MILLISECOND.convert(nextTime.getTime(), TimeUnit.SECOND) + 2208988800L));
                    nextTime = null;
                } else {
                    dout.writeInt((int) (TimeUnit.SECOND.currentTime() + 2208988800L));    
                }
        
                dout.flush();
                dout.close();
                s.close();
            }
        } catch (Exception e) {
            delegate.serverStoppedWithError(e);
        }
    }
    
    /**
     * Shuts down the server...
     */
    public final void shutdown() {
        try {
            keepRunning = false;
            serverSocket.close();
        } catch (IOException e) {
            // Ignore exception
        }
        delegate.serverDidStop();
    }
}
