import java.io.*;

import javax.swing.*;

import java.net.*;

public class TCPClient
{
	// TCP Components
	public static Socket socket = null;
	public static ObjectInputStream in = null;
	public static ObjectOutputStream out = null;
	
	// The thread-safe way to change the GUI components while changing state
	private static void changeStatusTS(EConnectionStatus newConnectStatus, boolean noError)
	{
		if (newConnectStatus != EConnectionStatus.NULL)
		{
			Connection.connectionStatus = newConnectStatus;
		}

		if (noError)
		{
			Connection.statusString = Connection.statusMessages[Connection.connectionStatus.ordinal()];
		}
		else
		{
			Connection.statusString = Connection.statusMessages[EConnectionStatus.NULL.ordinal()];
		}

		// Call the run() routine (Runnable interface) on the error-handling and GUI-update thread
		SwingUtilities.invokeLater(GUIClient.getTcpobj());
	}

	private static void cleanUp()
	{
		try
		{
			if (null != socket)
			{
				socket.close();
				socket = null;
			}
		}
		catch (IOException e)
		{
			socket = null;
		}

		try
		{
			if (null != in)
			{
				in.close();
				in = null;
			}
		}
		catch (IOException e)
		{
			in = null;
		}

		try
		{
			if (null != out)
			{
				out.close();
				out = null;
			}
		}
		catch (IOException e)
		{

		}

	}

	private static void handleBeginConnect()
	{
		try
		{
			socket = new Socket(Connection.hostIP, Connection.port);

			in = new ObjectInputStream(socket.getInputStream());
			out = new ObjectOutputStream(socket.getOutputStream());
			
			changeStatusTS(EConnectionStatus.CONNECTED, true);
		}
		// If error, clean up and output an error message
		catch (IOException e)
		{
			cleanUp();
			changeStatusTS(EConnectionStatus.DISCONNECTED, false);
		}
	}

	public static void handleConnectedForWriting()
	{
		try
		{
			// Send data
			if (Connection.toSend.getData().length() != 0)
			{
				out.writeObject(Connection.toSend);
				out.flush();
				Connection.toSend = new TCPFrame("");
				changeStatusTS(EConnectionStatus.NULL, true);
			}
		}
		catch (IOException e)
		{
			cleanUp();
			changeStatusTS(EConnectionStatus.DISCONNECTED, false);
		}
	}
	
	public static void handleConnectedForReading()
	{
		TCPFrame frame = null;
		try
		{
			// Receive data
			if(null != in)
			{
				try
				{
					frame = (TCPFrame) in.readObject();
				}
				catch (ClassNotFoundException e)
				{
	
				}
			}
			
			if ((frame != null) && (frame.getData().length() != 0))
			{
				// Check if it is the end of a transmission
				if (frame.getData().equals(Connection.END_SESSION))
				{
					changeStatusTS(EConnectionStatus.DISCONNECTING, true);
				}

				// Otherwise, receive what text
				else
				{
					changeStatusTS(EConnectionStatus.NULL, true);
				}
			}
		}
		catch (IOException e)
		{
			cleanUp();
		}
	}

	public static void handleDisconnecting()
	{
		// Tell other chatter to disconnect as well
		TCPFrame frame = new TCPFrame(Connection.END_SESSION);
		try
		{
			out.writeObject(frame);
			out.flush();
			changeStatusTS(EConnectionStatus.DISCONNECTED, true);
		}
		catch (IOException e)
		{
			cleanUp();
			changeStatusTS(EConnectionStatus.DISCONNECTED, false);
		}	
	}

	// ********************************************************************

	// The main procedure
	public static void main(String args[])
	{
		Connection.isHost = false;
		Connection.name = "Simple TCP Client";
		
		WatekNasluchujacy w1 = new WatekNasluchujacy();
		
		GUIClient.initGUI();
		
		(new Thread(w1)).start();
		
		while (true)
		{
			try	{ Thread.sleep(10); }
			catch (InterruptedException e) {}

			switch (Connection.connectionStatus)
			{
			case BEGIN_CONNECT:
				handleBeginConnect();
				break;

			case CONNECTED:
				handleConnectedForWriting();
				break;

			case DISCONNECTING:
				handleDisconnecting();
				break;

			default:
				break;
			}
		}
	}
}
