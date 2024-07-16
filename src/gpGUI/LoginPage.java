package gpGUI;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;


public class LoginPage implements ActionListener { // LoginPage class

    // JFrame
    JFrame frame = new JFrame();

    // JLabels
    JLabel LoginTitleLabel = new JLabel("Login");
    JLabel usernameLabel = new JLabel("Username:");
    JLabel PasswordLabel = new JLabel("Password:");

    // JTextField and JPasswordField
    public JTextField usernameField = new JTextField();
    public JPasswordField PasswordField = new JPasswordField();

    // JButton
    public JButton loginButton = new JButton("Login");

    // Message JLabel
    public JLabel messageLabel = new JLabel("");

    

    public LoginPage() { // Constructor

        // Labels
        usernameLabel.setBounds(51,161,62,15);
        usernameLabel.setFont(new Font(null, Font.PLAIN,12));

        PasswordLabel.setBounds(51,251,59,15);
        PasswordLabel.setFont(new Font(null, Font.PLAIN,12));

        messageLabel.setBounds(232,323,250,35);
        messageLabel.setFont(new Font(null, Font.ITALIC,25));

        LoginTitleLabel.setBounds(232,54,137,42);
        LoginTitleLabel.setFont(new Font(null, Font.BOLD,35));

        // Text fields boxes
        usernameField.setBounds(51,176,498,34);
        PasswordField.setBounds(51,266,498,34);

        // Buttons
        loginButton.setBounds(223,413,155,35);
        loginButton.setFocusable(false);
        loginButton.addActionListener(this);


        frame.add(usernameLabel);
        frame.add(PasswordLabel);
        frame.add(messageLabel);
        frame.add(usernameField);
        frame.add(PasswordField);
        frame.add(loginButton);
        frame.add(LoginTitleLabel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600,600);
        frame.setLayout(null);
        frame.setVisible(true);
        }

        /**
         * Performs the necessary actions when an action event occurs.
         * This method is called when the login button is clicked.
         *
         * @param e the action event that occurred
         */
        @Override
        public void actionPerformed(ActionEvent e) { // action listener
        if (e.getSource() == loginButton) { // if the login button is clicked
            String username = usernameField.getText();
            String password = String.valueOf(PasswordField.getPassword());
            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultSet = null;

            try { // try to connect to the database
            connection = ConnectionDB.getConnection();
            String query = "SELECT * FROM users WHERE Username = ? AND Password = ?"; // prepared statements to prevent SQL injection
            statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);

            resultSet = statement.executeQuery();
            if (resultSet.next()) { // if the username and password are correct
                messageLabel.setForeground(Color.green);
                int userID = resultSet.getInt("UserID");
                messageLabel.setText("Login successful");
                System.out.println("Login successful");

                frame.dispose();
                new LandingPage(userID);

            } else { // if the username and password are incorrect
                messageLabel.setForeground(Color.red);
                messageLabel.setText("Invalid username or password");
            }
            } catch (SQLException throwables) {
            throwables.printStackTrace();
            messageLabel.setForeground(Color.red);
            messageLabel.setText("Database error");
            } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                ConnectionDB.closeConnection(connection);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            }
        }
        }
    }
