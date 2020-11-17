import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.awt.print.PrinterException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	public static Connection conn;
	private ArrayList<Patient> connectivityList;
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
			System.err.println("ServerSocket instantiation failure");
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
				System.err.println("server accept failed");
			}
		}
	} 

	public static void main(String args[]) throws ClassNotFoundException 
	{
		ArrayList<Patient> connectivityList = new ArrayList<Patient>();
		connectivityList.add(createPatient("Sara", "213465", "m1"));
		connectivityList.add(createPatient("Joe", "124365", "m2"));
		connectivityList.add(createPatient("Khalil", "123465", "m3"));
		Server server = null;
		// Instantiate an object of this class. This will load the JDBC database driver
		server = new Server();
		// call this function, which will start it all...
		System.out.println("Waiting for clients ...");
		server.acceptConnections();
	}
	
	public static Patient createPatient(String name, String phoneNumber, String macAddress) 
	{
		return new Patient(name, phoneNumber, macAddress);
	}
	
	

	class ServerThread implements Runnable 
	{
		private Socket socket;
		private DataInputStream datain;
		private OutputStream outToClient;

		public ServerThread(Socket socket) 
		{
			// Inside the constructor: store the passed object in the data member
			this.socket = socket;
		}

		////////////////////////////////////
		// This is where you place the code you want to run in a thread
		// Every instance of a ServerThread will handle one client (TCP connection)
		public void run()
		{
			try 
			{
				// Input and output streams, obtained from the member socket object
				datain = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
				outToClient = socket.getOutputStream();
			} 
			catch (IOException e) 
			{
				return;
			}

			byte[] ba = new byte[256];
			boolean conversationActive = true;
			String fromClient;
			while (conversationActive) 
			{	
				try 
				{
					// read from the input stream buffer (read a message from client)
					datain.read(ba, 0, 256);
					fromClient = new String(ba);

					if (fromClient.equals("GetConnectivityList")) 
					{
						PrintWriter p = new PrintWriter(outToClient, true);
						for (Patient pat : connectivityList)
						{
							p.println(pat.toString());
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
				datain.close();
				outToClient.close();
				// When the server receives a "Q", we arrive here
				socket.close();
			} 
			catch (IOException e)
			{
			}
		}

	}

}