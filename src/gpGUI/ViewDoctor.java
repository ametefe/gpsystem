package gpGUI;
import java.awt.Font;
import java.sql.Connection;
import java.sql.Date;
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



public class ViewDoctor {

    // JFrame
    private final JFrame frame = new JFrame();

    // JTable and related
    public final JTable table;
    private final DefaultTableModel tableModel;

    // Connection
    private Connection connection;

    // JButton
    public JButton backButton = new JButton("Back");

    // Other fields
    private String feature = "View Doctor";

    public ViewDoctor(int userID) { // Constructor
        ConnectionDB.LogAction(getFeatureName(), userID);
        connection = null;

        tableModel = new DefaultTableModel(new String[] { "Doctor ID", "First Name", "Last Name",
                "Date of Birth", "Phone Number", "Specialisation", "Gender", "Email", "Availability" }, 0) {
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
    private void loadTableData() { // Load data from database into the table
        try {
            String query = "SELECT * FROM Doctors";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int doctorID = resultSet.getInt("DoctorID");
                String firstName = resultSet.getString("FirstName");
                String lastName = resultSet.getString("LastName");
                Date dob = resultSet.getDate("DOB");
                String phoneNumber = resultSet.getString("PhoneNumber");
                String specialisation = resultSet.getString("Specialisation");
                String gender = resultSet.getString("Gender");
                String email = resultSet.getString("Email");
                String availability = resultSet.getString("Availability");
                tableModel.addRow(new Object[] { doctorID, firstName, lastName, dob, phoneNumber, specialisation,
                        gender, email, availability });
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    // Get the feature name
    public String getFeatureName() {
        return feature;
    }

}
