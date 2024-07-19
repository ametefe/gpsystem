package gpGUI;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

import gpDB.ConnectionDB;

public class AddPatient implements ActionListener {
    private JFrame frame = new JFrame();
    //adding heading and back button
    private JLabel heading = new JLabel("Add a New Patient");
    private JButton backButton = new JButton("<");
    // adding name labels and text fields
    private JLabel firstNameLabel = new JLabel("First Name:");
    public JTextField firstNameTextField = new JTextField();
    private JLabel surnameLabel = new JLabel("Surname:");
    public JTextField surnameTextField = new JTextField();
    //adding email labels and text field
    private JLabel emailLabel = new JLabel("Email:");
    public JTextField emailTextField = new JTextField();
    // adding dob label and date picker
    private JLabel dobLabel = new JLabel("D.O.B:");
    public DatePicker dobDatePicker;
    // adding phone label and text field
    private JLabel phoneLabel = new JLabel("Phone Number:");
    public JTextField phoneTextField = new JTextField();
    // adding address label and text field
    private JLabel addressLabel = new JLabel("Address:");
    public JTextField addressTextField = new JTextField();
    // adding assigned doctor label and dropdown
    private JLabel assignedDoctorLabel = new JLabel("Doctor Assigned:");
    public JComboBox<String> doctorDropDown = new JComboBox<>();
    //adding gender label and gender dropdown
    private JLabel genderLabel = new JLabel("Gender:");
    public String[] genderOptions = {"Female", "Male", "Other"};
    public JComboBox<String> genderDropDown = new JComboBox<>(genderOptions);
    //adding submit button
    public JButton submitButton = new JButton("Submit");
    private String feature = "Add Patient";

    private int userID;

    public AddPatient(int userID) { //constructor
        // creating database connection
        ConnectionDB.LogAction(getFeatureName(), userID);
        this.userID = userID;
        initializeFrame();
        addComponentsToFrame();
        populateDoctorDropDown();
    }
    
    //initialising frame
    private void initializeFrame() {
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setLayout(null);

        // creating date picker functionality
        DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setFormatForDatesCommonEra("dd/MM/yyyy"); // Set your desired format
        dobDatePicker = new DatePicker(dateSettings);
        dobDatePicker.setBounds(175, 400, 350, 30);
        frame.add(dobDatePicker);

        frame.setVisible(true);
    }


    /**
     * Adds components to the frame.
     * This method sets the bounds and font for various components such as labels, text fields, buttons, etc.
     * It also adds these components to the frame.
     */
    private void addComponentsToFrame() {
        //heading bounds
        heading.setBounds(255, 0, 500, 50);
        heading.setFont(new Font("Courier", Font.BOLD, 20));

        //back button bounds
        backButton.setBounds(0, 0, 50, 50);
        backButton.setFont(new Font("Courier", Font.BOLD, 20));
        backButton.addActionListener(this);
        backButton.setFocusable(false);

        //first name label bounds
        firstNameLabel.setBounds(10, 80, 150, 30);
        firstNameLabel.setFont(new Font("Courier", Font.BOLD, 18));
        surnameLabel.setBounds(10, 120, 100, 30);
        surnameLabel.setFont(new Font("Courier", Font.BOLD, 20));
        firstNameTextField.setBounds(175, 80, 350, 34);
        surnameTextField.setBounds(175, 120, 350, 34);

        //dob labels bounds
        dobLabel.setBounds(10, 400, 100, 30);
        dobLabel.setFont(new Font("Courier", Font.BOLD, 20));

        //phone bounds
        phoneLabel.setBounds(10, 200, 150, 30);
        phoneLabel.setFont(new Font("Courier", Font.BOLD, 18));

        phoneTextField.setBounds(175, 200, 350, 34);

        //email bounds
        emailLabel.setBounds(10, 240, 100, 30);
        emailLabel.setFont(new Font("Courier", Font.BOLD, 20));
        emailTextField.setBounds(175, 240, 350, 34);

        //address bounds
        addressLabel.setBounds(10, 280, 100, 30);
        addressLabel.setFont(new Font("Courier", Font.BOLD, 20));
        addressTextField.setBounds(175, 280, 350, 34);

        //gender bounds
        genderLabel.setBounds(10, 320, 150, 30);
        genderLabel.setFont(new Font("Courier", Font.BOLD, 18));
        genderDropDown.setBounds(175, 320, 350, 30);

        //doctor bounds
        doctorDropDown.setBounds(175, 360, 350, 30);
        assignedDoctorLabel.setBounds(10, 360, 350, 30);

        //submit button bounds
        submitButton.setBounds(250, 500, 150, 50);
        submitButton.setFont(new Font("Courier", Font.BOLD, 20));
        submitButton.addActionListener(this);
        submitButton.setFocusable(false);

        //Adding to Frame
        frame.add(heading);
        frame.add(backButton);
        frame.add(firstNameLabel);
        frame.add(firstNameTextField);
        frame.add(surnameLabel);
        frame.add(surnameTextField);
        frame.add(addressLabel);
        frame.add(addressTextField);
        frame.add(dobLabel);
        frame.add(emailLabel);
        frame.add(emailTextField);
        frame.add(phoneLabel);
        frame.add(phoneTextField);

        frame.add(genderLabel);
        frame.add(genderDropDown);
        frame.add(assignedDoctorLabel);
        frame.add(doctorDropDown);
        frame.add(submitButton);
    }

