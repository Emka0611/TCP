
public class Connection
{
	public final static String END_SESSION = "END";
	
	public final static String statusMessages[] = { 
		" B��d! Nie mo�na po��czy�!",
		" Roz��czony",
		" Roz��czanie...",
		" ��czenie...",
		" Po��czony" };
	
	public static String hostIP = "localhost";
	public static int port = 1234;
	public static boolean isHost = false;
	public static EConnectionStatus connectionStatus = EConnectionStatus.DISCONNECTED;
	public static String name = "";
	public static String statusString = statusMessages[connectionStatus.ordinal()];
	public static StringBuffer toAppend = new StringBuffer("");
	public static TCPFrame toSend = new TCPFrame("");
	public static TCPFrame framesToSend[] = null;
	public final static int FIELDS_NUMBER = EFieldIndex.values().length; 
}
