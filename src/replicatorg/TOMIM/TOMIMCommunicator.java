package replicatorg.TOMIM;

import java.io.IOException;

import replicatorg.app.Base;

public class TOMIMCommunicator 
{
	private TOMIMDriver tmDriver;
	private boolean goingDownwards;
	private final char DOWNSIGNAL = 'y';
	private final char UPSIGNAL = 'n';
	
	public TOMIMCommunicator(boolean goingDownwards)
	{
		tmDriver = new TOMIMDriver();
		this.goingDownwards = goingDownwards;
	}
	
	public void beginCommunication()
	{
		tmDriver.connectToTOMIM();
	}
	
	public void outputByte()
	{
		if (tmDriver.getIsConnected())
		{
			if (goingDownwards)
			{
				tmDriver.sendByte(DOWNSIGNAL);
			}
			else
			{
				tmDriver.sendByte(UPSIGNAL);
			}
		}
	}
	public void getTemp()
	{
		
	}
	public void listenForEnd()
	{
		try 
		{
			while (tmDriver.getInputByte() == null || tmDriver.getInputByte().read() != 100) 
			{
				if (tmDriver.getInputByte() != null)
					System.out.println(tmDriver.getInputByte().toString());
				else
					System.out.println("Nothing yet . . .");
				// wait for Arduino to finish raising or lowering shutter doors
			}
		}
		catch (IOException ioe)
		{
			Base.logger.severe("Sorry, could not recieve finishing bit from TOMIM.");
		}
		tmDriver.close();
	}
	
	public void setSignal(boolean goingDownwards)
	{
		this.goingDownwards = goingDownwards;
	}
	
	public boolean isConnected()
	{
		return tmDriver.getIsConnected();
	}
}
