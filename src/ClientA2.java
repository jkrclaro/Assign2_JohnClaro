import java.net.Socket;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

//GUI
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import java.awt.Container;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.BorderFactory;

public class ClientA2
{
	private Socket socket;
	private DataInputStream fromServer;
	private DataOutputStream toServer;
	
	/**
	 * Constructor class for ClientA2
	 * @param socket - Socket object that establishes connection with the server.
	 * @param toServer - The data stream created with the server to send data in
	 * @param fromServer - The data stream created with the server to receive data in.
	 */
	public ClientA2(Socket socket, DataOutputStream toServer, DataInputStream fromServer)
	{
		this.socket = socket;
		this.fromServer = fromServer;
		this.toServer = toServer;
	}
	
	/**
	 * Start asking user for an account number.
	 * Keep asking user until a valid account number has been provided or the user wishes to cancel the prompt.
	 * @return A valid account number in string format.
	 */
	public String promptAccountNumber()
	{
		String input = "";
		
		while (true)
		{
			input = JOptionPane.showInputDialog(null, "Enter account number", "Prompt", JOptionPane.QUESTION_MESSAGE);
			
			if (input != null) // When user clicks OK
			{
				if (input.trim().isEmpty() != true && isInteger(input) == true) // Check validity of input
				{
					return input;
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Invalid account number", "Warning", JOptionPane.WARNING_MESSAGE);
				}
			}
			else // When user clicks cancel
			{
				JOptionPane.showMessageDialog(null, "Goodbye", "Goodbye", JOptionPane.INFORMATION_MESSAGE);
				System.exit(0);
			}
		}
	}
	
