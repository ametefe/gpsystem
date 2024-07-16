import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class BuildDatabase {

    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306"; 
    private static final String DATABASE_USER = "root";
    private static final String DATABASE_PASSWORD = "password";
    private static final String SQL_FILE_PATH_1 = "database\\SchemeTable.sql";
    private static final String SQL_FILE_PATH_2 = "database\\UserInformation.sql";

    public static void main(String[] args) {
        setupDatabase();
    }

    public static void setupDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
            return;
        }

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)) {
            executeSqlFromFile(connection, SQL_FILE_PATH_1);
            executeSqlFromFile(connection, SQL_FILE_PATH_2);
            System.out.println("Database tables have been successfully created/updated.");
        } catch (SQLException e) {
            System.out.println("A database access error has occurred.");
            e.printStackTrace();
        }
    }

    private static void executeSqlFromFile(Connection connection, String filePath) {
        try (Statement statement = connection.createStatement();
             BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            StringBuilder sqlStatement = new StringBuilder();

            while ((line = reader.readLine()) != null) {

                if (line.startsWith("--") || line.trim().isEmpty()) {
                    continue;
                }

                sqlStatement.append(line);

                if (line.endsWith(";")) {
                    statement.execute(sqlStatement.toString());
                    sqlStatement = new StringBuilder();
                }
            }
        } catch (SQLException e) {
            System.out.println("A database access error has occurred during executing file: " + filePath);
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error reading the SQL file: " + filePath);
            e.printStackTrace();
        }
    }
}
