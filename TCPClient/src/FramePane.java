import java.awt.Checkbox;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class FramePane
{
	private JPanel pane = null;
	
	public Vector<JTextField> frameFields = null;
	public Checkbox sequrityFlag = null;
	
	public static final int PANE_SIZE_X = 900;
	public static final int PANE_SIZE_Y = 40;
	
	public FramePane()
	{		
		frameFields = new Vector<JTextField>();
		sequrityFlag = new Checkbox();
		sequrityFlag.setState(true);
		
		pane = new JPanel(new GridLayout(1, Connection.FIELDS_NUMBER));
		
		for(int i=0; i<Connection.FIELDS_NUMBER; i++)
		{
			frameFields.add(new JTextField());
			pane.add(frameFields.get(i));
		}
		pane.setPreferredSize(new Dimension(PANE_SIZE_X, PANE_SIZE_Y));
	}
	
	public FramePane(String s)
	{		
		frameFields = new Vector<JTextField>();
		sequrityFlag = new Checkbox();
		sequrityFlag.setState(true);
		
		pane = new JPanel(new GridLayout(1, Connection.FIELDS_NUMBER));
		
		for(int i=0; i<Connection.FIELDS_NUMBER-1; i++)
		{
			frameFields.add(new JTextField());
			pane.add(frameFields.get(i));
		}
		pane.add(sequrityFlag);
		
		frameFields.get(EFieldIndex.DATA.ordinal()).setText(s);	
		pane.setPreferredSize(new Dimension(PANE_SIZE_X, PANE_SIZE_Y));
	}
	
	public void setEnabled(boolean enabled)
	{
		if(null != frameFields && null != sequrityFlag)
		{			
			for(int i=0; i<Connection.FIELDS_NUMBER-1; i++)
			{
				frameFields.get(i).setEnabled(enabled);
			}
			
			sequrityFlag.setEnabled(enabled);
		}
	}
	
	public void clear()
	{
		for(int i=0; i<Connection.FIELDS_NUMBER-1; i++)
		{
			frameFields.get(i).setText("");
		}
		sequrityFlag.setState(false);
	}

	public TCPFrame getFrame() 
	{
		Byte seqNumber = Byte.valueOf(frameFields.get(EFieldIndex.SEQ_NUMBER.ordinal()).getText());
		Byte packetsNumber = Byte.valueOf(frameFields.get(EFieldIndex.PACKET_NUMBER.ordinal()).getText());
		Byte dataLength = Byte.valueOf(frameFields.get(EFieldIndex.DATA_LENGTH.ordinal()).getText());
		String data = frameFields.get(EFieldIndex.DATA.ordinal()).getText();
		Byte checkSum = Byte.valueOf(frameFields.get(EFieldIndex.CHECK_SUM.ordinal()).getText());
		
		Byte sequrityFlag;
		
		if (false == this.sequrityFlag.getState())
		{
			sequrityFlag = 0; 
		}
		else
		{
			sequrityFlag = 1;
		}
		
		TCPFrame frame = new TCPFrame(seqNumber, packetsNumber, sequrityFlag, dataLength, data, checkSum);
		return frame;
	}
	
	public JPanel getPane()
	{
		return pane;
	}

}
