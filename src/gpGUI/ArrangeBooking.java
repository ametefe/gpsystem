package gpGUI;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JComboBox;
import com.github.lgooddatepicker.components.DatePicker;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.awt.Color;





public class ArrangeBooking implements ActionListener {

    // JFrame
    private JFrame frame = new JFrame();
    public JFrame dialogFrame = null;

    // JButton
    private JButton backButton = new JButton("<");
    public JButton submitButton = new JButton("Submit");
    public JButton okButton = null;

    // DatePicker
    public DatePicker datePicker = new DatePicker();

    // JComboBox
    public JComboBox<String> timeComboBox;
    public JComboBox<ComboItem> doctorComboBox;
    private JComboBox<ComboItem> patientComboBox;

    // JLabel
    private JLabel titleLabel = new JLabel("Arrange Booking");
    private JLabel doctorLabel = new JLabel("Select Doctor");
    private JLabel patientLabel = new JLabel("Select Patient");
    private JLabel timeDateLabel = new JLabel("Select time and date");
    public JLabel messageLabel = new JLabel("", JLabel.CENTER);

    // Other fields
    private int userID;
    private String feature = "Arrange Booking";

    /**
     * The ArrangeBooking class represents a booking arrangement for a user.
     * It initializes the frame, adds components to the frame, and logs the action.
     */
    public ArrangeBooking(int userID) {
        this.userID = userID;
        ConnectionDB.LogAction(getFeatureName(), this.userID);
        initializeFrame();
        addComponentsToFrame();
    }

    // Returns the feature name
    public String getFeatureName() {
        return feature;
    }

