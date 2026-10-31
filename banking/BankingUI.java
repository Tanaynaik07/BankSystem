
package banking;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class BankingUI extends JFrame {

    private final AccountsRepository repository;

    // Theme colors
    private final Color BACKGROUND = new Color(17, 24, 39);
    private final Color SIDEBAR = new Color(11, 17, 30);
    private final Color CARD = new Color(31, 41, 55);
    private final Color TEXT = new Color(243, 244, 246);
    private final Color MUTED = new Color(156, 163, 175);
    private CardLayout pageLayout;
    private JPanel pageContainer;

    private JLabel pageHeading;
    private JLabel pageDescription;

    private JPanel mainPanel;

    public BankingUI() {

        repository = new AccountsRepository();

        setTitle("Tanay Bank | Banking System");
        setSize(1200, 750);
        setMinimumSize(new Dimension(950, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND);

        add(createSidebar(), BorderLayout.WEST);
        add(createMainArea(), BorderLayout.CENTER);

        loadDashboardData();
    }

    // SIDEBAR

    private JPanel createSidebar() {

        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBackground(SIDEBAR);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(
                BorderFactory.createEmptyBorder(25, 18, 25, 18));

        JLabel logo = new JLabel("TANAY BANK");
        logo.setFont(new Font("SansSerif", Font.BOLD, 22));
        logo.setForeground(TEXT);

        JLabel subtitle = new JLabel("BANKING SYSTEM");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 10));
        subtitle.setForeground(MUTED);

        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(subtitle);
        sidebar.add(Box.createVerticalStrut(45));

        String[] menuItems = {
                "Overview",
                "Accounts",
                "Transfers",
                "Transactions"
        };

        for (String item : menuItems) {

            JButton button = new JButton(item);

            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
            button.setAlignmentX(Component.LEFT_ALIGNMENT);
            button.setForeground(TEXT);
            button.setBackground(SIDEBAR);
            button.setFont(new Font("SansSerif", Font.PLAIN, 14));
            button.setFocusPainted(false);
            button.setBorder(
                    BorderFactory.createEmptyBorder(10, 15, 10, 10));

            button.addActionListener(e -> showPage(item));

            sidebar.add(button);
            sidebar.add(Box.createVerticalStrut(8));
        }

        return sidebar;
    }
    // MAIN AREA

    private String currentPage = "Overview";

    private JPanel createMainArea() {

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND);
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(25, 30, 25, 30));

        pageHeading = new JLabel("Overview");
        pageHeading.setFont(new Font("SansSerif", Font.BOLD, 27));
        pageHeading.setForeground(TEXT);

        pageDescription = new JLabel("Welcome to your banking dashboard");
        pageDescription.setFont(new Font("SansSerif", Font.PLAIN, 13));
        pageDescription.setForeground(MUTED);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> showPage(currentPage));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BACKGROUND);

        JPanel headingPanel = new JPanel();
        headingPanel.setLayout(new BoxLayout(headingPanel, BoxLayout.Y_AXIS));
        headingPanel.setBackground(BACKGROUND);
        headingPanel.add(pageHeading);
        headingPanel.add(Box.createVerticalStrut(8));
        headingPanel.add(pageDescription);

        header.add(headingPanel, BorderLayout.WEST);
        header.add(refreshButton, BorderLayout.EAST);

        pageLayout = new CardLayout();
        pageContainer = new JPanel(pageLayout);
        pageContainer.setBackground(BACKGROUND);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(pageContainer, BorderLayout.CENTER);

        return mainPanel;
    }

    // REUSABLE STAT CARD

    private JPanel createStatCard(
            String title,
            String value,
            String subtitle) {

        JPanel card = new JPanel();

        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD);

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(55, 65, 81)),
                        BorderFactory.createEmptyBorder(
                                22, 22, 22, 22)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(MUTED);
        titleLabel.setFont(
                new Font("SansSerif", Font.BOLD, 12));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(TEXT);
        valueLabel.setFont(
                new Font("SansSerif", Font.BOLD, 27));

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setForeground(MUTED);
        subtitleLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 12));

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(subtitleLabel);

        return card;
    }

    // DASHBOARD CONTENT

    private void displayDashboard(
            long totalBalance,
            int totalAccounts,
            ArrayList<BankTransaction> transactions) {

        JPanel dashboard = new JPanel(new BorderLayout(0, 25));
        dashboard.setBackground(BACKGROUND);
        dashboard.setBorder(
                BorderFactory.createEmptyBorder(30, 0, 0, 0));

        // Stat cards
        JPanel cardsPanel = new JPanel(new GridLayout(1, 2, 18, 0));
        cardsPanel.setBackground(BACKGROUND);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

        JPanel balanceCard = createStatCard(
                "TOTAL BALANCE",
                currencyFormat.format(totalBalance),
                "Across all registered accounts");

        JPanel accountsCard = createStatCard(
                "TOTAL ACCOUNTS",
                String.valueOf(totalAccounts),
                "Registered bank accounts");

        cardsPanel.add(balanceCard);
        cardsPanel.add(accountsCard);

        dashboard.add(cardsPanel, BorderLayout.NORTH);

        // Recent transactions
        dashboard.add(
                createRecentTransactions(transactions),
                BorderLayout.CENTER);

        displayPage("Overview", dashboard);
    }

    private void displayPage(String name, JPanel page) {

        // Remove an older version of this page, if it exists.
        for (Component component : pageContainer.getComponents()) {
            if (name.equals(component.getName())) {
                pageContainer.remove(component);
                break;
            }
        }

        page.setName(name);
        pageContainer.add(page, name);

        pageLayout.show(pageContainer, name);

        pageContainer.revalidate();
        pageContainer.repaint();
    }

    // RECENT TRANSACTIONS TABLE
    private void showPage(String page) {

        currentPage = page;

        switch (page) {

            case "Overview":
                pageHeading.setText("Overview");
                pageDescription.setText(
                        "Welcome to your banking dashboard");
                loadDashboardData();
                break;

            case "Accounts":
                pageHeading.setText("Accounts");
                pageDescription.setText(
                        "View all registered bank accounts");
                loadAccountsPage();
                break;

            case "Transfers":
                pageHeading.setText("Transfers");
                pageDescription.setText(
                        "Move money between accounts");
                displayTransferPlaceholder();
                break;

            case "Transactions":
                pageHeading.setText("Transactions");
                pageDescription.setText(
                        "View your transaction history");
                loadTransactionsPage();
                break;
        }
    }

    private void loadAccountsPage() {

        SwingWorker<ArrayList<BankAccount>, Void> worker = new SwingWorker<>() {

            @Override
            protected ArrayList<BankAccount> doInBackground()
                    throws Exception {
                return repository.getAllAccounts();
            }

            @Override
            protected void done() {
                try {
                    ArrayList<BankAccount> accounts = get();

                    String[] columns = {
                            "ACCOUNT ID", "ACCOUNT HOLDER", "BALANCE"
                    };

                    DefaultTableModel model = new DefaultTableModel(columns, 0) {
                        @Override
                        public boolean isCellEditable(int row, int column) {
                            return false;
                        }
                    };

                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(
                            new Locale("en", "IN"));

                    for (BankAccount account : accounts) {
                        model.addRow(new Object[] {
                                account.getId(),
                                account.getName(),
                                currencyFormat.format(account.getBalance())
                        });
                    }

                    JTable table = new JTable(model);
                    styleTable(table);

                    JPanel panel = new JPanel(new BorderLayout());
                    panel.setBackground(BACKGROUND);
                    panel.setBorder(
                            BorderFactory.createEmptyBorder(30, 0, 0, 0));

                    panel.add(new JScrollPane(table), BorderLayout.CENTER);

                    displayPage("Accounts", panel);

                } catch (Exception e) {
                    showError("Unable to load accounts", e);
                }
            }
        };

        worker.execute();
    }

    private void loadTransactionsPage() {

        SwingWorker<ArrayList<BankTransaction>, Void> worker = new SwingWorker<>() {

            @Override
            protected ArrayList<BankTransaction> doInBackground()
                    throws Exception {
                return repository.getRecentTransactions(1000);
            }

            @Override
            protected void done() {
                try {
                    ArrayList<BankTransaction> transactions = get();

                    JPanel panel = createRecentTransactions(transactions);

                    displayPage("Transactions", panel);

                } catch (Exception e) {
                    showError("Unable to load transactions", e);
                }
            }
        };

        worker.execute();
    }

    private void styleTable(JTable table) {

        table.setRowHeight(42);
        table.setBackground(CARD);
        table.setForeground(TEXT);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));

        table.setGridColor(new Color(55, 65, 81));
        table.setSelectionBackground(new Color(55, 65, 81));
        table.setSelectionForeground(TEXT);
        table.setShowVerticalLines(false);

        table.getTableHeader().setBackground(SIDEBAR);
        table.getTableHeader().setForeground(TEXT);
        table.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12));

        table.getTableHeader().setPreferredSize(
                new Dimension(0, 40));
    }

    private void displayTransferPlaceholder() {

        JPanel page = new JPanel(new GridBagLayout());
        page.setBackground(BACKGROUND);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(CARD);

        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(55, 65, 81)),
                BorderFactory.createEmptyBorder(
                        35, 40, 35, 40)));

        form.setPreferredSize(new Dimension(480, 490));

        JLabel title = new JLabel("Send money");
        title.setFont(new Font("SansSerif", Font.BOLD, 25));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel(
                "Transfer funds between your accounts");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(MUTED);

        JTextField senderField = createInputField(
                "Enter sender account ID");

        JTextField receiverField = createInputField(
                "Enter receiver account ID");

        JTextField amountField = createInputField(
                "0.00");

        JButton transferButton = new JButton("Continue →");
        transferButton.setFont(
                new Font("SansSerif", Font.BOLD, 14));
        transferButton.setForeground(Color.WHITE);
        transferButton.setBackground(
                new Color(59, 130, 246));
        transferButton.setFocusPainted(false);
        transferButton.setBorder(
                BorderFactory.createEmptyBorder(14, 20, 14, 20));
        transferButton.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 48));
        transferButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        form.add(title);
        form.add(Box.createVerticalStrut(8));
        form.add(subtitle);
        form.add(Box.createVerticalStrut(35));

        addFormField(form, "FROM ACCOUNT", senderField);
        addFormField(form, "TO ACCOUNT", receiverField);
        addFormField(form, "AMOUNT (INR)", amountField);

        form.add(Box.createVerticalStrut(15));
        form.add(transferButton);

        page.add(form);

        displayPage("Transfers", page);
    }

    private void showError(String message, Exception e) {

        JOptionPane.showMessageDialog(
                this,
                message + ": " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private JPanel createRecentTransactions(
            ArrayList<BankTransaction> transactions) {

        JPanel panel = new JPanel(
                new BorderLayout(0, 15));

        panel.setBackground(BACKGROUND);

        JLabel title = new JLabel("Recent Transactions");
        title.setFont(
                new Font("SansSerif", Font.BOLD, 19));
        title.setForeground(TEXT);

        panel.add(title, BorderLayout.NORTH);

        String[] columns = {
                "ID",
                "TYPE",
                "SENDER",
                "RECEIVER",
                "AMOUNT"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(
                    int row, int column) {
                return false;
            }
        };

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(
                new Locale("en", "IN"));

        for (BankTransaction transaction : transactions) {

            model.addRow(new Object[] {
                    transaction.getId(),
                    transaction.getType().toUpperCase(),
                    transaction.getSenderName(),
                    transaction.getReceiverName(),
                    currencyFormat.format(
                            transaction.getAmount())
            });
        }

        JTable table = new JTable(model);

        table.setRowHeight(42);
        table.setBackground(CARD);
        table.setForeground(TEXT);
        table.setFont(
                new Font("SansSerif", Font.PLAIN, 13));

        table.setGridColor(new Color(55, 65, 81));
        table.setSelectionBackground(new Color(55, 65, 81));
        table.setSelectionForeground(TEXT);
        table.setShowVerticalLines(false);

        // Table header styling
        table.getTableHeader().setBackground(SIDEBAR);
        table.getTableHeader().setForeground(TEXT);
        table.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12));

        table.getTableHeader().setPreferredSize(
                new Dimension(0, 40));

        // Color amounts according to transaction type
        table.getColumnModel().getColumn(4)
                .setCellRenderer(
                        new DefaultTableCellRenderer() {

                            @Override
                            public Component getTableCellRendererComponent(
                                    JTable table,
                                    Object value,
                                    boolean isSelected,
                                    boolean hasFocus,
                                    int row,
                                    int column) {

                                super.getTableCellRendererComponent(
                                        table, value, isSelected,
                                        hasFocus, row, column);

                                String type = table.getValueAt(
                                        row, 1).toString();

                                if (!isSelected) {

                                    if (type.equals("DEPOSIT")) {
                                        setForeground(
                                                new Color(74, 222, 128));

                                    } else if (type.equals("WITHDRAW")) {
                                        setForeground(
                                                new Color(248, 113, 113));

                                    } else {
                                        setForeground(
                                                new Color(96, 165, 250));
                                    }
                                }

                                setHorizontalAlignment(
                                        SwingConstants.RIGHT);

                                return this;
                            }
                        });

        JScrollPane scrollPane = new JScrollPane(table);

        scrollPane.getViewport().setBackground(CARD);

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        new Color(55, 65, 81)));

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // LOAD DATABASE DATA IN BACKGROUND

    private void loadDashboardData() {

        SwingWorker<Void, Void> worker = new SwingWorker<>() {

            private long totalBalance;
            private int totalAccounts;

            private ArrayList<BankTransaction> transactions;

            @Override
            protected Void doInBackground() throws Exception {

                totalBalance = repository.getTotalBalance();

                totalAccounts = repository.getTotalAccounts();

                transactions = repository.getRecentTransactions(10);

                return null;
            }

            @Override
            protected void done() {

                try {

                    get();

                    displayDashboard(
                            totalBalance,
                            totalAccounts,
                            transactions);

                } catch (Exception e) {

                    JOptionPane.showMessageDialog(
                            BankingUI.this,
                            "Unable to load dashboard: "
                                    + e.getMessage(),
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    private JTextField createInputField(String placeholder) {

        JTextField field = new JTextField();
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setForeground(TEXT);
        field.setBackground(BACKGROUND);
        field.setCaretColor(TEXT);

        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(55, 65, 81)),
                BorderFactory.createEmptyBorder(
                        12, 14, 12, 14)));

        field.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 48));

        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        return field;
    }

    private void addFormField(
            JPanel parent,
            String labelText,
            JTextField field) {

        JLabel label = new JLabel(labelText);
        label.setForeground(MUTED);
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        parent.add(label);
        parent.add(Box.createVerticalStrut(9));
        parent.add(field);
        parent.add(Box.createVerticalStrut(22));
    }
}
