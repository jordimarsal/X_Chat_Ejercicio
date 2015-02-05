package chatservidor;

import java.util.ArrayList;

public class DataPuff {

	private static ArrayList<User> users;
	private static DataPuff puff;
	private static boolean flag;

	private DataPuff() {
		users = new ArrayList<User>();
		flag = false;
	}

	/**
	 * Patrón Singleton
	 * 
	 * @return única instancia
	 */
	public static DataPuff getInstance() {
		if (puff == null) {
			puff = new DataPuff();
		}
		return puff;
	}

	public void addUser(User u) {
		users.add(u);
	}

	public void removeUser(User u) {
		users.remove(u);
	}

	public String[] getListNickUsers() {
		String[] ret = new String[users.size()];
		int c = 0;
		for (User i : users) {
			ret[c] = i.getClave() + i.getNick();
			c++;
		}
		return ret;
	}

	public void sendMessage(String mess, User u) {
		insertarMensaje(mess, u);
	}

	public String[] receptionMessagesArray(User u) {
		return u.getArrayMessages();
	}

	private void insertarMensaje(String mess, User u) {
		boolean isBR = false;
		System.out.println("# insertarMensaje-");
		for (User i : users) {
			System.out.print("Active user:" + i.getNick() + " - ");
			if (i != u && i != null) {
				i.addMessageToPool(mess);
				System.out.println("insertarMensaje[" + mess + "/user:" + u.getNick() + "]");
				flag = true;
				isBR = true;
			}
			if (!isBR) System.out.println("");
			isBR = false;
		}
		System.out.println("# END insertarMensaje-");
	}

	public void clearMessages(User u) {
		for (User i : users) {
			if (i == u && i != null) {
				i.deleteMessages();
				flag = false;
			}
		}
	}

	public boolean checkFlag() {
		return flag;
	}
}
