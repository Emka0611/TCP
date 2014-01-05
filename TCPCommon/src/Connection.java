
public class Connection
{
	public final static String statusMessages[] = { 
		" B³¹d! Nie mo¿na po³¹czyæ!",
		" Roz³¹czony",
		" Roz³¹czanie...",
		" £¹czenie...",
		" Po³¹czony" };
	
	public static String hostIP = "localhost";
	public static int port = 1234;
	public static boolean isHost = false;
	public static EConnectionStatus connectionStatus = EConnectionStatus.DISCONNECTED;
	public static String name = "Simple TCP Client";
	public static String statusString = statusMessages[connectionStatus.ordinal()];
	public static StringBuffer toAppend = new StringBuffer("");
	public static StringBuffer toSend = new StringBuffer("");
}
