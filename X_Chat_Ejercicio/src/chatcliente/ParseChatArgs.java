package chatcliente;

public class ParseChatArgs {

	protected final static String NL = System.getProperty("line.separator");
	private String ip;
	private Integer puerto;
	private String nick;

	public ParseChatArgs(String[] args) {
		// System.out.println("num args: " + args.length);
		if (args.length == 3) {
			parseArgs(args);
		} else {
			System.out.println("ERROR:" + NL + "Los clientes se deben arrancar con tres argumentos:" + NL
					+ "    -h: Dirección IP del Servidor.  -h:127.0.0.1" + NL
					+ "    -p: Puerto del Servidor.        -p:1234" + NL
					+ "    -n: Nick del usuario.           -n:\"Don Pepito\" ó -n:Paquito");
			return;
		}
	}

	private void parseArgs(String[] args) {
		int c = 0;
		String comando, argumento;
		for (String s : args) {
			c++;
			String[] op = s.split(":");
			comando = op[0];
			argumento = op[1];
			// System.out.println("arg[" + c + "]: " + s);
			asignarArgs(comando, argumento);
		}
	}

	private void asignarArgs(String comando, String argumento) {
		// System.out.println("arg:" + argumento);
		switch (comando) {
			case "-h":
				setIp(argumento);
				break;
			case "-p":
				setPuerto(Integer.parseInt(argumento));
				break;
			case "-n":
				setNick(argumento);
				break;
		}
	}

	public String getIp() {
		return ip;
	}

	private void setIp(String ip) {
		this.ip = ip;
	}

	public int getPuerto() {
		return puerto;
	}

	private void setPuerto(int puerto) {
		this.puerto = puerto;
	}

	public String getNick() {
		return nick;
	}

	private void setNick(String nick) {
		this.nick = nick;
	}

}
