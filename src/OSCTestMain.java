import java.io.IOException;
import java.net.*;

import com.illposed.osc.*;

public class OSCTestMain 
{
	public static void main(String[] argssss) throws IOException 
	{
		byte[] byteAddress = new byte[]{127,0,0,1};
		InetAddress address = InetAddress.getByAddress("localhost", byteAddress);
		//InetAddress address = InetAddress.getLoopbackAddress();		
		System.out.println(address);
		
		int port = 9001;
		OSCPortOut out = new OSCPortOut(address, port);
		OSCMessage message = new OSCMessage("/test/");
		message.addArgument("helloTehgshfgsjhdgjsdfst");
		
		out.send(message);
		
	}

}
