package chatcliente;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextArea;
import javax.swing.JTextField;

//http://stackoverflow.com/questions/13731710/allowing-the-enter-key-to-press-the-submit-button-as-opposed-to-only-using-mo
public class EnviarListener implements ActionListener, KeyListener {

	JTextField campoTexto;
	JTextArea areaTexto;
	String nick;
	ClienteChat cc;
	ClienteChat2 cc2;

	public EnviarListener(ClienteChat cc, JTextField campoTexto, JTextArea areaTexto, String nick) {
		this.campoTexto = campoTexto;
		this.areaTexto = areaTexto;
		this.nick = nick;
		this.cc = cc;
	}

	public EnviarListener(ClienteChat2 clienteChat2, JTextField campoTexto2, JTextArea areaTexto2, String nick2) {
		this.campoTexto = campoTexto2;
		this.areaTexto = areaTexto2;
		this.nick = nick2;
		this.cc2 = clienteChat2;
	}

	@Override
	public void actionPerformed(ActionEvent submitClicked) {
		if (cc2 == null) {
			enviar();
		} else {
			enviarC2();
		}
	}

	private void enviar() {
		cc.procesarEnvio(campoTexto.getText());
		mostrarMensaje(nick + "> " + campoTexto.getText());
		campoTexto.setText("");
	}

	private void enviarC2() {
		cc2.procesarEnvio(campoTexto.getText());
		mostrarMensaje(nick + "> " + campoTexto.getText());
		campoTexto.setText("");
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			enviar();
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	private void mostrarMensaje(String mensaje) {
		areaTexto.append(mensaje + "\n");
	}

}
