import java.sql.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class AccessPoint {
	public static Connection conn;
	private static ArrayList<Patient> connectivityList;
	private int port = 3602;
	private ServerSocket serverSocket;

	public void acceptConnections() 
	{
		try 
		{
			serverSocket = new ServerSocket(port);
		} 
		catch (IOException e) 
		{
			System.err.println("AccessPointSocket instantiation failure");
			e.printStackTrace();
			System.exit(0);
		}
		while (true) 
		{
			try {
				Socket newConnection = serverSocket.accept();
				System.out.println("accepted connection");
				ServerThread st = new ServerThread(newConnection);
				new Thread(st).start();
			} 
			catch (IOException ioe) 
			{
				System.err.println("AccessPoint accept failed");
			}
		}
	} 

	public static void main(String args[]) throws ClassNotFoundException 
	{
		SQLManager sqlManager = new SQLManager("Patients.db");
		connectivityList = sqlManager.GetAllPatients();		
		AccessPoint AccessPoint = null;
		AccessPoint = new AccessPoint();
		System.out.println("Waiting for clients ...");
		AccessPoint.acceptConnections();
	}
	
	public static Patient createPatient(String name, String phoneNumber, String macAddress) 
	{
		return new Patient(name, phoneNumber, macAddress);
	}	

	class ServerThread implements Runnable 
	{
		private Socket socket;
		private DataInputStream fromClient;
		private DataOutputStream outToClient;

		public ServerThread(Socket socket) 
		{
			this.socket = socket;
		}


		public void run()
		{
			try 
			{
				fromClient = new DataInputStream(socket.getInputStream());
				outToClient = new DataOutputStream(socket.getOutputStream());
			} 
			catch (IOException e) 
			{
				return;
			}

	
			boolean conversationActive = true;
			
			while (conversationActive) 
			{	
				try 
				{
					String input = (String) fromClient.readUTF();
					String toClient = "";
					if(input.equals("GetConnectivityList")) 
					{
						if(connectivityList.isEmpty())
						{
							outToClient.writeUTF("empty");
						}
						
						
						else {
							for (Patient p : connectivityList)
							{
								toClient += p.toString() + "/";
							}
							System.out.println("Sending the connectivity list...");
							outToClient.writeUTF(toClient);
						}
					}
				}
				catch (IOException ioe) 
				{
					ioe.printStackTrace();
					conversationActive = false;
				}
			}
			try
			{
				System.out.println("closing socket");
				fromClient.close();
				outToClient.close();
				socket.close();
			} 
			catch (IOException e)
			{
			}
		}

	}

}
