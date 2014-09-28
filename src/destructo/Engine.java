/*
 * The MIT License
 *
 * Copyright 2014 Ryan Null
 */

package destructo;

import java.awt.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
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
    private int trim = 0; //-100 to 100 (-10% to 10%)
    
    //engine timer information
    private int timerStart;
    private int timerPreArm;
    private int timerPostArm;
    
    //command and control information
    private List<Command> commandQueue;
    private Server server;
    
    //thread control
    private boolean keepRunning = true;
    
    //engine control information
    private File file;

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
    
    /**
     * get the low throttle setting
     * @return double
     */
    public double getThrottleLow() {
        return this.throttleLow;
    }
    
    /**
     * get the arm throttle setting
     * @return double
     */
    public double getThrottleArm() {
        return this.throttleArm;
    }
    
    /**
     * get the min throttle setting
     * @return double
     */
    public double getThrottleMin() {
        return this.throttleMin;
    }

    public int getThrottle() {
        return throttle;
    }

    public void setThrottle(int throttle) {
        this.throttle = throttle;
        System.out.println("ENGINE " + this.id + " THR: " + throttle);
        
        double newThrottle = this.throttleMin + //this is where we start adding on top of
                (
                    (this.throttleMax - this.throttleMin) *  //get our max range of power we can span
                    ( (double)throttle / 100.0) + //get the throttle out of 100
                    (
                        (this.throttleMax - this.throttleMin) *
                        ( (double)trim / 1000.0)
                    ) //add in the trim as a a factor of the range of available throttle
                );
        
        //write the new throttle out the the gpio
        this.write(
                (double)Math.round(newThrottle * 100000) / 100000
        );
    }
    
    public int getTrim() {
        return trim;
    }

    public void setTrim(int trim) {
        this.trim = trim;
        
        //we've updated the trim, force a new throttle to be outputted
        this.setThrottle(this.throttle);
    }
    
    /**
     * start the engines
     */
    public void start() {
        System.out.println("ENGINE "  + this.id + " Starting");
        
        //bypass the setter so we don't auto write
        this.throttle = 0;
        
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        
        //do the start timer (low)
        Timer lowTimer = new Timer();
        StartLowTask slt = new StartLowTask();
        slt.setEngine(this);
        lowTimer.schedule(slt, this.timerStart);
        
        //do the pre arm timer (arm)
        Timer armTimer = new Timer();
        StartArmTask sat = new StartArmTask();
        sat.setEngine(this);
        armTimer.schedule(sat, this.timerStart + this.timerPreArm);
        
        //do the post arm timer (min)
        Timer minTimer = new Timer();
        StartMinTask smt = new StartMinTask();
        smt.setEngine(this);
        minTimer.schedule(smt, this.timerStart + this.timerPreArm + this.timerPostArm);
    }
    
    /**
     * idle the engines
     */
    public void idle() {
        System.out.println("ENGINE " + this.id + " Idling");
        this.write(this.throttleMin);
        
        this.throttle = 0;
    }
    
    /**
     * stop the engines
     */
    public void stop() {
        System.out.println("ENGINE " + this.id + " Stopping");
        this.write(this.throttleOff);
        
        //bypass the setThrottle method
        this.throttle = -1;
    }
    
    /**
     * send to pi-blaster
     */
    private void write(double speed) {
        String content = this.gpio + "=" + speed + "\n";
        System.out.print("ENGINE " + this.id + " WRITE: " + content);
        
        try {
            FileOutputStream fop = null;
            this.file = new File("/dev/pi-blaster");
            fop = new FileOutputStream(file);
            
            fop.write(content.getBytes());
            fop.flush();
            fop.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(201);
        }
    }
    
    @Override
    public void run() {
        /**
         * enter a loop that is responsible for:
         *   relaying information to the server message queue
         */
        
        Command cmd;
        while (this.keepRunning) {
            try {
                cmd = new Command();
                //send them our current throttle
                cmd.engineThrottle(this.id, this.throttle);
                this.server.queue(cmd);
                
                cmd = new Command();
                //send them our current trim
                cmd.engineTrim(this.id, this.trim);
                this.server.queue(cmd);
                
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Utility internal classes
     */
    class StartLowTask extends TimerTask {
        private Engine engine;
        
        @Override
        public void run() {
            engine.write(engine.getThrottleLow());
        }
        
        public void setEngine(Engine e) {
            this.engine = e;
        }
    }
    
    class StartArmTask extends TimerTask {
        private Engine engine;
        
        @Override
        public void run() {
            engine.write(engine.getThrottleArm());
        }
        
        public void setEngine(Engine e) {
            this.engine = e;
        }
    }
    
    class StartMinTask extends TimerTask {
        private Engine engine;
        
        @Override
        public void run() {
            engine.write(engine.getThrottleMin());
            System.out.println("ENGINE " + this.engine.id + " Startup procedures done for " + this.engine.id);
        }
        
        public void setEngine(Engine e) {
            this.engine = e;
        }
    }
    
}