    // Initializes the frame
    private void initializeFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setLayout(null);
    }

    /**
     * Adds components to the frame for arranging a booking.
     */
    private void addComponentsToFrame() {
        // Title Label
        titleLabel.setFont(new Font("Courier", Font.BOLD, 20));
        titleLabel.setBounds(200, 20, 200, 30);
        frame.add(titleLabel);

        // Back Button
        backButton.setFont(new Font("Courier", Font.BOLD, 20));
        backButton.setBounds(0, 0, 50, 50);
        backButton.addActionListener(this);
        backButton.setFocusable(false);
        frame.add(backButton);

        // Time and Date Label
        timeDateLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timeDateLabel.setBounds(10, 100, 200, 20);
        frame.add(timeDateLabel);

        // DatePicker
        datePicker.setBounds(220, 100, 200, 30);
        frame.add(datePicker);

        // Time ComboBox
        String[] times = generateTimes();
        timeComboBox = new JComboBox<>(times);
        timeComboBox.setBounds(220, 140, 90, 30);
        frame.add(timeComboBox);

        // Doctor Label
        doctorLabel.setFont(new Font("Arial", Font.BOLD, 16));
        doctorLabel.setBounds(10, 200, 200, 20);
        frame.add(doctorLabel);

        // Doctor ComboBox
        doctorComboBox = new JComboBox<>(); // The generics type is inferred from the variable declaration
        doctorComboBox.setBounds(220, 200, 200, 30);
        frame.add(doctorComboBox);

        // Patient Label
        patientLabel.setFont(new Font("Arial", Font.BOLD, 16));
        patientLabel.setBounds(10, 300, 200, 20);
        frame.add(patientLabel);

        // Patient ComboBox
        patientComboBox = new JComboBox<>(); // The generics type is inferred from the variable declaration
        patientComboBox.setBounds(220, 300, 200, 30);
        frame.add(patientComboBox);

        // Submit Button
        submitButton.setFont(new Font("Courier", Font.BOLD, 18));
        submitButton.setBounds(250, 500, 100, 50);
        submitButton.addActionListener(this);
        submitButton.setFocusable(false);
        frame.add(submitButton);

        // Message Label
        messageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        messageLabel.setBounds(150, 400, 300, 100); // Adjust width and height as necessary
        messageLabel.setVerticalAlignment(JLabel.TOP);
        frame.add(messageLabel);

        // Populate the ComboBoxes
        populateComboBoxes(); // This method will populate both doctorComboBox and patientComboBox

        // Make the frame visible
        frame.setVisible(true);
    }

    // Utility method to generate time strings with 15-minute intervals
    private String[] generateTimes() {
        String[] times = new String[36]; // For 9 hours (9 AM to 6 PM) in 15-minute intervals
        LocalTime time = LocalTime.of(9, 0); // Start time is 9:00 AM
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");

        for (int i = 0; i < times.length; i++) {
            times[i] = time.format(formatter);
            time = time.plusMinutes(15); // Increment the time by 15 minutes
        }

        return times;
    }

    private void populateComboBoxes() {
        populateComboBoxWithQuery(doctorComboBox, "SELECT DoctorID, CONCAT(firstname, ' ', lastname) AS fullname FROM doctors ORDER BY fullname", "DoctorID");
        populateComboBoxWithQuery(patientComboBox, "SELECT PatientID, CONCAT(firstname, ' ', lastname) AS fullname FROM patients ORDER BY fullname", "PatientID");
    }

    /**
     * Populates a JComboBox with data retrieved from a database query.
     *
     * @param comboBox the JComboBox to populate
     * @param query the SQL query to execute
     * @param idColumnName the name of the column containing the ID values
     */
    private void populateComboBoxWithQuery(JComboBox<ComboItem> comboBox, String query, String idColumnName) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionDB.getConnection();
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt(idColumnName); // Use the dynamic column name for ID
                String name = resultSet.getString("fullname");
                comboBox.addItem(new ComboItem(name, id));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the work days of a doctor based on the doctor's ID.
     *
     * @param doctorId the ID of the doctor
     * @return a string representation of the doctor's work days in JSON format
     */
    public String getDoctorWorkDays(int doctorId) {
        String workDays = "[]"; // Default to empty JSON array
        try (Connection connection = ConnectionDB.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT Availability FROM Doctors WHERE DoctorID = ?")) {
            statement.setInt(1, doctorId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                workDays = resultSet.getString("Availability");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return workDays;
    }

    /**
     * Checks if there is a conflict with the given appointment date, time, and doctor ID.
     *
     * @param appointmentDate The date of the appointment.
     * @param appointmentTime The time of the appointment.
     * @param doctorId The ID of the doctor.
     * @return true if there is a conflict, false otherwise.
     */
    public boolean isAppointmentConflict(Date appointmentDate, Time appointmentTime, int doctorId) {
        try {
            // Establish database connection
            Connection connection = null;
            connection = ConnectionDB.getConnection();

            // Prepare SQL query to check for existing appointments
            String sql = "SELECT COUNT(*) FROM appointments WHERE DoctorID = ? AND AppointmentDate = ? AND AppointmentTime = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, doctorId);
            statement.setDate(2, appointmentDate);
            statement.setTime(3, appointmentTime);

            // Execute query
            ResultSet resultSet = statement.executeQuery();

            // Check if there is any conflicting appointment
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Schedules an appointment for a patient with a doctor.
     *
     * @param patientId The ID of the patient.
     * @param doctorId The ID of the doctor.
     * @param appointmentDate The date of the appointment.
     * @param appointmentTime The time of the appointment.
     */
    public void scheduleAppointment(int patientId, int doctorId, Date appointmentDate, Time appointmentTime) {
        LocalDate appointmentLocalDate = appointmentDate.toLocalDate();

        // Check if the doctor works on the selected day
        if (!doesDoctorWorkOnThisDay(doctorId, appointmentLocalDate)) {
            String workDays = getFormattedDoctorWorkDays(doctorId);
            showMessage("The doctor does not work on the selected day. Doctor works on: " + workDays, true);
            return;
        }

        // Check for appointment conflicts
        if (isAppointmentConflict(appointmentDate, appointmentTime, doctorId)) {
            showMessage("Conflict! The selected appointment time is already booked.", true);
            return;
        }

        // Proceed to insert the appointment into the database
        try (Connection connection = ConnectionDB.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO appointments (PatientID, DoctorID, AppointmentDate, AppointmentTime) VALUES (?, ?, ?, ?)")) {

            statement.setInt(1, patientId);
            statement.setInt(2, doctorId);
            statement.setDate(3, appointmentDate);
            statement.setTime(4, appointmentTime);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                showConfirmationDialog();
                showMessage("", false);

                // Send messages to the patient and doctor
                sendAppointmentMessages(doctorId, patientId, appointmentDate.toLocalDate(), appointmentTime.toLocalTime());
            } else {
                showMessage("Failed to schedule appointment.", true);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Returns a formatted string of the work days for a doctor.
     *
     * @param doctorId the ID of the doctor
     * @return a formatted string of the work days
     */
    public String getFormattedDoctorWorkDays(int doctorId) {
        String workDaysJson = getDoctorWorkDays(doctorId);
        List<String> workDays = parseWorkDays(workDaysJson);
        // Convert the list to a comma-separated string
        return String.join(", ", workDays);
    }

    /**
     * Checks if a doctor works on a specific day.
     *
     * @param doctorId        the ID of the doctor
     * @param appointmentDate the date of the appointment
     * @return true if the doctor works on the specified day, false otherwise
     */
    public boolean doesDoctorWorkOnThisDay(int doctorId, LocalDate appointmentDate) {
        String workDaysJson = getDoctorWorkDays(doctorId);
        List<String> workDays = parseWorkDays(workDaysJson);
        String appointmentDay = appointmentDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        return workDays.contains(appointmentDay);
    }

    /**
     * Parses a JSON string representing work days and returns a list of work days.
     *
     * @param workDaysJson the JSON string representing work days
     * @return a list of work days
     */
    public List<String> parseWorkDays(String workDaysJson) {
        List<String> workDays = new ArrayList<>();
        // Remove the brackets and split the string by comma
        String[] days = workDaysJson.replace("[", "").replace("]", "").replace("\"", "").split(",");
        for (String day : days) {
            workDays.add(day.trim()); // Trim to remove any leading or trailing spaces
        }
        return workDays;
    }

    // Sends the Message to the Patient and Doctor
    public void sendAppointmentMessages(int doctorId, int patientId, LocalDate appointmentDate,
            LocalTime appointmentTime) {
        Connection connection = null;
        PreparedStatement statement = null;
        String messageForDoctor = String.format("An appointment has been scheduled with a patient on %s at %s.",
                appointmentDate.toString(), appointmentTime.format(DateTimeFormatter.ofPattern("h:mm a")));
        String messageForPatient = String.format("An appointment has been scheduled with your doctor on %s at %s.",
                appointmentDate.toString(), appointmentTime.format(DateTimeFormatter.ofPattern("h:mm a")));

        try {
            connection = ConnectionDB.getConnection();
            // Prepare SQL to insert message for the patient
            String insertMessageSql = "INSERT INTO Message(UserID, message) VALUES (?, ?)";

            // Insert message for the patient
            statement = connection.prepareStatement(insertMessageSql);
            statement.setInt(1, patientId);
            statement.setString(2, messageForPatient);
            statement.executeUpdate();

            // Insert message for the doctor
            statement.setInt(1, doctorId);
            statement.setString(2, messageForDoctor);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays a confirmation dialog with a success message for scheduling an appointment.
     * The dialog includes a message label and an "OK" button to close the dialog.
     */
    private void showConfirmationDialog() {
        // Create a new frame to act as the dialog
        dialogFrame = new JFrame("Confirmation");
        dialogFrame.setSize(300, 150);
        dialogFrame.setLayout(null);
        dialogFrame.setLocationRelativeTo(frame); // Center the dialog in relation to the main frame

        // Create a label to display the message "Appointment scheduled successfully!"
        JLabel messageLabel = new JLabel("Appointment scheduled successfully!", JLabel.CENTER);
        messageLabel.setBounds(10, 20, 280, 30);
        dialogFrame.add(messageLabel);

        // Create an "OK" button that closes the dialog
        okButton = new JButton("OK");
        okButton.setBounds(100, 70, 100, 30);
        okButton.addActionListener(e -> dialogFrame.dispose()); // Close the dialog when clicked
        dialogFrame.add(okButton);

        // Display the dialog
        dialogFrame.setVisible(true);
    }

    /**
     * Displays a message on the message label.
     *
     * @param message The message to be displayed.
     * @param isError Specifies whether the message is an error message or a normal message.
     */
    private void showMessage(String message, boolean isError) {
        String fullMessage = "<html><body style='text-align: center;'>" + message + "</body></html>";
        messageLabel.setText(fullMessage);

        if (isError) {
            messageLabel.setForeground(Color.RED);
        } else {
            messageLabel.setForeground(Color.BLACK); // Or any other color for normal messages
        }
    }

    // Action Listener for the buttons in the frame (Submit and Back)
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            LocalDate selectedDate = datePicker.getDate();
            if (selectedDate != null && selectedDate.isBefore(LocalDate.now())) {
                showMessage("Cannot select a past date. Please choose a valid future date.", true);
                return;
            }
            // Get the selected doctor and patient
            ComboItem selectedDoctor = (ComboItem) doctorComboBox.getSelectedItem();
            ComboItem selectedPatient = (ComboItem) patientComboBox.getSelectedItem();

            // Get the selected date and time
            Date appointmentDate = Date.valueOf(datePicker.getDate());
            Time appointmentTime = Time.valueOf(
                    LocalTime.parse((String) timeComboBox.getSelectedItem(), DateTimeFormatter.ofPattern("h:mm a")));

            // Schedule the appointment
            scheduleAppointment(selectedPatient.getValue(), selectedDoctor.getValue(), appointmentDate,
                    appointmentTime);
        }
        if (e.getSource() == backButton) {
            frame.dispose();
            new LandingPage(userID);
        }
    }

    // Testing purposes
    public static void main(String[] args) {
        new ArrangeBooking(1);
    }
}
