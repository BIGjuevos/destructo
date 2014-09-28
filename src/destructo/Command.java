/*
 * The MIT License
 *
 * Copyright 2014 Ryan Null
 */

package destructo;

/**
 *
 * @author ryan
 */
public class Command {
    public static final int TYPE_HELLO = 0;
    
    public static final int TYPE_INFO_ENGINE_THROTTLE = 100;
    public static final int TYPE_INFO_ENGINE_TRIM = 101;
    
    public static final int TYPE_SENSOR_ACCEL_FORCES = 200;
    
    private String message;
    private int type;
    
    public void hello(String name) {
        this.type = TYPE_HELLO;
        
        this.message = "hello " + name;
    }
    
    public void engineThrottle(int id, int throttle) {
        this.type = TYPE_INFO_ENGINE_THROTTLE;
        
        this.message = "E T " + id + " " + throttle;
    }
    
    public void engineTrim(int id, int trim) {
        this.type = TYPE_INFO_ENGINE_TRIM;
        
        this.message = "E R " + id + " " + trim;
    }
    
    public void accelData(double X, double Y, double Z) {
        this.type = TYPE_SENSOR_ACCEL_FORCES;
        
        this.message = "I A " + X + " " + Y + " " + Z;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public int getType() {
        return this.type;
    }
}
