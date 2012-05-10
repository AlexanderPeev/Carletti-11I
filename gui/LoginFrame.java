package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.security.auth.login.LoginException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import service.Service;

public class LoginFrame extends JFrame {
	private JTextField txfUser;
	private JPasswordField pwfPassword;
	private JButton btnLogin;
	private Controller controller = new Controller();

	public LoginFrame() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Login");
		this.setResizable(false);
		this.setSize(200, 150);
		this.setLocationRelativeTo(this.getRootPane());
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		JLabel lblUser = new JLabel("User:");
		this.add(lblUser);

		this.txfUser = new JTextField();
		this.txfUser.addKeyListener(controller);
		lblUser.setLabelFor(this.txfUser);
		this.add(this.txfUser);

		JLabel lblPassword = new JLabel("Password:");
		this.add(lblPassword);

		this.pwfPassword = new JPasswordField();
		this.pwfPassword.addKeyListener(controller);
		lblPassword.setLabelFor(this.pwfPassword);
		this.add(this.pwfPassword);

		JPanel pnlButtons = new JPanel();
		this.add(pnlButtons);

		this.btnLogin = new JButton("Login");
		this.btnLogin.addActionListener(controller);
		pnlButtons.add(this.btnLogin);

	}

	private class Controller implements ActionListener, KeyListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String username = txfUser.getText().trim(), password = new String(
					pwfPassword.getPassword()).trim();
			if (username.length() < 1) {
				JOptionPane.showMessageDialog(LoginFrame.this,
						"The user field must not be empty. ", "Input error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			else if (password.length() < 1) {
				JOptionPane.showMessageDialog(LoginFrame.this,
						"The password field must not be empty. ",
						"Input error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			try {
				Service.login(username, password);
				LoginFrame.this
						.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				MainFrame frame = new MainFrame();
				frame.setVisible(true);
				LoginFrame.this.setVisible(false);
			}
			catch (LoginException ex) {
				JOptionPane.showMessageDialog(LoginFrame.this, ex.getMessage(),
						"Login error", JOptionPane.ERROR_MESSAGE);
			}
		}

		@Override
		public void keyPressed(KeyEvent e) {
			// unused

		}

		@Override
		public void keyReleased(KeyEvent e) {
			// unused

		}

		@Override
		public void keyTyped(KeyEvent e) {
			if (e.getKeyChar() == '\n') {
				LoginFrame.this.btnLogin.doClick();
			}
		}

	}
}
