package destructo;

import java.io.IOException;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;

/**
 * Adapted from: http://www.raspberrypi.org/forums/viewtopic.php?f=81&t=57336
 * 
 * Original Author: wgvanveen
 * 
 * @author ryan
 */
public class ADXL345 {

   /**
    * Trying to implement the I2C connection 
    * implemented on ADXL345 Adafruit code
    */
   
   private I2CDevice device;
   
   private static final byte ADRESS = 0x53;
   
   private static final byte ADXL345_REG_POWER_CTL = 0x2D;
   
   private static final byte ADXL345_REG_DATAX0 = 0x32; //X-axis data 0
   private static final byte ADXL345_REG_DATAY0 = 0x34; //Y-axis data 0
   private static final byte ADXL345_REG_DATAZ0 = 0x36; //Z-axis data 0
   
   private static final double ADXL345_MG2G_MULTIPLIER = 0.004; // 4mg per lsb
   
   public ADXL345(I2CBus bus) throws IOException{
      device = bus.getDevice(ADRESS);
      device.write(ADXL345_REG_POWER_CTL, (byte)0x08);
   }
   
   public double readAxisX() throws IOException{
      byte[] buff = new byte[2];
      device.read(ADXL345_REG_DATAX0,buff,0,2);
      int response = (asInt(buff[0]) | ((int)buff[1])<< 8);
      double x = (double)response * ADXL345_MG2G_MULTIPLIER * 1.0;

      return round(x);
   }
   
   public double readAxisY() throws IOException{
      byte[] buff = new byte[2];
      device.read(ADXL345_REG_DATAY0,buff,0,2);
      int response = (asInt(buff[0]) | ((int)buff[1])<< 8);
      double y = (double)response * ADXL345_MG2G_MULTIPLIER * 1.0;
      
      return round(y);
   }
   
   public double readAxisZ() throws IOException{
      byte[] buff = new byte[2];
      device.read(ADXL345_REG_DATAZ0,buff,0,2);
      int response = (asInt(buff[0]) | ((int)buff[1])<< 8);
      double z = (double)response * ADXL345_MG2G_MULTIPLIER * 1.0;
      
      return round(z);
   }
   
   public int asInt(byte b){
      int i = b;
      if(i<0){
         i = i + 256;
      }
      return i;
   }
   
   public double round( double x ) {
      x = x * 1000000;
      x = Math.round(x);
      x = x / 1000000;
       
       return x;
   }
}
