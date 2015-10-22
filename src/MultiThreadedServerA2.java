import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.io.DataInputStream;
import java.net.ServerSocket;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;

class MultiThreadedServerA2
{	
	public static void main(String[] args)
	{
		final int portNumber = 2994;
		
		try
		{
			ServerSocket serverSocket = new ServerSocket(portNumber);
			System.out.println("Server started at " + new Date() + "\n");
			while (true)
			{
				try
				{
					Socket socket = serverSocket.accept();
					MyClient myClient = new MyClient(socket);
					myClient.start();
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

class MyClient extends Thread
{	
	private Socket socket;
	private String hostName;
	private String ipAddress;
	private DataOutputStream toClient;
	private DataInputStream fromClient;
	private long clientID;

	public MyClient(Socket socket) throws IOException
	{	
		this.socket = socket;
		this.hostName = socket.getInetAddress().getHostName();
		this.ipAddress = socket.getInetAddress().getHostAddress();
		this.toClient = new DataOutputStream(socket.getOutputStream());
		this.fromClient = new DataInputStream(socket.getInputStream());
		this.clientID = Thread.activeCount();
	}
	
	public void run()
	{
		System.out.println("Client " + clientID + " [Hostname: " + hostName + " , " + "IP: " + ipAddress +"]");
		try
		{
			while (true)
			{
				String fromClientMessage = fromClient.readUTF();
				
				if (fromClientMessage.contains("AccountNumber"))
				{
					List<String> clientData = Arrays.asList(fromClientMessage.split(","));
					
					String fullName = checkDatabase(clientData.get(1));
					toClient.writeUTF(fullName);
				}
				else
				{
					List<String> clientData = Arrays.asList(fromClientMessage.split(","));
					
					Double annualInterestRate = Double.parseDouble(clientData.get(0));
					Double numberOfYears = Double.parseDouble(clientData.get(1));
					Double loanAmount = Double.parseDouble(clientData.get(2));
					
					String monthlyPayment = calculateMonthlyPayment(annualInterestRate, numberOfYears, loanAmount);
					
					toClient.writeUTF(monthlyPayment);
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("A client has disconnected");
		}
	}
	
	public String checkDatabase(String accountNumber) throws SQLException
	{
		try
		{
			// Connect to database
			Properties connectionProperties = new Properties();
			connectionProperties.put("user", "root");
			connectionProperties.put("password", "root");
			String serverLink = "jdbc:mysql://localhost:3306/BankDatabase";
	        Connection connection = DriverManager.getConnection(serverLink, connectionProperties);
	        
	        Statement statement = connection.createStatement();
	        statement.executeQuery("SELECT * FROM RegisteredApplicants WHERE AccountNum = " + accountNumber);
	        
	        // Check what database returns
	        ResultSet resultSet = statement.getResultSet();
	        while (resultSet.next())
	        {	
	        	if (Integer.parseInt(accountNumber) == resultSet.getInt("AccountNum"))
	        	{
	        		String firstName = resultSet.getString("FirstName");
	        		String lastName = resultSet.getString("LastName");
	        		return firstName + " " + lastName;
	        	}
	        	else
	        	{
	        		return "ABCDEFGHJKLMNOP" + "QRSTUVWXYZ";
	        	}
	        }
		}
		catch (SQLException e)
		{
			System.out.println(e);
		}
		
		return "ABCDEFGHJKLMNOP" + "QRSTUVWXYZ";
	}
	
	public String calculateMonthlyPayment(double annualInterestRate, double numberOfYears, double loanAmount)
	{
		annualInterestRate /= 100.0;
		double monthlyRate = annualInterestRate / 12.0;
		double termInMonths = numberOfYears * 12;
		double monthlyPaymentNumber = (loanAmount * monthlyRate) / (1-Math.pow(1+monthlyRate, -termInMonths));
		String monthlyPaymentString = String.valueOf(monthlyPaymentNumber);
		
		return monthlyPaymentString;
	}
	
	
}