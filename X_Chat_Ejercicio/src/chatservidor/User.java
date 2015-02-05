package chatservidor;

import java.util.ArrayList;

public class User {

	protected final static String NL = System.getProperty("line.separator");

	private Integer clave;
	private String nick;
	private ArrayList<String> poolMessages;

	public User(Integer clave, String nick) {
		this.clave = clave;
		this.nick = nick;
		poolMessages = new ArrayList<String>();
	}

	public User() {
		poolMessages = new ArrayList<String>();
	}

	public Integer getClave() {
		return clave;
	}

	public void setClave(Integer clave) {
		this.clave = clave;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nombre) {
		this.nick = nombre;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(clave).append(nick);
		return sb.toString();
	}

	public void addMessageToPool(String mess) {
		poolMessages.add(mess);
	}

	public String[] getArrayMessages() {
		System.out.println("# getArrayMessages-");
		String[] ret = null;
		int n = poolMessages.size();
		if (n == 0) {
			n = 1;
			System.out.println("# ENDgetArrayMessages - null");
			return ret;// = new String[n];
		} else {
			ret = new String[n];
			int c = 0;
			for (String m : poolMessages) {
				System.out.println("ret:#" + m + "#");
				ret[c] = m;
				c++;
			}
		}
		// return poolMessages.toArray();
		System.out.println("# ENDgetArrayMessages - any");
		return ret;
	}

	public void deleteMessages() {
		poolMessages.clear();
	}
}
