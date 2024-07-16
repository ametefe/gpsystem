package gpGUI;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.sql.*;




public class ActivityLogView {
    private final JFrame frame = new JFrame();
    private final JTable table;
    private final DefaultTableModel tableModel;
    private Connection connection;
    public JButton backButton = new JButton("Back");

    public ActivityLogView(int userID) {
        connection = null;
        tableModel = new DefaultTableModel(
                new String[] { "LogID", "UserID", "Time Accessed", "User Type", "Feature Accessed" }, 0) {
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
    }

    // Load data from the database into the table
    private void loadTableData() {
        try {
            String query = "SELECT ActivityLog.LogID,ActivityLog.UserID, ActivityLog.TimeAccessed," +
                    "Users.UserType,ActivityLog.FeatureAccessed FROM ActivityLog INNER JOIN Users ON ActivityLog.UserID = Users.UserID;";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int logID = resultSet.getInt("LogID");
                int userID = resultSet.getInt("UserID");
                Timestamp timeAccessed = resultSet.getTimestamp("TimeAccessed");
                String userType = resultSet.getString("UserType");
                String featureAccessed = resultSet.getString("FeatureAccessed");
                tableModel.addRow(new Object[] { logID, userID, timeAccessed, userType, featureAccessed });
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    // Customize the appearance of the table
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

    public static void main(String[] args) {
        new ActivityLogView(1);
    }
}
