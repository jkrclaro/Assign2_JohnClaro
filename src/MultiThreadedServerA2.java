import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.List;

class MultiThreadedServerA2
{
	public static void main(String[] args)
	{
		try
		{
			ServerSocket serverSocket = new ServerSocket(1201);
			Socket socket = serverSocket.accept();
			
			DataInputStream inputStream = new DataInputStream(socket.getInputStream());
			DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
			
			while (true)
			{
				String msgin = inputStream.readUTF();
				List<String> values = Arrays.asList(msgin.split(","));
				
				Double annualInterestRate = Double.parseDouble(values.get(0));
				Double numberOfYears = Double.parseDouble(values.get(1));
				Double loanAmount = Double.parseDouble(values.get(2));
				
				double monthlyPayment = calculateMonthlyPayment(annualInterestRate, numberOfYears, loanAmount);
				
				outputStream.writeDouble(monthlyPayment);
			}
		}
		catch(Exception e)
		{
			System.err.println(e);
		}
	}
	
	public static double calculateMonthlyPayment(double annualInterestRate, double numberOfYears, double loanAmount)
	{
		annualInterestRate /= 100.0;
		double monthlyRate = annualInterestRate / 12.0;
		double termInMonths = numberOfYears * 12;
		double monthlyPayment = (loanAmount * monthlyRate) / (1-Math.pow(1+monthlyRate, -termInMonths));
		
		return monthlyPayment;
	}
}