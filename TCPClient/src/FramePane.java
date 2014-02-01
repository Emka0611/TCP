import java.awt.Checkbox;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class FramePane
{
	private JPanel pane = new JPanel(new GridLayout(1, Connection.FIELDS_NUMBER + 1));
	
	public Vector<JTextField> frameFields = new Vector<JTextField>();
	public Checkbox sequrityFlagCheckbox = new Checkbox();
	public JButton sendFramebutton = null;
	
	public static final int PANE_SIZE_X = 900;
	public static final int PANE_SIZE_Y = 40;
	
	
	public FramePane(TCPFrame frame)
	{		
		initButton();
		
		for(int i=0; i<Connection.FIELDS_NUMBER-1; i++)
		{
			frameFields.add(new JTextField());
			pane.add(frameFields.get(i));
		}
		
		fillFields(frame);
		
		pane.add(sequrityFlagCheckbox);
		pane.add(sendFramebutton);
		pane.setPreferredSize(new Dimension(PANE_SIZE_X, PANE_SIZE_Y));
	}
	
	private void initButton()
	{
		ActionListener buttonListener = new ActionAdapter()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (e.getActionCommand().equals("sendFrame"))
				{
					GUIClient.sendFrames(getFrame());
					TCPClient.handleConnectedForWriting();
					GUIClient.changeStatusNTS(EConnectionStatus.NULL, true);
				}
			}
		};
		
		sendFramebutton = new JButton("Wyslij ramke");
		sendFramebutton.setActionCommand("sendFrame");
		sendFramebutton.addActionListener(buttonListener);
	}

	private void fillFields(TCPFrame frame)
	{
		frameFields.get(EFieldIndex.PACKET_NUMBER.ordinal()).setText(frame.getPacketsNumer());		
		frameFields.get(EFieldIndex.SEQ_NUMBER.ordinal()).setText(frame.getSeqNumber());
		frameFields.get(EFieldIndex.DATA_LENGTH.ordinal()).setText(frame.getDataLength());		
		frameFields.get(EFieldIndex.DATA.ordinal()).setText(frame.getData());
		frameFields.get(EFieldIndex.CHECK_SUM.ordinal()).setText(frame.getCheckSum());
		
		sequrityFlagCheckbox.setState(Boolean.parseBoolean(frame.getSequrityFlag()));
		
	}

	public void setEnabled(boolean enabled)
	{
		if(null != frameFields && null != sequrityFlagCheckbox)
		{			
			for(int i=0; i<Connection.FIELDS_NUMBER-1; i++)
			{
				frameFields.get(i).setEnabled(enabled);
			}
			
			sequrityFlagCheckbox.setEnabled(enabled);
			sendFramebutton.setEnabled(enabled);
		}
	}
	
	public void clear()
	{
		for(int i=0; i<Connection.FIELDS_NUMBER-1; i++)
		{
			frameFields.get(i).setText("");
		}
		sequrityFlagCheckbox.setState(false);
	}

	public TCPFrame getFrame() 
	{
		String seqNumber = frameFields.get(EFieldIndex.SEQ_NUMBER.ordinal()).getText();
		String packetsNumber = frameFields.get(EFieldIndex.PACKET_NUMBER.ordinal()).getText();
		String dataLength = frameFields.get(EFieldIndex.DATA_LENGTH.ordinal()).getText();
		String data = frameFields.get(EFieldIndex.DATA.ordinal()).getText();
		String checkSum = frameFields.get(EFieldIndex.CHECK_SUM.ordinal()).getText();
		String sequrityFlag = Boolean.toString(this.sequrityFlagCheckbox.getState());
		
		TCPFrame frame = new TCPFrame(seqNumber, packetsNumber, dataLength, data, checkSum, sequrityFlag);
		return frame;
	}
	
	public JPanel getPane()
	{
		return pane;
	}

}
