package gpGUI;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;


public class RemoveBooking implements ActionListener {
    private JFrame frame = new JFrame();
    private JButton backButton = new JButton("<");
    private JComboBox<ComboItem> bookingComboBox;
    private JButton removeButton = new JButton("Remove Booking");
    private JLabel headingLabel = new JLabel("Remove Booking");
    public JLabel messageLabel = new JLabel("", JLabel.CENTER);

    private int userID;
    private String feature = "Remove Booking";

    public RemoveBooking(int userID) { // Constructor
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
        frame.setSize(600, 400);
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

        // booking label
        JLabel bookingLabel = new JLabel("Select Booking");
        bookingLabel.setFont(new Font("Arial", Font.BOLD, 16));
        bookingLabel.setBounds(10, 100, 200, 20);
        frame.add(bookingLabel);

        // booking dropdown
        bookingComboBox = new JComboBox<>();
        bookingComboBox.setBounds(170, 100, 400, 30);
        frame.add(bookingComboBox);

        // remove button
        removeButton.setFont(new Font("Courier", Font.BOLD, 18));
        removeButton.setBounds(200, 200, 300, 50);
        removeButton.addActionListener(this);
        removeButton.setFocusable(false);
        frame.add(removeButton);

        // message label
        messageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        messageLabel.setBounds(150, 300, 300, 100);
        messageLabel.setVerticalAlignment(JLabel.TOP);
        frame.add(messageLabel);

        // populate drop down
        populateBookingComboBox();

        frame.setVisible(true);
    }

    /**
     * Populates the booking combo box with appointment details.
     * This method retrieves appointment details from the database and populates the combo box
     * with the appointment ID, doctor name, doctor specialization, patient name, appointment date, and appointment time.
     * The combo box is sorted by appointment date and time.
     */
    private void populateBookingComboBox() {
        String sql =
                "SELECT " +
                        "Appointments.AppointmentID, " +
                        "CONCAT(Doctors.FirstName, ' ', Doctors.LastName) AS DoctorName, " +
                        "Doctors.Specialisation, " +
                        "CONCAT(Patients.FirstName, ' ', Patients.LastName) AS PatientName, " +
                        "Appointments.AppointmentDate, " +
                        "Appointments.AppointmentTime " +
                        "FROM " +
                        "Appointments " +
                        "JOIN " +
                        "Doctors ON Appointments.DoctorID = Doctors.DoctorID " +
                        "JOIN " +
                        "Patients ON Appointments.PatientID = Patients.PatientID " +
                        "ORDER BY " +
                        "Appointments.AppointmentDate, " +
                        "Appointments.AppointmentTime;";

        populateComboBoxWithQuery(bookingComboBox, sql, "AppointmentID");
    }

    /**
     * Populates a JComboBox with data retrieved from a database query.
     *
     * @param comboBox      the JComboBox to populate
     * @param query         the SQL query to execute
     * @param idColumnName  the name of the column containing the identifier
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
                int id = resultSet.getInt("AppointmentID"); // Get the AppointmentID as the identifier
                // Construct the display string including patient name, doctor name, specialisation, date, and time
                String displayText = resultSet.getString("PatientName") + " - " +
                        resultSet.getString("DoctorName") + " - " +
                        resultSet.getString("Specialisation") + " - " +
                        resultSet.getDate("AppointmentDate").toString() + " " +
                        resultSet.getTime("AppointmentTime").toString();
                comboBox.addItem(new ComboItem(displayText, id));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null)
                    resultSet.close();
                if (preparedStatement != null)
                    preparedStatement.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Refreshes the booking combo box by removing all items and repopulating it
    private void refreshBookingComboBox() {
        bookingComboBox.removeAllItems();
        populateBookingComboBox();
    }

    /**
     * Removes a booking from the database based on the provided booking ID.
     *
     * @param bookingId the ID of the booking to be removed
     */
    private void removeBooking(int bookingId) {
        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM Appointments WHERE AppointmentID = ?")) {

            statement.setInt(1, bookingId);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                showMessage("Booking removed successfully!", false);
            } else {
                showMessage("Failed to remove booking.", true);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
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

    /**
     * Performs the necessary actions when an action event occurs.
     *
     * @param e the action event that occurred
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == removeButton) {
            ComboItem selectedBooking = (ComboItem) bookingComboBox.getSelectedItem();
            if (selectedBooking == null) {
                showMessage("Please select a booking.", true);
                return;
            }
            removeBooking(selectedBooking.getValue());
            refreshBookingComboBox();
        }
        if (e.getSource() == backButton) {
            frame.dispose();
            new LandingPage(userID);
        }
    }

    public static void main(String[] args) {
        new RemoveBooking(1);
    }
}
