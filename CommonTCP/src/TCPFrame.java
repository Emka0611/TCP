import java.io.Serializable;
import java.util.zip.CRC32;

public class TCPFrame implements Serializable
{
	private static final long serialVersionUID = 1L;

	private Byte seqNumber = null;
	private Byte packetsNumer = null;
	private Byte dataLength = null;
	private String data = null;
	private Byte checkSum = null;
	private Boolean sequrityFlag = null;
	
	public TCPFrame(Byte seqNumber, Byte packetsNumer, Byte dataLength,
			String data, Byte checkSum, Boolean sequrityFlag)
	{
		this.seqNumber = seqNumber;
		this.packetsNumer = packetsNumer;
		this.dataLength = dataLength;
		this.data = data;
		this.checkSum = checkSum;
		this.sequrityFlag = sequrityFlag;
	}
	
	public TCPFrame(String data)
	{
		this.data = data;
		
		this.seqNumber = 0;
		this.packetsNumer = 0;
		this.dataLength = 0;
		this.checkSum = 0;
		this.sequrityFlag = false;
	}

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
	
	public Boolean getSequrityFlag()
	{
		return sequrityFlag;
	}

	public void setSequrityFlag(Boolean sequrityFlag)
	{
		this.sequrityFlag = sequrityFlag;
	}

	@Override
	public String toString()
	{
		String s = "" + seqNumber;
		s += " " + packetsNumer;
		s += " " + dataLength;
		s += " " + data;
		s += " " + checkSum;
		s += " " + sequrityFlag;
		return s;
	}

	public void calculateChecksum()
	{
		CRC32 crc = new CRC32();
		crc.update(data.getBytes());
		checkSum = (byte) crc.getValue();
	}
	
	public void crc16()
	{
		int crc = 0xFFFF;

		for (int j = 0; j < data.getBytes().length; j++)
		{
			crc = ((crc >>> 8) | (crc << 8)) & 0xffff;
			crc ^= (data.getBytes()[j] & 0xff);// byte to int, trunc sign
			crc ^= ((crc & 0xff) >> 4);
			crc ^= (crc << 12) & 0xffff;
			crc ^= ((crc & 0xFF) << 5) & 0xffff;
		}
		crc &= 0xffff;
		checkSum = (byte)crc;
	}
	
	public void encryptData()
	{
	    String encrypt = "";
	    for (int i = 0; i < data.length(); i++)
	    {
	        encrypt +=(char)(data.charAt(i) + 5);
	    }
	    data=encrypt;
	}
	
	public void decryptData()
	{
		
	}

}
