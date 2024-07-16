import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Droptable {

    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306";
    private static final String DATABASE_USER = "root";
    private static final String DATABASE_PASSWORD = "password";

    public static void main(String[] args) {
        dropDatabase();
    }

    /**
     * Drops the database "gpsystem" if it exists.
     * 
     * @devNote This method uses the MySQL JDBC Driver to connect to the database and execute the DROP DATABASE statement.
     * If the database does not exist, no action is taken.
     * 
     * @throws ClassNotFoundException if the MySQL JDBC Driver is not found.
     * @throws SQLException if a database access error occurs.
     */
    public static void dropDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
            return;
        }

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
             Statement statement = connection.createStatement()) {

            String sql = "DROP DATABASE IF EXISTS gpsystem";
            statement.executeUpdate(sql);
            System.out.println("Database gpsystem has been successfully dropped.");
        } catch (SQLException e) {
            System.out.println("A database access error has occurred.");
            e.printStackTrace();
        }
    }
}
