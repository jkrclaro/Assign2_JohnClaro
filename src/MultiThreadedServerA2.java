import java.io.*;
import java.net.*;

class MultiThreadedServerA2
{
	public static void main(String[] args)
	{
		try
		{
			ServerSocket serverSocket = new ServerSocket(1201);
			Socket socket = serverSocket.accept();
			
			DataInputStream inputStream = new DataInputStream(socket.getInputStream());
			DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
			
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
			
			String msgin = "";
			String msgout = "";
			
			while(!msgin.equals("end"))
			{
				msgin = inputStream.readUTF();
				System.out.println("MSGIN: " + msgin);
				msgout = bufferedReader.readLine();
				outputStream.writeUTF(msgout);
				outputStream.flush();
			}
			socket.close();
		}
		catch(Exception e)
		{
			System.err.println(e);
		}
	}
}