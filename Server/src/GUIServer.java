import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class GUIServer implements Runnable
{
	public final static GUIServer tcpObj = new GUIServer();

	public static JFrame mainFrame = null;

	public static JTextArea chatText = null;
	public static JTextField chatLine = null;
	
	public static JPanel statusBar = null;
	public static JLabel statusField = null;
	public static JTextField statusColor = null;
	
	public static JTextField ipField = null;
	public static JTextField portField = null;
	
	public static JButton connectButton = null;
	public static JButton disconnectButton = null;

	public static GUIServer getTcpobj()
	{
		return tcpObj;
	}

	private static void addIpField(JPanel optionsPane)
	{
		JPanel pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		pane.add(new JLabel("Host IP:"));
		ipField = new JTextField(10);
		ipField.setText(Connection.hostIP);
		ipField.setEnabled(!Connection.isHost);
		ipField.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				ipField.selectAll();
				
				// Should be editable only when disconnected
				if (Connection.connectionStatus != EConnectionStatus.DISCONNECTED)
				{
					changeStatusNTS(EConnectionStatus.NULL, true);
				}
				else
				{
					Connection.hostIP = ipField.getText();
				}
			}
		});

		pane.add(ipField);
		optionsPane.add(pane);
	}

	private static void addPortField(JPanel optionsPane)
	{
		JPanel pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pane.add(new JLabel("Port:"));
		portField = new JTextField(10);
		portField.setEditable(true);
		portField.setText((new Integer(Connection.port)).toString());
		portField.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				// should be editable only when disconnected
				if (Connection.connectionStatus != EConnectionStatus.DISCONNECTED)
				{
					changeStatusNTS(EConnectionStatus.NULL, true);
				}
				else
				{
					int temp;
					try
					{
						temp = Integer.parseInt(portField.getText());
						Connection.port = temp;
					}
					catch (NumberFormatException nfe)
					{
						portField.setText((new Integer(Connection.port)).toString());
						mainFrame.repaint();
					}
				}
			}
		});
		
		pane.add(portField);
		optionsPane.add(pane);
	}
	
	private static void addConnectDisconnectButtons(JPanel optionsPane)
	{
		ActionAdapter buttonListener = null;

		JPanel buttonPane = new JPanel(new GridLayout(1, 2));
		buttonListener = new ActionAdapter()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (e.getActionCommand().equals("connect"))
				{
					changeStatusNTS(EConnectionStatus.BEGIN_CONNECT, true);
				}
				else
				{
					changeStatusNTS(EConnectionStatus.DISCONNECTING, true);
				}
			}
		};

		connectButton = new JButton("Connect");
		connectButton.setActionCommand("connect");
		connectButton.addActionListener(buttonListener);
		connectButton.setEnabled(true);
		
		disconnectButton = new JButton("Disconnect");
		disconnectButton.setMnemonic(KeyEvent.VK_D);
		disconnectButton.setActionCommand("disconnect");
		disconnectButton.addActionListener(buttonListener);
		disconnectButton.setEnabled(false);
		
		buttonPane.add(connectButton);
		buttonPane.add(disconnectButton);
		
		optionsPane.add(buttonPane);
	}

	private static JPanel initOptionsPane()
	{
		JPanel optionsPane = new JPanel(new GridLayout(3, 1));

		addIpField(optionsPane);
		addPortField(optionsPane);
		addConnectDisconnectButtons(optionsPane);
		
		return optionsPane;
	}
	
	private static JPanel initChatPane()
	{
		JPanel chatPane = new JPanel(new BorderLayout());
		chatText = new JTextArea(10, 20);
		chatText.setLineWrap(true);
		chatText.setEditable(false);
		chatText.setForeground(Color.blue);
		JScrollPane chatTextPane = new JScrollPane(chatText, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		chatLine = new JTextField();
		chatLine.setEnabled(false);
		chatLine.addActionListener(new ActionAdapter()
		{
			public void actionPerformed(ActionEvent e)
			{
				TCPFrame frame = new TCPFrame(chatLine.getText());
				if (!frame.getData().equals(""))
				{
					appendToChatBox("OUTGOING: " + frame.getData() + "\n");
					//chatLine.selectAll();

					sendFrame(frame);
					chatLine.setText("");
				}
			}
		});

		chatPane.add(chatLine, BorderLayout.SOUTH);
		chatPane.add(chatTextPane, BorderLayout.CENTER);
		chatPane.setPreferredSize(new Dimension(200, 200));
		
		return chatPane;
	}
	
	private static void initStatusBar()
	{
		statusField = new JLabel();
		statusField.setText(Connection.statusMessages[EConnectionStatus.DISCONNECTED.ordinal()]);
		initStatusColor();
		statusBar = new JPanel(new BorderLayout());
		statusBar.add(statusColor, BorderLayout.WEST);
		statusBar.add(statusField, BorderLayout.CENTER);
	}

	private static void initStatusColor()
	{
		statusColor = new JTextField(1);
		statusColor.setBackground(Color.red);
		statusColor.setEditable(false);
	}

	public static void initGUI()
	{
		initStatusBar();
		JPanel optionsPane = initOptionsPane();
		JPanel chatPane = initChatPane();

		JPanel mainPane = new JPanel(new BorderLayout());
		mainPane.add(statusBar, BorderLayout.SOUTH);
		mainPane.add(optionsPane, BorderLayout.WEST);
		mainPane.add(chatPane, BorderLayout.CENTER);

		mainFrame = new JFrame(Connection.name);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setContentPane(mainPane);
		mainFrame.setSize(mainFrame.getPreferredSize());
		mainFrame.setLocation(200, 200);
		mainFrame.pack();
		mainFrame.setVisible(true);
		
		mainFrame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				changeStatusNTS(EConnectionStatus.DISCONNECTING, true);
				System.exit(0);
			}
		});
	}

	// The non-thread-safe way to change the GUI components while changing state
	private static void changeStatusNTS(EConnectionStatus newConnectStatus, boolean noError)
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
		
		tcpObj.run();
	}

	public void run()
	{
		switch (Connection.connectionStatus)
		{
		case DISCONNECTED:
			connectButton.setEnabled(true);
			disconnectButton.setEnabled(false);
			ipField.setEnabled(true);
			portField.setEnabled(true);
			chatLine.setText("");
			chatLine.setEnabled(false);
			statusColor.setBackground(Color.red);
			break;

		case DISCONNECTING:
			connectButton.setEnabled(false);
			disconnectButton.setEnabled(false);
			ipField.setEnabled(false);
			portField.setEnabled(false);
			chatLine.setEnabled(false);
			statusColor.setBackground(Color.orange);
			break;

		case CONNECTED:
			connectButton.setEnabled(false);
			disconnectButton.setEnabled(true);
			ipField.setEnabled(false);
			portField.setEnabled(false);
			chatLine.setEnabled(true);
			statusColor.setBackground(Color.green);
			break;

		case BEGIN_CONNECT:
			connectButton.setEnabled(false);
			disconnectButton.setEnabled(false);
			ipField.setEnabled(false);
			portField.setEnabled(false);
			chatLine.setEnabled(false);
			chatLine.grabFocus();
			statusColor.setBackground(Color.orange);
			break;

		default:
			break;
		}

		ipField.setText(Connection.hostIP);
		portField.setText((new Integer(Connection.port)).toString());
		statusField.setText(Connection.statusString);
		chatText.append(Connection.toAppend.toString());
		Connection.toAppend.setLength(0);

		mainFrame.repaint();
	}

	public static void appendToChatBox(String s)
	{
		synchronized (Connection.toAppend)
		{
			Connection.toAppend.append(s);
		}
	}

	public static void sendFrame(TCPFrame frame)
	{
		synchronized (Connection.toSend)
		{
			Connection.toSend = frame;
		}
	}
}

class ActionAdapter implements ActionListener
{
	public void actionPerformed(ActionEvent e)
	{
	}
}
