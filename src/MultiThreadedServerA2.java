import java.net.Socket;
import java.io.DataInputStream;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.List;

class MultiThreadedServerA2
{	
	public static void main(String[] args)
	{
		final int portNumber = 2994;
		try
		{
			ServerSocket serverSocket = new ServerSocket(portNumber);
			System.out.println("Server is currently listening at port " + portNumber);
			
			while (true)
			{
				try
				{
					Socket clientSocket = serverSocket.accept();
					Thread thread = new Thread(new MyClient(clientSocket));
					thread.start();
					System.out.println("Number of clients currently connected: " + (Thread.activeCount() - 1));
				}
				catch (Exception e)
				{
					System.err.println("Error in connection attempt");
				}
			}
		}
		catch (Exception e)
		{
			System.err.println("Error in establishing a server");
		}
	}
}

class MyClient implements Runnable
{	
	private Socket socket;

	public MyClient(Socket socket)
	{	
		this.socket = socket;
	}
	
	public void run()
	{
		try
		{
			DataInputStream fromClient = new DataInputStream(socket.getInputStream());
			while (true)
			{
				System.out.println(fromClient.readUTF());
			}
		}
		catch (Exception e)
		{
			System.out.println("Error in retrieving message from client");
		}
	}
}