import java.io.*;
import java.net.*;

class ClientA2
{
	public static void main(String[] args)
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