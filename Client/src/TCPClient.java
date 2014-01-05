import java.io.*;
import javax.swing.*;
import java.net.*;

public class TCPClient
{
	// Indicates the end of a session
	public final static String END_CHAT_SESSION = new Character((char) 0).toString();

	// TCP Components
	public static ServerSocket hostServer = null;
	public static Socket socket = null;
	public static BufferedReader in = null;
	public static PrintWriter out = null;

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
		SwingUtilities.invokeLater(GUIClient.getTcpobj());
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
			out.close();
			out = null;
		}
	}

	// ********************************************************************

	// Checks the current state and sets the enables/disables
	// accordingly

	private static void handleBeginConnect()
	{
		try
		{
			socket = new Socket(Connection.hostIP, Connection.port);

			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			changeStatusTS(EConnectionStatus.CONNECTED, true);
		}
		// If error, clean up and output an error message
		catch (IOException e)
		{
			cleanUp();
			changeStatusTS(EConnectionStatus.DISCONNECTED, false);
		}
	}

	private static void handleConnected()
	{
		String s;
		try
		{
			// Send data
			if (Connection.toSend.length() != 0)
			{
				out.print(Connection.toSend);
				out.flush();
				Connection.toSend.setLength(0);
				changeStatusTS(EConnectionStatus.NULL, true);
			}

			// Receive data
			if (in.ready())
			{
				s = in.readLine();
				if ((s != null) && (s.length() != 0))
				{
					// Check if it is the end of a transmission
					if (s.equals(END_CHAT_SESSION))
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
		}
		catch (IOException e)
		{
			cleanUp();
			changeStatusTS(EConnectionStatus.DISCONNECTED, false);
		}
	}

	private static void handleDisconnecting()
	{
		// Tell other chatter to disconnect as well
		out.print(END_CHAT_SESSION);
		out.flush();

		// Clean up (close all streams/sockets)
		cleanUp();
		changeStatusTS(EConnectionStatus.DISCONNECTED, true);
	}

	// ********************************************************************

	// The main procedure
	public static void main(String args[])
	{
		Connection.isHost = false;
		Connection.name = "Simple TCP Client";
		
		GUIClient.initGUI();

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
				handleConnected();
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
