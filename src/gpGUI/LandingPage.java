package gpGUI;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import gpExceptions.UnknownUserException;
import gpSystem.UserManagement;
import gpSystem.UserType;

public class LandingPage implements ActionListener {
    public JFrame frame = new JFrame();
    public JButton logoutButton = new JButton("Logout");
    public JButton addPatient = new JButton("Add Patient");
    public JButton addDoctor = new JButton("Add Doctor");
    public JButton changePatientDoctor = new JButton("Change Doctor");
    public JButton viewDoctor = new JButton("View Doctor");
    public JButton viewPatient = new JButton("View Patient");
    public JButton viewBooking = new JButton("View Bookings");
    public JButton activityLogger = new JButton("Activity Logger");
    public JButton messageBoard = new JButton("Message Board");
    public JButton arrangeBooking = new JButton("Arrange Booking");
    public JButton rescheduleBooking = new JButton("Reschedule Booking");
    public JButton removeBooking = new JButton("Remove Booking");
    public JLabel messageLabel = new JLabel();

    private int userID;

    public LandingPage(int userID) { // Constructor
        // Buttons Location
        this.userID = userID;
        logoutButton.setBounds(219, 508, 150, 50);
        logoutButton.setFont(new Font("Courier", Font.BOLD, 10));
        addPatient.setBounds(69, 75, 150, 50);
        addPatient.setFont(new Font("Courier", Font.BOLD, 10));
        addDoctor.setBounds(369, 75, 150, 50);
        addDoctor.setFont(new Font("Courier", Font.BOLD, 10));
        changePatientDoctor.setBounds(69, 187, 150, 50);
        changePatientDoctor.setFont(new Font("Courier", Font.BOLD, 10));
        viewDoctor.setBounds(369, 187, 150, 50);
        viewDoctor.setFont(new Font("Courier", Font.BOLD, 10));
        viewBooking.setBounds(69, 298, 150, 50);
        viewBooking.setFont(new Font("Courier", Font.BOLD, 10));
        activityLogger.setBounds(369, 298, 150, 50);
        activityLogger.setFont(new Font("Courier", Font.BOLD, 10));
        messageBoard.setBounds(69, 410, 150, 50);
        messageBoard.setFont(new Font("Courier", Font.BOLD, 10));
        viewPatient.setBounds(369, 410, 150, 50);
        viewPatient.setFont(new Font("Courier", Font.BOLD, 10));
        arrangeBooking.setBounds(219, 75, 150, 50);
        arrangeBooking.setFont(new Font("Courier", Font.BOLD, 10));
        rescheduleBooking.setFont(new Font("Courier", Font.BOLD, 10));
        rescheduleBooking.setBounds(219, 187, 150, 50);
        removeBooking.setFont(new Font("Courier", Font.BOLD, 10));
        removeBooking.setBounds(219, 298, 150, 50);

        // button listeners
        logoutButton.addActionListener(this);
        addDoctor.addActionListener(this);
        changePatientDoctor.addActionListener(this);
        addPatient.addActionListener(this);
        viewDoctor.addActionListener(this);
        viewBooking.addActionListener(this);
        viewPatient.addActionListener(this);
        activityLogger.addActionListener(this);
        messageBoard.addActionListener(this);
        arrangeBooking.addActionListener(this);
        rescheduleBooking.addActionListener(this);
        removeBooking.addActionListener(this);

        // button focusable
        logoutButton.setFocusable(false);
        addPatient.setFocusable(false);
        addDoctor.setFocusable(false);
        changePatientDoctor.setFocusable(false);
        viewDoctor.setFocusable(false);
        viewPatient.setFocusable(false);
        viewBooking.setFocusable(false);
        activityLogger.setFocusable(false);
        messageBoard.setFocusable(false);
        arrangeBooking.setFocusable(false);
        rescheduleBooking.setFocusable(false);
        removeBooking.setFocusable(false);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);

        UserType userType;
        String fullName;
        try {
            userType = UserManagement.userType(userID);
            fullName = UserManagement.fullName(userID);
        }
        catch (UnknownUserException e) {
            // print some error message?
            return;
        }
        catch (Exception err) {
            // don't do this! just to save time for now
            return;
        }

        // shows the buttons based on the user type
        populateFrameForUser(userType);
        welcomeMessage(fullName);
    }
    
    private void welcomeMessage(String fullName)
    {
        messageLabel.setText("Welcome " + fullName + " to the GP System");
        messageLabel.setBounds(109, 10, 400, 50);
     
        // Color of the message
        messageLabel.setForeground(java.awt.Color.BLUE);
        messageLabel.setFont(new Font("Courier", Font.BOLD, 16));
     
        frame.add(messageLabel);
    }

    private void populateFrameForUser(UserType userType) {
        switch (userType) {
            case e_DOCTOR:
            case e_PATIENT: {
                // adds all the features the Doctor and Patients can access
                frame.add(messageBoard);
                frame.add(logoutButton);

                break;
            }
            case e_ADMIN: {
                frame.add(logoutButton);
                frame.add(addPatient);
                frame.add(addDoctor);
                frame.add(changePatientDoctor);
                frame.add(viewDoctor);
                frame.add(viewPatient);
                frame.add(viewBooking);
                frame.add(activityLogger);
                frame.add(messageBoard);
                frame.add(arrangeBooking);
                frame.add(rescheduleBooking);
                frame.add(removeBooking);
                break;
            }
        }

        // displays the buttons
        frame.setLayout(null);
        frame.setVisible(true);
    }
    
    @Override
    // action listener for the buttons on the landing page to navigate to the different pages
    public void actionPerformed(ActionEvent e) {
        frame.dispose();
        if (e.getSource() == logoutButton) {
            new LoginPage();
        } else if (e.getSource() == addDoctor) {
            new AddDoctor(userID);
        } else if (e.getSource() == changePatientDoctor) {
            new ChangePatient(userID);
        } else if (e.getSource() == addPatient) {
            new AddPatient(userID);
        } else if (e.getSource() == viewDoctor) {
            new ViewDoctor(userID);
        } else if (e.getSource() == viewBooking) {
            new ViewBookingOptions(userID);
        } else if (e.getSource() == viewPatient) {
            new ViewPatient(userID);
        } else if (e.getSource() == activityLogger) {
            new ActivityLogView(userID);
        } else if (e.getSource() == messageBoard) {
            new MessageScreen(userID);
        } else if (e.getSource() == arrangeBooking) {
            new ArrangeBooking(userID);
        } else if (e.getSource() == rescheduleBooking) {
            new RescheduleBooking(userID);
        } else if (e.getSource() == removeBooking) {
            new RemoveBooking(userID);
        }
    }

    // main
    public static void main(String[] args) {
        new LandingPage(1);
    }
}
