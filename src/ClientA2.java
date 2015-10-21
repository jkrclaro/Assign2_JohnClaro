import java.io.*;
import java.net.*;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JTextField;

public class ClientA2
{
	public static void main(String[] args)
	{
		JFrame frame = new JFrame("Client");
		JPanel panel = new JPanel();
		
		frame.setSize(500, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setLayout(new BorderLayout());
		
		JTextArea textArea = new JTextArea();
		JButton button = new JButton("Submit");
		
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) 
			{
				textArea.append("Hello\n");
			}
			
		});
		
		Container c = frame.getContentPane();
		c.add(textArea, BorderLayout.CENTER);
		c.add(button, BorderLayout.SOUTH);
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
		gc.gridy = 1;
		gc.gridheight = 100;
		panel.add(submitButton, gc);
		
		
		// END
		
		frame.setVisible(true);
	}
	
	public void start()
	{
		try
		{
			Socket socket = new Socket("127.0.0.1", 1201);
			DataInputStream inputStream = new DataInputStream(socket.getInputStream());
			DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
			
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
			
			while(true)
			{
				// Prompt data
				System.out.println("Enter annual interest rate: ");
				String annualInterestRate = bufferedReader.readLine();
				System.out.println("Enter number of years: ");
				String numberOfYears = bufferedReader.readLine();
				System.out.println("Enter loan amount: ");
				String loanAmount = bufferedReader.readLine();
				
				outputStream.writeUTF(annualInterestRate + "," + numberOfYears + "," + loanAmount);
				Double msgin = inputStream.readDouble();
				System.out.println(msgin);
			}
		}
		catch(Exception e)
		{
			System.err.println(e);
		}
	}
}