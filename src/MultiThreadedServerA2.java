import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.io.DataInputStream;
import java.net.ServerSocket;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

// GUI
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class MultiThreadedServerA2
{	
	private JTextPane textPane;
	private Connection connection;
	
	/**
	 * Create a server socket to start listening for new connection from clients at port 8000.
	 * Creates a new thread when a new connection is established.
	 * Also checks if the GUI has been closed, if it has then close the server socket.
	 */
	public MultiThreadedServerA2()
	{
		JFrame frame = new JFrame();
		textPane = new JTextPane();
		JScrollPane scrollPane = new JScrollPane(textPane);
		
		frame.setTitle("Server");
		frame.setSize(500, 300);
		frame.setLayout(new BorderLayout());
		frame.add(scrollPane, BorderLayout.CENTER);
		frame.setVisible(true);
		
		try
		{
			startDatabaseConnection();
			ServerSocket serverSocket = new ServerSocket(8000);
			updateServerLog("Server started at " + new Date(), Color.RED);
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent event)
				{
					try 
					{
						serverSocket.close();
					} 
					catch (IOException error) 
					{
						JOptionPane.showMessageDialog(null, error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
					
					System.exit(0);
				}
			});
			
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
	 * Ensures that a new line is created in the textPane.
	 * @param log - The string that's being added to the text pane.
	 * @param colorSpecified - Changes the color text in the text pane.
	 */
	private void updateServerLog(String log, Color colorSpecified)
	{
		StyledDocument document = textPane.getStyledDocument();
		SimpleAttributeSet color = new SimpleAttributeSet();
		StyleConstants.setForeground(color, colorSpecified);
		try
		{
			document.insertString(document.getLength(), log + "\n", color);
		}
		catch (Exception error)
		{
			JOptionPane.showMessageDialog(null, error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Establish a database connection between the server and the database in the phpMyAdmin.
	 * Sends user details as "root".
	 * Sends password details as "root".
	 * Please ensure to have the correct authentication for your phpMyAdmin database.
	 * Connects at the port 3306 of the phpMyAdmin database.
	 * Also, please ensure to have a database called 'BankDatabase'.
	 */
	private void startDatabaseConnection()
	{
		Properties connectionProperties = new Properties();
		connectionProperties.put("user", "root");
		connectionProperties.put("password", "");
		String serverLink = "jdbc:mysql://localhost:3306/BankDatabase";
		
        try 
        {
			connection = DriverManager.getConnection(serverLink, connectionProperties);
		} 
        catch (SQLException error) 
        {
			JOptionPane.showMessageDialog(null, error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}
	
	/**
	 * A class that handles the thread objects.
	 */
	private class MyClient extends Thread
	{	
		private Socket socket;
		private String hostname;
		private String ipAddress;
		private DataOutputStream toClient;
		private DataInputStream fromClient;
		private long clientID;
		
		/**
		 * Constructor for the Thread objects.
		 * @param socket - The server socket created in the class MultiThreadedServerA2
		 */
		public MyClient(Socket socket)
		{	
			try 
			{
				this.socket = socket;
				this.hostname = socket.getInetAddress().getHostName();
				this.ipAddress = socket.getInetAddress().getHostAddress();
				this.toClient = new DataOutputStream(socket.getOutputStream());
				this.fromClient = new DataInputStream(socket.getInputStream());
				this.clientID = Thread.activeCount() - 1;
			} 
			catch (IOException error) 
			{
				JOptionPane.showMessageDialog(null, error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		/**
		 * This method starts when a new thread is created.
		 * It gets stuck in a while loop to keep sending and retrieving data for the client.
		 * Client sends string data in two ways
		 * 1) Client sends account number data in format: 'AccountNumber,1001' or 'AccountNumber,1002'.
		 * 2) Client sends loan details in format: '3.5,3,5000' or '0.0,0,0'.
		 * Therefore, we can separate the string between the ",".
		 * So if the client sends the account number, it will then be splitted into an array, e.g.['AccountNumber','1001']
		 * where clientData.get(0) is equal to 'AccountNumber' and clientData.get(1) is equal to '1002'.
		 * This will mean that if clientData.get(0) doesn't equal to 'AccountNumber' then the client sent his/her loan details.
		 */
		public void run()
		{	
			try
			{
				while (true)
				{	
					String messageFromClient = fromClient.readUTF();
					List<String> clientData = Arrays.asList(messageFromClient.split(","));
					
					if (clientData.get(0).equals("AccountNumber"))
					{	
						updateServerLog("Starting thread for Client " + clientID + " at " + new Date(), Color.RED);
						
						String fullName = queryFullName(clientData.get(1));
						if (fullName != "")
						{
							updateServerLog("Client " + clientID + "'s hostname is " + hostname, Color.BLUE);
							updateServerLog("Client " + clientID + "'s IP address is " + ipAddress, Color.BLUE);
							toClient.writeUTF(fullName);
						}
						else
						{
							updateServerLog("Client " + clientID + " failed to login", Color.BLUE);
							toClient.writeUTF("");
						}
					}
					else
					{	
						Double annualInterestRate = Double.parseDouble(clientData.get(0));
						Double numberOfYears = Double.parseDouble(clientData.get(1));
						Double loanAmount = Double.parseDouble(clientData.get(2));
						
						updateServerLog("Client " + clientID + " sent data: ", Color.DARK_GRAY);
						updateServerLog(" > Annual interest rate: " + annualInterestRate, Color.GRAY);
						updateServerLog(" > Number of years: " + numberOfYears, Color.GRAY);
						updateServerLog(" > Loan amount: " + loanAmount, Color.GRAY);
						
						double monthlyPayment = calculateMonthlyPayment(annualInterestRate, numberOfYears, loanAmount);
						double totalPayment = calculateTotalPayment(monthlyPayment, numberOfYears);
						String calculatedMonthlyPayment = format(monthlyPayment);
						String calculatedTotalPayment = format(totalPayment);
						
						updateServerLog("Client " + clientID + " received data: ", Color.DARK_GRAY);
						updateServerLog(" > Monthly payment: " + calculatedMonthlyPayment, Color.GRAY);
						updateServerLog(" > Total payment: " + calculatedTotalPayment, Color.GRAY);
						
						
						toClient.writeUTF("Monthly payment: " + calculatedMonthlyPayment + "\n" + "Total payment: " + calculatedTotalPayment);
					}
				}
			}
			catch (Exception e)
			{
				try 
				{
					socket.close();
					updateServerLog("Client " + clientID + " has disconnected.", Color.RED);
				} catch (Exception error) 
				{
					JOptionPane.showMessageDialog(null, error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		
		/**
		 * Sends a SQL query to database "BankDatabase" for a table called "RegisteredApplicants"
		 * to check for records that matches the account number the client sent.
		 * @param accountNumber - Account number client sent.
		 * @return The first name and full name of the account number specified, otherwise an empty string.
		 */
		private String queryFullName(String accountNumber)
		{
			try
			{
				String query = "SELECT * FROM RegisteredApplicants WHERE AccountNum = " + accountNumber;
				PreparedStatement getApplicants = connection.prepareStatement(query); 
				// Prepared statements are better than create statements since they make SQL injections more difficult to perform.
				
				ResultSet resultSet = getApplicants.executeQuery();
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
		
		private double calculateMonthlyPayment(double annualInterestRate, double numberOfYears, double loanAmount)
		{	
			annualInterestRate /= 100.0;
			double monthlyRate = annualInterestRate / 12.0;
			double termInMonths = numberOfYears * 12;
			double monthlyPaymentNumber = (loanAmount * monthlyRate) / (1-Math.pow(1+monthlyRate, -termInMonths));
			double monthlyPayment = round(monthlyPaymentNumber);
			
			return monthlyPayment;
		}
		
		private double calculateTotalPayment(double monthlyPayment, double numberOfYears)
		{
			double totalPayment = monthlyPayment * 12 * numberOfYears;
			
			return totalPayment;
		}
		
		/**
		 * Rounds the value in two decimals, if the value was 123.45678, it will then be rounded up to 123.46.
		 * @param value - Calculated monthly payment
		 * @return Monthly payment in two decimals.
		 */
		private double round(double value)
		{
			if (Double.isNaN(value) || Double.isInfinite(value))
			{
				return 0.0;
			}
			else
			{
				BigDecimal bigDecimal = new BigDecimal(value);
				bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_UP);
				return bigDecimal.doubleValue();
			}
		}
		
		/**
		 * Formats the value to have commas separating the numbers, e.g.
		 * If the value was 1234.56 then it will become 1,234.56 or
		 * If the value was 56789.12 then it will become 56,789.12
		 * @param value - Monthly payment in two decimals
		 * @return Monthly payment with commas separating if necessary.
		 */
		private String format(double value)
		{
			DecimalFormat decimalFormat = new DecimalFormat("#,###.00");
			String result = decimalFormat.format(value);
			return result;
		}
	}
	
	public static void main(String[] args)
	{
		new MultiThreadedServerA2();
	}
}