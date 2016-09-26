package stocks.client;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;

public abstract class LoginDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;
	protected JTextField textField;
	protected JPasswordField passwordField;
	private boolean initialized = false;
	private String title = "Login", errorMsg = "Login failed.";
	private JButton bLogin;
	protected JTextField keyField;
	
	public LoginDialog(Frame parent, String title) {
		super(parent, true);
		this.setTitle(title);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setPreferredSize(new Dimension(400,350));
		this.title = title;
		
		JLabel lTitle = new JLabel(title);
		lTitle.setFont(new Font("Segoe UI Light", Font.PLAIN, 24));
		lTitle.setHorizontalAlignment(SwingConstants.CENTER);
		JPanel panel = new JPanel();
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(panel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE)
						.addComponent(lTitle, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lTitle, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(panel, GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
					.addContainerGap())
		);
		textField = new JTextField();
		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onLogin();
			}
		});
		textField.setColumns(10);
		passwordField = new JPasswordField();
		passwordField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onLogin();
			}
		});

		bLogin = new JButton("Login / Register");
		bLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onLogin();
			}
		});
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setFocusable(false);
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});
		JLabel lblUsername = new JLabel("Username");
		lblUsername.setFont(new Font("Segoe UI Light", Font.PLAIN, 16));
		
		JLabel lblNewLabel = new JLabel("Password");
		lblNewLabel.setFont(new Font("Segoe UI Light", Font.PLAIN, 16));
		
		JLabel lblKey = new JLabel("Key");
		lblKey.setFont(new Font("Segoe UI Light", Font.PLAIN, 16));
		
		keyField = new JTextField();
		keyField.addActionListener(e -> {onLogin();});
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(bLogin, GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
							.addGap(22)
							.addComponent(btnCancel, GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE))
						.addGroup(gl_panel.createSequentialGroup()
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel.createSequentialGroup()
									.addComponent(lblUsername)
									.addPreferredGap(ComponentPlacement.RELATED))
								.addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
									.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
										.addComponent(lblKey)
										.addComponent(lblNewLabel))
									.addPreferredGap(ComponentPlacement.RELATED)))
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(passwordField, GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
								.addComponent(textField, GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
								.addComponent(keyField, GroupLayout.PREFERRED_SIZE, 273, GroupLayout.PREFERRED_SIZE))))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(textField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblUsername))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(passwordField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(keyField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblKey))
					.addPreferredGap(ComponentPlacement.RELATED, 135, Short.MAX_VALUE)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(bLogin, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnCancel, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		panel.setLayout(gl_panel);
		getContentPane().setLayout(groupLayout);

		onCreate();
		
		addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				if (!isLoggedIn()) {
					onCancel();
				}
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		this.pack();
		this.setLocationRelativeTo(this);
		this.setVisible(true);
	}

	public String getTitle() {
		return title;
	}
	
	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	protected void onLogin() {
		new Thread(() -> {
			textField.setEnabled(false);
			passwordField.setEnabled(false);
			keyField.setEnabled(false);
			bLogin.setEnabled(false);
			if (!initialized) {
				synchronized (this) {
					while (!initialized) {
						try {
							this.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
			login();
			if (isLoggedIn()) {
				getOwner().setVisible(true);
				LoginDialog.this.dispose();
			} else {
				JOptionPane.showMessageDialog(getOwner(), errorMsg, "Login Error", JOptionPane.ERROR_MESSAGE);
			}
			textField.setEnabled(true);
			passwordField.setEnabled(true);
			bLogin.setEnabled(true);
			bLogin.requestFocusInWindow();
		}).start();
	}
	
	private void onCreate() {
		new Thread(() -> {
			initialize();
			initialized = true;
			synchronized (this) {
				this.notifyAll();
			}
		}).start();
	}
	
	protected abstract void initialize();

	protected abstract void login();
	
	protected abstract boolean isLoggedIn();

	protected void onCancel() {
		this.dispose();
		System.exit(0);
	}
}
