package gpGUI;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import com.github.lgooddatepicker.components.DatePicker;


public class RescheduleBooking implements ActionListener {
    private JFrame frame = new JFrame();
    private JButton backButton = new JButton("<");
    public DatePicker datePicker = new DatePicker();
    public JComboBox<String> timeComboBox;
    public JComboBox<ComboItem> doctorComboBox;
    private JComboBox<ComboItem> appointmentComboBox;
    public JButton rescheduleButton = new JButton("Reschedule Booking");
    private JLabel headingLabel = new JLabel("Reschedule Booking");
    private JLabel doctorLabel = new JLabel("Select Doctor");
    private JLabel bookingLabel = new JLabel("Select Booking");
    private JLabel timeDateLabel = new JLabel("Select new time and date");
    public JLabel messageLabel = new JLabel("", JLabel.CENTER);

    private int userID;
    private String feature = "Reschedule Booking";
    public JFrame dialogFrame = null;
    public JButton okButton = null;

    public RescheduleBooking(int userID) { // Constructor
        this.userID = userID;
        ConnectionDB.LogAction(getFeatureName(), this.userID);
        initializeFrame();
        addComponentsToFrame();
    }

    public String getFeatureName() {
        return feature;
    }

    private void initializeFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setLayout(null);
    }

    private void addComponentsToFrame() {
        // heading Label
        headingLabel.setFont(new Font("Courier", Font.BOLD, 20));
        headingLabel.setBounds(200, 20, 300, 30);
        frame.add(headingLabel);

        // back button
        backButton.setFont(new Font("Courier", Font.BOLD, 20));
        backButton.setBounds(0, 0, 50, 50);
        backButton.addActionListener(this);
        backButton.setFocusable(false);
        frame.add(backButton);

        // date and time label
        timeDateLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timeDateLabel.setBounds(10, 400, 200, 20);
        frame.add(timeDateLabel);

        // datePicker
        datePicker.setBounds(220, 400, 200, 30);
        frame.add(datePicker);

        // time dropdown
        String[] times = generateTimes();
        timeComboBox = new JComboBox<>(times);
        timeComboBox.setBounds(220, 440, 90, 30);
        frame.add(timeComboBox);

        // doctor label
        doctorLabel.setFont(new Font("Arial", Font.BOLD, 16));
        doctorLabel.setBounds(10, 200, 200, 20);
        frame.add(doctorLabel);

        // doctor dropdown
        doctorComboBox = new JComboBox<>();
        doctorComboBox.setBounds(220, 200, 250, 30);
        frame.add(doctorComboBox);

        // booking label
        bookingLabel.setFont(new Font("Arial", Font.BOLD, 16));
        bookingLabel.setBounds(10, 300, 200, 20);
        frame.add(bookingLabel);

        // booking dropdown
        appointmentComboBox = new JComboBox<>();
        appointmentComboBox.setBounds(220, 300, 250, 30);
        frame.add(appointmentComboBox);

        // reschedule button
        rescheduleButton.setFont(new Font("Courier", Font.BOLD, 18));
        rescheduleButton.setBounds(200, 500, 300, 50);
        rescheduleButton.addActionListener(this);
        rescheduleButton.setFocusable(false);
        frame.add(rescheduleButton);

        // message label
        messageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        messageLabel.setBounds(150, 130, 300, 100);
        messageLabel.setVerticalAlignment(JLabel.TOP);
        frame.add(messageLabel);

        // populate drop downs
        populateComboBoxes();
        frame.setVisible(true);
    }

    /**
     * Generates an array of time slots in 15-minute intervals starting from 9:00 AM.
     *
     * @return an array of time slots in the format "h:mm a"
     */
    private String[] generateTimes() {
        String[] times = new String[36];
        LocalTime time = LocalTime.of(9, 0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");

        for (int i = 0; i < times.length; i++) {
            times[i] = time.format(formatter);
            time = time.plusMinutes(15);
        }

        return times;
    }

    /**
     * Populates the combo boxes with data.
     * This method retrieves a list of doctors from the database and populates the doctorComboBox with their names.
     * It then selects the first doctor in the combo box and updates the appointmentComboBox accordingly.
     * Additionally, it adds an action listener to the doctorComboBox, so that when a different doctor is selected,
     * the appointmentComboBox is updated accordingly.
     */
    private void populateComboBoxes() {
        String doctorQuery = "SELECT d.DoctorID, CONCAT(d.firstname, ' ', d.lastname) AS fullname " +
                "FROM Doctors d " +
                "JOIN Appointments a ON d.DoctorID = a.DoctorID " +
                "GROUP BY d.DoctorID, d.firstname, d.lastname " +
                "ORDER BY fullname";

        populateComboBoxWithQuery(doctorComboBox, doctorQuery, "DoctorID");

        if (doctorComboBox.getItemCount() > 0) {
            doctorComboBox.setSelectedIndex(0);
            ComboItem selectedDoctor = (ComboItem) doctorComboBox.getSelectedItem();
            if (selectedDoctor != null) {
                updateAppointmentComboBox(selectedDoctor.getValue());
            }
        }

        doctorComboBox.addActionListener(e -> {
            ComboItem selectedDoctor = (ComboItem) doctorComboBox.getSelectedItem();
            if (selectedDoctor != null) {
                updateAppointmentComboBox(selectedDoctor.getValue());
            }
        });
    }

    /**
     * Updates the appointment combo box with appointments for a specific doctor.
     * 
     * @param doctorId the ID of the doctor
     */
    private void updateAppointmentComboBox(int doctorId) {
        appointmentComboBox.removeAllItems();
        String appointmentQuery = "SELECT a.AppointmentID, a.PatientID, a.AppointmentDate, a.AppointmentTime, CONCAT(p.FirstName, ' ', p.LastName) AS fullname "
                +
                "FROM Appointments a " +
                "JOIN Patients p ON a.PatientID = p.PatientID " +
                "WHERE a.DoctorID = " + doctorId + " " +
                "ORDER BY a.AppointmentDate, a.AppointmentTime";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionDB.getConnection();
            preparedStatement = connection.prepareStatement(appointmentQuery);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int appointmentId = resultSet.getInt("AppointmentID");
                String name = resultSet.getString("fullname");
                Date appointmentDate = resultSet.getDate("AppointmentDate");
                Time appointmentTime = resultSet.getTime("AppointmentTime");
                // Format the date and time for display
                String displayDate = appointmentDate.toString();
                String displayTime = appointmentTime.toString();
                // Construct the display string for the combo box item
                String itemDisplayText = name + " - " + displayDate + " " + displayTime;
                // Add the formatted string as an item in the combo box
                appointmentComboBox.addItem(new ComboItem(itemDisplayText, appointmentId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Populates a JComboBox with data retrieved from a database query.
     * 
     * @param comboBox      the JComboBox to populate
     * @param query         the SQL query to execute
     * @param idColumnName  the name of the column containing the ID values
     */
    private void populateComboBoxWithQuery(JComboBox<ComboItem> comboBox, String query, String idColumnName) {
        try (Connection connection = ConnectionDB.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt(idColumnName);
                String name = resultSet.getString("fullname");
                comboBox.addItem(new ComboItem(name, id));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the work days of a doctor from the database.
     *
     * @param doctorId the ID of the doctor
     * @return a string representing the work days of the doctor in JSON format
     */
    public String getDoctorWorkDays(int doctorId) {
        String workDays = "[]"; // Default to an empty JSON array
        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT Availability FROM Doctors WHERE DoctorID = ?")) {
            statement.setInt(1, doctorId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    workDays = resultSet.getString("Availability");
                }
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
        String sql = "SELECT COUNT(*) FROM appointments WHERE DoctorID = ? AND AppointmentDate = ? AND AppointmentTime = ?";
        try (Connection connection = ConnectionDB.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, doctorId);
            statement.setDate(2, appointmentDate);
            statement.setTime(3, appointmentTime);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Reschedules an appointment by updating the appointment date and time in the database.
     * 
     * @param appointmentId The ID of the appointment to be rescheduled.
     * @param doctorId The ID of the doctor for the appointment.
     * @param appointmentDate The new date for the appointment.
     * @param appointmentTime The new time for the appointment.
     */
    public void rescheduleAppointment(int appointmentId, int doctorId, Date appointmentDate,
                                      Time appointmentTime) {
        LocalDate appointmentLocalDate = appointmentDate.toLocalDate();

        // Check if the doctor works on the selected day
        if (!doesDoctorWorkOnThisDay(doctorId, appointmentLocalDate)) {
            String workDays = getFormattedDoctorWorkDays(doctorId);
            showMessage("The doctor does not work on the selected day. Doctor works on: " + workDays, true);
            return;
        }

        // Check for appointment conflicts
        if (isAppointmentConflict(appointmentDate, appointmentTime, doctorId)) { // Assume this method is also updated to ignore the current appointment
            showMessage("Conflict! The selected appointment time is already booked.", true);
            return;
        }

        // Proceed to update the appointment in the database
        try (
                Connection connection = ConnectionDB.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "UPDATE appointments SET  AppointmentDate = ?, AppointmentTime = ? WHERE AppointmentID = ?")) {

            statement.setDate(1, appointmentDate);
            statement.setTime(2, appointmentTime);
            statement.setInt(3, appointmentId);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                showConfirmationDialog();
                showMessage("Appointment rescheduled successfully.", false);

                // Uses the appointID to get the patientID
                try (PreparedStatement sqlStatement = connection.prepareStatement("SELECT PatientID FROM Appointments WHERE AppointmentID = ?")) {
                    ;
                    sqlStatement.setInt(1, appointmentId);
                    ResultSet resultSet = sqlStatement.executeQuery();
                    resultSet.next();
                    int patientId = resultSet.getInt("PatientID");
                    sendRescheduleMessages(doctorId, patientId, appointmentLocalDate, appointmentTime.toLocalTime());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

            } else {
                showMessage("Failed to reschedule appointment. Please check the appointment details and try again.",
                        true);
            }
        } catch (SQLException ex) {
            showMessage("An error occurred while updating the appointment: " + ex.getMessage(), true);
            ex.printStackTrace();
        }
    }

    /**
     * Returns a formatted string representation of the doctor's work days.
     *
     * @param doctorId the ID of the doctor
     * @return a comma-separated string of the doctor's work days
     */
    public String getFormattedDoctorWorkDays(int doctorId) {
        return String.join(", ", parseWorkDays(getDoctorWorkDays(doctorId)));
    }

    /**
     * Checks if a doctor works on a specific day.
     *
     * @param doctorId        the ID of the doctor
     * @param appointmentDate the date of the appointment
     * @return true if the doctor works on the specified day, false otherwise
     */
    public boolean doesDoctorWorkOnThisDay(int doctorId, LocalDate appointmentDate) {
        List<String> workDays = parseWorkDays(getDoctorWorkDays(doctorId));
        String appointmentDay = appointmentDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        return workDays.contains(appointmentDay);
    }

    /**
     * Parses a JSON string representing work days and returns a list of parsed work days.
     *
     * @param workDaysJson the JSON string representing work days
     * @return a list of parsed work days
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

    /**
     * Sends reschedule messages to the doctor and patient regarding a rescheduled appointment.
     *
     * @param doctorId        the ID of the doctor
     * @param patientId       the ID of the patient
     * @param appointmentDate the new appointment date
     * @param appointmentTime the new appointment time
     */
    public void sendRescheduleMessages(int doctorId, int patientId, LocalDate appointmentDate,
                                       LocalTime appointmentTime) {
        Connection connection = null;
        PreparedStatement statement = null;
        String messageForDoctor = String.format("Your appointment has been rescheduled with a patient to %s at %s.",
                appointmentDate.toString(), appointmentTime.format(DateTimeFormatter.ofPattern("h:mm a")));
        String messageForPatient = String.format("Your appointment has been rescheduled with your doctor to %s at %s.",
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
     * Displays a message on the GUI with the specified text and color.
     *
     * @param message The message to be displayed.
     * @param isError A boolean value indicating whether the message is an error message.
     */
    private void showMessage(String message, boolean isError) {
        String fullMessage = "<html><body style='text-align: center;'>" + message + "</body></html>";
        messageLabel.setText(fullMessage);

        if (isError) {
            messageLabel.setForeground(Color.RED);
        } else {
            messageLabel.setForeground(Color.BLACK);
        }
    }

    private void showConfirmationDialog() { // Confirmation dialog box
        dialogFrame = new JFrame("Confirmation");
        dialogFrame.setSize(300, 150);
        dialogFrame.setLayout(null);
        dialogFrame.setLocationRelativeTo(frame);

        JLabel messageLabel = new JLabel("Booking rescheduled successfully!", JLabel.CENTER);
        messageLabel.setBounds(10, 20, 280, 30);
        dialogFrame.add(messageLabel);

        okButton = new JButton("OK");
        okButton.setBounds(100, 70, 100, 30);
        okButton.addActionListener(e -> dialogFrame.dispose());
        dialogFrame.add(okButton);

        dialogFrame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) { // action listener for the buttons in the frame
        if (e.getSource() == rescheduleButton) {
            ComboItem selectedBooking = (ComboItem) appointmentComboBox.getSelectedItem();
            ComboItem selectedDoctor = (ComboItem) doctorComboBox.getSelectedItem();
            LocalDate selectedDate = datePicker.getDate();

            if (selectedBooking == null || selectedDoctor == null || selectedDate == null) {
                showMessage("Please select booking, doctor, and date.", true);
                return;
            }

            Date bookingDate = Date.valueOf(selectedDate);
            Time bookingTime = Time.valueOf(
                    LocalTime.parse((String) timeComboBox.getSelectedItem(), DateTimeFormatter.ofPattern("h:mm a")));
            rescheduleAppointment(selectedBooking.getValue(), selectedDoctor.getValue(), bookingDate, bookingTime);

        }
        if (e.getSource() == backButton) {
            frame.dispose();
            new LandingPage(userID);
        }
    }
}
