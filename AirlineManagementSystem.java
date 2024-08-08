import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.*;
import java.util.ArrayList;
import java.util.List;

// Abstract class representing a user
abstract class User {
    protected String name;

    public User(String name) {
        this.name = name;
    }

    public abstract void displayInfo();
}

// Interface for booking operations
interface BookingOperations {
    void bookFlight(String flight, String from, String to);
    void cancelBooking(String flight, String from, String to);
}

// Passenger class extending User and implementing BookingOperations
class Passenger extends User implements BookingOperations {
    private List<String[]> bookings;

    public Passenger(String name) {
        super(name);
        bookings = new ArrayList<>();
    }

    @Override
    public void displayInfo() {
        System.out.println("Passenger Name: " + name);
    }

    @Override
    public void bookFlight(String flight, String from, String to) {
        bookings.add(new String[]{flight, from, to});
        System.out.println(name + " booked flight: " + flight + " from " + from + " to " + to);
    }

    @Override
    public void cancelBooking(String flight, String from, String to) {
        bookings.removeIf(booking -> booking[0].equals(flight) && booking[1].equals(from) && booking[2].equals(to));
        System.out.println(name + " canceled booking: " + flight + " from " + from + " to " + to);
    }

    public List<String[]> getBookings() {
        return bookings;
    }
}

// Ticket class representing a ticket
class Ticket {
    private static int ticketCounter = 0;
    private int ticketId;
    private String passengerName;
    private String flight;
    private String from;
    private String to;

    public Ticket(String passengerName, String flight, String from, String to) {
        this.ticketId = ++ticketCounter;
        this.passengerName = passengerName;
        this.flight = flight;
        this.from = from;
        this.to = to;
    }

    public void displayTicket() {
        System.out.println("Ticket ID: " + ticketId + ", Passenger: " + passengerName + ", Flight: " + flight + ", From: " + from + ", To: " + to);
    }

    public String getTicketInfo() {
        return "Ticket ID: " + ticketId + "\nPassenger: " + passengerName + "\nFlight: " + flight + "\nFrom: " + from + "\nTo: " + to;
    }

    public String[] toTableRow() {
        return new String[]{String.valueOf(ticketId), passengerName, flight, from, to};
    }
}

// GUI class for the Airline Management System
public class AirlineManagementSystem extends JFrame implements Printable {
    private JTextField nameField;
    private JTextField flightField;
    private JTextField fromField;
    private JTextField toField;
    private JTable bookingTable;
    private DefaultTableModel tableModel;
    private Passenger currentPassenger;
    private Ticket currentTicket;
    private List<Ticket> tickets;

    public AirlineManagementSystem() {
        tickets = new ArrayList<>();

        setTitle("Airline Management System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JLabel headerLabel = new JLabel("Airline Management System", JLabel.CENTER);
        headerLabel.setFont(new Font("Serif", Font.BOLD, 24));
        headerLabel.setOpaque(true);
        headerLabel.setBackground(new Color(70, 130, 180));
        headerLabel.setForeground(Color.WHITE);
        add(headerLabel, BorderLayout.NORTH);

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inputPanel.setOpaque(false);
        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Flight:"));
        flightField = new JTextField();
        inputPanel.add(flightField);
        inputPanel.add(new JLabel("From:"));
        fromField = new JTextField();
        inputPanel.add(fromField);
        inputPanel.add(new JLabel("To:"));
        toField = new JTextField();
        inputPanel.add(toField);
        JButton bookButton = new JButton("Book Flight");
        bookButton.setBackground(new Color(34, 139, 34));
        bookButton.setForeground(Color.WHITE);
        inputPanel.add(bookButton);
        JButton cancelButton = new JButton("Cancel Booking");
        cancelButton.setBackground(new Color(178, 34, 34));
        cancelButton.setForeground(Color.WHITE);
        inputPanel.add(cancelButton);

        // Booking Table
        String[] columnNames = {"Ticket ID", "Passenger Name", "Flight", "From", "To"};
        tableModel = new DefaultTableModel(columnNames, 0);
        bookingTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(bookingTable);

        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton printButton = new JButton("Print Ticket");
        printButton.setBackground(new Color(70, 130, 180));
        printButton.setForeground(Color.WHITE);
        printButton.setPreferredSize(new Dimension(150, 30));
        footerPanel.add(printButton);
        JButton deleteButton = new JButton("Delete Selected Ticket");
        deleteButton.setBackground(new Color(178, 34, 34));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setPreferredSize(new Dimension(200, 30));
        footerPanel.add(deleteButton);
        JLabel footerLabel = new JLabel("Thank you for using our Airline Management System!", JLabel.CENTER);
        footerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        footerPanel.add(footerLabel);

        // Add components to frame
        JPanel centerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon icon = new ImageIcon("background.jpg");
                Image img = icon.getImage();
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                g.setColor(new Color(255, 255, 255, 128));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        centerPanel.setOpaque(false);
        centerPanel.add(inputPanel, BorderLayout.NORTH);
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);

        // Action Listeners
        bookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String flight = flightField.getText();
                String from = fromField.getText();
                String to = toField.getText();
                if (currentPassenger == null || !currentPassenger.name.equals(name)) {
                    currentPassenger = new Passenger(name);
                }
                currentPassenger.bookFlight(flight, from, to);
                currentTicket = new Ticket(name, flight, from, to);
                tickets.add(currentTicket);
                tableModel.addRow(currentTicket.toTableRow());
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String flight = flightField.getText();
                String from = fromField.getText();
                String to = toField.getText();
                if (currentPassenger != null && currentPassenger.name.equals(name)) {
                    currentPassenger.cancelBooking(flight, from, to);
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        if (tableModel.getValueAt(i, 1).equals(name) && tableModel.getValueAt(i, 2).equals(flight)
                                && tableModel.getValueAt(i, 3).equals(from) && tableModel.getValueAt(i, 4).equals(to)) {
                            tableModel.removeRow(i);
                            break;
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "No booking found for the given details.");
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = bookingTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String name = (String) tableModel.getValueAt(selectedRow, 1);
                    String flight = (String) tableModel.getValueAt(selectedRow, 2);
                    String from = (String) tableModel.getValueAt(selectedRow, 3);
                    String to = (String) tableModel.getValueAt(selectedRow, 4);
                    tableModel.removeRow(selectedRow);
                    if (currentPassenger != null && currentPassenger.name.equals(name)) {
                        currentPassenger.cancelBooking(flight, from, to);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "No ticket selected for deletion.");
                }
            }
        });

        printButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = bookingTable.getSelectedRow();
                if (selectedRow >= 0) {
                    currentTicket = tickets.get(selectedRow);
                    PrinterJob job = PrinterJob.getPrinterJob();
                    job.setPrintable(AirlineManagementSystem.this);
                    boolean doPrint = job.printDialog();
                    if (doPrint) {
                        try {
                            job.print();
                        } catch (PrinterException ex) {
                            JOptionPane.showMessageDialog(null, "Error printing ticket: " + ex.getMessage());
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "No ticket selected for printing.");
                }
            }
        });
    }

    @Override
    public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
        if (page > 0) {
            return NO_SUCH_PAGE;
        }
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());
        g.drawString(currentTicket.getTicketInfo(), 100, 100);
        return PAGE_EXISTS;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AirlineManagementSystem().setVisible(true);
            }
        });
    }
}
