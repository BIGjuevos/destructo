/*
 * The MIT License
 *
 * Copyright 2014 Ryan Null
 */

package destructo;

import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is responsible for managing the information delivered to bossy about the engines
 * 
 * It is also responsible for putting into action commands from bossy
 * 
 * @author Ryan Null
 */
public class Engine implements Runnable {
    //engine speed information
    private int id;
    private int gpio;
    private double throttleOff;
    private double throttleLow;
    private double throttleArm;
    private double throttleMin;
    private double throttleMax;
    private double throttleTrim;
    private int throttle = 0; //0-100
    
    //engine timer information
    private int timerStart;
    private int timerPreArm;
    private int timerPostArm;
    
    //command and control information
    private List<Command> commandQueue;
    private Server server;
    
    //thread control
    private boolean keepRunning = true;

    /**
     * set all of the pertinent information
     * 
     * @param i id of the engine 0...3
     * @param g gpio id on pi-blaster
     * @param off off speed
     * @param low low speed
     * @param arm arm speed
     * @param min min speed
     * @param max max speed
     * @param trim trim percentage -50 -> 50 = -5% to +5%
     * @param start timer that occurs from engine off to engine low
     * @param preArm timer that occurs from engine low to engine arm
     * @param postArm timer that occurs from engine arm to engine min
     */
    public Engine(int i, int g, double off, double low, double arm, double min, double max, double trim, int start, int preArm, int postArm) {
        //set all of the engine properties
        this.id = i;
        this.gpio = g;
        this.throttleOff = off;
        this.throttleLow = low;
        this.throttleArm = arm;
        this.throttleMin = min;
        this.throttleMax = max;
        this.throttleTrim = trim;
        
        //set the timers for the engine
        this.timerStart = start;
        this.timerPreArm = preArm;
        this.timerPostArm = postArm;
        
        //when using this, it may throw a ConcurrentModificationException, that we should watch out for
        this.commandQueue = Collections.synchronizedList( new LinkedList() );
    }
    
    /**
     * set the server object
     * @param s Server to communicate with
     */
    public void setServer(Server s) {
        this.server = s;
    }
    
    public void start() {
        
    }
    
    @Override
    public void run() {
        /**
         * enter a loop that is responsible for:
         *   reading from the commandQueue and executing as fast as possible
         *   relaying information to the server message queue
         */
        
        Command cmd;
        while (this.keepRunning) {
            try {
                cmd = new Command();
                cmd.engine(this.id, this.throttle);
                
                this.server.queue(cmd);
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
