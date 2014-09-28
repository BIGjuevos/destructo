/*
 * The MIT License
 *
 * Copyright 2014 ryan.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package destructo;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Purpose of this class is to fly the drone based on the current target and current sensor data
 * @author ryan
 */
public class Control implements Runnable {
    
    private Copter copter;
    private Server server;
    private Target target;
    
    private Accelerometer accel;
    private Thread accelThread;

    Control(Copter c, Server s) throws IOException {
        this.copter = c;
        this.server = s;
        this.accel = new Accelerometer(s);
        
        System.out.println("CONTRO   Ready");
    }

    @Override
    public void run() {
        //start the accelerometer so it always has the newest data in it
        this.accelThread = new Thread(accel);
        this.accelThread.start();
        
        try {
            //wait a bit for data
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //set our initial target as our current reference frame
        this.target = new Target();
        this.target.setX(this.accel.getX());
        this.target.setY(this.accel.getY());
        this.target.setZ(this.accel.getZ());
        
        //slert that we are now running
        System.out.println("CONTRO   Running");
        
        int diff = 0;
        
        //let's enter a basic control estimate loop
        while (true) {
            try {
                //z check
                if ( ( diff = check(this.target.getZ(), this.accel.getZ() ) ) != 0 ) {
                    
                }
                //x check
                //y check
                
                //check often
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public int check(double target, double current) {
        //we must take the accelerometer and convert it into thrust... this should be fun
        
        
        return 0;
    }
}
