package msja;

import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Date;

public class MSJA {
    public static void main(String[] args) {
        new MS();
    }
}

class Data {
    private static Data instance;
    int i = 0;
    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> pins = new ArrayList<>();
    ArrayList<String> choice = new ArrayList<>();
    ArrayList<Double> balance = new ArrayList<>();
    HashMap<Integer, ArrayList<String>> transactionHistory = new HashMap<>();
    private static final String TRANSACTION_HISTORY_FILE = "transaction_history.txt";

    private Data() {
        // Private constructor to enforce Singleton pattern
    }

    public static Data getInstance() {
        if (instance == null) {
            instance = new Data();
        }
        return instance;
    }

    public void DBcheck_Create(){
        File Data = new File("Data.txt");
        File TH = new File (TRANSACTION_HISTORY_FILE);
        
        if (!Data.exists()) {
            try {
                Data.createNewFile();}

            catch (IOException e) {
                System.out.println("Error creating file");
            }
        }

        if (!TH.exists()) {
            try {
                TH.createNewFile();}

            catch (IOException e) {
                System.out.println("Error creating file");
            }
        }

    }        

    public void writeFile() {
        try (FileWriter writer = new FileWriter("Data.txt")) {
            writer.write("Names :\n");
            for (String item : names) {
                writer.write(item + "\n");
            }

            writer.write("Pins :\n");
            for (String item : pins) {
                writer.write(item + "\n");
            }

            writer.write("Balance :\n");
            for (Double item : balance) {
                writer.write(item + "\n");
            }

            writer.write("Choice :\n");
            for (String item : choice) {
                writer.write(item + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    public void readFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("Data.txt"))) {
            String line;
            names.clear();
            pins.clear();
            balance.clear();
            choice.clear();

            boolean isNames = true, isPins = false, isBalance = false, isChoice = false;

            while ((line = reader.readLine()) != null) {
                if (line.equals("Names :")) {
                    isNames = true;
                    isPins = isBalance = isChoice = false;
                } else if (line.equals("Pins :")) {
                    isPins = true;
                    isNames = isBalance = isChoice = false;
                } else if (line.equals("Balance :")) {
                    isBalance = true;
                    isNames = isPins = isChoice = false;
                } else if (line.equals("Choice :")) {
                    isChoice = true;
                    isNames = isPins = isBalance = false;
                } else {
                    if (isNames) {
                        names.add(line);
                    } else if (isPins) {
                        pins.add(line);
                    } else if (isBalance) {
                        balance.add(Double.parseDouble(line));
                    } else if (isChoice) {
                        choice.add(line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading from file: " + e.getMessage());
            
        }
    }

    public void saveTransactionHistory() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(TRANSACTION_HISTORY_FILE))) {
            for (int i = 0; i < names.size(); i++) {
                writer.println("User " + i);
                for (String transaction : transactionHistory.get(i)) {
                    writer.println(transaction);
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving transaction history: " + e.getMessage());
        }
    }

    public void loadTransactionHistory() {
        try (BufferedReader reader = new BufferedReader(new FileReader(TRANSACTION_HISTORY_FILE))) {
            String line;
            int userIndex = 0;
            while ((line = reader.readLine())!= null) {
                if (line.startsWith("User ")) {
                    userIndex = Integer.parseInt(line.substring(5));
                    if (userIndex >= names.size()) {
                        names.add(""); // initialize name for new user
                    }
                    transactionHistory.put(transactionHistory.size(), new ArrayList<>());
                } else {
                    transactionHistory.get(userIndex).add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading transaction history: " + e.getMessage());

        }
    }

    public void addAcc(String fullName, String pin, String choice) {
        names.add(fullName);
        pins.add(pin);
        balance.add(0.0);
        this.choice.add(choice);
        i = names.size() - 1;
        transactionHistory.put(i, new ArrayList<>()); // Initialize transaction history for new user
    }

    public void logTransaction(int userId, String transaction) {
        transactionHistory.get(userId).add(transaction);
    }

    public ArrayList<String> getTransactionHistory(int userId) {
        return transactionHistory.get(userId);
    }
}

class MS extends JFrame implements ActionListener {
    JLabel welcomeLabel, pinLabel, createLabel;
    JPasswordField pinField;
    JButton loginButton, exitButton;
    JPanel backgroundPanel;
    private static HashMap<String, String> accounts = new HashMap<>();
    Data data = Data.getInstance();
    
    public MS() {
        backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Load background image
                Image img = new ImageIcon("ms.jpg").getImage();
                // Draw image at the specified location
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };

        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);

        // Set up the frame
        setTitle("MoneySafeGuard Application");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;

        // Create and add components
        welcomeLabel = new JLabel("Welcome to MoneySafeGuard", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        backgroundPanel.add(welcomeLabel, gbc);

        pinLabel = new JLabel("Enter PIN:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        backgroundPanel.add(pinLabel, gbc);

        pinField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        backgroundPanel.add(pinField, gbc);

        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(200, 50)); // Set the size of the login button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // Make the button span both columns
        backgroundPanel.add(loginButton, gbc);
        loginButton.addActionListener(this);
        loginButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(Color.cyan); // Change to highlight color
            }

            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(null); // Change back to default color
            }
        });

        exitButton = new JButton("Exit");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2; // Make the button span both columns
        backgroundPanel.add(exitButton, gbc);
        exitButton.addActionListener(this);
        exitButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                exitButton.setBackground(Color.cyan); // Change to highlight color
            }

            public void mouseExited(MouseEvent e) {
                exitButton.setBackground(null); // Change back to default color
            }
        });

        createLabel = new JLabel("<html><a href=''>Register</a></html>");
        createLabel.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        backgroundPanel.add(createLabel, gbc);
        createLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        createLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new Screen(MS.this);
                setVisible(false);
            }

            public void mouseEntered(MouseEvent e) {
                createLabel.setForeground(Color.cyan); // Change to highlight color
            }

            public void mouseExited(MouseEvent e) {
                createLabel.setForeground(null); // Change back to default color
            }
        });

        setVisible(true);
        data.DBcheck_Create();
        data.readFile();
        data.loadTransactionHistory();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            String enteredPIN = new String(pinField.getPassword());
            if (enteredPIN.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your PIN", "Warning", JOptionPane.WARNING_MESSAGE);
            } else if (enteredPIN.length() < 6) {
                JOptionPane.showMessageDialog(this, "PIN should be at least 6 digits long", "Warning",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                if (!data.pins.isEmpty()) {
                    boolean pinFound = false;
                    for (int i = 0; i < data.pins.size(); i++) {
                        if (data.pins.get(i).equals(enteredPIN)) {
                            data.i = i;
                            new NewScreen(this, data.names.get(i), data.choice.get(i));
                            this.setVisible(false);
                            dispose();
                            pinFound = true;
                            break; // exit the loop since we found a match
                        }
                    }
                    if (!pinFound) {
                        JOptionPane.showMessageDialog(this, "Invalid PIN", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    // Handle the case where the lists are empty
                    System.out.println("Database are empty!");

                }
            }
        } else if (e.getSource() == exitButton) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", "Exit Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
    }
}

