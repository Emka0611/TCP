import java.io.Serializable;

public class TCPFrame implements Serializable
{
	private static final long serialVersionUID = 1L;

	public TCPFrame(Byte seqNumber, Byte packetsNumer, Byte sequrityFlag, Byte dataWindowWidth, Byte dataLength,
			String data, Byte checkSum)
	{

		this.seqNumber = seqNumber;
		this.packetsNumer = packetsNumer;
		this.sequrityFlag = sequrityFlag;
		this.dataWindowWidth = dataWindowWidth;
		this.dataLength = dataLength;
		this.data = data;
		this.checkSum = checkSum;
	}
	
	public TCPFrame(String data)
	{
		this.data = data;
	}

	private Byte seqNumber;
	private Byte packetsNumer;
	private Byte sequrityFlag;
	private Byte dataWindowWidth;
	private Byte dataLength;
	private String data;
	private Byte checkSum;

	public Byte getSeqNumber()
	{
		return seqNumber;
	}

	public void setSeqNumber(Byte seqNumber)
	{
		this.seqNumber = seqNumber;
	}

	public Byte getPacketsNumer()
	{
		return packetsNumer;
	}

	public void setPacketsNumer(Byte packetsNumer)
	{
		this.packetsNumer = packetsNumer;
	}

	public Byte getSequrityFlag()
	{
		return sequrityFlag;
	}

	public void setSequrityFlag(Byte sequrityFlag)
	{
		this.sequrityFlag = sequrityFlag;
	}

	public Byte getDataWindowWidth()
	{
		return dataWindowWidth;
	}

	public void setDataWindowWidth(Byte dataWindowWidth)
	{
		this.dataWindowWidth = dataWindowWidth;
	}

	public Byte getDataLength()
	{
		return dataLength;
	}

	public void setDataLength(Byte dataLength)
	{
		this.dataLength = dataLength;
	}

	public String getData()
	{
		return data;
	}

	public void setData(String data)
	{
		this.data = data;
	}

	public Byte getCheckSum()
	{
		return checkSum;
	}

	public void setCheckSum(Byte checkSum)
	{
		this.checkSum = checkSum;
	}

	@Override
	public String toString()
	{
		String s = "" + seqNumber;
		s += " " + packetsNumer;
		s += " " + sequrityFlag;
		s += " " + dataWindowWidth;
		s += " " + dataLength;
		s += " " + data;
		s += " " + checkSum;
		return s;
	}

}
