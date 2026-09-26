import javax.swing.*;
import java.awt.*;

public class mainFrame extends JFrame {
    JLabel description;
    JButton inverting, non_inverting, differential, integrator, current_to_voltage, voltage_to_current;
    Container container;
    GridBagConstraints constraints;
    GridBagLayout layout;

    public mainFrame() {
        super("OP-AMP Application Solver");

        description = new JLabel("Please select a Method to Solve the OP-AMP");
        description.setFont(new Font("Arial", Font.BOLD, 14));
        description.setHorizontalAlignment(SwingConstants.CENTER);

        inverting = new JButton("Inverting");
        non_inverting = new JButton("Non-Inverting");
        differential = new JButton("Differential");
        integrator = new JButton("Integrator");
        current_to_voltage = new JButton("Current to Voltage Converter");
        voltage_to_current = new JButton("Voltage to Current Converter");

        container = this.getContentPane();
        constraints = new GridBagConstraints();
        layout = new GridBagLayout();
        container.setLayout(layout);
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.weightx = 1;

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 3;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.CENTER;
        container.add(description, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        container.add(inverting, constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        container.add(non_inverting, constraints);

        constraints.gridx = 2;
        constraints.gridy = 1;
        container.add(differential, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        container.add(integrator, constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;
        container.add(current_to_voltage, constraints);

        constraints.gridx = 2;
        constraints.gridy = 2;
        container.add(voltage_to_current, constraints);

        this.setVisible(true);
        this.setSize(800, 600);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}