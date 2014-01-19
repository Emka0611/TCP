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
		SwingUtilities.invokeLater(GUIServer.getTcpobj());
	}

	private static void cleanUp()
	{
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

		try
		{
			if (out != null)
			{
				out.close();
				out = null;
			}
		}
		catch (IOException e)
		{

		}

	}

	public static void handleBeginConnect()
	{
		try
		{
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
			
			if(null != in)
			{
				// Receive data
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
					GUIServer.appendToChatBox("INCOMING: " + frame.toString() + "\n");
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

	// The main procedure
	public static void main(String args[])
	{
		try
		{
			hostServer = new ServerSocket(Connection.port);
		}
		catch (IOException e)
		{
			changeStatusTS(EConnectionStatus.DISCONNECTED, false);
		}
		
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
