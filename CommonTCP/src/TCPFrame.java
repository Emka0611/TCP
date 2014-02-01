import java.io.Serializable;
import java.util.zip.CRC32;

public class TCPFrame implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String seqNumber = null;
	private String packetsNumer = null;
	private String dataLength = null;
	private String data = null;
	private String checkSum = null;
	private String sequrityFlag = null;

	public TCPFrame(String seqNumber, String packetsNumer, String dataLength, String data, String checkSum,
			String sequrityFlag)
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

		this.seqNumber = "0";
		this.packetsNumer = "0";
		this.dataLength = "0";
		this.checkSum = "0";
		this.sequrityFlag = "false";
	}

	public String getSeqNumber()
	{
		return seqNumber;
	}

	public void setSeqNumber(String seqNumber)
	{
		this.seqNumber = seqNumber;
	}

	public String getPacketsNumer()
	{
		return packetsNumer;
	}

	public void setPacketsNumer(String packetsNumer)
	{
		this.packetsNumer = packetsNumer;
	}

	public String getDataLength()
	{
		return dataLength;
	}

	public void setDataLength(String dataLength)
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

	public String getCheckSum()
	{
		return checkSum;
	}

	public void setCheckSum(String checkSum)
	{
		this.checkSum = checkSum;
	}

	public String getSequrityFlag()
	{
		return sequrityFlag;
	}

	public void setSequrityFlag(String sequrityFlag)
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

	public long calculateChecksum()
	{
		int crc = 0xFFFF;

		for (int j = 0; j < data.getBytes().length; j++)
		{
			crc = ((crc >>> 8) | (crc << 8)) & 0xffff;
			crc ^= (data.getBytes()[j] & 0xff);// String to int, trunc sign
			crc ^= ((crc & 0xff) >> 4);
			crc ^= (crc << 12) & 0xffff;
			crc ^= ((crc & 0xFF) << 5) & 0xffff;
		}
		crc &= 0xffff;
		return crc;
	}

	/*
	 * public int crc16() {
	 * 
	 * return crc; CRC32 crc = new CRC32(); crc.update(data.getBytes()); return
	 * crc.getValue(); }
	 */
	
	public void encryptData()
	{
		String encrypt = "";
		for (int i = 0; i < data.length(); i++)
		{
			encrypt += (char) (data.charAt(i) + 5);
		}
		data = encrypt;
	}

	public void decryptData()
	{
		String decrypt = "";
		for (int i = 0; i < data.length(); i++)
		{
			decrypt += (char) (data.charAt(i) - 5);
		}
		data = decrypt;
	}

	public boolean validate()
	{
		boolean res = false;

		if (Integer.parseInt(seqNumber) < Integer.parseInt(packetsNumer)
				&& Integer.parseInt(dataLength) == data.length() && Long.parseLong(checkSum) == calculateChecksum())
		{
			res = true;
		}

		return res;
	}

}
