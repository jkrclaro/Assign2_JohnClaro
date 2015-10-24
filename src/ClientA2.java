import java.net.Socket;
import java.net.UnknownHostException;
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
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import java.awt.Container;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.BorderFactory;

public class ClientA2
{	
	private Socket socket;
	private DataOutputStream toServer;
	private DataInputStream fromServer;
	
	public ClientA2() throws UnknownHostException, IOException
	{
		this.socket = null;
		this.toServer = null;
		this.fromServer = null;
	}
	
	/**
	 * Start asking user for an account number.
	 * Keep asking user until a valid account number has been provided or the user wishes to cancel the prompt.
	 * @return A valid account number.
	 */
	public String promptAccountNumber()
	{
		while (true)
		{
			String input = JOptionPane.showInputDialog(null, "Enter account number");
			
			if (input != null) // When user clicks OK
			{
				if (input.trim().isEmpty() != true && isNumeric(input) == true) // Check validity of input
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
	private boolean isNumeric(String input)
	{
		try
		{
			int number = Integer.parseInt(input);  // Can only be parsed if user provided a number, e.g. ..., -2, -1, 0, 1, 2...
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
	
	public void establishConnection() throws UnknownHostException, IOException
	{
		this.socket = new Socket("localhost", 2994);
		this.toServer = new DataOutputStream(socket.getOutputStream());
		this.fromServer = new DataInputStream(socket.getInputStream());
	}
	
	public boolean sendAccountNumber(String accountNumber) throws IOException
	{
		establishConnection();
		toServer.writeUTF("AccountNumber" + "," + accountNumber);
		String fullName = fromServer.readUTF();
		
		if (fullName != null)
		{
			JOptionPane.showMessageDialog(null, "Welcome, " + fullName);
			return true;
		}
		else
		{
			JOptionPane.showMessageDialog(null, "Sorry, you are not a registered client", "Warning", JOptionPane.WARNING_MESSAGE);
			return false;
		}
	}
	
	public void calculateLoan()
	{
		JFrame frame = new JFrame("Client");
		JPanel panel = new JPanel();
		
		frame.setSize(500, 300);
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
		
		JTextField annualInterestRateField = new JTextField(10);
		JTextField numberOfYearsField = new JTextField(10);
		JTextField loanAmountField = new JTextField(10);
		
		JButton submitButton = new JButton("Submit");
		submitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) 
			{
				String annualInterestRate = annualInterestRateField.getText();
				String numberOfYears = numberOfYearsField.getText();
				String loanAmount = loanAmountField.getText();
				
				try 
				{
					toServer.writeUTF("Monthly Payment: " + annualInterestRate + "," + numberOfYears + "," + loanAmount);
					textArea.append(fromServer.readUTF() + "\n");
				} 
				catch (IOException error) {
					error.printStackTrace();
				}
			}
			
		});
		
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.HORIZONTAL;
		
		// First Column
		gc.gridx = 0;
		gc.gridy = 0;
		panel.add(annualInterestRateLabel, gc);
		
		gc.gridx = 0;
		gc.gridy = 1;
		panel.add(numberOfYearsLabel, gc);
		
		gc.gridx = 0;
		gc.gridy = 2;
		panel.add(loanAmountLabel, gc);
		
		// Second Column
		gc.gridx = 1;
		gc.gridy = 0;
		panel.add(annualInterestRateField, gc);
		
		gc.gridx = 1;
		gc.gridy = 1;
		panel.add(numberOfYearsField, gc);
		
		gc.gridx = 1;
		gc.gridy = 2;
		panel.add(loanAmountField, gc);
		
		gc.gridx = 2;
		gc.gridy = 2;
		gc.gridheight = 100;
		panel.add(submitButton, gc);
		
		frame.setVisible(true);
	}
	
	public static void main(String[] args)
	{
		try
		{
			ClientA2 client = new ClientA2();
			String accountNumber = client.promptAccountNumber();
			boolean isLoginSuccessful = client.sendAccountNumber(accountNumber);
			if (isLoginSuccessful == true)
			{
				client.calculateLoan();
			}
		}
		catch (UnknownHostException error)
		{
			JOptionPane.showMessageDialog(null, error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		catch (IOException error)
		{
			JOptionPane.showMessageDialog(null, error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}
}