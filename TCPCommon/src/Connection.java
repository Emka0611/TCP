
public class Connection
{
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
	public static String name = "Simple TCP Client";
	public static String statusString = statusMessages[connectionStatus.ordinal()];
	public static StringBuffer toAppend = new StringBuffer("");
	public static StringBuffer toSend = new StringBuffer("");
}