    //adding list of available doctors to doctor dropdown
    private void populateDoctorDropDown() {
        try {
            Connection conn = ConnectionDB.getConnection();
            PreparedStatement statement = conn
                    .prepareStatement("SELECT CONCAT(FirstName, ' ', LastName) AS FullName FROM Doctors");
            ResultSet rs = statement.executeQuery();

            doctorDropDown.removeAllItems();
            while (rs.next()) {
                doctorDropDown.addItem(rs.getString("FullName"));
            }

            rs.close();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to fetch doctor list.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //when submit button is pressed add to database
        if (e.getSource() == submitButton) {
            if (validateForm()) {
                String firstName = firstNameTextField.getText();
                String surname = surnameTextField.getText();
                String email = emailTextField.getText();

                LocalDate dobLocalDate = dobDatePicker.getDate();
                String dob = (dobLocalDate != null) ? dobLocalDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "";
                
                String phoneNumber = phoneTextField.getText();
                String gender = (String) genderDropDown.getSelectedItem();
                String address = addressTextField.getText();
                String doctorAssigned = (String) doctorDropDown.getSelectedItem();
                int doctorID = fetchDoctorID(doctorAssigned);

                addPatientToDB(firstName, surname,dob, phoneNumber, address, gender, email, doctorID);
            }
        // when back button is pressed, go back to the landing page
        } else if (e.getSource() == backButton) {
            frame.dispose();
            new LandingPage(userID);
        }
    }

    private boolean validateForm() {
        return true;
    }

    //fetching the DoctorID from the doctor name
    private int fetchDoctorID(String doctorFullName) {
        int doctorID = 0;
        try {
            Connection conn = ConnectionDB.getConnection();
            PreparedStatement statement = conn.prepareStatement("SELECT DoctorID FROM Doctors WHERE CONCAT(FirstName, ' ', LastName) = ?");
            statement.setString(1, doctorFullName);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                doctorID = rs.getInt("DoctorID");
            } else {
                JOptionPane.showMessageDialog(null, "Selected doctor not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            rs.close();
            statement.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching doctor ID.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return doctorID;
    }

    // add patient details to the database
    private void addPatientToDB(String firstname, String surname, String dob, String phoneNumber,
            String address, String gender, String email, int assignedDoctorID) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;

        try {
            connection = ConnectionDB.getConnection();

            // get the maximum UserID from the Users table
            int maxUserId = 0;
            String getMaxUserIdSql = "SELECT MAX(UserID) FROM Users";
            statement = connection.prepareStatement(getMaxUserIdSql);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                maxUserId = resultSet.getInt(1);
            }

            // increment maxUserId to get the new UserID for the patient
            int newUserId = maxUserId + 1;

            // insert patient details into the Patients table
            String insertPatientSql = "INSERT INTO Patients (FirstName, LastName, DOB, Gender, Address, PhoneNumber, Email, DoctorID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(insertPatientSql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, firstname);
            statement.setString(2, surname);
            statement.setString(3, dob);
            statement.setString(4, gender);
            statement.setString(5, address);
            statement.setString(6, phoneNumber);
            statement.setString(7, email);
            statement.setInt(8, assignedDoctorID);
            statement.executeUpdate();

            // retrieve the generated PatientID
            generatedKeys = statement.getGeneratedKeys();
            int patientID = -1;
            if (generatedKeys.next()) {
                patientID = generatedKeys.getInt(1);
            }

            // create username and password for the patient
            String username = (firstname.substring(0, 1) + surname.substring(0, 1) + newUserId).toLowerCase();
            String password = "password";

            // insert user details into the Users table
            String insertUserSql = "INSERT INTO Users(UserID, Username, Password, UserType) VALUES (?, ?, ?, ?)";
            statement = connection.prepareStatement(insertUserSql);
            statement.setInt(1, newUserId);
            statement.setString(2, username);
            statement.setString(3, password);
            statement.setString(4, "Patient");
            statement.executeUpdate();

            // send message to patient about their login credentials
            String insertMessageSql = "INSERT INTO Message(UserID, message) VALUES (?, ?)";
            statement = connection.prepareStatement(insertMessageSql);
            statement.setInt(1, patientID);
            statement.setString(2, "You have been added to the GP System.");
            statement.executeUpdate();

            displayPopup("Success", "Patient added successfully.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            displayPopup("Error", "Failed to add patient.");
        } finally {
            // close resources
            ConnectionDB.closeConnection(connection);
        }
    }

    //getter for feature
    public String getFeatureName() {
        return feature;
    }
    
    //method for pop up
    private void displayPopup(String title, String message) {
        JFrame popframe = new JFrame(title);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JLabel label = new JLabel(message);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);

        // create close button
        JButton closeButton = new JButton("Close");
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                popframe.dispose();
            }
        });
        panel.add(Box.createVerticalStrut(10));
        panel.add(closeButton);
        popframe.add(panel);
        popframe.setSize(200, 120);
        popframe.setLocationRelativeTo(frame);
        popframe.setVisible(true);
    }
    public static void main(String[] args) {
        new AddPatient(1);
    }
}