	/**
	 * Checks if the input the user provided is a positive integer number.
	 * @param input - The input provided by the user after being prompted for an account number.
	 * @return Boolean depending if the user provided a positive number.
	 */
	private boolean isInteger(String input)
	{
		int number = 0;
		
		try
		{
			number = Integer.parseInt(input);  // Can only be parsed if user provided a number, e.g. ..., -2, -1, 0, 1, 2...
			if (number >= 0) // Check if positive
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (NumberFormatException error)
		{
			return false;
		}
	}
	
	/**
	 * Checks if the input the user provided is a positive double number.
	 * @param input - The input provided by the user after being prompted for an account number.
	 * @return Boolean depending if the user provided a positive number.
	 */
	private boolean isDouble(String input)
	{
		double number = 0.0;
		
		try
		{
			number = Double.parseDouble(input);  // Can only be parsed if user provided a number, e.g. ..., -2, -1, 0, 1, 2...
			
			if (number >= 0.0) // Check if positive
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (NumberFormatException error)
		{
			return false;
		}
	}
	
	/**
	 * Send the account number to the server using the data stream 'toServer'.
	 * @param accountNumber - Account number specified by the client at the start.
	 * @return Boolean depending whether the account number is in the table 'RegisteredApplicants', if it is return true, otherwise false.
	 */
	public boolean sendAccountNumber(String accountNumber)
	{	
		try
		{
			toServer.writeUTF("AccountNumber" + "," + accountNumber);
			String fullName = fromServer.readUTF();
			
			if (fullName.length() == 0)
			{
				JOptionPane.showMessageDialog(null, "Sorry, you are not a registered client", "Warning", JOptionPane.WARNING_MESSAGE);
				return false;
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Welcome, " + fullName);
				return true;
			}
		}
		catch (Exception error)
		{
			JOptionPane.showMessageDialog(null, error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		
		return false;
	}
	
	/**
	 * Create a GUI for when sending loan details.
	 * Ask the user for an annual interest rate, number of years and the loan amount.
	 * If the user inputs an invalid detail in annual interest rate, number of years or loan amount field
	 * Then make the label RED to symbolize an invalid input.
	 * Otherwise, make the label GREEN to symbolize a valid input.
	 * Also, close the socket if the user closes the GUI.
	 */
	public void sendLoanDetails()
	{
		JFrame frame = new JFrame("Client");
		JPanel panel = new JPanel();
		
		frame.setSize(500, 175);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
	
		JTextArea textArea = new JTextArea();
		
		Container c = frame.getContentPane();
		c.add(textArea, BorderLayout.CENTER);
		c.add(panel, BorderLayout.NORTH);
		
		panel.setBorder(BorderFactory.createTitledBorder("Client"));
		
		JLabel annualInterestRateLabel = new JLabel("Annual Interest Rate: ");
		JLabel numberOfYearsLabel = new JLabel("Number Of Years: ");
		JLabel loanAmountLabel = new JLabel("Loan Amount: ");
		
		JTextField annualInterestRateField = new JTextField(15);
		JTextField numberOfYearsField = new JTextField(15);
		JTextField loanAmountField = new JTextField(15);
		
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		
		JButton submitButton = new JButton("Submit");
		submitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) 
			{
				String annualInterestRate = annualInterestRateField.getText();
				String numberOfYears = numberOfYearsField.getText();
				String loanAmount = loanAmountField.getText();
				
				if (!annualInterestRate.trim().isEmpty() && 
					isDouble(annualInterestRate) && 
					Double.parseDouble(annualInterestRate) <= 1000) 
				{
					annualInterestRateLabel.setForeground(Color.GREEN);
				}
				else
				{
					annualInterestRateLabel.setForeground(Color.RED);
				}
				
				if (!numberOfYears.trim().isEmpty() && isInteger(numberOfYears)) 
				{
					numberOfYearsLabel.setForeground(Color.GREEN);
				}
				else
				{
					numberOfYearsLabel.setForeground(Color.RED);
				}
				
				if (!loanAmount.trim().isEmpty() && isDouble(loanAmount)) 
				{
					loanAmountLabel.setForeground(Color.GREEN);
				}
				else
				{
					loanAmountLabel.setForeground(Color.RED);
				}
				
				textArea.setText("");
				if (annualInterestRateLabel.getForeground() == Color.GREEN &&
					numberOfYearsLabel.getForeground() == Color.GREEN &&
					loanAmountLabel.getForeground() == Color.GREEN)
				{
					try 
					{
						toServer.writeUTF(annualInterestRate + "," + numberOfYears + "," + loanAmount);
						textArea.setText(fromServer.readUTF());
					} 
					catch (IOException error) 
					{
						JOptionPane.showMessageDialog(null, error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}	
				}
			}
			
		});
		
		// First Column
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		panel.add(annualInterestRateLabel, gridBagConstraints);
		
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		panel.add(numberOfYearsLabel, gridBagConstraints);
		
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		panel.add(loanAmountLabel, gridBagConstraints);
		
		// Second Column
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		panel.add(annualInterestRateField, gridBagConstraints);
		
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		panel.add(numberOfYearsField, gridBagConstraints);
		
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		panel.add(loanAmountField, gridBagConstraints);
		
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 2;
		panel.add(submitButton, gridBagConstraints);
		
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event)
			{
				try 
				{
					socket.close();
				} 
				catch (IOException error) 
				{
					JOptionPane.showMessageDialog(null, error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
				
				System.exit(0);
			}
		});
		
		frame.setVisible(true);
	}

	public static void main(String[] args)
	{
		try
		{
			Socket socket = new Socket("localhost", 8000);
			DataOutputStream fromServer = new DataOutputStream(socket.getOutputStream());
			DataInputStream toServer = new DataInputStream(socket.getInputStream());
			String accountNumber = "";
			boolean loggedIn = false;
			
			ClientA2 client = new ClientA2(socket, fromServer, toServer);
			
			while (!loggedIn)
			{
				accountNumber = client.promptAccountNumber();
				loggedIn = client.sendAccountNumber(accountNumber);
				
				// When logged in keep sending loan details until closed.
				if (loggedIn)
				{
					client.sendLoanDetails();
				}
			}
		}
		catch (IOException error)
		{
			JOptionPane.showMessageDialog(null, error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}