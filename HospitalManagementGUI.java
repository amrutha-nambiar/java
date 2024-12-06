import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.Vector;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

class Patient
{
    private String name, gender, tokenNumber, disease = "N/A", doctorName = "N/A";
    private int age;
    private double medicalFee = 0;

    public Patient(String name, int age, String gender, String tokenNumber)
    {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.tokenNumber = tokenNumber;
    }

    public void updateDetails(String disease, String doctorName, double medicalFee)
    {
        this.disease = disease;
        this.doctorName = doctorName;
        this.medicalFee = medicalFee;
    }

    public void displayBill()
    {
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        JOptionPane.showMessageDialog(null,
            String.format("--- BILL DETAILS ---\nPatient: %s\nDoctor: %s\nDisease: %s\nFee: $%.2f\nDate & Time: %s\n--------------------",
                name, doctorName, disease, medicalFee, dateTime));
    }

    public String getName()
    {
        return name;
    }

    public int getAge()
    {
        return age;
    }

    public String getGender()
    {
        return gender;
    }

    public String getTokenNumber()
    {
        return tokenNumber;
    }

    public String getDisease()
    {
        return disease;
    }

    public String getDoctorName()
    {
        return doctorName;
    }

    public double getMedicalFee()
    {
        return medicalFee;
    }

    @Override
    public String toString()
    {
        return String.format("%-15s %-6d %-10s %-15s %-20s %-20s %-10.2f",
            name, age, gender, tokenNumber, disease, doctorName, medicalFee);
    }
}

