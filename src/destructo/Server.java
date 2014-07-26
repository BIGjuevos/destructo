/*
 * The MIT License
 *
 * Copyright 2014 Ryan Null
 */

package destructo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the class that sends stuff to the bossy server
 * 
 * @author ryan
 */
public class Server implements Runnable {
    //protocol information
    private int port;
    private String host;
    
    //command and control information
    private LinkedList<Command> outboundQueue;
    
    /**
     *
     * @param message
     */
    public void queue(Command message) {
        this.outboundQueue.push(message);
    }
    
    public Server( String h ) {
        //setup server connection information
        this.port = 31313;
        this.host = h;
        
        //establish synchronous queues
        this.outboundQueue = new LinkedList();
    }
    
    @Override
    public void run() {
        DatagramSocket clientSocket;
        InetAddress IPAddress;
        byte[] sendData = new byte[1024];
        
        /**
         * Initialize out socket
         */
        try {
            clientSocket = new DatagramSocket();
            IPAddress = InetAddress.getByName(this.host);
        } catch (SocketException | UnknownHostException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("SERVER Failed to initialize server");
            return;
        }
        
        /**
         * enter a server loop that is responsible for:
         *   processing outbound messages to the server
         */
        Command cmd;
        DatagramPacket sendPacket;
        
        System.out.println("SERVER Server Ready, entering loop");
        while ( true ) {
            if ( this.outboundQueue.size() > 0 ) {
                try {
                    cmd = this.outboundQueue.pop();
                } catch ( Exception e ) {
                    continue; //keep going around
                }
                
                switch ( cmd.getType() ) {
                    case 0: //hello
                        System.out.println("SERVER Sending message '" + cmd.getMessage() + "'");
                        break; 
                }
                
                sendData = cmd.getMessage().getBytes();
                
                sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, this.port);
                try {
                    clientSocket.send(sendPacket);
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                //wait for a bit instead of eating CPU up
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
