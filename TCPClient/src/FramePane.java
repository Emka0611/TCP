import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JTextField;



public class FramePane
{
	private JPanel pane = null;
	
	public Vector<JTextField> frameFields = null;
	
	public static final int PANE_SIZE_X = 900;
	public static final int PANE_SIZE_Y = 40;
	
	public FramePane()
	{		
		frameFields = new Vector<JTextField>();
		pane = new JPanel(new GridLayout(1, Connection.FIELDS_NUMBER));
		
		for(int i=0; i<Connection.FIELDS_NUMBER; i++)
		{
			frameFields.add(new JTextField());
			pane.add(frameFields.get(i));
		}
		
		setEnabled(true);
		pane.setPreferredSize(new Dimension(PANE_SIZE_X, PANE_SIZE_Y));
	}
	
	public FramePane(String s)
	{		
		frameFields = new Vector<JTextField>();
		pane = new JPanel(new GridLayout(1, Connection.FIELDS_NUMBER));
		
		for(int i=0; i<Connection.FIELDS_NUMBER; i++)
		{
			frameFields.add(new JTextField());
			pane.add(frameFields.get(i));
		}
		
		frameFields.get(EFieldIndex.DATA.ordinal()).setText(s);
		
		setEnabled(true);
		pane.setPreferredSize(new Dimension(PANE_SIZE_X, PANE_SIZE_Y));
	}
	
	public void setEnabled(boolean enabled)
	{
		if(null != frameFields)
		{			
			for(int i=0; i<Connection.FIELDS_NUMBER; i++)
			{
				frameFields.get(i).setEnabled(enabled);
			}
		}
	}
	
	public void clear()
	{
		for(int i=0; i<Connection.FIELDS_NUMBER; i++)
		{
			frameFields.get(i).setText("");
		}
	}

	public TCPFrame getFrame() 
	{
		//TODO zabezpiecznie pustakami
		Byte seqNumber = Byte.valueOf(frameFields.get(EFieldIndex.SEQ_NUMBER.ordinal()).getText());
		Byte packetsNumber = Byte.valueOf(frameFields.get(EFieldIndex.PACKET_NUMBER.ordinal()).getText());
		Byte sequrityFlag = Byte.valueOf(frameFields.get(EFieldIndex.SEQUIRITY_FLAG.ordinal()).getText());
		Byte dataLength = Byte.valueOf(frameFields.get(EFieldIndex.DATA_LENGTH.ordinal()).getText());
		String data = frameFields.get(EFieldIndex.DATA.ordinal()).getText();
		Byte checkSum = Byte.valueOf(frameFields.get(EFieldIndex.CHECK_SUM.ordinal()).getText());
		
		TCPFrame frame = new TCPFrame(seqNumber, packetsNumber, sequrityFlag, dataLength, data, checkSum);
		return frame;
	}
	
	public JPanel getPane()
	{
		return pane;
	}

}