class Screen extends JFrame implements ActionListener {
    JLabel fullNameLabel, pinLabel, confirmPinLabel, phoneNumberLabel, choiceLabel;
    JTextField fullNameField, phoneNumberField;
    JPasswordField pinField, confirmPinField;
    JComboBox<String> choiceBox;
    JButton createButton, backButton;
    MS mainScreen;
    String fullName;
    String pin;
    String confirmPin;
    String phoneNumber;
    String choice;

    JPanel background;

    public Screen(MS mainScreen) {
        this.mainScreen = mainScreen;

        background = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Load background image
                Image img = new ImageIcon("bc.jpg").getImage();
                // Draw image at the specified location
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };

        setTitle("Create Account");
        background.setLayout(new GridBagLayout());
        setContentPane(background);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;

        fullNameLabel = new JLabel("Full Name:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        background.add(fullNameLabel, gbc);

        fullNameField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        background.add(fullNameField, gbc);

        pinLabel = new JLabel("PIN:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        background.add(pinLabel, gbc);

        pinField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        background.add(pinField, gbc);

        confirmPinLabel = new JLabel("Confirm PIN:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(confirmPinLabel, gbc);

        confirmPinField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 2;
        background.add(confirmPinField, gbc);

        phoneNumberLabel = new JLabel("Phone Number:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        background.add(phoneNumberLabel, gbc);

        phoneNumberField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 3;
        background.add(phoneNumberField, gbc);

        choiceLabel = new JLabel("Sex:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        background.add(choiceLabel, gbc);

        choiceBox = new JComboBox<>(new String[] { "", "Female","Male" });
        gbc.gridx = 1;
        gbc.gridy = 4;
        background.add(choiceBox, gbc);

        createButton = new JButton("Create Account");
        gbc.gridx = 0;
        gbc.gridy = 5;
        background.add(createButton, gbc);
        createButton.addActionListener(this);
        createButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                createButton.setBackground(Color.cyan); // Change to highlight color
            }

            public void mouseExited(MouseEvent e) {
                createButton.setBackground(null); // Change back to default color
            }
        });

        backButton = new JButton("Back");
        gbc.gridx = 1;
        gbc.gridy = 5;
        background.add(backButton, gbc);
        backButton.addActionListener(this);
        backButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                backButton.setBackground(Color.cyan); // Change to highlight color
            }

            public void mouseExited(MouseEvent e) {
                backButton.setBackground(null); // Change back to default color
            }
        });
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == createButton) {
            fullName = fullNameField.getText();
            pin = new String(pinField.getPassword());
            confirmPin = new String(confirmPinField.getPassword());
            phoneNumber = phoneNumberField.getText();
            choice = (String) choiceBox.getSelectedItem();

            if (fullName.isEmpty() || pin.isEmpty() || confirmPin.isEmpty() || phoneNumber.isEmpty()
                    || choice.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields", "Warning",
                        JOptionPane.WARNING_MESSAGE);
            } else if (pin.length() < 6) {
                JOptionPane.showMessageDialog(this, "PIN should be at least 6 digits long", "Warning",
                        JOptionPane.WARNING_MESSAGE);
            } else if (!pin.equals(confirmPin)) {
                JOptionPane.showMessageDialog(this, "PINs do not match", "Warning", JOptionPane.WARNING_MESSAGE);
            } else {
                Data data = Data.getInstance();
                data.addAcc(fullName, pin, choice);
                data.writeFile();
                JOptionPane.showMessageDialog(this, "Account created successfully", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                mainScreen.setVisible(true);
                this.dispose();
            }
        } else if (e.getSource() == backButton) {
            mainScreen.setVisible(true);
            this.dispose();
        }
    }
}

