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
//import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GUIClient implements Runnable
{	
	public final static GUIClient tcpObj = new GUIClient();

	public  static JTextField messageField = null;
	public static JFrame mainFrame = null;
	
	public static JPanel statusBar = null;
	public static JLabel statusField = null;
	public static JTextField statusColor = null;
	
	public static JTextField ipField = null;
	public static JTextField portField = null;
	public static JLabel windowWidthLabel = null;
	public static JTextField windowWidth = null;

	public static JButton generateButton = null;
	public static JButton connectButton = null;
	public static JButton disconnectButton = null;
	public static JButton sendButton = null;
	public static JButton clearButton = null;
	
	public static FramePane framePane = new FramePane();
	
	public static final int OPT_SIZE_X = 300;
	public static final int OPT_SIZE_Y = 100;
	
	public static final int MSG_SIZE_X = 900;
	public static final int MSG_SIZE_Y = 100;
	
	public static final int PANE_SIZE_X = 900;
	public static final int PANE_SIZE_Y = 40;
	
	public static GUIClient getTcpobj()
	{
		return tcpObj;
	}
	
	private static void generateFrames()
	{	
		//TODO
	}
	
	private static JPanel initMessagePane()
	{
		JPanel messagePane = new JPanel(new GridLayout(3,1));
		
		messageField = new JTextField(10);
		messageField.setEnabled(false);
		messageField.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				messageField.selectAll();
				
				// Should be editable only when disconnected
				if (Connection.connectionStatus != EConnectionStatus.DISCONNECTED)
				{
					changeStatusNTS(EConnectionStatus.NULL, true);
				}
				else
				{
					TCPClient.message = messageField.getText();
				}
			}
		});
		
		ActionAdapter buttonListener = new ActionAdapter()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (e.getActionCommand().equals("generate"))
				{
					generateFrames();
				}
			}
		};
	
		generateButton = new JButton("Oblicz ramki");
		generateButton.setActionCommand("generate");
		generateButton.addActionListener(buttonListener);
		generateButton.setEnabled(false);
		
		
		messagePane.add(new JLabel("Wpisz wiadomosc:"));
		messagePane.add(messageField);
		messagePane.add(generateButton);
		messagePane.setPreferredSize(new Dimension(MSG_SIZE_X,MSG_SIZE_Y));
		
		return messagePane;
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
		windowWidthLabel.setVisible(false);
		
		windowWidth = new JTextField();
		windowWidth.setEnabled(false);
		windowWidth.setVisible(false);
		
		optionsPane.add(new JLabel("IP Serwera:"));
		optionsPane.add(ipField);
		optionsPane.add(new JLabel("Port:"));
		optionsPane.add(portField);
		optionsPane.add(windowWidthLabel);
		optionsPane.add(windowWidth);
		
		optionsPane.setPreferredSize(new Dimension(OPT_SIZE_X,OPT_SIZE_Y));
		return optionsPane;
	}
	
	private static JPanel initButtons()
	{
		ActionAdapter buttonListener = null;
	
		JPanel buttonPane = new JPanel(new GridLayout(1, 4));
		buttonListener = new ActionAdapter()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (e.getActionCommand().equals("send"))
				{
					TCPFrame frame = framePane.getFrame();
					if (!framePane.getFrame().getData().equals(""))
					{
						sendFrame(frame);
						framePane.clear();
					}
				}
				else
				{
					framePane.clear();
				}
			}
		};
	
		sendButton = new JButton("Wyslij wszytkie");
		sendButton.setActionCommand("send");
		sendButton.addActionListener(buttonListener);
		sendButton.setEnabled(false);
		
		clearButton = new JButton("Wyczysc");
		clearButton.setActionCommand("clear");
		clearButton.addActionListener(buttonListener);
		clearButton.setEnabled(false);
		
		buttonListener = null;

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
		
		buttonPane.add(sendButton);
		buttonPane.add(clearButton);
		buttonPane.add(connectButton);
		buttonPane.add(disconnectButton);
		
		return buttonPane;		
	}




	

	
