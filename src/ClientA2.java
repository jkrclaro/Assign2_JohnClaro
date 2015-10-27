import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

//GUI
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import java.awt.Container;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import javax.swing.BorderFactory;

public class ClientA2
{
	private Socket socket;
	private DataInputStream fromServer;
	private DataOutputStream toServer;

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
	 * Checks if the input the user provided is a positive number.
	 * @param The input provided by the user after being prompted for an account number.
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
	
	public boolean sendAccountNumber(String accountNumber)
	{
		String fullName = "";
		
		try
		{
			toServer.writeUTF("AccountNumber" + "," + accountNumber);
			fullName = fromServer.readUTF();
			
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
	
	public void calculateLoan()
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

		JLabel annualInterestRateValidityLabel = new JLabel("X");
		JLabel numberOfYearsValidityLabel = new JLabel("X");
		JLabel loanAmountValidityLabel = new JLabel("X");
		
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
				
				if (!annualInterestRate.trim().isEmpty() && isDouble(annualInterestRate)) 
				{
					annualInterestRateValidityLabel.setText("O");
				}
				else
				{
					annualInterestRateValidityLabel.setText("X");
				}
				
				if (!numberOfYears.trim().isEmpty() && isInteger(numberOfYears)) 
				{
					numberOfYearsValidityLabel.setText("O");
				}
				else
				{
					numberOfYearsValidityLabel.setText("X");
				}
				
				if (!loanAmount.trim().isEmpty() && isDouble(loanAmount)) 
				{
					loanAmountValidityLabel.setText("O");
				}
				else
				{
					loanAmountValidityLabel.setText("X");
				}
				
				textArea.setText("");
				if (annualInterestRateValidityLabel.getText().equals("O") &&
					numberOfYearsValidityLabel.getText().equals("O") &&
					loanAmountValidityLabel.getText().equals("O"))
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
				
				annualInterestRateValidityLabel.setVisible(true);
				numberOfYearsValidityLabel.setVisible(true);
				loanAmountValidityLabel.setVisible(true);
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
		
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 2;
		panel.add(submitButton, gridBagConstraints);
		
		// Third column
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 0;
		panel.add(annualInterestRateValidityLabel, gridBagConstraints);
		annualInterestRateValidityLabel.setVisible(false);
		
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		panel.add(numberOfYearsValidityLabel, gridBagConstraints);
		numberOfYearsValidityLabel.setVisible(false);
		
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 2;
		panel.add(loanAmountValidityLabel, gridBagConstraints);
		loanAmountValidityLabel.setVisible(false);
		
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
				
				if (loggedIn)
				{
					client.calculateLoan();
				}
			}
		}
		catch (IOException error)
		{
			JOptionPane.showMessageDialog(null, error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}