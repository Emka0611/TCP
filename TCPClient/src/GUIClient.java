import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GUIClient implements Runnable
{
	public final static GUIClient tcpObj = new GUIClient();

	public static JFrame mainFrame = null;
	
	public static JPanel statusBar = null;
	public static JLabel statusField = null;
	public static JTextField statusColor = null;
	
	public static JTextField ipField = null;
	public static JTextField portField = null;
	
	public static JButton connectButton = null;
	public static JButton disconnectButton = null;
	public static JButton sendButton = null;
	public static JButton clearButton = null;
	
	public static Vector<JTextField> frameFields = null; 

	public static GUIClient getTcpobj()
	{
		return tcpObj;
	}

	private static void initSendClearButton()
	{
		ActionAdapter buttonListener = null;
	
		JPanel buttonPane = new JPanel(new GridLayout(1, 2));
		buttonListener = new ActionAdapter()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (e.getActionCommand().equals("send"))
				{
					String s = getFrame().toString();
					if (!s.equals(""))
					{
						sendString(s);
						frameFieldsClear();
					}
				}
				else
				{
					frameFieldsClear();
				}
			}
		};
	
		sendButton = new JButton("Wyslij");
		sendButton.setActionCommand("send");
		sendButton.addActionListener(buttonListener);
		sendButton.setEnabled(false);
		
		clearButton = new JButton("Wyczysc");
		clearButton.setActionCommand("clear");
		clearButton.addActionListener(buttonListener);
		clearButton.setEnabled(false);
		
		buttonPane.add(sendButton);
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
	
	private static void initConnectDisconnectButtons(JPanel optionsPane)
	{
		ActionAdapter buttonListener = null;

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

		connectButton = new JButton("Polacz");
		connectButton.setActionCommand("connect");
		connectButton.addActionListener(buttonListener);
		connectButton.setEnabled(true);
		
		disconnectButton = new JButton("Rozlacz");
		disconnectButton.setActionCommand("disconnect");
		disconnectButton.addActionListener(buttonListener);
		disconnectButton.setEnabled(false);
	}

	private static JPanel initOptionsPane()
	{
		JPanel optionsPane = new JPanel(new GridLayout(8, 2));
		initIpField(optionsPane);
		initPortField(optionsPane);
		initConnectDisconnectButtons(optionsPane);
		
		optionsPane.add(new JLabel("IP Serwera:"));
		optionsPane.add(ipField);
		optionsPane.add(new JLabel("Port:"));
		optionsPane.add(portField);
		optionsPane.add(new JLabel());
		optionsPane.add(new JLabel());
		optionsPane.add(new JLabel());
		optionsPane.add(new JLabel());
		optionsPane.add(new JLabel());
		optionsPane.add(new JLabel());
		optionsPane.add(new JLabel());
		optionsPane.add(new JLabel());
		optionsPane.add(new JLabel());
		optionsPane.add(new JLabel());
		optionsPane.add(connectButton, 14);
		optionsPane.add(disconnectButton, 15);
		
		return optionsPane;
	}
	
	private static void initFields()
	{		
		frameFields = new Vector<JTextField>();
		
		for(int i=0; i<Connection.FIELDS_NUMBER; i++)
		{
			frameFields.add(new JTextField());
		}
		
		framePanelSetEnabled(false);
	}
	
	private static void framePanelSetEnabled(boolean enabled)
	{
		for(int i=0; i<Connection.FIELDS_NUMBER; i++)
		{
			frameFields.get(i).setEnabled(enabled);
		}
		
		sendButton.setEnabled(enabled);
		clearButton.setEnabled(enabled);
	}
	
	private static void frameFieldsClear()
	{
		for(int i=0; i<Connection.FIELDS_NUMBER; i++)
		{
			frameFields.get(i).setText("");
		}
	}
	
	private static TCPFrame getFrame() 
	{
		TCPFrame frame = new TCPFrame();
		frame.setSeqNumber((byte)126);
		//byte tmp;
		//tmp = 
/*		String s = "";
		
		for(int i=0; i<Connection.FIELDS_NUMBER; i++)
		{
			s+=frameFields.get(i).getText();
		}*/
		
		return frame;
	}
	
	private static void addComponents (JPanel framePane)
	{
		initFields();
		initSendClearButton();
		
		framePane.add(new JLabel("Numer sekwencyjny:"));
		framePane.add(frameFields.get(0));
		
		framePane.add(new JLabel("Liczba pakietow:"));
		framePane.add(frameFields.get(1));

		framePane.add(new JLabel("Flaga bezpieczenstwa:"));
		framePane.add(frameFields.get(2));
		
		framePane.add(new JLabel("Szerokosc okna danych: "));
		framePane.add(frameFields.get(3));
		
		framePane.add(new JLabel("Dlugosc danych:"));
		framePane.add(frameFields.get(4));
		
		framePane.add(new JLabel("Dane:"));
		framePane.add(frameFields.get(5));
		
		framePane.add(new JLabel("Suma kontrolna:"));
		framePane.add(frameFields.get(6));
		
		framePane.add(sendButton);
		framePane.add(clearButton);
	}
	
	private static JPanel initFramePane()
	{
		JPanel framePane = new JPanel(new GridLayout(8, 2));
		framePane.setPreferredSize(new Dimension(400,300));
		addComponents(framePane);
		
		return framePane;
	}
	
	private static void initStatusColor()
	{
		statusColor = new JTextField(1);
		statusColor.setBackground(Color.red);
		statusColor.setEditable(false);
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

	/*	public static void appendToChatBox(String s)
	{
		synchronized (Connection.toAppend)
		{
			Connection.toAppend.append(s);
		}
	}*/
	
	private static void sendString(String s)
	{
		synchronized (Connection.toSend)
		{
			Connection.toSend.append(s + "\n");
		}
	}

	public static void initGUI()
	{
		initStatusBar();
		initSendClearButton();
		JPanel optionsPane = initOptionsPane();
		JPanel framePane = initFramePane();

		JPanel mainPane = new JPanel(new BorderLayout());
		mainPane.add(statusBar, BorderLayout.SOUTH);
		mainPane.add(optionsPane, BorderLayout.EAST);
		mainPane.add(framePane, BorderLayout.WEST);

		mainFrame = new JFrame(Connection.name);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setContentPane(mainPane);
		mainFrame.setSize(mainFrame.getPreferredSize());
		mainFrame.setLocation(700, 200);
		mainFrame.pack();
		mainFrame.setVisible(true);
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
			statusColor.setBackground(Color.red);
			framePanelSetEnabled(false);
			break;

		case DISCONNECTING:
			connectButton.setEnabled(false);
			disconnectButton.setEnabled(false);
			ipField.setEnabled(false);
			portField.setEnabled(false);
			statusColor.setBackground(Color.orange);
			framePanelSetEnabled(false);
			break;

		case CONNECTED:
			connectButton.setEnabled(false);
			disconnectButton.setEnabled(true);
			ipField.setEnabled(false);
			portField.setEnabled(false);
			statusColor.setBackground(Color.green);
			framePanelSetEnabled(true);
			break;

		case BEGIN_CONNECT:
			connectButton.setEnabled(false);
			disconnectButton.setEnabled(false);
			ipField.setEnabled(false);
			portField.setEnabled(false);
			statusColor.setBackground(Color.orange);
			framePanelSetEnabled(false);
			break;

		default:
			break;
		}

		ipField.setText(Connection.hostIP);
		portField.setText((new Integer(Connection.port)).toString());
		statusField.setText(Connection.statusString);
		Connection.toAppend.setLength(0);

		mainFrame.repaint();
	}

/*	public static void appendToChatBox(String s)
	{
		synchronized (Connection.toAppend)
		{
			Connection.toAppend.append(s);
		}
	}*/
}

class ActionAdapter implements ActionListener
{
	public void actionPerformed(ActionEvent e)
	{
		
	}
}
