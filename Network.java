import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

public class Network{
	
	private static boolean server;						//if this boolean is true then it will run in server mode
	public static Socket clientSocket = null; 			//holds the socket if it is a client
	private static ServerSocket serverSocketM = null;	//same stuff as above except for server mode
	public static Socket serverSocketC = null;			//server sockets are weird
	public static BufferedReader input = null;			//reads from socket
	private static PrintWriter out = null;				//outputs to socket
	private static int magicNumber;						//random number to generate
	public Boolean donePlacing;							//is the player done placing
	public static boolean alive = true;					//are you still breathing?
	private static boolean verbose = false;				//verbose, for debugging
	
	// constructor and things
	public Network(boolean serv, boolean ver) throws IOException {
		donePlacing = false;
		verbose = ver;									// sets verbose
		server = serv;									// passed if this is the server or not
		init();											// starts the network
		verboseOut("Done.");
	}

	// initializes the connections depending if it is the server or not
	private static void init() throws IOException {
		if (server) {										//if it's in server mode
			verboseOut("Server");
			magicNumber = (int) (Math.random()*2);			// creates the magic
			verboseOut(magicNumber);
			if(magicNumber == 0){
				serverInit(1);					// handles all of the socket crap
			} else {
				serverInit(0);
			}
		} else {											// client setup
			verboseOut("Client");
			magicNumber = clientInit();						// handles all of the socket crap, as
		}
	}

	private static void serverInit(int sentMagic) throws IOException {
		ServerSocket listener = new ServerSocket(420);							// creates server socket listening on port 420 blazin
		verboseOut("Server is open");											// debugging
		JOptionPane.showMessageDialog(null, "Waiting for the client...\nYour IP address is " + Inet4Address.getLocalHost().getHostAddress() + "\n Press 'OK' once the client is ready.",
										"Preparing Battleship", JOptionPane.INFORMATION_MESSAGE);
		Socket socket = listener.accept();										// creates a socket that accepts a connection from the listener
		input = new BufferedReader(new InputStreamReader(socket.getInputStream()));// creates the buffered reader for reading inbound information
		out = new PrintWriter(socket.getOutputStream(), true);					// printwriter for output
		verboseOut("Connection Established.");
		out.println(sentMagic);													// sends connection established message and the magic number
		serverSocketC = socket;													// creates a copy of the socket to the global variable
		serverSocketM = listener;												// same with server socket
	}

	private static int clientInit() throws IOException {
		verboseOut("Client init");
		String serverAddress = JOptionPane.showInputDialog("Enter IP Address of The Host");// prompts user for IP address													
		Socket s = new Socket(serverAddress, 420);											// tries to connect to the server on port 420 blazin
		out = new PrintWriter(s.getOutputStream(), true);									// sets up print writer
		input = new BufferedReader(new InputStreamReader(s.getInputStream()));				// sets up reader
		String answer = input.readLine();													// reads the server response
		verboseOut("connected");										
		clientSocket = s;
		verboseOut(answer);
		return Integer.parseInt(answer);
	}

	// used to test if the server/client can still send
	@SuppressWarnings("unused")
	private static void pingPong() throws IOException {
		String reply = null;
		if (server) {								// different client and server functions
			out.println("ping");					// sends ping
			while (reply == null) {					// waits for a reply
				reply = input.readLine();
			}
			if (reply.contentEquals("pong")) {		// if the reply is good
				System.out.println("test is good");
			} else {
				System.out.println("error");
				dieDieDie();						// boom headshot
			}
		}
		else {										// client version
			while (reply == null) {
				reply = input.readLine();			// waits for message
			}
			if (reply.contentEquals("ping")) {		// if it got the correct message
				out.println("pong");				// send pong
				System.out.println("test is good");
			} else {
				System.out.println("error");
				dieDieDie();
			}
		}
	}

	// used to send the attack messages
	public String attack(String argument) throws IOException {
		out.println(argument);						// sends the raw argument
		String reply = null;
		while (reply == null) {
			try {
				TimeUnit.MILLISECONDS.sleep(250);
				reply = input.readLine();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return reply;								// gives the raw reply

	}

	// this will wait for an attack and return the raw message, the program then
	// uses defendReply to finish the transaction
	public String defend() throws IOException {
		String reply = null;
		while (reply == null) {
			try {
				TimeUnit.MILLISECONDS.sleep(250);
				reply = input.readLine();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return reply;
	}

	public void defendReply(String argument) throws IOException {
		out.println(argument);								// just sends the message it is passed. so don't fuck it up AJ
	}

	// this is used in phase one, the server waits for the client to finish and
	// then will send a reply when it is done.
	public void placeBoats() throws IOException {
		String reply = null;
		if (server) {										// server version
			while (reply == null) {
				try {
					TimeUnit.SECONDS.sleep(1);
					reply = input.readLine();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (reply.contentEquals("PLACED")) {
				while (donePlacing == false) {
					try {
						TimeUnit.MILLISECONDS.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				out.println("CONTINUE");
			} else {
				verboseOut("error");
				dieDieDie();								// boom headshot
			}
		}

		else {												// client version
			while (donePlacing == false) {
				try {
					TimeUnit.MILLISECONDS.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			out.println("PLACED");
			while (reply == null) {
				try {
					reply = input.readLine();
					TimeUnit.MILLISECONDS.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (reply.contentEquals("CONTINUE")) {
				verboseOut("continue to next phase");
			} else {
				verboseOut("error");
				dieDieDie();								// boom headshot
			}
		}
	}

	// closes all of the connections
	public static void exit() throws IOException {
		serverSocketC.close();
		serverSocketM.close();
		clientSocket.close();
	}

	// kills program... boom headshot
	public static void dieDieDie() throws IOException {
		SecureRandom random = new SecureRandom();
		System.out.println("DIE DIE DIE!!!!1");
		out.print("DIE DIE DIE!!!!1");
		System.out.println(new BigInteger(130, random).toString(32) + new BigInteger(130, random).toString(32));
		out.println(new BigInteger(130, random).toString(32) + new BigInteger(130, random).toString(32));
		exit();
		alive = false;
	}

	// returns the magic number
	public int getMagicNumber() {
		return magicNumber;
	}
	
	public boolean getServerBool(){
		return server;
	}
	
	//only outputs if in verbose mode
	private static void verboseOut(String output)
	{
		if(verbose)
		{
			System.out.println(output);
		}
	}
	
	private static void verboseOut(int output)
	{
		if(verbose)
		{
			System.out.println(output);
		}
	}
	
}
