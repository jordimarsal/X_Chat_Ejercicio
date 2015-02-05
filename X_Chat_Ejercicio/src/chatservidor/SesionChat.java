package chatservidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SesionChat extends Thread {
	protected final static String NL = System.getProperty("line.separator");
	private Socket clientSocket = null;
	private int numUser;
	private boolean isActive = true;
	private static int recepciones = 0;
	private String[] recepMessages;
	PrintWriter out;

	User us = null;
	DataPuff pool;

	public SesionChat(Socket socket, int conexActivas, DataPuff pool) {
		clientSocket = socket;
		numUser = conexActivas;
		this.pool = pool;
	}

	@Override
	public void run() {
		String outputLine = "";
		String inputLine = "";
		try {
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			while (isActive) {

				if (pool.checkFlag()) {
					System.out.println("-FLAG=TRUE-");
					if (checkMessages()) {
						sendRecepMessages();
					}
				}
				if (in.ready()) {
					inputLine = in.readLine();
				}
				if (inputLine != null) {

					System.out.println("SESION " + numUser + " Recibe(" + recepciones++ + "): " + inputLine);

					try {
						Thread.sleep(100);
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}

					if (in.ready()) {
						inputLine = in.readLine();
					}
					outputLine = procesarInput(inputLine);
					inputLine = null;
				}
			}
			System.out.println("SESION CERRADA");
			out.close();
			in.close();
			clientSocket.close();

		} catch (IOException ex) {
		}
	}

	private void sendRecepMessages() {
		System.out.println("    # sendRecepMessages");
		for (String stringOutputLine : recepMessages) {
			System.out.println("       SESION " + numUser + ": checkMessages:#" + stringOutputLine + "#");
			if (stringOutputLine != null) {
				out.println(stringOutputLine);
			}
		}
		System.out.println("    # ENDsendRecepMessages");
	}

	private void directMessage(String outputLine) {
		System.out.println("  directMessage SESION " + numUser + ": " + outputLine);
		out.println(outputLine);

	}

	private void broadcast(String outputLine) {
		System.out.println("### broadcast");
		if (checkMessages()) {
			sendRecepMessages();
		}
		System.out.println("    broadcast SESION " + numUser + ": " + outputLine);
		pool.sendMessage(outputLine, us);
		System.out.println("### ENDbroadcast");
	}

	private String procesarInput(String input) {
		String outputLine = "";
		System.out.println("# procesarInput input:<" + input + ">");
		String pre = input.substring(0, 4);
		String pos = input.substring(5, input.length());

		switch (pre) {
			case "HOLA":
				us = new User(numUser, pos);
				pool.addUser(us);
				outputLine = "HELO" + numUser + pos;
				directMessage(outputLine);
				informPreviousUsers();
				broadcast("USRA" + us);
				break;
			case "ACK_":
				outputLine = input;
				broadcast(outputLine);
				break;
			case "BYE_":
				outputLine = "USRX" + numUser + pos;
				broadcast(outputLine);
				pool.removeUser(us);
				isActive = false;
				break;
		}
		return outputLine;
	}

	private void informPreviousUsers() {
		for (String st : pool.getListNickUsers()) {
			System.out.print("informPreviousUsers: ");
			directMessage("USRA" + st);
		}
	}

	private boolean checkMessages() {
		if (us != null) {
			recepMessages = pool.receptionMessagesArray(us);
			pool.clearMessages(us);
		}
		if (recepMessages == null) {
			System.out.println("    checkMessages==null");
			return false;
		}
		System.out.println("    checkMessages==true");
		return true;
	}
}
