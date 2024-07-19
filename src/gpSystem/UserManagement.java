package gpSystem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import gpDB.DatabaseQuery;

public class UserManagement {
    
    public static UserType userType(int userID) throws UnknownUserException, UnsupportedUserTypeException, SQLException {    
        try (
            ResultSet resultSet = DatabaseQuery.execute((connection) -> {
                String query = "SELECT UserType FROM users WHERE UserID = ?";
        
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setInt(1, userID);
                    return statement;
                }
                catch (SQLException e) {
                    return null;
                }
            });
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
