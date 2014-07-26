/*
 * The MIT License
 *
 * Copyright 2014 Ryan Null
 */

package destructo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *This is the class that aggregates all sensore information and delivers it to bossy
 * 
 * @author ryan
 */
public class Copter implements Runnable {
    //engines
    private Engine frontLeft;
    private Engine frontRight;
    private Engine backLeft;
    private Engine backRight;
    
    //engine threads
    private Thread threadFrontLeft;
    private Thread threadFrontRight;
    private Thread threadBackLeft;
    private Thread threadBackRight;
    
    //command and control
    private Server server;
    
    //listening stuff
    private DatagramSocket socket;
    
    /**
     * constructor that sets us up
     * @param s the server we need to talk to
     */
    public Copter(Server s) {
        //set the server
        this.server = s;
        
        //initialize the engines
        this.frontLeft =  new Engine(0,  4, 0, 0.005, 0.06, 0.111, 0.19, 0, 500000, 1000000, 2000000);
        this.frontLeft.setServer(this.server);
        
        this.frontRight = new Engine(1, 17, 0, 0.005, 0.06, 0.111, 0.19, 0, 500000, 1000000, 2000000);
        this.frontRight.setServer(this.server);
        
        this.backLeft =   new Engine(2, 18, 0, 0.005, 0.06, 0.111, 0.19, 0, 500000, 1000000, 2000000);
        this.backLeft.setServer(this.server);
        
        this.backRight =  new Engine(3, 22, 0, 0.005, 0.06, 0.111, 0.19, 0, 500000, 1000000, 2000000);
        this.backRight.setServer(this.server);
    }
    
    @Override
    public void run() {
        this.startup();
        
        byte[] recvData = new byte[64];
        try {
            this.socket = new DatagramSocket(31313);
        } catch (SocketException ex) {
            Logger.getLogger(Copter.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(100);
        }
        
        while (true) {
            try {
                recvData = new byte[64];
                DatagramPacket recvPacket = new DatagramPacket(recvData, recvData.length);
                this.socket.receive(recvPacket);
                String sentence = new String( recvPacket.getData() );
                sentence = sentence.trim();
                
                System.out.println("Got message from server " + sentence);
                
                if ( sentence.substring(0,2).equals("E S") ) {
                    //we need to start an engine
                    String[] parts = sentence.split(" ");
                    
                    int engineId = Integer.parseInt(parts[2]);
                    switch (engineId) {
                        case 0:
                            this.frontLeft.start();
                            break;
                        case 1:
                            this.frontRight.start();
                            break;
                        case 2:
                            this.backLeft.start();
                            break;
                        case 3:
                            this.backRight.start();
                            break;
                    }
                }
            } catch (IOException ex) {
                System.exit(101);
            }
        }
    }
    
    private void startup() {
        System.out.println("COPTER Going through start procedures");
        
        //say hello to the server
        Command cmd = new Command();
        cmd.hello("destructo");
        this.server.queue(cmd);
        
        //do other stuff, like wait for their reply
        System.out.println("COPTER Waiting for server to say they heard us");
        try {
            DatagramSocket socket = new DatagramSocket(31313);
            byte[] recvData = new byte[64];
            DatagramPacket recvPacket = new DatagramPacket(recvData, recvData.length);
            socket.receive(recvPacket);

            String sentence = new String( recvPacket.getData() );
            sentence = sentence.trim();
            
            //verify to see that the server heard us just finr
            if ( !sentence.contains("welcome destructo") ) {
                System.out.println("COPTER Server replied improperly with: '" + sentence + "'");
                
                System.exit(3);
            }
            
            socket.close();
        } catch (SocketException e) {
            System.exit(1);
        } catch (IOException ex) {
            System.exit(2);
        }
        
        System.out.println("COPTER Server acknowledged us");
        //send off configuration
        
        //start the engine threads
        System.out.println("COPTER starting engine threads");
        this.threadFrontLeft = new Thread(this.frontLeft);
        this.threadFrontLeft.start();
        
        this.threadFrontRight = new Thread(this.frontRight);
        this.threadFrontRight.start();
        
        this.threadBackLeft = new Thread(this.backLeft);
        this.threadBackLeft.start();
        
        this.threadBackRight = new Thread(this.backRight);
        this.threadBackRight.start();
        
        System.out.println("COPTER engine threads started");
    }
}
