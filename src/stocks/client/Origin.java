package stocks.client;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Origin extends JFrame {

	private static final long serialVersionUID = 1L;
	private Epsilon e;
	public static final String title = "Bourstad Simulation";

	public Origin() {
		super(title);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(800,600));
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		e = new Epsilon();
		add(e);
		
		pack();
		setLocationRelativeTo(null);
		new Login(Origin.this);
	}
	
	public Epsilon get() {
		return e;
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			new Origin();
		});
	}

}
