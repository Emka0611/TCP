import java.util.Timer;
import java.util.TimerTask;

public class ReminderTask
{
	Timer timer;

	public ReminderTask()
	{
		timer = new Timer();
	}

	class RemindTask extends TimerTask
	{
		public void run()
		{
			if(EServerState.TRANSMISSION == TCPServer.getState())
			{
				TCPServer.timoutTransmission();
				timer.cancel(); // Terminate the timer thread
				System.out.println("TIMER STOPPED::ERROR");
			}
		}
	}
	
	public void start(int seconds)
	{
		timer = new Timer();
		timer.schedule(new RemindTask(), seconds * 1000);
		System.out.println("TIMER STARTED");
	}
	
	public void stop()
	{
		try
		{
			timer.cancel();
			System.out.println("TIMER STOPPED::OK");
		}
		catch (IllegalStateException e)
		{
			
		}
		
	}

}
