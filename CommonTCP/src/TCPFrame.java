
public class TCPFrame
{
	private byte seqNumber;
	private byte packetsNumer;
	private byte sequrityFlag;
	private byte dataWindowWidth;
	private byte dataLength;
	private char[] data;
	private byte checkSum;
	
	public byte getSeqNumber()
	{
		return seqNumber;
	}
	public void setSeqNumber(byte seqNumber)
	{
		this.seqNumber = seqNumber;
	}
	public byte getPacketsNumer()
	{
		return packetsNumer;
	}
	public void setPacketsNumer(byte packetsNumer)
	{
		this.packetsNumer = packetsNumer;
	}
	public byte getSequrityFlag()
	{
		return sequrityFlag;
	}
	public void setSequrityFlag(byte sequrityFlag)
	{
		this.sequrityFlag = sequrityFlag;
	}
	public byte getDataWindowWidth()
	{
		return dataWindowWidth;
	}
	public void setDataWindowWidth(byte dataWindowWidth)
	{
		this.dataWindowWidth = dataWindowWidth;
	}
	public byte getDataLength()
	{
		return dataLength;
	}
	public void setDataLength(byte dataLength)
	{
		this.dataLength = dataLength;
	}
	public char[] getData()
	{
		return data;
	}
	public void setData(char[] data)
	{
		this.data = data;
	}
	public byte getCheckSum()
	{
		return checkSum;
	}
	public void setCheckSum(byte checkSum)
	{
		this.checkSum = checkSum;
	}	
}