class NewScreen extends JFrame implements ActionListener {
     MS login;
    String fullName;
    String choice;
    JButton withdrawButton, depositButton, balanceButton, historyButton, settingButton;
    JPanel bg;
    public NewScreen(MS login, String fullName, String choice) {
        this.login = login;
        this.fullName = fullName;
        this.choice = choice;

        bg = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Load background image
                Image img = new ImageIcon("va.jpg").getImage();
                // Draw image at the specified location
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };

        setTitle("Logged In");
        bg.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;
        setContentPane(bg);

        // Withdraw Button
        withdrawButton = new JButton("Withdraw");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2; // Single column
        bg.add(withdrawButton, gbc);
        withdrawButton.addActionListener(this);
        withdrawButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                withdrawButton.setBackground(Color.cyan); // Change to highlight color
            }

            public void mouseExited(MouseEvent e) {
                withdrawButton.setBackground(null); // Change back to default color
            }
        });

        // Deposit Button
        depositButton = new JButton("Deposit");
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2; // Single column
        bg.add(depositButton, gbc);
        depositButton.addActionListener(this);
        depositButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                depositButton.setBackground(Color.cyan); // Change to highlight color
            }

            public void mouseExited(MouseEvent e) {
                depositButton.setBackground(null); // Change back to default color
            }
        });

        // Balance Button
        balanceButton = new JButton("Balance");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1; // Single column
        bg.add(balanceButton, gbc);
        balanceButton.addActionListener(this);
        balanceButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                balanceButton.setBackground(Color.cyan); // Change to highlight color
            }

            public void mouseExited(MouseEvent e) {
                balanceButton.setBackground(null); // Change back to default color
            }
        });

        // History Button
        historyButton = new JButton("History");
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1; // Single column
        bg.add(historyButton, gbc);
        historyButton.addActionListener(this);
        historyButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                historyButton.setBackground(Color.cyan); // Change to highlight color
            }

            public void mouseExited(MouseEvent e) {
                historyButton.setBackground(null); // Change back to default color
            }
        });

        // Setting Button
        settingButton = new JButton("Setting");
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 1; // Single column
        bg.add(settingButton, gbc);
        settingButton.addActionListener(this);
        settingButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                settingButton.setBackground(Color.cyan); // Change to highlight color
            }

            public void mouseExited(MouseEvent e) {
                settingButton.setBackground(null); // Change back to default color
            }
        });

        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        // Implement actions for buttons
        if (e.getSource() == withdrawButton) {
            new WithdrawScreen(this);
            this.setVisible(false);
        } else if (e.getSource() == depositButton) {
            new depositScreen(this);
            this.setVisible(false);
        } else if (e.getSource() == balanceButton) {
            new balanceScreen(this);
            this.setVisible(false);

        } else if (e.getSource() == historyButton) {
            new historyScreen(this);
            this.setVisible(false);

        } else if (e.getSource() == settingButton) {
            new SettingScreen(this, fullName);
            this.setVisible(false);
        }
    }
}

