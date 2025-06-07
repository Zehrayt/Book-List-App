package main;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException; // DÜZELTME 1: Eksik import eklendi.

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JPasswordField;

import util.User;
import db.DBConnection;
// MainPage importu zaten var, kontrol edelim.
import main.MainPage; 

public class LogIn extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField nametxt;
	private JTextField surnametxt;
	private JTextField userSignUpTextField; 
	private JPasswordField passSignUpField; 
	private JTextField userSignInTextField; 
	private JPasswordField passSignInField; 
	
	DBConnection db = new DBConnection();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LogIn frame = new LogIn();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public LogIn() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 225, 221);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		// --- KAYIT OLMA (SIGN UP) BÖLÜMÜ ---
		JLabel userNamelbl = new JLabel("Name");
		userNamelbl.setBounds(231, 23, 45, 13);
		contentPane.add(userNamelbl);
		
		nametxt = new JTextField();
		nametxt.setBounds(317, 20, 96, 19);
		contentPane.add(nametxt);
		nametxt.setColumns(10);
		
		JLabel userSurnameLabel = new JLabel("Surname");
		userSurnameLabel.setBounds(231, 46, 64, 13);
		contentPane.add(userSurnameLabel);
		
		surnametxt = new JTextField();
		surnametxt.setBounds(317, 43, 96, 19);
		contentPane.add(surnametxt);
		surnametxt.setColumns(10);
		
		JLabel userNameloglbl = new JLabel("User Name");
		userNameloglbl.setBounds(231, 78, 66, 13);
		contentPane.add(userNameloglbl);
		
		userSignUpTextField = new JTextField();
		userSignUpTextField.setBounds(317, 72, 96, 19);
		contentPane.add(userSignUpTextField);
		userSignUpTextField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Password");
		lblNewLabel.setBounds(231, 104, 64, 13);
		contentPane.add(lblNewLabel);
		
		passSignUpField = new JPasswordField();
		passSignUpField.setBounds(317, 101, 96, 19);
		contentPane.add(passSignUpField);
		
		JLabel userAgelbl = new JLabel("Age");
		userAgelbl.setBounds(231, 141, 45, 17);
		contentPane.add(userAgelbl);
		
		JComboBox<Integer> agecbx = new JComboBox<>();
		agecbx.setBounds(286, 141, 51, 21);
		for (int i = 10; i < 66; i++) {
			agecbx.addItem(i);
		}
		contentPane.add(agecbx);
		
		JButton savebtn = new JButton("Save");
		savebtn.setBounds(347, 141, 66, 21);
		contentPane.add(savebtn);
		
		
		
		JLabel usernameSignInlbl = new JLabel("User Name");
		usernameSignInlbl.setBounds(27, 32, 66, 13);
		contentPane.add(usernameSignInlbl);
		
		userSignInTextField = new JTextField();
		userSignInTextField.setBounds(100, 32, 96, 19);
		contentPane.add(userSignInTextField);
		userSignInTextField.setColumns(10);
		
		JLabel passwordSigninLbl = new JLabel("Password");
		passwordSigninLbl.setBounds(28, 81, 65, 13);
		contentPane.add(passwordSigninLbl);
		
		passSignInField = new JPasswordField();
		passSignInField.setBounds(100, 78, 96, 19);
		contentPane.add(passSignInField);

		JButton signInBtn = new JButton("SignIn");
		signInBtn.setBounds(27, 141, 80, 21);
		contentPane.add(signInBtn);
		
		JButton signupSwitchBtn = new JButton("SignUp");
		signupSwitchBtn.setBounds(114, 141, 80, 21);
		contentPane.add(signupSwitchBtn);
		
		
		
		signupSwitchBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setBounds(100, 100, 455, 221);
			}
		});
		
		savebtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(nametxt.getText().isEmpty() || surnametxt.getText().isEmpty() || 
						userSignUpTextField.getText().isEmpty() || new String(passSignUpField.getPassword()).isEmpty()) {
					JOptionPane.showMessageDialog(contentPane, "Complete all the fields!");
				} else {
					User us = new User();
					us.setName(nametxt.getText());
					us.setSurname(surnametxt.getText());
					us.setUsername(userSignUpTextField.getText());
					us.setPassword(new String(passSignUpField.getPassword())); 
					us.setAge((Integer) agecbx.getSelectedItem());
					try {
						db.saveUser(us);
						JOptionPane.showMessageDialog(contentPane, "Saved! You can now log in.");
						nametxt.setText("");
						surnametxt.setText("");
						userSignUpTextField.setText("");
						passSignUpField.setText("");
						setBounds(100, 100, 225, 221);
					} catch (Exception e2) {
						e2.printStackTrace();
						JOptionPane.showMessageDialog(contentPane, "Error during registration.");
					}
				}
			}
		});
		
		
		signInBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String username = userSignInTextField.getText();
				String password = new String(passSignInField.getPassword());
				
				if (username.isEmpty() || password.isEmpty()) {
					JOptionPane.showMessageDialog(contentPane, "Please enter username and password.");
					return;
				}
				
				try {
					
					User loggedInUser = db.getUserByUsernameAndPassword(username, password);
					
					if (loggedInUser != null) {
						MainPage mp = new MainPage(loggedInUser);
						 
						mp.setVisible(true);
						dispose(); 
					} else {
						JOptionPane.showMessageDialog(contentPane, "Login Failed! Invalid username or password.");
						userSignInTextField.setText("");
						passSignInField.setText("");
					}
				} catch (SQLException e3) { 
					e3.printStackTrace();
					JOptionPane.showMessageDialog(contentPane, "A database error occurred.");
				}
			}
		});
	}
}