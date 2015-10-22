import java.net.Socket;
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
	public void login(Socket socket, DataOutputStream toServer, DataInputStream fromServer)
	{	
		try
		{				
			while (true)
			{				
				String input = JOptionPane.showInputDialog(null, "Account Number");
				toServer.writeUTF("AccountNumber" + "," + input);
				String fullName = fromServer.readUTF();
				
				// TODO: Handle user input when OK button is clicked and empty
				if (input != null)
				{
					if (fullName != "ABCDEFGHJKLMNOPQRSTUVWXYZ")
					{
						JOptionPane.showMessageDialog(null, "Welcome, " + fullName);
						break;
					}
					else
					{
						JOptionPane.showMessageDialog(null, "Sorry, you are not a registered client", "Warning", JOptionPane.WARNING_MESSAGE);
					}
				}
				else
				{
					socket.close();
					System.exit(0);
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("Cannot connect to the server");
		}
	}
	
	public void send(Socket socket, DataOutputStream toServer, DataInputStream fromServer)
	{
		JFrame frame = new JFrame("Client");
		JPanel panel = new JPanel();
		
		frame.setSize(500, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setLayout(new BorderLayout());
		
		JTextArea textArea = new JTextArea();
		
		Container c = frame.getContentPane();
		c.add(textArea, BorderLayout.CENTER);
		c.add(panel, BorderLayout.NORTH);
		
		// JPanel + GridBagConstraints
		
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
			public void actionPerformed(ActionEvent e) 
			{
				String annualInterestRate = annualInterestRateField.getText();
				String numberOfYears = numberOfYearsField.getText();
				String loanAmount = loanAmountField.getText();
				
				try 
				{
					toServer.writeUTF(annualInterestRate + "," + numberOfYears + "," + loanAmount);
					textArea.append(fromServer.readUTF() + "\n");
				} 
				catch (IOException error) {
					System.out.println(error);
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
			Socket socket = new Socket("localhost", 2994);
			DataOutputStream toServer = new DataOutputStream(socket.getOutputStream());
			DataInputStream fromServer = new DataInputStream(socket.getInputStream());
			
			ClientA2 clientA2 = new ClientA2();
			clientA2.login(socket, toServer, fromServer);
			clientA2.send(socket, toServer, fromServer);
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(null, "Unable to connect to server", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}