package spidergui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import spidercore.SpiderMain;

public class SpiderFrame extends JFrame {

	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -8555975295026408613L;	
	private static final int WIDTH = 300;
	private static final int HEIGHT = 200;
	
	private static JTextPane textoStatus = new JTextPane();
	
	public SpiderFrame () {
		initGUI();
	}
	
	private void initGUI () {
		
		Dimension dimensiones = new Dimension(WIDTH, HEIGHT);
		
		setTitle("Spider Search");
		setSize(dimensiones);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new GridBagLayout());
		
		GridBagConstraints constraints = new GridBagConstraints();
		
		JPanel panelStatus = new JPanel();
		add(panelStatus);
		
		textoStatus.setText("");
		panelStatus.add(textoStatus);
	}
	
	public static void main (String[] args) {
		EventQueue.invokeLater( new Runnable() {
			public void run () {
				SpiderFrame mainFrame = new SpiderFrame();
				mainFrame.setVisible(true);
			}
        } );
		
		SpiderMain.execute(textoStatus);
	}

}
