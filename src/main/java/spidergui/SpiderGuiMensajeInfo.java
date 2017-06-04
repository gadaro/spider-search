package spidergui;

import javax.swing.JOptionPane;

public class SpiderGuiMensajeInfo extends JOptionPane {

	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -4654580597549474565L;

	public SpiderGuiMensajeInfo(String mensaje) {
		showMessageDialog(null, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
	}
	
}
