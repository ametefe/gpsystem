package gpGUI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class ConnectionDB {

    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/gpsystem";
    private static final String DATABASE_USER = "root"; //storing the username and password in plain text is not secure ------------------------------------- *Vuln: 1*
    private static final String DATABASE_PASSWORD = "password"; 

    /**
     * Returns a connection to the database.
     *
     * @return a connection to the database
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
        } catch (SQLException e) {
            throw e;
        }
    }

    /**
     * Closes the given database connection.
     *
     * @param conn the connection to be closed
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Resets the Message table by deleting all existing records and inserting a test message.
     * This method establishes a database connection, executes the necessary SQL queries,
     * and closes the connection afterwards.
     *
     * @throws SQLException if an error occurs while executing the SQL queries.
     */
    public static void resetMessageTable() {
        Connection connection = null;
        try {
            connection = ConnectionDB.getConnection();
            String query = "DELETE FROM Message";
            connection.createStatement().executeUpdate(query);
            query = "INSERT INTO Message (UserID, message, IsRead) VALUES (1, 'Test message 1', FALSE)";
            connection.createStatement().executeUpdate(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            ConnectionDB.closeConnection(connection);
        }
    }

    /**
     * Logs an action performed by a user.
     * 
     * @param feature The feature that was accessed by the user.
     * @param userID The ID of the user performing the action.
     */
    public static void LogAction(String feature, int userID) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = ConnectionDB.getConnection();
            String query = "SELECT UserType FROM Users WHERE UserID = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, userID);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Insert log with user type
                String insertQuery = "INSERT INTO ActivityLog (UserID, TimeAccessed, FeatureAccessed) VALUES (?, ?, ?)";
                statement = connection.prepareStatement(insertQuery);
                statement.setInt(1, userID);
                statement.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                statement.setString(3, feature);
                statement.executeUpdate();
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }
}
