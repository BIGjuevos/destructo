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
    public static final int TYPE_INFO_ENGINE = 1;
    
    private String message;
    private int type;
    
    public void hello(String name) {
        this.type = TYPE_HELLO;
        
        this.message = "hello " + name;
    }
    
    public void engine(int id, int throttle) {
        this.type = TYPE_INFO_ENGINE;
        
        this.message = "E " + id + " " + throttle;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public int getType() {
        return this.type;
    }
}