class WithdrawScreen extends JFrame implements ActionListener {
    JLabel balanceLabel;
    JButton withdrawButton, backButton;
    JTextField withdrawAmountField;
    double balance = 0.0;
    NewScreen mainScreen;
    Data data = Data.getInstance();
    
    JPanel background;
    
    public WithdrawScreen(NewScreen mainScreen) {
       this.mainScreen = mainScreen;
        this.balance = data.balance.get(data.i); // inherit balance from mainScreen'

        background = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Load background image
                Image img = new ImageIcon("bc.jpg").getImage();
                // Draw image at the specified location
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };

        setTitle("Withdraw Cash");
        background.setLayout(new GridBagLayout()); // Use GridBagLayout for centering components
        setContentPane(background);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;

        balanceLabel = new JLabel("Balance: $" + String.format("%.2f", balance));
        gbc.gridx = 0;
        gbc.gridy = 0;
        background.add(balanceLabel, gbc);

        withdrawAmountField = new JTextField(10);
        gbc.gridx = 0;
        gbc.gridy = 1;
        background.add(withdrawAmountField, gbc);

        withdrawButton = new JButton("Withdraw");
        withdrawButton.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 2;
        background.add(withdrawButton, gbc);

        withdrawButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                withdrawButton.setBackground(Color.cyan); // Change to highlight color
            }

            public void mouseExited(MouseEvent e) {
                withdrawButton.setBackground(null); // Change back to default color
            }
        });

        backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the WithdrawScreen
                mainScreen.setVisible(true); // Show the NewScreen
            }
        });

        backButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                backButton.setBackground(Color.cyan); // Change to highlight color
            }

            public void mouseExited(MouseEvent e) {
                backButton.setBackground(null); // Change back to default color
            }
        });
        gbc.gridy = 3;
        background.add(backButton, gbc);

        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame on the screen
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == withdrawButton) {
        try {
            double withdrawAmount = Double.parseDouble(withdrawAmountField.getText());
            if (withdrawAmount <= 0) {
                JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a positive value.");
            } else if (withdrawAmount > balance) {
                JOptionPane.showMessageDialog(this, "Insufficient funds.");
            } else {
                // Show confirmation dialog
                int option = JOptionPane.showConfirmDialog(this,
                        String.format("Withdraw $%.2f from your balance?\nRemaining balance: $%.2f", withdrawAmount, balance),
                        "Confirm Withdrawal", JOptionPane.YES_NO_OPTION);
                
                if (option == JOptionPane.YES_OPTION) {
                    balance -= withdrawAmount;
                    balanceLabel.setText("Balance: $" + String.format("%.2f", balance));
                    data.balance.set(data.i, balance);
                    data.writeFile();

                    // Log transaction history with time and date
                    Date date = new Date();
                    String transaction = date.toString() + " - Withdrawal: -$" + String.format("%.2f", withdrawAmount);
                    data.logTransaction(data.i, transaction);
                    data.saveTransactionHistory();

                    // Show confirmation dialog with the formatted message
                    JOptionPane.showMessageDialog(this, String.format("Withdrawal of $%.2f from your balance.\nRemaining balance: $%.2f", withdrawAmount, balance));

                    this.dispose(); // close this window
                    mainScreen.setVisible(true); // show mainScreen
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid amount.");
        }
    }
}
}


class depositScreen extends JFrame implements ActionListener {
    JLabel balanceLabel;
    JButton depositButton, backButton;
    JTextField depositAmountField;
    NewScreen mainScreen;
    Data data = Data.getInstance();
    
    JPanel background;
    public depositScreen(NewScreen mainScreen) {
        this.mainScreen = mainScreen;
        
        background = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Load background image
                Image img = new ImageIcon("bc.jpg").getImage();
                // Draw image at the specified location
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };
        
