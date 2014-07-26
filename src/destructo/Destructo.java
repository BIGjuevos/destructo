/*
 * The MIT License
 *
 * Copyright 2014 Ryan Null
 */

package destructo;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ryan
 */
public class Destructo {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Welcome to desctructo");
        
        if ( args.length != 1 ) {
            System.out.println("Invalid command.");
            System.out.println("Proper usage: java -jar destructo.jar <server-ip>");
            
            System.exit(1);
        } else {
            System.out.println("DESTRU We'll be connecting to the server " + args[0]);
        }
        
        //initialize our server
        Server server = new Server(args[0]);
        Thread threadServer = new Thread(server);
        
        //start the server
        threadServer.start();
        
        //create a new copter object
        Copter copter = new Copter(server);
        Thread threadCopter = new Thread(copter);
        
        //start the copter thread
        threadCopter.start();
        try {
            //wait for everything to finish
            threadServer.join();
            threadCopter.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(Destructo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
