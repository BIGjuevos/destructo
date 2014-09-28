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

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ryan
 */
class Accelerometer implements Runnable {
    private I2CBus bus;
    
    private ADXL345 adxl345;
    
    private double X,Y,Z;
    
    private Server server;

    public Accelerometer(Server s) {
        try {
            this.bus = I2CFactory.getInstance(I2CBus.BUS_1);
            this.server = s;
            
            this.adxl345 = new ADXL345(this.bus);
            
            System.out.println("ACCELR   Ready");
        } catch (IOException ex) {
            Logger.getLogger(Accelerometer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        System.out.println("ACCELR   Running");
        
        Command c = new Command();
        
        while ( true ) {
            try {
                this.X = this.adxl345.readAxisX();
                this.Y = this.adxl345.readAxisY();
                this.Z = this.adxl345.readAxisZ();
                
                //the command will have accelerometer data in it
                c.accelData(this.X, this.Y, this.Z);
                
                server.queue( c );
                
                Thread.sleep(100);
            } catch (IOException ex) {
                Logger.getLogger(Accelerometer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(Accelerometer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public double getX() {
        return X;
    }

    public double getY() {
        return Y;
    }

    public double getZ() {
        return Z;
    }
}
