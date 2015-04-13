package replicatorg.TOMIM;

import replicatorg.app.Base;
import replicatorg.app.ui.MachineStatusPanel;
import replicatorg.machine.MachineProgressEvent;

public class TOMIMThread extends Thread
{
	boolean running = true;
	private TOMIMCommunicator tComm;
	private MachineStatusPanel mspanel;
	
	final static boolean OPEN_INSULATION = false;
	
	public TOMIMThread(TOMIMCommunicator tComm, MachineStatusPanel mspanel)
	{
		this.tComm = tComm;
		this.mspanel = mspanel;
	}
	public void terminate()
	{
		running = false;
	}
	
	public void run()
	{
		while (running)
		{
			try
			{
				Thread.sleep((long) 1000);
				MachineProgressEvent currentEvent = mspanel.getCurrentEvent();
				float elapsedSteps = currentEvent.getLines();
				float totalSteps = currentEvent.getTotalLines();
				Base.logger.info("Checking to open insulation.");
				if (elapsedSteps / totalSteps > .95)
				{
					// open insulation if build 95% finished
					Base.logger.info("Connecting to TOMIM.");
					if (tComm != null)
					{
						tComm.setSignal(OPEN_INSULATION);
						tComm.beginCommunication();
						if (tComm.isConnected())
						{
							tComm.outputByte();
							tComm.listenForEnd(); // listens for end of traversal
							Base.logger.info("Disconnected from TOMIM");
						}
					}
					running = false;
				}
			}
			catch (InterruptedException e)
			{
				Base.logger.severe(e.toString());
                running = false;
			}
			catch(NumberFormatException nfe)
			{
				Base.logger.severe("Sorry. Incorrect Parsing of Status String.");
				running = false;
			}
		}
	}
}
