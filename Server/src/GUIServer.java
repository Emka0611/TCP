import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
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

	public static JTextArea chatText = null;
	
	public static JFrame mainFrame = new JFrame(Connection.name);
	
	public static JPanel statusBar = null;
	public static JLabel statusField = null;
	public static JTextField statusColor = null;

	public static JTextField ipField = null;
	public static JTextField portField = null;
	public static JLabel windowWidthLabel = null;
	public static JTextField windowWidth = null;

	public static JButton connectButton = null;
	public static JButton disconnectButton = null;
	public static JButton clearButton = null;

	public static JPanel mainPane = new JPanel(new GridBagLayout());
	public static JPanel buttonPane;

	public static final int OPT_SIZE_X = 300;
	public static final int OPT_SIZE_Y = 100;
	
	public static final int CHAT_SIZE_X = 700;
	public static final int CHAT_SIZE_Y = 200;

	public static GUIServer getTcpobj()
	{
		return tcpObj;
	}
	
	private static void initButtons()
	{
		ActionAdapter buttonListener = null;

		buttonPane = new JPanel(new GridLayout(1, 4));
		
		buttonListener = new ActionAdapter()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (e.getActionCommand().equals("connect"))
				{
					if(0 == windowWidth.getText().length())
					{
						windowWidth.setText("10");
					}
					TCPServer.windowWidth = Byte.valueOf(windowWidth.getText());
					changeStatusNTS(EConnectionStatus.BEGIN_CONNECT, true);
				}
				else if (e.getActionCommand().equals("disconnect"))
				{
					changeStatusNTS(EConnectionStatus.DISCONNECTING, true);
				}
				else
				{
					chatText.setText("");
				}
			}
		};

		connectButton = new JButton("Polacz");
		connectButton.setActionCommand("connect");
		connectButton.addActionListener(buttonListener);
		connectButton.setEnabled(true);

		disconnectButton = new JButton("Rozlacz");
		disconnectButton.setActionCommand("disconnect");
		disconnectButton.addActionListener(buttonListener);
		disconnectButton.setEnabled(false);
		
		clearButton = new JButton("Wyczysc");
		clearButton.setActionCommand("clear");
		clearButton.addActionListener(buttonListener);
		clearButton.setEnabled(false);

		buttonPane.add(connectButton);
		buttonPane.add(disconnectButton);
		buttonPane.add(clearButton);
	}
	
	private static void initIpField(JPanel optionsPane)
	{
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
	}
	
	private static void initPortField(JPanel optionsPane)
	{
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
	}

	private static JPanel initOptionsPane()
	{
		JPanel optionsPane = new JPanel(new GridLayout(3, 2));

		initIpField(optionsPane);
		initPortField(optionsPane);

		windowWidthLabel = new JLabel("Szerkoœæ okna danych:");
		windowWidth = new JTextField();

		optionsPane.add(new JLabel("IP Serwera:"));
		optionsPane.add(ipField);
		optionsPane.add(new JLabel("Port:"));
		optionsPane.add(portField);
		optionsPane.add(windowWidthLabel);
		optionsPane.add(windowWidth);

		optionsPane.setPreferredSize(new Dimension(OPT_SIZE_X, OPT_SIZE_Y));
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
		
		chatPane.add(chatTextPane, BorderLayout.CENTER);
		chatPane.setPreferredSize(new Dimension(CHAT_SIZE_X, CHAT_SIZE_Y));
		
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
		JPanel optionsPane = initOptionsPane();
		JPanel chatPane = initChatPane();

		initStatusBar();
		initButtons();
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;
		
		c.gridx = 0;
		c.gridy = 0;
		mainPane.add(chatPane, c);
		
		c.gridx = 1;
		c.gridy = 0;
		mainPane.add(optionsPane, c);

		c.gridx = 0;
		c.gridy = 1;
		c.ipady = 30;
		mainPane.add(buttonPane, c);

		c.gridy = 2;
		c.ipady = 0;
		mainPane.add(statusBar, c);

		mainFrame.setContentPane(mainPane);
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

		changeStatusNTS(EConnectionStatus.NULL, true);
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
			clearButton.setEnabled(true);
			ipField.setEnabled(true);
			portField.setEnabled(true);
			windowWidth.setEnabled(true);
			statusColor.setBackground(Color.red);
			break;

		case DISCONNECTING:
			connectButton.setEnabled(false);
			disconnectButton.setEnabled(false);
			clearButton.setEnabled(false);
			ipField.setEnabled(false);
			portField.setEnabled(false);
			windowWidth.setEnabled(false);
			statusColor.setBackground(Color.orange);
			break;

		case CONNECTED:
			connectButton.setEnabled(false);
			disconnectButton.setEnabled(true);
			clearButton.setEnabled(true);
			ipField.setEnabled(false);
			portField.setEnabled(false);
			windowWidth.setEnabled(false);
			statusColor.setBackground(Color.green);
			break;

		case BEGIN_CONNECT:
			connectButton.setEnabled(false);
			disconnectButton.setEnabled(false);
			clearButton.setEnabled(false);
			ipField.setEnabled(false);
			portField.setEnabled(false);
			windowWidth.setEnabled(false);
			statusColor.setBackground(Color.orange);
			break;

		default:
			break;
		}

		ipField.setText(Connection.hostIP);
		portField.setText((new Integer(Connection.port)).toString());
		statusField.setText(Connection.statusString);
		chatText.append(Connection.toAppend.toString());
		windowWidth.setText(TCPServer.windowWidth.toString());
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

	public static void sendFrames(TCPFrame frame)
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