        setTitle("Deposit Cash");
        background.setLayout(new GridBagLayout()); // Use GridBagLayout for centering components
         setContentPane(background);
         
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;

        balanceLabel = new JLabel("Balance: $" + String.format("%.2f", data.balance.get(data.i)));
        gbc.gridx = 0;
        gbc.gridy = 0;
        background.add(balanceLabel, gbc);

        depositAmountField = new JTextField(10);
        gbc.gridx = 0;
        gbc.gridy = 1;
       background.add(depositAmountField, gbc);

        depositButton = new JButton("Deposit");
        gbc.gridx = 0;
        gbc.gridy = 2;
        background.add(depositButton, gbc);
        depositButton.addActionListener(this);

          depositButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
             depositButton.setBackground(Color.cyan); // Change to highlight color
            }

            public void mouseExited(MouseEvent e) {
                depositButton.setBackground(null); // Change back to default color
            }
        });
        
          
        backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the BalanceScreen
                mainScreen.setVisible(true); // Show the NewScreen
            }
        });
        
          backButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
              backButton.setBackground(Color.cyan); // Change to highlight color
            }

            public void mouseExited(MouseEvent e) {
                backButton.setBackground(null); // Change back to default color
            }
        });
        
          
        gbc.gridy = 3;
        background.add(backButton, gbc);

        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame on the screen
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == depositButton) {
        try {
            double depositAmount = Double.parseDouble(depositAmountField.getText());
            if (depositAmount <= 0) {
                JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a positive value.");
            } else {
                // Show confirmation dialog
                int option = JOptionPane.showConfirmDialog(this,
                        String.format("Deposit $%.2f to your balance?\nCurrent balance: $%.2f", depositAmount, data.balance.get(data.i)),
                        "Confirm Deposit", JOptionPane.YES_NO_OPTION);
                
                if (option == JOptionPane.YES_OPTION) {
                    data.balance.set(data.i, data.balance.get(data.i) + depositAmount);
                    balanceLabel.setText("Balance: $" + String.format("%.2f", data.balance.get(data.i)));
                    data.writeFile();

                    // Log transaction history with time and date
                    Date date = new Date();
                    String transaction = date.toString() + " - Deposit: +$" + String.format("%.2f", depositAmount);
                    data.logTransaction(data.i, transaction);
                    data.saveTransactionHistory();

                    // Show confirmation dialog with the formatted message
                    JOptionPane.showMessageDialog(this, String.format("Deposit of $%.2f added to your balance.\nNew balance: $%.2f", depositAmount, data.balance.get(data.i)));

                    this.dispose(); // close this window
                    mainScreen.setVisible(true); // show mainScreen
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid amount.");
        }
    }
}
}

class balanceScreen extends JFrame {
    JLabel balanceLabel;
    JButton backButton;
    NewScreen mainScreen;
    Data data = Data.getInstance();
    
    JPanel background;
    public balanceScreen(NewScreen mainScreen) {
        this.mainScreen = mainScreen;
        
         background = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Load background image
                Image img = new ImageIcon("bc.jpg").getImage();
                // Draw image at the specified location
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };
         
        setTitle("Check Balance");
        background.setLayout(new GridBagLayout()); // Use GridBagLayout for centering components
         setContentPane(background);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;

        // Add components
        balanceLabel = new JLabel("Balance: $" + String.format("%.2f", data.balance.get(data.i)));
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Example of setting font
        gbc.gridx = 0;
        gbc.gridy = 0;
        background.add(balanceLabel, gbc);

        backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the BalanceScreen
                mainScreen.setVisible(true); // Show the NewScreen
            }
        });
        
          backButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
              backButton.setBackground(Color.cyan); // Change to highlight color
            }

            public void mouseExited(MouseEvent e) {
                backButton.setBackground(null); // Change back to default color
            }
        });
        
        gbc.gridy = 1;
        add(backButton, gbc);

        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame on the screen
        setVisible(true);
    }
}

class historyScreen extends JFrame {
    JTextArea historyArea;
    Data data = Data.getInstance();
    NewScreen mainScreen;
    JButton backButton;
     
