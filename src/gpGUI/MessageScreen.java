package gpGUI;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;


public class MessageScreen {

    public JFrame frame = new JFrame("Messages");
    public JTable table;
    private Connection connection = null;
    DefaultTableModel tableModel;
    JButton ContinueButton = new JButton("Back");

    public MessageScreen(int userID) { // Constructor
        tableModel = new DefaultTableModel(new String[]{"Message", "Message ID"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) {
                    return Integer.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };

        table = new JTable(tableModel) {
            // Hide the "Message ID" column from view
            @Override
            public void createDefaultColumnsFromModel() {
                super.createDefaultColumnsFromModel();
                getColumnModel().removeColumn(getColumnModel().getColumn(1)); // Hide the second column
            }
        };

        Font tableFont = new Font("SansSerif", Font.PLAIN, 18);
        table.setFont(tableFont);
        table.setRowHeight(30);

        try {
            connection = ConnectionDB.getConnection();
            String query = "SELECT message, MessageId FROM Message WHERE UserID = ? AND IsRead = FALSE;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userID);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String message = resultSet.getString("message");
                int messageId = resultSet.getInt("MessageId");
                tableModel.addRow(new Object[] { message, messageId });
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            ConnectionDB.closeConnection(connection);
        }

        // Display the message in a dialog box when the user double clicks on a message
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JTable target = (JTable) e.getSource();
                    int row = target.getSelectedRow();
                    int messageId = (Integer) tableModel.getValueAt(row, 1); // Get message ID

                    // Update the message as read in the database using the message_id
                    try {
                        connection = ConnectionDB.getConnection();
                        String query = "UPDATE Message SET IsRead = TRUE WHERE MessageId = ?;";
                        PreparedStatement statement = connection.prepareStatement(query);
                        statement.setInt(1, messageId);
                        statement.executeUpdate();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    } finally {
                        ConnectionDB.closeConnection(connection);
                    }

                    // Display the message text in a dialog box
                    String message = (String) tableModel.getValueAt(row, 0);
                    JOptionPane.showMessageDialog(frame, message, "Message", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });


        ContinueButton.setBounds(215, 510, 150, 50);
        ContinueButton.setFocusable(false);
        ContinueButton.addActionListener(e -> {
            frame.dispose();
            new LandingPage(userID);
        });

        frame.add(ContinueButton);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane);
        scrollPane.setBounds(0, 0, 600, 510);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setLayout(null);
        frame.setVisible(true);
    }
}
