/*
 * The MIT License
 *
 * Copyright 2014 Ryan Null
 */

package destructo;

import java.util.List;

/**
 * This is the class that connects to the bossy server
 * 
 * @author ryan
 */
public class Server implements Runnable {
    //protocol information
    private int port;
    private String host;
    
    //command and control information
    private List<Command> outboundQueue;
    
    public Server( String h ) {
        this.port = 31313;
        this.host = h;
    }
    
    @Override
    public void run() {
        
    }
}
