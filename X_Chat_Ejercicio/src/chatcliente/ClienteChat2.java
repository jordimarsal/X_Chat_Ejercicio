package chatcliente;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClienteChat2 extends JFrame implements ActionListener {

	// constantes
	private static final long serialVersionUID = -2797432157450644864L;
	protected final static String NL = System.getProperty("line.separator");
	private static final String HOLA = "HOLA";
	private static final String ACK = "ACK_";
	private static final int MAX_USERS = 10;

	// Jframe y parte visual
	public static JTextField campoTexto; // Para mostrar mensajes de los usuarios
	public static JTextArea areaTexto; // Para ingresar mensaje a enviar
	private JPanel rootPanel, northPanel, southPanel;
	private JButton btnEnviar;

	// campos privados (recibidos del parser)
	private static String ip;// = "127.0.0.1"; // ip a la cual se conecta
	private static Integer puerto;
	private static String nick;

	// campos privados
	private static boolean isConnected;
	private static String[] users; // almacena el sharp(id de sistema) y el nick
	private static Integer usuariosActivos;
	static PrintWriter out; // para procesar envios de texto desde otros métodos lo hacemos campo
	private static Integer thisUser = 0; // se recibe el numero con el HELO (solo admite un digito, por tanto de 0 a 9,
											// aunque 0 es el servidor)

	/**
	 * CONSTRUCTOR
	 */
	public ClienteChat2() {
		super("Chat - " + nick + " <" + ip + ":" + puerto + ">"); // Establece titulo al Frame
		usuariosActivos = 1;// 0: servidor, 1: este, ++ se irán añadiendo
		users = new String[MAX_USERS];
		users[0] = "#";// servidor
		users[1] = nick;

		this.getContentPane().setLayout(new BorderLayout());
		this.setResizable(false);

		rootPanel = new JPanel(new GridBagLayout());
		// http://www.chuidiang.com/java/layout/GridBagLayout/GridBagLayout.php
		GridBagConstraints constraints = new GridBagConstraints();
		GridBagConstraints constraints2 = new GridBagConstraints();
		constraints.gridx = 0; // El área de texto empieza en la columna cero.
		constraints.gridy = 0; // El área de texto empieza en la fila cero
		constraints.gridwidth = 3; // El área de texto ocupa 3 columnas.
		constraints.gridheight = 2; // El área de texto ocupa 2 filas.
		constraints.weighty = 1.0;
		constraints.fill = GridBagConstraints.BOTH;

		constraints2.gridx = 0;
		constraints2.gridy = 2;
		constraints2.gridwidth = 3;
		constraints2.gridheight = 1;
		constraints2.weighty = 0.0;
		constraints2.fill = GridBagConstraints.BASELINE_LEADING;

		northPanel = new JPanel();

		southPanel = new JPanel(new FlowLayout());
		// southPanel.setBackground(Color.green);

		areaTexto = new JTextArea(); // Crear displayArea
		areaTexto.setEditable(false);
		areaTexto.setForeground(Color.BLACK);

		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.PAGE_AXIS));
		northPanel.add(new JScrollPane(areaTexto));

		campoTexto = new JTextField(30); // crea el campo para texto
		campoTexto.setForeground(Color.BLACK);
		EnviarListener listener = new EnviarListener(this, campoTexto, areaTexto, nick);
		campoTexto.addActionListener(listener);

		southPanel.add(campoTexto); // Coloca el campo de texto

		btnEnviar = new JButton("Enviar");
		btnEnviar.setMnemonic(69);
		btnEnviar.addActionListener(listener);

		southPanel.add(btnEnviar);

		rootPanel.add(northPanel, constraints);
		rootPanel.add(southPanel, constraints2);

		this.add(rootPanel);

		setSize(450, 420); // Establecer tamaño a ventana
		setVisible(true); // Pone visible la ventana

		// http://stackoverflow.com/questions/6723257/how-to-set-focus-on-jtextfield
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				campoTexto.requestFocus();
			}
		});
	}

	/**
	 * MAIN
	 * 
	 * @param args
	 *            : obligatorios 3 parametros
	 * 
	 *            <pre>
	 *            -h: Dirección IP del Servidor -h:127.0.0.1
	 *            -p: Puerto del Servidor       -p:1234
	 *            -n: Nick del usuario          -n:\"Don Pepito\" ó -n:Paquito
	 * </pre>
	 */
	public static void main(String[] args) {
		ParseChatArgs argsCli = new ParseChatArgs(args);
		String arg2 = "-h:127.0.0.1 -p:1234 -n:\"Don José\"";
		ip = argsCli.getIp();
		puerto = argsCli.getPuerto();
		nick = argsCli.getNick();
		ClienteChat2 main = new ClienteChat2(); // Instanciacion de la clase ClienteChat
		main.setLocationRelativeTo(null); // Centrar el JFrame
		// main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // habilita cerrar la ventana
		// http://lineadecodigo.com/tag/addwindowlistener/
		main.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				enviarAdios();
				System.exit(0);
			}
		});

		try {
			main.mostrarMensaje("Conectando con el servidor", 0);
			Socket clientSocket = new Socket(ip, puerto);
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			System.out.println("Cliente (" + nick + ") envia: " + HOLA + " " + nick);
			out.println(HOLA + " " + nick);

			String fromServer;

			while ((fromServer = in.readLine()) != null) {

				System.out.println("Cliente recibe: " + fromServer);
				if (fromServer.equals("NACK")) {
					System.out.println("NACK: FIN CLIENTE");
					break;
				}

				isConnected = procesoEntradas(fromServer);
				/*
								if (isConnected ) {
									System.out.println("Client: " + fromUser);
									out.println(fromUser);
								}*/
				if (!isConnected) {
					System.out.println("FIN CLIENTE, NACK");
					break;
				}
				// previousFromServer = fromServer;
			}

			out.close();
			in.close();
			clientSocket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Para mostrar texto en displayArea
	private static void mostrarMensaje(String mensaje, Integer user) {
		String prompt = "> ";
		if (user == 0) prompt = "";
		System.out.println("mostrarMensaje:" + mensaje);
		areaTexto.append(users[user] + prompt + mensaje + "\n");
	}

	private static boolean procesoEntradas(String input) {
		boolean isOK = false, isConnex = true;
		String s = "";
		String pre = input.substring(0, 4);
		String pos = input.substring(5, input.length());
		Integer user = 0;

		switch (pre) {
			case "ACK_":
				s = input.substring(4, 5);
				user = Integer.parseInt(s.substring(0, 1));
				if (user != thisUser) {
					s = pos;
					isOK = true;
				}
				break;
			case "HELO":
				// System.out.println(input + " : " + input.length());
				s = input.substring(4, 5);
				System.out.println("HELO s:#" + s + "#");
				thisUser = Integer.parseInt(s.substring(0, 1));
				s = "Conectado";
				user = 0;
				isOK = true;
				break;
			case "NACK":
				isOK = false;
				isConnex = false;
				break;
			case "USRA":// add user
				s = input.substring(4, 5);
				user = Integer.parseInt(s.substring(0, 1)); // Solo admitiría 9 clientes + servidor (solo es un digito)
				// System.out.println("USRA s:#" + s + "#" + pos + "#");
				if (user != thisUser) {
					users[user] = pos;
					s = "Se ha conectado: " + pos;
					isOK = true;
					user = 0; // para prompt del server informando conexion
				}
				break;
			case "USRX":// disconnect user
				s = input.substring(4, 5);
				user = Integer.parseInt(s.substring(0, 1));
				users[user] = "";
				s = "Se ha desconectado: " + pos;
				user = 0;
				isOK = true;
				break;
		}
		if (isOK) mostrarMensaje(s, user); // user 0 es el servidor, user 1 es thisUser, ++ segun se van introduciendo
											// en users, para mostrar el hash
		return isConnex;
	}

	public void procesarEnvio(String string) {
		System.out.println("Cliente[" + nick + "/" + ClienteChat2.thisUser + "]:" + ACK + thisUser + string);
		out.println(ACK + thisUser + string);
	}

	public static void enviarAdios() {
		System.out.println("ClienteDespedida[" + nick + "/" + ClienteChat2.thisUser + "]:" + "BYE_" + thisUser + nick);
		out.println("BYE_" + thisUser + nick);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {// hay un EnviarListener.java
	}
}
