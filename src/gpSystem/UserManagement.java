package gpSystem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import gpDB.DatabaseQuery;
import gpExceptions.UnknownUserException;
import gpExceptions.UnsupportedUserTypeException;

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

    public static String fullName(int userID) throws UnknownUserException, SQLException {
        try (
            ResultSet resultSet = DatabaseQuery.execute((connection) -> {
                String query = "SELECT "
                    + "Users.UserID, "
                    + "Users.Username, "
                    + "Users.UserType, "
                    + "CASE "
                    + "WHEN Users.UserType = 'Doctor' THEN CONCAT(Doctors.FirstName, ' ', Doctors.LastName) "
                    + "WHEN Users.UserType = 'Patient' THEN CONCAT(Patients.FirstName, ' ', Patients.LastName) "
                    + "WHEN Users.UserType = 'Admin' THEN CONCAT(Admins.FirstName, ' ', Admins.LastName) "
                    + "END AS FullName "
                    + "FROM Users "
                    + "LEFT JOIN Doctors ON Users.UserID = Doctors.DoctorID AND Users.UserType = 'Doctor' "
                    + "LEFT JOIN Patients ON Users.UserID = Patients.PatientID AND Users.UserType = 'Patient' "
                    + "LEFT JOIN Admins ON Users.UserID = Admins.AdminID AND Users.UserType = 'Admin' "
                    + "WHERE Users.UserID = ?;";
                
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

            String fullName = resultSet.getString("FullName");
            return fullName;
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
            throw throwables;
        }
    }
}
