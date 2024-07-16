package gpGUI;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class ViewBookingOptions {

    // JFrame
    private JFrame frame = new JFrame();

    // JButton
    private JButton backButton = new JButton("<");

    // HashMaps
    private HashMap<String, Integer> doctorsMap = new HashMap<>();
    private HashMap<String, Integer> patientsMap = new HashMap<>();

    //Other variables
    private int userID;

    public ViewBookingOptions(int userID) {
        this.userID = userID;
        initializeFrame();
        addComponents();
    }

    private void initializeFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setLayout(null);
    }

    private void addComponents() {
        backButton.setBounds(0, 0, 50, 50);
        backButton.setFont(new Font("Courier", Font.BOLD, 20));
        backButton.setFocusable(false);

        // Panel for section 1 (Names)
        JPanel panel1 = new JPanel();
        panel1.setLayout(null);
        panel1.setBounds(50, 50, 500, 150);

        // ComboBox for names
        JComboBox<String> namesComboBox = new JComboBox<>();
        populateDoctorsName(namesComboBox);
        namesComboBox.setBounds(10, 50, 200, 30);
        panel1.add(namesComboBox);

        // Confirm button for names
        JButton confirmNamesButton = new JButton("Confirm");
        confirmNamesButton.setBounds(220, 50, 100, 30);

        confirmNamesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedName = (String) namesComboBox.getSelectedItem();
                if (selectedName != null) {
                    int doctorID = doctorsMap.get(selectedName);
                    ViewBooking viewBooking = new ViewBooking(userID);
                    viewBooking.loadDoctorAppointments(doctorID);
                    frame.dispose();
                }
            }
        });

        panel1.add(confirmNamesButton);
        frame.add(panel1);

        JPanel panel2 = new JPanel();
        panel2.setLayout(null);
        panel2.setBounds(50, 210, 500, 150);

        JComboBox<String> patientComboBox = new JComboBox<>();
        populatePatientName(patientComboBox);
        patientComboBox.setBounds(10, 50, 200, 30);
        panel2.add(patientComboBox);

        JButton confirmPatientButton = new JButton("Confirm");
        confirmPatientButton.setBounds(220, 50, 100, 30);
        confirmPatientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedPatient = (String) patientComboBox.getSelectedItem();
                if (selectedPatient != null) {
                    int patientID = patientsMap.get(selectedPatient);
                    ViewBooking viewBooking = new ViewBooking(userID);
                    viewBooking.loadPatientAppointments(patientID);
                    frame.dispose(); 
                }
            }
        });

        panel2.add(confirmPatientButton);
        frame.add(panel2);

        // Panel for section 3 (Months and Years)
        JPanel panel3 = new JPanel();
        panel3.setLayout(null);
        panel3.setBounds(50, 370, 500, 150);

        // ComboBox for months
        String[] months = { "January", "February", "March", "April", "May", "June", "July", "August", "September",
                "October", "November", "December" };
        JComboBox<String> monthsComboBox = new JComboBox<>(months);
        monthsComboBox.setBounds(10, 50, 100, 30);
        panel3.add(monthsComboBox);

        // ComboBox for years
        String[] years = new String[2090 - 2024 + 1];
        for (int i = 2024; i <= 2090; i++) {
            years[i - 2024] = String.valueOf(i);
        }
        JComboBox<String> yearsComboBox = new JComboBox<>(years);
        yearsComboBox.setBounds(120, 50, 100, 30);
        panel3.add(yearsComboBox);

        // Section 1 Title: View appointment by Doctor
        JLabel section1Title = new JLabel("View appointment by Doctor");
        section1Title.setFont(new Font("Arial", Font.BOLD, 16));
        section1Title.setBounds(0, 0, 300, 30); 
        panel1.add(section1Title);

        // Section 2 Title: View appointment by Patient
        JLabel section2Title = new JLabel("View appointment by Patient");
        section2Title.setFont(new Font("Arial", Font.BOLD, 16));
        section2Title.setBounds(0, 0, 300, 30); // Adjust positioning as needed
        panel2.add(section2Title);

        // Section 3 Title: View appointment by Month and Year
        JLabel section3Title = new JLabel("View appointment by Month and Year");
        section3Title.setFont(new Font("Arial", Font.BOLD, 16));
        section3Title.setBounds(0, 0, 300, 30); // Adjust positioning as needed
        panel3.add(section3Title);

        // Confirm button for years
        JButton confirmYearsButton = new JButton("Confirm");
        confirmYearsButton.setBounds(230, 50, 100, 30);
        confirmYearsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedMonthName = (String) monthsComboBox.getSelectedItem();
                int selectedMonth = Arrays.asList(months).indexOf(selectedMonthName) + 1; // Convert month name to number

                String selectedYearStr = (String) yearsComboBox.getSelectedItem();
                int selectedYear = Integer.parseInt(selectedYearStr); // Parse the selected year
                ViewBooking viewBooking = new ViewBooking(userID);
                viewBooking.loadAppointmentsByMonthAndYear(selectedMonth, selectedYear);
                frame.dispose();
            }
        });

        panel3.add(confirmYearsButton);
        frame.add(panel3);

        backButton.addActionListener(e -> {
            frame.dispose();
            new LandingPage(userID);
        });

        frame.add(backButton);
        frame.setVisible(true);
    }

    /**
     * Populates the given JComboBox with the names of doctors retrieved from the database.
     * The names are fetched from the "Doctors" table and added to the JComboBox.
     * Additionally, a mapping of doctor names to their corresponding IDs is stored in the doctorsMap.
     * 
     * @param namesComboBox the JComboBox to populate with doctor names
     */
    private void populateDoctorsName(JComboBox<String> namesComboBox) {
        String query = "SELECT DoctorID, CONCAT(FirstName, ' ', LastName) AS DoctorName FROM Doctors;";
        try (Connection conn = ConnectionDB.getConnection();
                PreparedStatement statement = conn.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String name = resultSet.getString("DoctorName");
                int id = resultSet.getInt("DoctorID");
                doctorsMap.put(name, id);
                namesComboBox.addItem(name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error fetching names from database.");
        }
    }

    /**
     * Populates the patient name in the given JComboBox.
     * The patient names are retrieved by executing the SQL query "SELECT PatientID, CONCAT(FirstName, ' ', LastName) AS PatientName FROM Patients;".
     * The retrieved patient names are stored in a patientsMap, where the key is the patient name and the value is the patient ID.
     * If an error occurs while fetching the names from the database, an error message is displayed.
     *
     * @param patientComboBox the JComboBox to populate with patient names
     */
    private void populatePatientName(JComboBox<String> patientComboBox) {
        String query = "SELECT PatientID, CONCAT(FirstName, ' ', LastName) AS PatientName FROM Patients;";

        try (Connection conn = ConnectionDB.getConnection();
                PreparedStatement statement = conn.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String name = resultSet.getString("PatientName");
                int id = resultSet.getInt("PatientID");
                patientsMap.put(name, id);
                patientComboBox.addItem(name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error fetching names from database.");
        }
    }

    // main
    public static void main(String[] args) {
        new ViewBookingOptions(1);
    }
}
