package zelda.tmp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	public static void main(String[] args) {
		final String host = "localhost";
	    final int port = 19999;
	    try (
	    	final Socket socket = new Socket(InetAddress.getByName(host), port);
	    	final PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
    		final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    ) {
	    	System.out.println("Connection established!");
	    	//Scanner sc = new Scanner(System.in);
	    	System.out.println("Kuraa serverille");
	    	out.println("Kuraa serverille");
	    	System.out.println(in.readLine());
	    	System.out.println("GG");
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	}
}
