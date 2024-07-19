package gpGUI;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import gpDB.ConnectionDB;


public class ViewBooking {

    // JFrame
    private final JFrame frame = new JFrame();

    // JPanel
    private final JPanel buttonPanel = new JPanel(new GridLayout(1, 1)); // Panel to hold buttons

    // JTable and related
    public final JTable table;
    private final DefaultTableModel tableModel;

    // JButton
    public JButton backButton = new JButton("Back");

    // Other fields
    private String feature = "View Bookings";

    public ViewBooking(int userID) { // Constructor
        ConnectionDB.LogAction(getFeatureName(), userID);
        tableModel = new DefaultTableModel(new String[] { "Appointment ID", "Doctor Name", "Department",
                "Patient Name", "Appointment Date", "Appointment Time" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        customizeTableAppearance();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(0, 0, 800, 500); // Adjusted height

        // Adding buttons to the buttonPanel
        buttonPanel.add(backButton);

        // Adding buttonPanel and scrollPane to the frame
        frame.add(buttonPanel);
        frame.add(scrollPane);

        // Setting bounds for buttonPanel and backButton
        buttonPanel.setBounds(0, 500, 800, 50);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(820, 600);
        frame.setLayout(null);
        frame.setVisible(true);

        backButton.addActionListener(e -> {
            frame.dispose();
            new LandingPage(userID);
        });
    }

    /**
     * Customizes the appearance of the table by centering the cell contents,
     * setting the font for the table and table header.
     */
    private void customizeTableAppearance() {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setCellRenderer(centerRenderer);
        }

        Font tableFont = new Font("SansSerif", Font.PLAIN, 10);
        table.setFont(tableFont);
        Font headerFont = new Font("SansSerif", Font.BOLD, 12);
        table.getTableHeader().setFont(headerFont);
    }

    /**
     * Loads the appointments of a specific doctor from the database and populates them in a table.
     *
     * @param doctorID the ID of the doctor whose appointments are to be loaded
     */
    public void loadDoctorAppointments(int doctorID) {
        try {
            Connection connection = ConnectionDB.getConnection();
            String query = "SELECT " +
                    "Appointments.AppointmentID, " +
                    "Appointments.AppointmentDate, " +
                    "Appointments.AppointmentTime, " +
                    "CONCAT(Doctors.FirstName, ' ', Doctors.LastName) AS DoctorName, " +
                    "Doctors.Specialisation AS DoctorSpeciality, " +
                    "CONCAT(Patients.FirstName, ' ', Patients.LastName) AS PatientName " +
                    "FROM Appointments " +
                    "INNER JOIN Doctors ON Appointments.DoctorID = Doctors.DoctorID " +
                    "INNER JOIN Patients ON Appointments.PatientID = Patients.PatientID " +
                    "WHERE Doctors.DoctorID = ? " +
                    "ORDER BY Appointments.AppointmentDate, Appointments.AppointmentTime;";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, doctorID);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int appointmentID = resultSet.getInt("AppointmentID");
                String doctorName = resultSet.getString("DoctorName");
                String doctorSpeciality = resultSet.getString("DoctorSpeciality");
                String patientName = resultSet.getString("PatientName");
                Date appointmentDate = resultSet.getDate("AppointmentDate");
                Time appointmentTime = resultSet.getTime("AppointmentTime");
                tableModel.addRow(new Object[] { appointmentID, doctorName, doctorSpeciality, patientName,
                        appointmentDate, appointmentTime });
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Loads the appointments for a specific patient from the database and populates the table with the data.
     *
     * @param patientID the ID of the patient
     */
    public void loadPatientAppointments(int patientID) {
        try {
            Connection connection = ConnectionDB.getConnection();
            String query = "SELECT " +
                    "Appointments.AppointmentID, " +
                    "Appointments.AppointmentDate, " +
                    "Appointments.AppointmentTime, " +
                    "CONCAT(Doctors.FirstName, ' ', Doctors.LastName) AS DoctorName, " +
                    "Doctors.Specialisation AS DoctorSpeciality, " +
                    "CONCAT(Patients.FirstName, ' ', Patients.LastName) AS PatientName " +
                    "FROM Appointments " +
                    "INNER JOIN Doctors ON Appointments.DoctorID = Doctors.DoctorID " +
                    "INNER JOIN Patients ON Appointments.PatientID = Patients.PatientID " +
                    "WHERE Patients.PatientID = ? " +
                    "ORDER BY Appointments.AppointmentDate, Appointments.AppointmentTime;";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, patientID);
            ResultSet resultSet = statement.executeQuery();

            // Clear the table before adding new rows
            tableModel.setRowCount(0);

            while (resultSet.next()) {
                int appointmentID = resultSet.getInt("AppointmentID");
                Date appointmentDate = resultSet.getDate("AppointmentDate");
                Time appointmentTime = resultSet.getTime("AppointmentTime");
                String doctorName = resultSet.getString("DoctorName");
                String doctorSpeciality = resultSet.getString("DoctorSpeciality");
                String patientName = resultSet.getString("PatientName");

                // Add a row to the table model for each record
                tableModel.addRow(new Object[] { appointmentID, doctorName, doctorSpeciality, patientName,
                        appointmentDate, appointmentTime });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Optionally, handle the error more gracefully here
        }
    }

    /**
     * Loads appointments from the database based on the selected month and year.
     *
     * @param selectedMonth The selected month.
     * @param selectedYear  The selected year.
     */
    public void loadAppointmentsByMonthAndYear(int selectedMonth, int selectedYear) {
        try {
            Connection connection = ConnectionDB.getConnection();
            // Adjust the query to filter by month and year
            String query = "SELECT " +
                    "Appointments.AppointmentID, " +
                    "Appointments.AppointmentDate, " +
                    "Appointments.AppointmentTime, " +
                    "CONCAT(Doctors.FirstName, ' ', Doctors.LastName) AS DoctorName, " +
                    "Doctors.Specialisation AS DoctorSpeciality, " +
                    "CONCAT(Patients.FirstName, ' ', Patients.LastName) AS PatientName " +
                    "FROM Appointments " +
                    "INNER JOIN Doctors ON Appointments.DoctorID = Doctors.DoctorID " +
                    "INNER JOIN Patients ON Appointments.PatientID = Patients.PatientID " +
                    "WHERE MONTH(Appointments.AppointmentDate) = ? AND YEAR(Appointments.AppointmentDate) = ? " +
                    "ORDER BY Appointments.AppointmentDate, Appointments.AppointmentTime;";

            PreparedStatement statement = connection.prepareStatement(query);
            // Set the month and year parameters
            statement.setInt(1, selectedMonth);
            statement.setInt(2, selectedYear);
            ResultSet resultSet = statement.executeQuery();

            tableModel.setRowCount(0);

            while (resultSet.next()) {
                int appointmentID = resultSet.getInt("AppointmentID");
                Date appointmentDate = resultSet.getDate("AppointmentDate");
                Time appointmentTime = resultSet.getTime("AppointmentTime");
                String doctorName = resultSet.getString("DoctorName");
                String doctorSpeciality = resultSet.getString("DoctorSpeciality");
                String patientName = resultSet.getString("PatientName");

                // Add a row to the table model for each record
                tableModel.addRow(new Object[]{appointmentID, doctorName, doctorSpeciality, patientName,
                        appointmentDate, appointmentTime});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getFeatureName() {
        return feature;
    }

    public static void main(String[] args) {
        new ViewBooking(1);
    }
}
