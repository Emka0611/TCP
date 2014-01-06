
public class WatekNasluchujacy implements Runnable
{

	@Override
	public void run()
	{
		while (true)
		{
			try	{ Thread.sleep(10); }
			catch (InterruptedException e) {}

			switch (Connection.connectionStatus)
			{
			case CONNECTED:
				TCPServer.handleConnectedForReading();
				break;

			default:
				break;
			}
		}
		
	}

}
