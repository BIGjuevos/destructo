/*
 * The MIT License
 *
 * Copyright 2014 Ryan Null
 */

package destructo;

import com.pi4j.component.gyroscope.Gyroscope;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import java.io.IOException;
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
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Welcome to desctructo");
        
        if ( args.length != 1 ) {
            System.out.println("Invalid command.");
            System.out.println("Proper usage: java -jar destructo.jar <server-ip>");
            
            System.exit(1);
        } else {
            System.out.println("DESTRU   We'll be connecting to the server " + args[0]);
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
        
        //create a new control object
        Control control = new Control(copter, server);
        Thread threadControl = new Thread(control);
        threadControl.start();
        
        try {    
            //wait for everything to finish
            threadServer.join();
            threadCopter.join();
            threadControl.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(Destructo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
