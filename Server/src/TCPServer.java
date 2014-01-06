import java.io.*;
import javax.swing.*;
import java.net.*;

public class TCPServer
{
	// TCP Components
	public static ServerSocket hostServer = null;
	public static Socket socket = null;
	public static ObjectInputStream in = null;
	public static ObjectOutputStream out = null;
	
	public static String msg = null;

	// The thread-safe way to change the GUI components while
	// changing state
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

		// Call the run() routine (Runnable interface) on the
		// error-handling and GUI-update thread
		SwingUtilities.invokeLater(GUIServer.getTcpobj());
	}

	// Cleanup for disconnect
	private static void cleanUp()
	{
		try
		{
			if (hostServer != null)
			{
				hostServer.close();
				hostServer = null;
			}
		}
		catch (IOException e)
		{
			hostServer = null;
		}

		try
		{
			if (socket != null)
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
			if (in != null)
			{
				in.close();
				in = null;
			}
		}
		catch (IOException e)
		{
			in = null;
		}

		if (out != null)
		{
			try
			{
				out.close();
				out = null;
			}
			catch (IOException e)
			{

			}
		}
	}

	public static void handleBeginConnect()
	{
		try
		{
			hostServer = new ServerSocket(Connection.port);
			socket = hostServer.accept();

			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());

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
			if (Connection.toSend.length() != 0)
			{
				out.writeObject(Connection.toSend);
				out.flush();
				Connection.toSend = "";
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
		String s = null;
		try
		{
			// Receive data
			try
			{
				s = (String) in.readObject();
			}
			catch (ClassNotFoundException e)
			{

			}
			
			if ((s != null) && (s.length() != 0))
			{
				// Check if it is the end of a transmission
				if (s.equals(Connection.END_CHAT_SESSION))
				{
					changeStatusTS(EConnectionStatus.DISCONNECTING, true);
				}

				// Otherwise, receive what text
				else
				{
					GUIServer.appendToChatBox("INCOMING: " + s + "\n");
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
		try
		{
			// Tell other chatter to disconnect as well
			out.writeObject(Connection.END_CHAT_SESSION);
			out.flush();
		}
		catch (IOException e)
		{
		}
			
		cleanUp();
		changeStatusTS(EConnectionStatus.DISCONNECTED, true);
	}

	// The main procedure
	public static void main(String args[])
	{
		Connection.isHost = true;
		Connection.name = "Simple TCP Server";
		WatekNasluchujacy w1 = new WatekNasluchujacy();
		
		GUIServer.initGUI();
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
