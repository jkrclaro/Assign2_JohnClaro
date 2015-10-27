import java.net.Socket;
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
import java.util.Date;

// GUI
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class MultiThreadedServerA2
{	
	private JTextArea textArea;
	private Connection connection;
	
	public MultiThreadedServerA2()
	{
		JFrame frame = new JFrame();
		textArea = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(textArea);
		
		frame.setTitle("Server");
		frame.setSize(500, 300);
		frame.setLayout(new BorderLayout());
		frame.add(scrollPane, BorderLayout.CENTER);
		frame.setVisible(true);
		
		try
		{
			startDatabaseConnection();
			ServerSocket serverSocket = new ServerSocket(8000);
			updateServerLog("Server started at " + new Date());
			
			while (true)
			{
				Socket socket = serverSocket.accept();
				MyClient myClient = new MyClient(socket);
				myClient.start();
			}
		}
		catch (Exception error)
		{
			JOptionPane.showMessageDialog(null, error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * This method ensures that a new line is created in the textArea
	 * @param log
	 */
	private void updateServerLog(String log)
	{
		textArea.append(log + "\n");
	}
	
	private void startDatabaseConnection()
	{
		Properties connectionProperties = new Properties();
		connectionProperties.put("user", "root");
		connectionProperties.put("password", "root");
		String serverLink = "jdbc:mysql://localhost:3306/BankDatabase";
		
        try 
        {
			connection = DriverManager.getConnection(serverLink, connectionProperties);
		} 
        catch (SQLException error) 
        {
			JOptionPane.showMessageDialog(null, "Unable to connect to database", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}
	
	
	
	private class MyClient extends Thread
	{	
		private Socket socket;
		private String hostname;
		private String ipAddress;
		private DataOutputStream toClient;
		private DataInputStream fromClient;
		private long clientID;

		public MyClient(Socket socket) throws IOException
		{	
			this.socket = socket;
			this.hostname = socket.getInetAddress().getHostName();
			this.ipAddress = socket.getInetAddress().getHostAddress();
			this.toClient = new DataOutputStream(socket.getOutputStream());
			this.fromClient = new DataInputStream(socket.getInputStream());
			this.clientID = Thread.activeCount() - 1;
		}
		
		public void run()
		{	
			try
			{
				while (true)
				{	
					String messageFromClient = fromClient.readUTF();
					
					if (messageFromClient.contains("AccountNumber"))
					{	
						updateServerLog("Starting thread for Client " + clientID + " at " + new Date());
						
						List<String> clientData = Arrays.asList(messageFromClient.split(","));
						
						String fullName = queryDatabase(clientData.get(1));
						if (fullName != "")
						{
							updateServerLog("Client " + clientID + "'s hostname is " + hostname);
							updateServerLog("Client " + clientID + "'s IP address is " + ipAddress);
							toClient.writeUTF(fullName);
						}
						else
						{
							updateServerLog("Client " + clientID + " failed to login");
							toClient.writeUTF("");
						}
					}
					else
					{
						List<String> clientData = Arrays.asList(messageFromClient.split(","));
						
						
						Double annualInterestRate = Double.parseDouble(clientData.get(0));
						Double numberOfYears = Double.parseDouble(clientData.get(1));
						Double loanAmount = Double.parseDouble(clientData.get(2));
						
						updateServerLog("Client " + clientID + " sent data: ");
						updateServerLog(" > Annual interest rate: " + annualInterestRate);
						updateServerLog(" > Number of years: " + numberOfYears);
						updateServerLog(" > Loan amount: " + loanAmount);
						
						// TODO: Scientific notations ?
						String monthlyPayment = calculateMonthlyPayment(annualInterestRate, numberOfYears, loanAmount);
						String totalPayment = calculateTotalPayment(monthlyPayment, numberOfYears);
						
						
						toClient.writeUTF("Monthly payment: " + monthlyPayment + "\n" + "Total payment: " + totalPayment);
					}
				}
			}
			catch (Exception e)
			{
				try 
				{
					socket.close();
					updateServerLog("Client " + clientID + " has disconnected.");
				} catch (Exception error) 
				{
					JOptionPane.showMessageDialog(null, error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		
		public String queryDatabase(String accountNumber) throws SQLException
		{
			try
			{   
		        // TODO: Use prepared statements instead to avoid SQL injection
		        
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
		        }
			}
			catch (SQLException error)
			{
				JOptionPane.showMessageDialog(null, error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
			
			return "";
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
		
		public String calculateTotalPayment(String monthlyPaymentString, double numberOfYears)
		{
			double monthlyPayment = Double.parseDouble(monthlyPaymentString);
			double totalPayment = monthlyPayment * 12 * numberOfYears;
			String totalPaymentString = String.valueOf(totalPayment);
			
			return totalPaymentString;
		}
	}
	
	public static void main(String[] args)
	{
		new MultiThreadedServerA2();
	}
}