public class HospitalManagementGUI
{
    private Connection connection;
    private JFrame frame;
    private JTextField nameField, ageField, otherGenderField;
    private ButtonGroup genderGroup;
    private JRadioButton maleButton, femaleButton, otherButton;
    private JPasswordField passwordField;
    private JTable patientTable;
    private final String adminPassword = "MICKEYMOUSE";
    private final String dbName = "sql12750192"; // Database name
    private final String connectionURL = "jdbc:mysql://sql12.freesqldatabase.com:3306/sql12750192"; // Fixed here
    private final String username = "sql12750192"; // Database username
    private final String password = "A5N97sNXBY"; // Database password

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> new HospitalManagementGUI().createAndShowGUI());
    }

    private void createAndShowGUI()
    {
        frame = new JFrame("JJ HOSPITAL");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        connectToDatabase(); // Connect to database
        showWelcomeScreen();
        frame.setVisible(true);
    }

    private void connectToDatabase()
    {
        try
        {
            // Load the MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Step 1: Connect to MySQL server
            connection = DriverManager.getConnection(connectionURL, username, password);
            System.out.println("Connected to database: " + dbName);

            // Create patients table if it doesn't exist
            String createTableSQL = "CREATE TABLE IF NOT EXISTS patients ( " +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "token_number VARCHAR(255) NOT NULL UNIQUE, " +
                "name VARCHAR(255) NOT NULL, " +
                "age INT NOT NULL, " +
                "gender ENUM('Male', 'Female', 'Other'), " +
                "disease VARCHAR(255), " +
                "doctor_name VARCHAR(255), " +
                "medical_fee DECIMAL(10, 2))";
            Statement stmt = connection.createStatement();
            stmt.execute(createTableSQL);
            stmt.close();
        }
        catch (Exception e)
        {
            e.printStackTrace(); // Print the full stack trace to console
            JOptionPane.showMessageDialog(frame, "Error connecting to database: " + e.getMessage());
            System.exit(1);
        }
    }

    private void showWelcomeScreen()
    {
        String welcomeImageUrl = "https://img.freepik.com/premium-vector/modern-colored-medical-hospital-building-with-sky-clouds_1322206-57571.jpg"; // Welcome image
        BackgroundPanel welcomePanel = new BackgroundPanel(welcomeImageUrl);
        welcomePanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 0, 10, 0);
        JLabel hospitalLabel = new JLabel("JJ Hospital", JLabel.CENTER);
        hospitalLabel.setFont(new Font("Serif", Font.BOLD, 70));
        hospitalLabel.setForeground(Color.BLACK);
        gbc.gridy = 0;
        welcomePanel.add(hospitalLabel, gbc);

        JLabel subtitleLabel = new JLabel("A Bridge to Better Health", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Serif", Font.ITALIC, 50));
        subtitleLabel.setForeground(Color.BLACK);
        gbc.gridy = 1;
        welcomePanel.add(subtitleLabel, gbc);

        // Timer to switch to the main menu
        Timer timer = new Timer(2000, e -> showMainMenu());
        timer.setRepeats(false);
        timer.start();

        setContentPanel(welcomePanel);
    }

    private void showMainMenu()
    {
        String imageUrl = "https://www.hiranandanihospital.org/public/international-patient/request-quote.jpg"; // Main menu image
        BackgroundPanel mainMenuPanel = new BackgroundPanel(imageUrl);
        mainMenuPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JButton registerButton = createButton("Register Patient", e -> showRegistrationForm());
        JButton loginButton = createButton("Admin Login", e -> showLoginForm());
        JButton exitButton = createButton("Exit", e -> System.exit(0));

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainMenuPanel.add(registerButton, gbc);
        gbc.gridy = 1;
        mainMenuPanel.add(loginButton, gbc);
        gbc.gridy = 2;
        mainMenuPanel.add(exitButton, gbc);

        setContentPanel(mainMenuPanel);
    }

    private JButton createButton(String text, ActionListener listener)
    {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(250, 50));
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(0, 153, 255));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        button.setFocusPainted(false);
        button.addActionListener(listener);
        button.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseEntered(java.awt.event.MouseEvent evt)
            {
                button.setBackground(new Color(0, 122, 204));
            }

            public void mouseExited(java.awt.event.MouseEvent evt)
            {
                button.setBackground(new Color(0, 153, 255));
            }
        });
        return button;
    }

    private JButton createBackButton(ActionListener listener)
    {
        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(150, 60));
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setBackground(new Color(255, 51, 51));
        backButton.setForeground(Color.WHITE);
        backButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        backButton.setFocusPainted(false);
        backButton.addActionListener(listener);
        return backButton;
    }

    private void showRegistrationForm()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField(20);
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Age:"));
        ageField = new JTextField(20);
        inputPanel.add(ageField);

        inputPanel.add(new JLabel("Gender:"));
        JPanel genderPanel = new JPanel(new FlowLayout());
        maleButton = new JRadioButton("Male");
        femaleButton = new JRadioButton("Female");
        otherButton = new JRadioButton("Other");
        otherGenderField = new JTextField(10);
        otherGenderField.setEnabled(false);

        genderGroup = new ButtonGroup();
        genderGroup.add(maleButton);
        genderGroup.add(femaleButton);
        genderGroup.add(otherButton);

        otherButton.addActionListener(e -> otherGenderField.setEnabled(true));
        maleButton.addActionListener(e -> otherGenderField.setEnabled(false));
        femaleButton.addActionListener(e -> otherGenderField.setEnabled(false));

        genderPanel.add(maleButton);
        genderPanel.add(femaleButton);
        genderPanel.add(otherButton);
        genderPanel.add(new JLabel("Custom:"));
        genderPanel.add(otherGenderField);

        inputPanel.add(genderPanel);

        JButton submitButton = createButton("Register", e -> registerPatient());
        inputPanel.add(submitButton);

        JButton backButton = createBackButton(e -> showMainMenu());
        inputPanel.add(backButton);

        panel.add(inputPanel, BorderLayout.CENTER);
        setContentPanel(panel);
    }

    private void registerPatient()
    {
        try
        {
            String name = nameField.getText().trim();
            String ageText = ageField.getText().trim();
            String gender = getSelectedGender();
            int age = Integer.parseInt(ageText); // Might throw an exception

            // Validate name
            if (name.isEmpty() || !name.matches("[a-zA-Z ]+"))
            {
                JOptionPane.showMessageDialog(frame, "Please enter a valid name (letters only).");
                return;
            }

            // Validate age
            if (age < 0 || age > 120)
            {
                JOptionPane.showMessageDialog(frame, "Please enter a valid age (0-120).");
                return;
            }

            // Validate gender
            if (gender.isEmpty())
            {
                return; // Already shows a relevant message in getSelectedGender()
            }

            String token = generateUniqueToken();
            Patient newPatient = new Patient(name, age, gender, token);

            if (addPatientToDatabase(newPatient)) // Save to database
            {
                String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                JOptionPane.showMessageDialog(frame, "Patient registered successfully!\nToken: " + token + "\nDate & Time: " + dateTime);
                showMainMenu();
            }
            else
            {
                JOptionPane.showMessageDialog(frame, "Registration limit reached.");
            }

        }
        catch (NumberFormatException e)
        {
            JOptionPane.showMessageDialog(frame, "Please enter a valid age.");
        }
    }

    private boolean addPatientToDatabase(Patient patient)
    {
        try
        {
            String sql = "INSERT INTO patients (token_number, name, age, gender) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, patient.getTokenNumber());
            stmt.setString(2, patient.getName());
            stmt.setInt(3, patient.getAge());
            stmt.setString(4, patient.getGender());
            stmt.executeUpdate();
            return true;
        }
        catch (SQLException e)
        {
            System.out.println("Error adding patient: " + e.getMessage());
            return false;
        }
    }

    private String getSelectedGender()
    {
        if (maleButton.isSelected()) return "Male";
        if (femaleButton.isSelected()) return "Female";
        if (otherButton.isSelected())
        {
            String customGender = otherGenderField.getText().trim();
            if (customGender.isEmpty())
            {
                JOptionPane.showMessageDialog(frame, "Please specify the custom gender.");
                return "";
            }
            return customGender;
        }
        JOptionPane.showMessageDialog(frame, "Please select a gender.");
        return "";
    }

    private void showLoginForm()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(144, 238, 144)); // Optional: set a background color
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel passwordLabel = new JLabel("Admin Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 18));
        centerPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(30);
        passwordField.setPreferredSize(new Dimension(250, 40));
        gbc.gridy = 1;
        centerPanel.add(passwordField, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(20, 10, 10, 10);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton loginButton = createButton("Login", e -> loginAdmin());
        JButton backButton = createBackButton(e -> showMainMenu());

        Dimension buttonSize = new Dimension(150, 60);
        loginButton.setPreferredSize(buttonSize);
        backButton.setPreferredSize(buttonSize);

        buttonPanel.add(loginButton);
        buttonPanel.add(backButton);
        centerPanel.add(buttonPanel, gbc);

        panel.add(centerPanel, BorderLayout.CENTER);
        setContentPanel(panel);
    }

    private void loginAdmin()
    {
        String enteredPassword = new String(passwordField.getPassword());
        if (enteredPassword.equals(adminPassword))
        {
            showAdminPanel();
        }
        else
        {
            JOptionPane.showMessageDialog(frame, "Invalid password.");
        }
    }

    private void showAdminPanel()
    {
        JPanel panel = new JPanel(new BorderLayout());
        JButton viewAllPatientsButton = createButton("View All Patients", e -> viewAllPatients());
        JButton editPatientButton = createButton("Edit Patient by Token", e -> updatePatientDetails());
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(viewAllPatientsButton);
        buttonPanel.add(editPatientButton);
        panel.add(buttonPanel, BorderLayout.CENTER);

        JButton backButton = createBackButton(e -> showMainMenu());
        panel.add(backButton, BorderLayout.SOUTH);

        setContentPanel(panel);
    }

    private void viewAllPatients()
    {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = {"Name", "Age", "Gender", "Token No", "Disease", "Doctor", "Fee"};
        Vector<Vector<String>> data = new Vector<>();

        try
        {
            String sql = "SELECT * FROM patients";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
            {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("name"));
                row.add(String.valueOf(rs.getInt("age")));
                row.add(rs.getString("gender"));
                row.add(rs.getString("token_number"));
                row.add(rs.getString("disease"));
                row.add(rs.getString("doctor_name"));
                row.add(String.valueOf(rs.getDouble("medical_fee")));
                data.add(row);
            }
        }
        catch (SQLException e)
        {
            JOptionPane.showMessageDialog(frame, "Error fetching patients: " + e.getMessage());
        }

        patientTable = new JTable(data, new Vector<>(java.util.Arrays.asList(columnNames)));
        JScrollPane scrollPane = new JScrollPane(patientTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton updateButton = createButton("Update Patient", e -> updatePatientDetails());
        JButton generateBillButton = createButton("Generate Bill", e -> generateBill());
        JPanel actionPanel = new JPanel(new FlowLayout());
        actionPanel.add(updateButton);
        actionPanel.add(generateBillButton);
        panel.add(actionPanel, BorderLayout.SOUTH);

        JButton backButton = createBackButton(e -> showAdminPanel());
        panel.add(backButton, BorderLayout.NORTH);

        setContentPanel(panel);
    }

    private void updatePatientDetails()
    {
        String token = JOptionPane.showInputDialog("Enter patient token number:");
        Patient patient = findPatientByToken(token);
        if (patient != null)
        {
            String disease = JOptionPane.showInputDialog("Enter disease:");
            String doctorName = JOptionPane.showInputDialog("Enter doctor name:");
            try
            {
                double fee = Double.parseDouble(JOptionPane.showInputDialog("Enter medical fee:"));
                patient.updateDetails(disease, doctorName, fee);
                updatePatientInDatabase(patient); // Update the database
                JOptionPane.showMessageDialog(frame, "Patient details updated.");
            }
            catch (NumberFormatException e)
            {
                JOptionPane.showMessageDialog(frame, "Please enter a valid medical fee.");
            }
        }
        else
        {
            JOptionPane.showMessageDialog(frame, "Patient not found.");
        }
    }

    private Patient findPatientByToken(String token)
    {
        try
        {
            String sql = "SELECT * FROM patients WHERE token_number = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
            {
                Patient patient = new Patient(rs.getString("name"), rs.getInt("age"),
                    rs.getString("gender"), rs.getString("token_number"));
                patient.updateDetails(rs.getString("disease"),
                    rs.getString("doctor_name"), rs.getDouble("medical_fee"));
                return patient;
            }
        }
        catch (SQLException e)
        {
            JOptionPane.showMessageDialog(frame, "Error searching for patient: " + e.getMessage());
        }
        return null;
    }

    private void updatePatientInDatabase(Patient patient)
    {
        try
        {
            String sql = "UPDATE patients SET disease = ?, doctor_name = ?, medical_fee = ? WHERE token_number = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, patient.getDisease());
            stmt.setString(2, patient.getDoctorName());
            stmt.setDouble(3, patient.getMedicalFee());
            stmt.setString(4, patient.getTokenNumber());
            stmt.executeUpdate();
        }
        catch (SQLException e)
        {
            JOptionPane.showMessageDialog(frame, "Error updating patient details: " + e.getMessage());
        }
    }

    private void generateBill()
    {
        String token = JOptionPane.showInputDialog("Enter patient token number:");
        Patient patient = findPatientByToken(token);
        if (patient != null)
        {
            patient.displayBill();
        }
        else
        {
            JOptionPane.showMessageDialog(frame, "Patient not found.");
        }
    }

    private void setContentPanel(JPanel panel)
    {
        frame.setContentPane(panel);
        frame.revalidate();
        frame.repaint();
    }

    private String generateUniqueToken()
    {
        Random rand = new Random();
        return String.format("T%04d", rand.nextInt(10000));
    }

    // BackgroundPanel class for setting background images
    class BackgroundPanel extends JPanel
    {
        private BufferedImage backgroundImage;

        public BackgroundPanel(String imageUrl)
        {
            try
            {
                URL url = new URL(imageUrl);
                this.backgroundImage = ImageIO.read(url);
            }
            catch (MalformedURLException e)
            {
                System.err.println("Invalid URL: " + e.getMessage());
            }
            catch (IOException e)
            {
                System.err.println("Error loading image: " + e.getMessage());
            }
        }

        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            if (backgroundImage != null)
            {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}