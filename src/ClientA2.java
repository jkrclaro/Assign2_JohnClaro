import java.net.Socket;
import java.util.Scanner;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.DataOutputStream;
import java.io.BufferedReader;

public class ClientA2
{
	public static void main(String[] args)
	{
		try
		{	
			System.out.println("What is your name?: ");
			Scanner clientInput = new Scanner(System.in);
			String clientName = clientInput.next();
			Socket socket = new Socket("localhost", 2994);
			
			DataOutputStream toServer = new DataOutputStream(socket.getOutputStream());
			
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
			
			while (true)
			{
				System.out.println("Enter a message: ");
				toServer.writeUTF(clientName + ": " + bufferedReader.readLine());
			}
		}
		catch(Exception e)
		{
			System.out.println("Cannot connect to the server");
		}
	}
}