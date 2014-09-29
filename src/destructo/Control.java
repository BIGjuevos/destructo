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
    
    private static int TRIM_SCALE = 7;

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
        
        //set our initial target as our current reference frame averaged for the last couple seconds
        double  ax = this.accel.getX(),
                ay = this.accel.getY(),
                az = this.accel.getZ();
        
        //calculate a running average
        for ( int i = 0; i < 8; i++ ) {
            ax -= ax / 10;
            ax += this.accel.getX() / 10;
            
            ay -= ay / 10;
            ay += this.accel.getY() / 10;
            
            az -= az / 10;
            az += this.accel.getZ() / 10;
            
            //wait a bit before the next sample
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        System.out.println("CONTRO   Axis Average Samples");
        System.out.println("CONTRO   X: " + ax);
        System.out.println("CONTRO   Y: " + ay);
        System.out.println("CONTRO   Z: " + az);
        
        this.target = new Target();
        this.target.setX(ax);
        this.target.setY(ay);
        this.target.setZ(az);
        
        //slert that we are now running
        System.out.println("CONTRO   Running");
        
        double diff;
        
        //let's enter a basic control estimate loop
        while (true) {
            try {
                if ( !this.copter.isEnginesOn() ) {
                    Thread.sleep(10);
                    continue;
                }
                //z check
                if ( ( diff = check(this.target.getZ(), this.accel.getZ() ) ) != 0.0 ) {
                    //update all engine throttles
                    this.copter.getFrontLeft().setTrim(
                            (int) (this.copter.getFrontLeft().getTrim() + ( diff * TRIM_SCALE ) )
                    );
                    this.copter.getFrontRight().setTrim(
                            (int) (this.copter.getFrontRight().getTrim() + ( diff * TRIM_SCALE ) )
                    );
                    this.copter.getBackLeft().setTrim(
                            (int) (this.copter.getBackLeft().getTrim() + ( diff * TRIM_SCALE ) )
                    );
                    this.copter.getBackRight().setTrim(
                            (int) (this.copter.getBackRight().getTrim() + ( diff * TRIM_SCALE ) )
                    );
                }
                //x check
                if ( ( diff = check(this.target.getX(), this.accel.getX() ) ) != 0.0 ) {
                    if ( diff < 0 ) {
                        this.copter.getFrontLeft().setTrim(
                                (int) (this.copter.getFrontLeft().getTrim() - ( diff * TRIM_SCALE ) )
                        );
                        this.copter.getFrontRight().setTrim(
                                (int) (this.copter.getFrontRight().getTrim() - ( diff * TRIM_SCALE ) )
                        );
                    } else {
                        this.copter.getBackLeft().setTrim(
                                (int) (this.copter.getBackLeft().getTrim() - ( diff * TRIM_SCALE ) )
                        );
                        this.copter.getBackRight().setTrim(
                                (int) (this.copter.getBackRight().getTrim() - ( diff * TRIM_SCALE ) )
                        );
                    }
                }
                //y check
                if ( ( diff = check(this.target.getY(), this.accel.getY() ) ) != 0.0 ) {
                    if ( diff > 0 ) {
                        this.copter.getFrontLeft().setTrim(
                                (int) (this.copter.getFrontLeft().getTrim() - ( diff * TRIM_SCALE ) )
                        );
                        this.copter.getBackLeft().setTrim(
                                (int) (this.copter.getBackLeft().getTrim() - ( diff * TRIM_SCALE ) )
                        );
                    } else {
                        this.copter.getFrontRight().setTrim(
                                (int) (this.copter.getFrontRight().getTrim() - ( diff * TRIM_SCALE ) )
                        );
                        this.copter.getBackRight().setTrim(
                                (int) (this.copter.getBackRight().getTrim() - ( diff * TRIM_SCALE ) )
                        );
                    }
                }
                
                //check often
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public double check(double target, double current) {
        //we must take the accelerometer and convert it into thrust... this should be fun
        
        double diff = current - target;
        
        //our max diff should be 1 on a mathematical level, let's correct based off of that.
        diff = Math.min(diff / 3, 1);
        
        if ( Math.abs(diff) > 0.005 ) {
            return -1 * diff;
        } else {
            return 0.0;
        }
    }
}
