package zelda;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
	private final Socket clientSocket;
	private final int id;
	
	public Server(final Socket socket, final int id) {
		System.out.println("Created server with id " + id);
		clientSocket = socket;
		this.id = id;
	}
	
	public static void main(final String[] args) {
		final int port = 19999;
		int count = 0;
		try (final ServerSocket serverSocket = new ServerSocket(port)) {
			while (true) {
				final Socket clientSocket = serverSocket.accept();
				final Runnable runnable = new Server(clientSocket, ++count);
				Thread thread = new Thread(runnable);
				thread.start();
				System.out.println("Thread for client started");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try (
			final Socket socket = this.clientSocket;
			final PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		) {
			System.out.println("Server listening requests from " + id);
			String inputLine;
			String outputLine;
			while ((inputLine = in.readLine()) != null) {
				outputLine = id + ": Response to " + inputLine;
				out.println(outputLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Bye bye " + id);
	}
}