/*	private static void addComponentsToFramePane (JPanel framePane)
	{
		
		framePane.add(frameFields.get(EFieldIndex.SEQ_NUMBER.ordinal()));
		
		framePane.add(frameFields.get(EFieldIndex.PACKET_NUMBER.ordinal()));

		framePane.add(frameFields.get(EFieldIndex.SEQUIRITY_FLAG.ordinal()));
		
		framePane.add(frameFields.get(EFieldIndex.DATA_LENGTH.ordinal()));
		
		framePane.add(frameFields.get(EFieldIndex.DATA.ordinal()));
		
		framePane.add(frameFields.get(EFieldIndex.CHECK_SUM.ordinal()));
		
	} */
	
	private static JPanel initFramePaneLabel()
	{
		JPanel framePane = new JPanel(new GridLayout(1, Connection.FIELDS_NUMBER));
		framePane.setPreferredSize(new Dimension(PANE_SIZE_X, PANE_SIZE_Y));
		addComponentsToFramePaneLabel(framePane);
		
		return framePane;
	}
	
	private static void addComponentsToFramePaneLabel (JPanel framePane)
	{
		framePane.add(new JLabel("Numer sekwencyjny:", JLabel.CENTER));
		framePane.add(new JLabel("Liczba pakietow:", JLabel.CENTER));
		framePane.add(new JLabel("Flaga bezpieczenstwa:", JLabel.CENTER));	
		framePane.add(new JLabel("Dlugosc danych:", JLabel.CENTER));	
		framePane.add(new JLabel("Dane:", JLabel.CENTER));	
		framePane.add(new JLabel("Suma kontrolna:", JLabel.CENTER));
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
	
	public static void sendFrame(TCPFrame frame)
	{
		synchronized (Connection.toSend)
		{
			Connection.toSend = frame;
		}
	}

	public static void initGUI()
	{
		JPanel messagePane = initMessagePane();
		initStatusBar();
		GridBagConstraints c = new GridBagConstraints();
		
		JPanel buttonPane = initButtons();
		JPanel optionsPane = initOptionsPane();
		JPanel framePaneLabel = initFramePaneLabel();

		JPanel mainPane = new JPanel(new GridBagLayout());
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		mainPane.add(messagePane, c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		mainPane.add(optionsPane, c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 2;
		mainPane.add(framePaneLabel, c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 3;
		mainPane.add(framePane.getPane(), c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 5;
		mainPane.add(statusBar, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 4;
		c.ipady = 30; 
		mainPane.add(buttonPane, c);
		

		
		mainFrame = new JFrame(Connection.name);
		mainFrame.setContentPane(mainPane);
		mainFrame.setLocation(100, 400);
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

	public void run()
	{
		switch (Connection.connectionStatus)
		{
		case DISCONNECTED:
			connectButton.setEnabled(true);
			disconnectButton.setEnabled(false);
			clearButton.setEnabled(false);
			sendButton.setEnabled(false);
			generateButton.setEnabled(false);
			ipField.setEnabled(true);
			portField.setEnabled(true);
			statusColor.setBackground(Color.red);
			framePane.setEnabled(false);
			messageField.setEnabled(false);
			break;

		case DISCONNECTING:
			connectButton.setEnabled(false);
			disconnectButton.setEnabled(false);
			clearButton.setEnabled(false);
			sendButton.setEnabled(false);
			generateButton.setEnabled(false);
			ipField.setEnabled(false);
			portField.setEnabled(false);
			statusColor.setBackground(Color.orange);
			framePane.setEnabled(false);
			messageField.setEnabled(false);
			break;

		case CONNECTED:
			connectButton.setEnabled(false);
			disconnectButton.setEnabled(true);
			sendButton.setEnabled(true);
			clearButton.setEnabled(true);
			generateButton.setEnabled(true);
			ipField.setEnabled(false);
			portField.setEnabled(false);
			statusColor.setBackground(Color.green);
			framePane.setEnabled(true);
			windowWidth.setText(Byte.toString(TCPClient.windowWidth));
			windowWidth.setVisible(true);
			windowWidthLabel.setVisible(true);
			messageField.setEnabled(true);
			break;

		case BEGIN_CONNECT:
			connectButton.setEnabled(false);
			disconnectButton.setEnabled(false);
			sendButton.setEnabled(false);
			clearButton.setEnabled(false);
			generateButton.setEnabled(false);
			ipField.setEnabled(false);
			portField.setEnabled(false);
			statusColor.setBackground(Color.orange);
			framePane.setEnabled(false);
			messageField.setEnabled(false);
			break;

		default:
			break;
		}

		ipField.setText(Connection.hostIP);
		portField.setText((new Integer(Connection.port)).toString());
		statusField.setText(Connection.statusString);
		windowWidth.setText(new Byte (TCPClient.windowWidth).toString());
		Connection.toAppend.setLength(0);

		mainFrame.repaint();
	}
}

class ActionAdapter implements ActionListener
{
	public void actionPerformed(ActionEvent e)
	{
		
	}
}
