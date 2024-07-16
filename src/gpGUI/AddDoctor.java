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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

public class AddDoctor implements ActionListener {
    private JFrame frame = new JFrame();
    private JLabel heading = new JLabel("Add a New Doctor");
    private JButton backButton = new JButton("<");
    // label for name textboxes
    private JLabel firstNameLabel = new JLabel("First Name:");
    public JTextField firstNameTextField = new JTextField();
    private JLabel surnameLabel = new JLabel("Surname:");
    public JTextField surnameTextField = new JTextField();
    // label for email textboxe
    private JLabel emailLabel = new JLabel("Email:");
    public JTextField emailTextField = new JTextField();
    // label for date of birth textboxe
    private JLabel dobLabel = new JLabel("D.O.B:");
    public DatePicker dobDatePicker;
    // label for phone number textboxe
    private JLabel phoneLabel = new JLabel("Phone Number:");
    public JTextField phoneTextField = new JTextField();
    // label for availability checkboxes
    private JLabel availabilityLabel = new JLabel("Availability:");
    // create checkboxes
    private String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    public JCheckBox[] checkboxes = new JCheckBox[days.length];
    // // label for speciality dropdown
    private JLabel specialityLabel = new JLabel("Speciality:");
    public String[] specialityOptions = {"Dermatology", "Family medicine", "Neurology", "Surgery", "Emergency medicine"};
    // create drop down for speciality
    public JComboBox<String> specialityDropDown = new JComboBox<>(specialityOptions);
    // label for gender dropdown
    private JLabel genderLabel = new JLabel("Gender:");
    public String[] genderOptions = {"Female", "Male", "Other"};
    // create drop down for gender
    public JComboBox<String> genderDropDown = new JComboBox<>(genderOptions);
    //create submit button
    public JButton submitButton = new JButton("Submit");

    private int userID;
    private String feature = "Add Doctor";
    private boolean Successful = false;

    public AddDoctor(int userID) {
        // connection to the database
        this.userID = userID;
        ConnectionDB.LogAction(getFeatureName(), this.userID);
        initializeFrame();
        addComponentsToFrame();
    }

    private void initializeFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 700);
        frame.setLayout(null);

        DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setFormatForDatesCommonEra("dd/MM/yyyy");
        dobDatePicker = new DatePicker(dateSettings);
        dobDatePicker.setBounds(175, 200, 350, 30);
        frame.add(dobDatePicker);

        frame.setVisible(true);
    }

    private void addComponentsToFrame() {
        //heading layout
        heading.setFont(new Font("Courier", Font.BOLD, 20));
        heading.setBounds(255, 0, 500, 50);

        // back button layout
        backButton.setFont(new Font("Courier", Font.BOLD, 20));
        backButton.setBounds(0, 0, 50, 50);
        backButton.addActionListener(this);
        backButton.setFocusable(false);

        // name layout
        firstNameLabel.setFont(new Font("Courier", Font.BOLD, 20));
        firstNameLabel.setBounds(10, 80, 150, 30);
        firstNameTextField.setBounds(175, 80, 350, 34);

        surnameLabel.setFont(new Font("Courier", Font.BOLD, 20));
        surnameLabel.setBounds(10, 120, 150, 30);
        surnameTextField.setBounds(175, 120, 350, 34);

        //date of birth layout
        dobLabel.setFont(new Font("Courier", Font.BOLD, 20));
        dobLabel.setBounds(10, 200, 100, 30);

        // phone number layout
        phoneLabel.setFont(new Font("Courier", Font.BOLD, 18));
        phoneLabel.setBounds(10, 240, 150, 30);
        phoneTextField.setBounds(175, 240, 350, 34);

        // email layout
        emailLabel.setFont(new Font("Courier", Font.BOLD, 20));
        emailLabel.setBounds(10, 280, 100, 30);
        emailTextField.setBounds(175, 280, 350, 34);

        // availability layout
        availabilityLabel.setFont(new Font("Courier", Font.BOLD, 18));
        availabilityLabel.setBounds(10, 320, 150, 30);

        // speciality layout
        specialityLabel.setFont(new Font("Courier", Font.BOLD, 18));
        specialityLabel.setBounds(10, 470, 150, 30);
        specialityDropDown.setBounds(175, 470, 350, 34);

        // gender layout
        genderLabel.setFont(new Font("Courier", Font.BOLD, 18));
        genderLabel.setBounds(10, 510, 150, 30);
        genderDropDown.setBounds(175, 510, 350, 30);

        // submit button layout
        submitButton.setFont(new Font("Courier", Font.BOLD, 20));
        submitButton.setBounds(250, 570, 150, 50);
        submitButton.addActionListener(this);
        submitButton.setFocusable(false);

        // Adding components to frame
        frame.add(heading);
        frame.add(backButton);
        frame.add(firstNameLabel);
        frame.add(firstNameTextField);
        frame.add(surnameLabel);
        frame.add(surnameTextField);
        frame.add(dobLabel);
        frame.add(phoneLabel);
        frame.add(phoneTextField);
        frame.add(emailLabel);
        frame.add(emailTextField);
        frame.add(availabilityLabel);
        int x = 200;
        int y = 330;
        int checkboxWidth = 100;
        int checkboxHeight = 20;
        for (int i = 0; i < days.length; i++) {
            checkboxes[i] = new JCheckBox(days[i]);
            checkboxes[i].setBounds(x, y, checkboxWidth, checkboxHeight);
            frame.add(checkboxes[i]);
            if (i % 2 == 0) {
                x += 150;
            } else {
                x = 200;
                y += 30;
            }

        }
        frame.add(specialityLabel);
        frame.add(specialityDropDown);
        frame.add(genderLabel);
        frame.add(genderDropDown);
        frame.add(submitButton);
    }
    
