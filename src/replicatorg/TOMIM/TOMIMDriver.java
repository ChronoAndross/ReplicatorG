package replicatorg.TOMIM;

import gnu.io.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;

import replicatorg.app.Base;


public class TOMIMDriver implements SerialPortEventListener 
{
	
	SerialPort TOMIMSerialPort = null;
	
	final String [] PORT_LIST = {"COM1", "COM2", "COM3", "COM6", "COM10"}; 
	String TOMIMPortName = null;
	boolean isConnected = false;
	
	private static final int TIME_OUT = 1000; // Port open timeout
    private static final int DATA_RATE = 9600; // Arduino serial port
    
    private BufferedReader input = null;
    private OutputStream output;

	
	public void connectToTOMIM()
	{
		try 
		{
			CommPortIdentifier portId = null;
			Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
			CommPortIdentifier currPortID = (CommPortIdentifier)portEnum.nextElement();
			while (portId == null && portEnum.hasMoreElements()) 
			{
	                // Iterate through your host computer's serial port IDs
	                //
	                CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
	                
	                for (int i = 0; i < PORT_LIST.length; i++) 
	                {
	                	String portName = PORT_LIST[i];
	                    if ( currPortId.getName().equals(portName) 
	                    		|| currPortId.getName().startsWith(portName))
	                    {
	                        // Try to connect to the Arduino on this port
	                        //
	                        // Open serial port
	                    	Base.logger.info("Success! Connected to TOMIM on port" + currPortId.getName());
	                        TOMIMSerialPort = (SerialPort)currPortId.open(getClass().getName(), TIME_OUT);
	                        portId = currPortId;
	                        
	                        break;
	                    }
	                }
	        }
			
			if (portId == null || TOMIMSerialPort == null) 
	        {
	                Base.logger.severe("Oops... Could not connect to TOMIM");
	                return;
	        }
	        
	            // set port parameters
			TOMIMSerialPort.setSerialPortParams(DATA_RATE,
	                            SerialPort.DATABITS_8,
	                            SerialPort.STOPBITS_1,
	                            SerialPort.PARITY_NONE);

	            // add event listeners
			TOMIMSerialPort.addEventListener(this);
			TOMIMSerialPort.notifyOnDataAvailable(true);
			isConnected = true;

	            // Give the Arduino some time
	            try { Thread.sleep(2000); } catch (InterruptedException ie) {}
	        
	            
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	
	}
	
	public void serialEvent(SerialPortEvent portEvent)
	{
        try 
        {
            switch (portEvent.getEventType() ) 
            {
                case SerialPortEvent.DATA_AVAILABLE: 
                    if ( input == null ) 
                    {
                        input = new BufferedReader(
                            new InputStreamReader(
                                    TOMIMSerialPort.getInputStream()));
                    }
                    break;

                default:
                    break;
            }
        } 
        catch (Exception e) 
        {
            System.err.println(e.toString());
        }
        
	}
	
	public void sendByte(char signal)
	{
		try 
		{
			output = TOMIMSerialPort.getOutputStream();
			output.write(signal);
		}
		catch (IOException ioe)
		{
			Base.logger.severe("Could not communicate with TOMIM.");
		}
		
	}
	
	public synchronized void close()
	{
		if (TOMIMSerialPort != null)
		{
			TOMIMSerialPort.removeEventListener();
			TOMIMSerialPort.close();
		}
	}
	
	
	
	public BufferedReader getInputByte()
	{
		return input;
	}
	
	public boolean getIsConnected()
	{
		return isConnected;
	}
}
