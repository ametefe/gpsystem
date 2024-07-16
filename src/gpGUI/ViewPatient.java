package gpGUI;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;


public class ViewPatient {
    private final JFrame frame = new JFrame();
    public final JTable table;
    private final DefaultTableModel tableModel;
    private Connection connection;
    public JButton backButton = new JButton("Back");
    private String feature = "View Patient";

    public ViewPatient(int userID) {
        ConnectionDB.LogAction(getFeatureName(), userID);
        connection = null;

        tableModel = new DefaultTableModel(new String[] { "Patient ID", "First Name", "Last Name",
                "Email","Date of Birth", "Phone Number", "Address", "Gender","Doctor Name" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
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
        scrollPane.setBounds(0, 0, 800, 500);
        backButton.setBounds(0, 500, 700, 50);

        frame.add(scrollPane);
        frame.add(backButton);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(820, 600);
        frame.setLayout(null);
        frame.setVisible(true);

        backButton.addActionListener(e -> {
            frame.dispose();
            new LandingPage(userID);
        });
    }
    
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

    // Load data from the database into the table

    private void loadTableData() {
        try {
            String query = "SELECT patients.PatientID, patients.FirstName, patients.LastName, patients.DOB, " +
                    "patients.Address, patients.PhoneNumber, patients.Email,patients.Gender, CONCAT(doctors.FirstName, ' ', doctors.LastName) AS DoctorName "
                    +
                    "FROM patients LEFT JOIN doctors ON patients.DoctorID = doctors.DoctorID";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int patientID = resultSet.getInt("PatientID");
                String firstName = resultSet.getString("FirstName");
                String lastName = resultSet.getString("LastName");
                String email = resultSet.getString("Email");
                String dob = resultSet.getString("DOB");
                String phoneNumber = resultSet.getString("PhoneNumber");
                String address = resultSet.getString("Address");
                String gender = resultSet.getString("Gender");
                String doctorName = resultSet.getString("DoctorName");
                
                tableModel.addRow(new Object[] { patientID, firstName, lastName, email, dob, phoneNumber, address,
                        gender,doctorName });
            }
            
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public String getFeatureName() {
        return feature;
    }

    public static void main(String[] args) {
        new ViewPatient(1);
    }
}