@Override
public void actionPerformed(ActionEvent e) {
    if (e.getSource() == backButton) {
        frame.dispose();
        new LandingPage(userID);
    } else if (e.getSource() == submitButton) {
        try {
            String firstName = firstNameTextField.getText();
            String surname = surnameTextField.getText();

            LocalDate dobLocalDate = dobDatePicker.getDate();
            String dob = (dobLocalDate != null) ? dobLocalDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "";

            String phoneNumber = phoneTextField.getText();
            String speciality = (String) specialityDropDown.getSelectedItem();
            String gender = (String) genderDropDown.getSelectedItem();
            String email = emailTextField.getText();
            String availabilityJson = buildAvailabilityJson();

            // Add doctor to database
            addDoctorToDB(firstName, surname,dob, phoneNumber, speciality, gender, email, availabilityJson);

            // Reset form fields
            resetFormFields();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Please fill in the form.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

private void resetFormFields() {

    // clears the text fields
    JTextField[] textFields = { firstNameTextField, surnameTextField, phoneTextField,
            emailTextField };
    for (JTextField textField : textFields) {
        textField.setText("");
    }

    // clears the checkboxes
    //JCheckBox[] checkboxes = { monday, tuesday, wednesday, thursday, friday, saturday, sunday };
    for (JCheckBox checkbox : checkboxes) {
        checkbox.setSelected(false);
    }

    // clears the dob picker
    dobDatePicker.clear();
}

private String buildAvailabilityJson() {
    StringBuilder availability = new StringBuilder("[");
    //JCheckBox[] checkboxes = { monday, tuesday, wednesday, thursday, friday, saturday, sunday };

    for (JCheckBox checkbox : checkboxes) {
        if (checkbox.isSelected()) {
            availability.append("\"").append(checkbox.getText()).append("\",");
        }
    }

    if (availability.length() > 1) {
        availability.setLength(availability.length() - 1); // removes trailing comma
    }
    availability.append("]");

    return availability.toString();
}

private void addDoctorToDB(String firstname, String surname, String dob, String phoneNumber, String speciality, String gender, String email, String availability) {
    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet generatedKeys = null;

    try {
        // prepare SQL statement to get the maximum UserID from the Users table
        connection = ConnectionDB.getConnection();
        String getMaxUserIdSql = "SELECT MAX(UserID) FROM Users";
        statement = connection.prepareStatement(getMaxUserIdSql);
        ResultSet resultSet = statement.executeQuery();
        int maxUserId = 0;
        if (resultSet.next()) {
            maxUserId = resultSet.getInt(1);
        }

        // increment maxUserId to get the new UserID for the doctor
        int newUserId = maxUserId + 1;

        // prepare SQL statement to insert the doctor into the Doctors table
        String insertDoctorSql = "INSERT INTO Doctors(DoctorID, FirstName, LastName, dob, PhoneNumber, Specialisation, Gender, Email, Availability) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        statement = connection.prepareStatement(insertDoctorSql, Statement.RETURN_GENERATED_KEYS);
        statement.setInt(1, newUserId); // set DoctorID using newUserId
        statement.setString(2, firstname);
        statement.setString(3, surname);
        statement.setString(4, dob);
        statement.setString(5, phoneNumber);
        statement.setString(6, speciality);
        statement.setString(7, gender);
        statement.setString(8, email);
        statement.setString(9, availability);
        statement.executeUpdate();

        // retrieve the generated DoctorID
        generatedKeys = statement.getGeneratedKeys();
        int doctorID = -1;
        if (generatedKeys.next()) {
            doctorID = generatedKeys.getInt(1);
        }

        // create username and password
        String username = (firstname.substring(0, 1) + surname.substring(0, 1) + newUserId).toLowerCase();
        String password = "password";

        // insert user details into the Users table
        String insertUserSql = "INSERT INTO Users(UserID, Username, Password, UserType) VALUES (?, ?, ?, ?)";
        statement = connection.prepareStatement(insertUserSql);
        statement.setInt(1, newUserId);
        statement.setString(2, username);
        statement.setString(3, password); // for simplicity, using a plain text password, not recommended in production
        statement.setString(4, "Doctor");
        statement.executeUpdate();

        // send message to doctor about their login credentials
        String insertMessageSql = "INSERT INTO Message(UserID, message) VALUES (?, ?)";
        statement = connection.prepareStatement(insertMessageSql);
        statement.setInt(1, doctorID);
        statement.setString(2, "You have been added to the GP System.");
        statement.executeUpdate();

        // set success flag
        Successful = true;

        displayPopup("Success", "Doctor added successfully.");

    } catch (SQLException ex) {
        ex.printStackTrace();
        displayPopup("Failed", "Failed to add a new doctor.");
        //closing generatedKeys, Prepared Statement and Connection
    } finally {
        if (generatedKeys != null) {
            try {
                generatedKeys.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

    //getter methods
    public boolean isSuccessful() {
        return Successful;
    }

    public String getFeatureName() {
        return feature;
    }

    //pop up message
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

    //main method
    public static void main(String[] args) {
        new AddDoctor(1);
    }
}