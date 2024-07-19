package gpDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

public class DatabaseQuery {
    
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/gpsystem";
    private static final String DATABASE_USER = "root"; //storing the username and password in plain text is not secure ------------------------------------- *Vuln: 1*
    private static final String DATABASE_PASSWORD = "password"; 

    /**
     * Returns a connection to the database.
     *
     * @return a connection to the database
     * @throws SQLException if a database access error occurs
     */
    private static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
        } catch (SQLException e) {
            // ideally log something meaningful
            throw e;
        }
    }

    public static ResultSet execute(Function<Connection, PreparedStatement> queryBuilder) throws SQLException {
        try (
            Connection connection = getConnection();
            PreparedStatement statement = queryBuilder.apply(connection);
        ) {
            return statement.executeQuery();  
        }
    }
}
