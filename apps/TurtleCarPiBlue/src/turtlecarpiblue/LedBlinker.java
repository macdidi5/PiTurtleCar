package turtlecarpiblue;

import com.pi4j.io.gpio.GpioPinDigitalOutput;

public class LedBlinker extends Thread {
    
    private final GpioPinDigitalOutput pin;
    private boolean exit = false;
    private boolean blink = false;
    
    public LedBlinker(GpioPinDigitalOutput pin) {
        this.pin = pin;
    }
    
    @Override
    public void run() {
        int i = 0;
        
        while (!exit) {
            if (!blink) {
                synchronized(this) {
                    try {
                        wait();
                    }
                    catch (InterruptedException e) {
                        System.out.println("============ " + e.toString());
                    }
                }
            }
            
            if (!exit) {
                pin.pulse(500, true);
                delay(500);
            }
        }
    }
    
    public synchronized void startBlink() {
        blink = true;
        notify();
    }
    
    public void stopBlink() {
        blink = false;
    }
    
    public synchronized void exit() {
        exit = true;
        notify();
    }
    
    public static void delay(int ms) {
        try {
            Thread.sleep(ms);
        }
        catch (InterruptedException e) {
            System.out.println("============ " + e.toString());
        }        
    }
    
}
