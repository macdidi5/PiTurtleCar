package turtlecarpiblue;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataListener;
import com.pi4j.io.serial.SerialFactory;
import com.pi4j.io.serial.SerialPortException;
import com.pi4j.system.NetworkInfo;

public class TurtleCarPiBlue {

    private static boolean exit = false;

    public static void main(String[] args) {
        
        // Create Pi4J GpioController object
        final GpioController gpio = GpioFactory.getInstance();
        
        // Create left DC motor GPIO pin object
        final GpioPinDigitalOutput pin01 = 
                gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01);
        final GpioPinDigitalOutput pin03 = 
                gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03);
        
        // Create right DC motor GPIO pin object
        final GpioPinDigitalOutput pin00 = 
                gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00);
        final GpioPinDigitalOutput pin02 = 
                gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02);
        
        // Front light
        final GpioPinDigitalOutput pin04 = 
                gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04);
        
        // Left and right light
        final GpioPinDigitalOutput pin05 = 
                gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05);
        final GpioPinDigitalOutput pin06 = 
                gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06);
        
        // Back light
        final GpioPinDigitalOutput pin07 = 
                gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07);
        
        // Create and start blinker object
        LedBlinker leftBlinker = new LedBlinker(pin05);
        LedBlinker rightBlinker = new LedBlinker(pin06);
        LedBlinker backBlinker = new LedBlinker(pin07);
        
        leftBlinker.start();
        rightBlinker.start();
        backBlinker.start();
        
        // Create Serial object and register listener
        final Serial serial = SerialFactory.createInstance();
                
        // Create L293D DC motor control object
        L293D l293d = new L293D(pin01, pin03, pin00, pin02);
        
        // Create Bluetooth serial data listener
        final SerialDataListener dataListener = new SerialDataListener() {
            @Override
            public void dataReceived(SerialDataEvent event) {
                String command = event.getData();

                switch (command) {
                    // Forward
                    case "f":
                    case "F":
                        l293d.leftForward();
                        l293d.rightForward();
                        break;
                    // Backward
                    case "b":
                    case "B":
                        backBlinker.startBlink();
                        l293d.leftBackward();
                        l293d.rightBackward();
                        break;
                    // Turn left
                    case "l":
                    case "L":
                        leftBlinker.startBlink();
                        l293d.leftBackward();
                        l293d.rightForward();
                        break;
                    // Turn right
                    case "r":
                    case "R":
                        rightBlinker.startBlink();
                        l293d.leftForward();
                        l293d.rightBackward();
                        break;
                    // Stop
                    case "s":
                    case "S":
                        backBlinker.stopBlink();
                        pin07.pulse(1000);
                        leftBlinker.stopBlink();
                        rightBlinker.stopBlink();
                        l293d.leftStop();
                        l293d.rightStop();
                        break;
                    // Main light
                    case "m":
                    case "M":
                        pin04.toggle();
                        break;
                    case "i":
                    case "I":
                        try {
                            String ip = NetworkInfo.getIPAddress();
                            serial.writeln(ip);
                        }
                        catch (Exception e) {
                            System.out.println(e);
                        }
                        
                        go(leftBlinker, rightBlinker, backBlinker);
                        
                        break;
                    case "e":
                    case "E":
                        exit = true;
                        break;
                }
            }            
        };
        
        // Register Bluetooth serial data listener
        serial.addListener(dataListener);
        
        System.out.println("TurtleCarPiBlue Ready...");
        
        go(leftBlinker, rightBlinker, backBlinker);
                
        try {
            serial.open(Serial.DEFAULT_COM_PORT, 115200);

            while(!exit) {
                delay(1000);
            }
        }
        catch(SerialPortException e) {
            System.out.println(e.getMessage());
            return;
        }
        finally {
            leftBlinker.exit();
            rightBlinker.exit();
                        
            serial.removeListener(dataListener);
            serial.close();
            
            System.out.println("TurtleCarPiBlue Bye...");
            gpio.shutdown();
            System.exit(0);
        }
        
    }
    
    private static void go(LedBlinker leftBlinker,
            LedBlinker rightBlinker,
            LedBlinker backBlinker) {
        leftBlinker.startBlink();
        rightBlinker.startBlink();
        backBlinker.startBlink();
        delay(2000);
        leftBlinker.stopBlink();
        rightBlinker.stopBlink();
        backBlinker.stopBlink();
    }
    
    private static void delay(int ms) {
        try {
            Thread.sleep(ms);
        }
        catch (InterruptedException e) {
            System.out.println(e.toString());
        }
    }    
    
}
