package gpSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import gpDB.ConnectionDB;

public class UserManagement {

    private static PreparedStatement prepareStatement(Connection connection, int userID) throws SQLException {
        String query = "SELECT UserType FROM users WHERE UserID = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, userID);
        return statement;
    }

    public static UserType userType(int userID) throws UnknownUserException, UnsupportedUserTypeException, SQLException {    
        try (
            Connection connection = ConnectionDB.getConnection();
            PreparedStatement preparedStatement = prepareStatement(connection, userID);
            ResultSet resultSet = preparedStatement.executeQuery();
        ) {
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
