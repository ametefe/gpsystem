import gpGUI.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.sql.Date;
import java.sql.Time;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class GpTester{

    private Connection conn = null;


    @BeforeEach
    public void setUp() throws SQLException {
        conn = ConnectionDB.getConnection();
        Droptable.dropDatabase();
        BuildDatabase.setupDatabase();
    }

    @Test
    public void testDBConnection() {
        Assertions.assertNotNull(conn);
    }

    @Test
    public void testSuccessfulLogin() throws SQLException {
        LoginPage login = new LoginPage();
        login.usernameField.setText("sa2090");
        login.PasswordField.setText("Password1");
        login.loginButton.doClick();
        Assertions.assertEquals("Login successful", login.messageLabel.getText());
    }

    @Test
    public void testSuccessfulLogout() throws SQLException {
        LoginPage login = new LoginPage();
        login.usernameField.setText("sa2090");
        login.PasswordField.setText("Password1");
        login.loginButton.doClick();
        LandingPage landing = new LandingPage(1);
        landing.logoutButton.doClick();
        Assertions.assertEquals(false, landing.frame.isVisible());
    }

    @Test
    public void testMessageScreen() throws SQLException {

        ConnectionDB.resetMessageTable();
        MessageScreen message = new MessageScreen(1);
        int rowCount = message.table.getModel().getRowCount();
        System.out.println(rowCount);

        Assertions.assertTrue(rowCount > 0, "The message table should have at least one column.");
    }

    @Test
    public void testAddDoctor() {
        // Create a new doctor
        AddDoctor addDoctor = new AddDoctor(1);
        addDoctor.firstNameTextField.setText("John");
        addDoctor.surnameTextField.setText("Doe");
        addDoctor.emailTextField.setText("JohnDoe@yahoo.com");
        LocalDate date = LocalDate.parse("1990-01-01");
        addDoctor.dobDatePicker.setDate(date);
        addDoctor.phoneTextField.setText("1234567890");
        // Simulate selecting availability checkboxes
        for (JCheckBox checkbox : addDoctor.checkboxes) {
            if (checkbox.getText().equals("Monday") || checkbox.getText().equals("Tuesday")
                    || checkbox.getText().equals("Wednesday")) {
                checkbox.setSelected(true);
            }
        }
        // JComboBox for Speciality and Gender
        addDoctor.specialityDropDown.setSelectedIndex(1); 
        addDoctor.genderDropDown.setSelectedIndex(1);
        // Simulate clicking the submit button
        addDoctor.submitButton.doClick();
        // Check if the doctor was added successfully
        Assertions.assertTrue(addDoctor.isSuccessful());
    }

    @Test
    public void testChangeDoctor() {

        ChangePatient changePatient = new ChangePatient(1);
        changePatient.showDoctorSelectionPopup(4);
        changePatient.confirmButton.doClick();
        JFrame frame = changePatient.getFrame();
        frame.dispose();
        try {
            PreparedStatement statement = conn.prepareStatement(
        "SELECT CONCAT(doctors.FirstName, ' ', doctors.LastName)"+
        " AS DoctorName FROM patients LEFT JOIN doctors "+ 
        "ON patients.DoctorID = doctors.DoctorID WHERE patientID = 4;");
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            String doctorName = resultSet.getString("DoctorName");
            assertEquals("Taylor Paddington", doctorName);
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Failed to retrieve doctor name.");

        }

    }
    
    @Test
    public void testAddPatient() {
        AddPatient addPatient = new AddPatient(1);
        addPatient.firstNameTextField.setText("Bless");
        addPatient.surnameTextField.setText("Tefeya");
        LocalDate date = LocalDate.parse("1990-01-01");
        addPatient.dobDatePicker.setDate(date);
        addPatient.addressTextField.setText("1234 Street");
        addPatient.phoneTextField.setText("1234567890");
        addPatient.emailTextField.setText("awgawiugwo@gmail.com");
        addPatient.genderDropDown.setSelectedIndex(1);
        addPatient.doctorDropDown.setSelectedIndex(1);
        addPatient.submitButton.doClick();

        try {
            PreparedStatement statement = conn
                    .prepareStatement("SELECT * FROM Patients WHERE FirstName = 'Bless' AND LastName = 'Tefeya';");
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            String firstName = resultSet.getString("FirstName");
            String surname = resultSet.getString("LastName");
            assertEquals("Bless", firstName);
            assertEquals("Tefeya", surname);
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Failed to retrieve patient name.");
        }
    }
    
    @Test
    public void testViewDoctorScreen() throws SQLException {

        ViewDoctor viewDoctor = new ViewDoctor(1);
        int rowCount = viewDoctor.table.getModel().getRowCount();

        Assertions.assertTrue(rowCount > 0, "The message table should have at least one column.");
    }

    @Test
    public void testRoleBasedAccessAdmin() throws SQLException {
        LandingPage landing = new LandingPage(1);
        Assertions.assertTrue(landing.addPatient.isVisible());
        Assertions.assertTrue(landing.addDoctor.isVisible());
        Assertions.assertTrue(landing.changePatientDoctor.isVisible());
        Assertions.assertTrue(landing.viewDoctor.isVisible());
        Assertions.assertTrue(landing.viewBooking.isVisible());
        Assertions.assertTrue(landing.activityLogger.isVisible());
        Assertions.assertTrue(landing.messageBoard.isVisible());
        Assertions.assertTrue(landing.arrangeBooking.isVisible());
        Assertions.assertTrue(landing.rescheduleBooking.isVisible());
        Assertions.assertTrue(landing.removeBooking.isVisible());
    }

    @Test
    public void testRoleBasedAccessDoctorPatient() throws SQLException {
        LandingPage landing = new LandingPage(1);
        Assertions.assertTrue(landing.messageBoard.isVisible());
    }

    @Test
    public void testBookingAndConflictingBooking() {
        ArrangeBooking arrangeBooking = new ArrangeBooking(1);

        // Set up initial booking details
        arrangeBooking.doctorComboBox.setSelectedIndex(1);
        arrangeBooking.datePicker.setDate(LocalDate.parse("2024-11-25"));
        arrangeBooking.timeComboBox.setSelectedIndex(1);

        // Attempt to book the first appointment
        arrangeBooking.submitButton.doClick();
        arrangeBooking.okButton.doClick();

        // Verify that the appointment was booked successfully
        try {
            PreparedStatement statement = conn.prepareStatement(
                    "SELECT COUNT(*) FROM Appointments WHERE DoctorID = 3 AND PatientID = 4 AND AppointmentDate ='2024-11-25' AND AppointmentTime = '09:15:00';");
            ResultSet resultSet = statement.executeQuery();

            // Checks if the appointment has been booked
            resultSet.next();
            int count = resultSet.getInt(1);
            assertEquals(1, count);
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Failed to verify booking.");
        }

        // Attempt to book a conflicting appointment with the same details
        arrangeBooking.submitButton.doClick();
        // Assuming this method is implemented to close the dialog if shown

        // Check for the conflict message
        assertEquals(
                "<html><body style='text-align: center;'>Conflict! The selected appointment time is already booked.</body></html>",
                arrangeBooking.messageLabel.getText());
    }


    
    @Test
    public void testPastBooking() {
        ArrangeBooking arrangeBooking = new ArrangeBooking(1);
        arrangeBooking.doctorComboBox.setSelectedIndex(1);
        // Picks the 25th of November 2020
        arrangeBooking.datePicker.setDate(LocalDate.parse("2020-11-25"));
        arrangeBooking.timeComboBox.setSelectedIndex(1);
        arrangeBooking.submitButton.doClick();
        assertEquals(
                "<html><body style='text-align: center;'>Cannot select a past date. Please choose a valid future date.</body></html>",
                arrangeBooking.messageLabel.getText());
    }

    @Test
    public void testRescheduleBooking() {
        RescheduleBooking rearrangeBooking = new RescheduleBooking(1);

        // Convert date and time strings to appropriate types
        LocalDate localDate = LocalDate.parse("2024-04-16");
        Date date = Date.valueOf(localDate);

        LocalTime localTime = LocalTime.parse("09:15:00");
        Time time = Time.valueOf(localTime);
        
        rearrangeBooking.rescheduleAppointment(1, 2, date, time);

        try {
            PreparedStatement statement = conn.prepareStatement(
                    "SELECT COUNT(*) FROM Appointments WHERE DoctorID = 2 AND PatientID = 4 AND AppointmentDate ='2024-04-16' AND AppointmentTime = '09:15:00';");
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            assertEquals(1, count);
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Failed to verify booking.");
        }


    }
    

    
    
}