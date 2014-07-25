/*
 * The MIT License
 *
 * Copyright 2014 Ryan Null
 */

package destructo;

import java.util.List;
import java.util.LinkedList;
import java.util.Collections;

/**
 * This class is responsible for managing the information delivered to bossy about the engines
 * 
 * It is also responsible for putting into action commands from bossy
 * 
 * @author Ryan Null
 */
public class Engine implements Runnable {
    //engine speed information
    private int gpio;
    private double throttleOff;
    private double throttleLow;
    private double throttleArm;
    private double throttleMin;
    private double throttleMax;
    private double throttleTrim;
    
    //engine timer information
    private int timerStart;
    private int timerPreArm;
    private int timerPostArm;
    
    //command and control information
    private List<Command> commandQueue;
    private Server server;

    /**
     * set all of the pertinent information
     * 
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
    public Engine(int g, double off, double low, double arm, double min, double max, double trim, int start, int preArm, int postArm) {
        //set all of the engine properties
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
    
    @Override
    public void run() {
        /**
         * enter a loop that is responsible for:
         *   reading from the commandQueue and executing as fast as possbile
         *   relaying information to the server message queue
         */
    }
}
