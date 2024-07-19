package gpSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import gpDB.ConnectionDB;

public class UserManagement {
    public static UserType userType(int userID) throws UnknownUserException, UnsupportedUserTypeException, SQLException {
        // determines the user type from the database and displays the buttons accordingly
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = ConnectionDB.getConnection();
            String query = "SELECT UserType FROM users WHERE UserID = ?"; // prepared statements to prevent SQL injection
            statement = connection.prepareStatement(query);
            statement.setInt(1, userID);
            resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                throw new UnknownUserException(userID);
            }

            String userType = resultSet.getString("UserType");
            if (userType.equals("Doctor")) {
                return UserType.e_DOCTOR;
            }
            else if (userType.equals("Patient")) {
                return UserType.e_PATIENT;
            }
            else if (userType.equals("Admin")) {
                return UserType.e_ADMIN;
            }
            else {
                throw new UnsupportedUserTypeException(userType);
            }
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
            throw throwables;
        }
        finally {
            try {
                if (resultSet != null)
                    resultSet.close();
                if (statement != null)
                    statement.close();
                ConnectionDB.closeConnection(connection);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                throw throwables;
            }
        } 
    }
}

final class UnknownUserException extends Exception {
    public int userID;

    public UnknownUserException(int userID) {
        this.userID = userID;
    }
}

final class UnsupportedUserTypeException extends Exception {
    public String userType;

    public UnsupportedUserTypeException(String userType) {
        this.userType = userType;
    }
}
