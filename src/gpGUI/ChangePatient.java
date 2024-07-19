package gpGUI;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import gpDB.ConnectionDB;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ChangePatient { 

    // JFrame
    private final JFrame frame = new JFrame();

    // JTable and related
    private final JTable table;
    private final DefaultTableModel tableModel;

    // Connection
    private Connection connection;

    // JButton
    public JButton confirmButton = new JButton("Confirm");
    public JButton backButton = new JButton("Back");

    // Other fields
    private String feature = "Change Patient";

    public JFrame getFrame() {
        return frame;
    }

    public ChangePatient(int userID) { // Constructor
        ConnectionDB.LogAction(getFeatureName(), userID);
        connection = null;
        tableModel = new DefaultTableModel(new String[]{"Patient ID", "First Name", "Last Name",
                "Date of Birth", "Address", "Phone Number", "Email","Gender", "Doctor Name"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Making all cells non-editable
            }
        };

        table = new JTable(tableModel);
        customizeTableAppearance();
        try {
            connection = ConnectionDB.getConnection();
            loadTableData();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(0, 0, 700, 500);
        backButton.setBounds(0, 500, 700, 50);

        frame.add(scrollPane);
        frame.add(backButton);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(720, 600);
        frame.setLayout(null);
        frame.setVisible(true);

        backButton.addActionListener(e -> {
            frame.dispose();
            new LandingPage(userID);
        });

        // Add a mouse listener to the table to handle double-click events
        table.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        int patientID = (Integer) tableModel.getValueAt(row, 0);
                        showDoctorSelectionPopup(patientID);
                    }
                }
            }

            // Unused methods
            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });

        confirmButton = new JButton("Confirm");
    }

    /**
     * Customizes the appearance of the table by center-aligning the cells,
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
     * Loads data from the database into the table model for display.
     * This method retrieves patient information along with the name of the associated doctor
     * and adds each row of data to the table model.
     * 
     * @throws SQLException if there is an error executing the SQL query
     */
    private void loadTableData() {
        try {
            String query = "SELECT patients.PatientID, patients.FirstName, patients.LastName, patients.DOB, " +
                    "patients.Address, patients.PhoneNumber, patients.Email,patients.Gender, CONCAT(doctors.FirstName, ' ', doctors.LastName) AS DoctorName " +
                    "FROM patients LEFT JOIN doctors ON patients.DoctorID = doctors.DoctorID";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int patientID = resultSet.getInt("PatientID");
                String firstName = resultSet.getString("FirstName");
                String lastName = resultSet.getString("LastName");
                String dob = resultSet.getString("DOB");
                String address = resultSet.getString("Address");
                String phoneNumber = resultSet.getString("PhoneNumber");
                String email = resultSet.getString("Email");
                String gender = resultSet.getString("Gender");
                String doctorName = resultSet.getString("DoctorName");
                tableModel.addRow(new Object[] { patientID, firstName, lastName, dob, address, phoneNumber, email, gender, doctorName });
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Displays a popup window to select a doctor for a given patient.
     * 
     * @param patientID the ID of the patient
     */
    public void showDoctorSelectionPopup(int patientID) {
        JFrame doctorFrame = new JFrame("Select a Doctor");

        try {
            Map<String, Integer> doctorMap = new HashMap<>();
            String doctorQuery = "SELECT DoctorID, CONCAT(FirstName, ' ', LastName) AS FullName FROM doctors";
            PreparedStatement statement = connection.prepareStatement(doctorQuery);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("DoctorID");
                String fullName = resultSet.getString("FullName");
                doctorMap.put(fullName, id);
            }

            JComboBox<String> comboBox = new JComboBox<>(doctorMap.keySet().toArray(new String[0]));

            // Add a confirm button to assign the patient to the selected doctor
            confirmButton.addActionListener(e -> {
                String selectedDoctorName = (String) comboBox.getSelectedItem();
                Integer selectedDoctorId = doctorMap.get(selectedDoctorName);
                try {
                    String updateQuery = "UPDATE patients SET DoctorID = ? WHERE PatientID = ?";
                    PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                    updateStatement.setInt(1, selectedDoctorId);
                    updateStatement.setInt(2, patientID);
                    updateStatement.executeUpdate();

                    sendMessagesToPatientAndDoctor(patientID, selectedDoctorName, selectedDoctorId);

                    refreshTableData();
                    doctorFrame.dispose(); // Close the doctor selection window after confirmation
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            });

            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(e -> {
                doctorFrame.dispose(); // Close the doctor selection window without any action
            });

            JPanel comboBoxPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            comboBoxPanel.add(comboBox);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.add(confirmButton);
            buttonPanel.add(cancelButton);

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(comboBoxPanel, BorderLayout.NORTH);
            panel.add(buttonPanel, BorderLayout.CENTER);

            doctorFrame.getContentPane().add(panel);
            doctorFrame.setSize(300, 150);
            doctorFrame.setLocationRelativeTo(frame); // Center the window on screen
            doctorFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            doctorFrame.setVisible(true);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends messages to the patient and doctor after assigning the patient to a new doctor.
     *
     * @param patientID The ID of the patient being assigned to the doctor.
     * @param selectedDoctorName The name of the selected doctor.
     * @param selectedDoctorId The ID of the selected doctor.
     * @throws SQLException If an error occurs while executing the SQL statements.
     */
    private void sendMessagesToPatientAndDoctor(int patientID, String selectedDoctorName, Integer selectedDoctorId) throws SQLException {
        String patientMessage = "You have been assigned to Doctor " + selectedDoctorName;
        String doctorMessage = "You have been assigned a new patient:";
        String insertMessageQuery = "INSERT INTO Message(UserID, message) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(insertMessageQuery)) {
            statement.setInt(1, patientID);
            statement.setString(2, patientMessage);
            statement.executeUpdate();

            statement.setInt(1, selectedDoctorId);
            statement.setString(2, doctorMessage);
            statement.executeUpdate();
        }
    }

    // Refreshes the table data by clearing the table model and loading the data again
    private void refreshTableData() {
        tableModel.setRowCount(0);
        loadTableData();
    }

    // Returns the name of the feature
    public String getFeatureName() {
        return feature;
    }

    public static void main(String[] args) {
        new ChangePatient(1);
    }
}