    public historyScreen(NewScreen mainScreen) {
        this.mainScreen = mainScreen;
       
        setTitle("Transaction History");
       setLayout(new BorderLayout());
        
        historyArea = new JTextArea(20, 30);
        historyArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(historyArea);
    add(scrollPane, BorderLayout.CENTER);
        backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the historyScreen
                mainScreen.setVisible(true); // Show the NewScreen
            }
        });
        
        backButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
              backButton.setBackground(Color.cyan); // Change to highlight color
            }

            public void mouseExited(MouseEvent e) {
                backButton.setBackground(null); // Change back to default color
            }
        });
        
        updateHistory();
       add(backButton, BorderLayout.SOUTH);

        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void updateHistory() {
        StringBuilder historyText = new StringBuilder();
        ArrayList<String> transactionHistory = data.getTransactionHistory(data.i);
        for (String transaction : transactionHistory) {
            historyText.append(transaction).append("\n");
        }
        historyArea.setText(historyText.toString());
    }
}

class SettingScreen extends JFrame implements ActionListener {

    private NewScreen setting;
    private JButton changeNameButton, changePinButton, logoutButton,backButton;
    private JLabel welcomeLabel;
    private String fullName;
    JPanel bg;
    Data data;

    public SettingScreen(NewScreen mainScreen, String fullName) {
        this.setting = setting;
        this.fullName = fullName;
        
        bg = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Load background image
                Image img = new ImageIcon("va.jpg").getImage();
                // Draw image at the specified location
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };
        
        data = Data.getInstance();
        setTitle("Settings");
        bg.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;
        setContentPane(bg);
        
        welcomeLabel = new JLabel( fullName, JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
       bg.add(welcomeLabel, gbc);

        changeNameButton = new JButton("Change Name");
        gbc.gridx = 0;
        gbc.gridy = 1;
        
        bg.add(changeNameButton, gbc);
        changeNameButton.addActionListener(this);
         changeNameButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                 changeNameButton.setBackground(Color.cyan); // Change to highlight color
            }

            public void mouseExited(MouseEvent e) {
                 changeNameButton.setBackground(null); // Change back to default color
            }
        });
         
        changePinButton = new JButton("Change PIN");
        gbc.gridx = 0;
        gbc.gridy = 2;
        bg.add(changePinButton, gbc);
        changePinButton.addActionListener(this);
         changePinButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                 changePinButton.setBackground(Color.cyan); // Change to highlight color
            }

            public void mouseExited(MouseEvent e) {
                 changePinButton.setBackground(null); // Change back to default color
            }
        });

         backButton = new JButton("Home");
          gbc.gridx = 0;
        gbc.gridy = 4;
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the historyScreen
                mainScreen.setVisible(true); // Show the NewScreen
            }
        });
        
        backButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
              backButton.setBackground(Color.cyan); // Change to highlight color
            }

            public void mouseExited(MouseEvent e) {
                backButton.setBackground(null); // Change back to default color
            }
        });
         bg.add(backButton, gbc);
        
        logoutButton = new JButton("Logout");
        gbc.gridx = 0;
        gbc.gridy = 5;
        bg.add(logoutButton, gbc);
        logoutButton.addActionListener(this);
        logoutButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                logoutButton.setBackground(Color.cyan); // Change to highlight color
            }

            public void mouseExited(MouseEvent e) {
                logoutButton.setBackground(null); // Change back to default color
            }
        });

        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == changeNameButton) {
            String newName = JOptionPane.showInputDialog(this, "Enter new name:");
            if (newName != null && !newName.isEmpty()) {
                fullName = newName;
                welcomeLabel.setText("Welcome " + fullName);
                data.names.set(data.i, newName); // Update the main screen's name
                data.writeFile();
                JOptionPane.showMessageDialog(this, "Name changed successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Name cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == changePinButton) {
            Data data = Data.getInstance();
            String currentPIN = JOptionPane.showInputDialog(this, "Enter current PIN:");
            if (currentPIN != null && !currentPIN.isEmpty()) {
                String newPin = JOptionPane.showInputDialog(this, "Enter new PIN:");
                if (newPin != null && newPin.length() >= 6) {
                    if (currentPIN.equals(data.pins.get(data.i))) {
                        data.pins.set(data.i, newPin);
                        data.writeFile(); // Update the file with new PIN
                        JOptionPane.showMessageDialog(this, "PIN changed successfully!", "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Current PIN is incorrect", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "PIN should be at least 6 digits long", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Current PIN cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }else if (e.getSource() == logoutButton) {
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            new MS();  // Assuming MS is your login screen or main screen
            this.dispose();  // Dispose the current screen
        }
    }
}
}
}
