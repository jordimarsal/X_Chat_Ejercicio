package chatservidor;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
	ServerSocket server = null;
	protected static int puerto;
	private static int conexActivas = 1;
	DataPuff pool;

	public Servidor(Integer puerto) throws BindException {
		Servidor.puerto = puerto;
		pool = DataPuff.getInstance();
	}

	public static void main(String args[]) {
		Servidor ws;
		try {
			ws = new Servidor(1234);
			System.out.println("- - Servidor arriba " + puerto + " - -");
			ws.start();
		} catch (BindException e) {
			// puedes terminar el programa, porque no puedes escuchar en el puerto
			e.printStackTrace();
		}
	}

	private void start() {
		try {
			server = new ServerSocket(puerto);
		} catch (IOException ex) {
			System.exit(1);
		}
		while (true) {
			try {
				Socket socket = server.accept();
				System.out.println("Conexion #" + conexActivas + " aceptada");
				SesionChat ses = new SesionChat(socket, conexActivas, pool);
				conexActivas++;
				new Thread(ses).start();
			} catch (IOException ex) {
				// Maneja la excepcion
				ex.printStackTrace();
			}
		}

	}

	public DataPuff getPool() {
		return pool;
	}

}
