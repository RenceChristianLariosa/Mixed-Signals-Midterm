import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class IntegratorSolverFrame extends JFrame {

    public JTextField r1Field, cfField, vinField, vout0Field;
    public JTextArea resultArea;
    public CircuitDiagramPanel diagramPanel;
    public GridBagConstraints gbc;

    public IntegratorSolverFrame() {
        super("Ideal Integrator Op-Amp Solver");

        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // --- 1. Input Panel ---
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Inputs"));
        GridBagConstraints gbcInput = new GridBagConstraints();
        gbcInput.insets = new Insets(5, 5, 5, 5);
        gbcInput.fill = GridBagConstraints.HORIZONTAL;
        gbcInput.weightx = 1.0;

        gbcInput.gridx = 0; gbcInput.gridy = 0;
        inputPanel.add(new JLabel("Input Resistor (R1) Ohms:"), gbcInput);
        gbcInput.gridx = 1;
        r1Field = new JTextField("10000");
        r1Field.addActionListener(e -> diagramPanel.repaint());
        r1Field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) { diagramPanel.repaint(); }
        });
        inputPanel.add(r1Field, gbcInput);

        gbcInput.gridx = 0; gbcInput.gridy = 1;
        inputPanel.add(new JLabel("Feedback Capacitor (Cf) Farads:"), gbcInput);
        gbcInput.gridx = 1;
        cfField = new JTextField("0.00001");
        cfField.addActionListener(e -> diagramPanel.repaint());
        cfField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) { diagramPanel.repaint(); }
        });
        inputPanel.add(cfField, gbcInput);

        gbcInput.gridx = 0; gbcInput.gridy = 2;
        inputPanel.add(new JLabel("Input Voltage (Vin) Volts:"), gbcInput);
        gbcInput.gridx = 1;
        vinField = new JTextField("1");
        vinField.addActionListener(e -> diagramPanel.repaint());
        vinField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) { diagramPanel.repaint(); }
        });
        inputPanel.add(vinField, gbcInput);

        gbcInput.gridx = 0; gbcInput.gridy = 3;
        inputPanel.add(new JLabel("Initial Output Vout(0) Volts:"), gbcInput);
        gbcInput.gridx = 1;
        vout0Field = new JTextField("0");
        vout0Field.addActionListener(e -> diagramPanel.repaint());
        vout0Field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) { diagramPanel.repaint(); }
        });
        inputPanel.add(vout0Field, gbcInput);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.30; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(inputPanel, gbc);

        // --- 2. Diagram Panel ---
        diagramPanel = new CircuitDiagramPanel();
        diagramPanel.setBorder(BorderFactory.createTitledBorder("Circuit Diagram"));
        diagramPanel.setBackground(Color.WHITE);
        diagramPanel.setPreferredSize(new Dimension(650, 450));

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.70;
        add(diagramPanel, gbc);

        // --- 3. Calculate Button ---
        JButton calcButton = new JButton("Calculate Output Voltage");
        calcButton.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(calcButton, gbc);

        // --- 4. Result Area ---
        resultArea = new JTextArea(4, 20);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.BOLD, 12));
        resultArea.setBackground(new Color(240, 240, 240));
        gbc.gridy = 2;
        add(new JScrollPane(resultArea), gbc);

        calcButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculate();
            }
        });

        setVisible(true);
    }

    public void calculate() {
        try {
            double r1 = Double.parseDouble(r1Field.getText());
            double cf = Double.parseDouble(cfField.getText());
            double vin = Double.parseDouble(vinField.getText());
            double vout0 = Double.parseDouble(vout0Field.getText());

            if (r1 == 0) throw new ArithmeticException("R1 cannot be zero.");
            if (cf == 0) throw new ArithmeticException("Cf cannot be zero.");

            // Assuming t = 1 second for demonstration of the integral
            double t = 1.0;
            double integralPart = vin * t;
            double outputVoltage = -(1.0 / (r1 * cf)) * integralPart + vout0;

            String formula = String.format(
                    "Vo(t) = - (1 / (R1 * Cf)) * ∫(Vin dt) + Vout(0)\n" +
                            "Assuming constant DC input and evaluating at t = 1 second:\n" +
                            "Vo(1) = - (1 / (%.0f * %.6f)) * (%.2f * 1) + %.2f\n" +
                            "Vo(1) = %.4f V",
                    r1, cf, vin, vout0, outputVoltage
            );

            resultArea.setText("Result:\n" + formula);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (ArithmeticException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Math Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==========================================
    // INNER CLASS: CIRCUIT DIAGRAM DRAWER
    // ==========================================
    public class CircuitDiagramPanel extends JPanel {

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(2));
            g2.setFont(new Font("Arial", Font.BOLD, 12));

            // --- Fixed Layout Coordinates (Matches Reference Image) ---
            int opAmpX = 450;
            int opAmpY = 220;
            int opAmpSize = 80;

            int invInputY = opAmpY - 20;      // Inverting input pin
            int nonInvInputY = opAmpY + 20;   // Non-inverting input pin
            int outputY = opAmpY;             // Output pin
            int outputX = opAmpX + opAmpSize; // Right tip of op-amp

            // --- 1. Draw Op-Amp Symbol ---
            drawOpAmp(g2, opAmpX, opAmpY, opAmpSize);

            // --- 2. Draw Feedback Capacitor (Cf) ---
            String cfVal = cfField.getText();
            int cfTopWireY = invInputY - 60;
            int cfCenterX = (opAmpX + outputX) / 2;
            int capGap = 8;

            // Feedback wire route
            g2.drawLine(opAmpX - 20, invInputY, opAmpX - 20, cfTopWireY);       // Up from inverting input
            g2.drawLine(opAmpX - 20, cfTopWireY, cfCenterX - capGap, cfTopWireY); // Left side of Cf
            g2.drawLine(cfCenterX + capGap, cfTopWireY, outputX + 20, cfTopWireY); // Right side of Cf
            g2.drawLine(outputX + 20, cfTopWireY, outputX + 20, outputY);       // Down to output

            // Capacitor Symbol
            g2.drawLine(cfCenterX - capGap, cfTopWireY - 15, cfCenterX - capGap, cfTopWireY + 15);
            g2.drawLine(cfCenterX + capGap, cfTopWireY - 15, cfCenterX + capGap, cfTopWireY + 15);

            // Labels for Cf
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString("Cf = " + cfVal + " F", cfCenterX - 45, cfTopWireY - 25);
            g2.drawString("i_F", cfCenterX + 15, cfTopWireY - 10);
            g2.fillOval(outputX + 17, outputY - 3, 6, 6); // Dot at output junction

            // --- 3. Draw Output and Load Resistor (RL) ---
            g2.drawLine(outputX, outputY, outputX + 70, outputY);
            g2.drawString("Vo", outputX + 75, outputY + 5);

            // RL to ground
            int rlWireX = outputX + 40;
            int rlTopY = outputY + 30;
            int rlBottomY = outputY + 80;
            int rlGroundY = outputY + 110;

            g2.drawLine(rlWireX, outputY, rlWireX, rlTopY);
            drawResistorVertical(g2, rlWireX, rlTopY, rlWireX, rlBottomY, "RL = 10k \u03A9");
            g2.drawLine(rlWireX, rlBottomY, rlWireX, rlGroundY);
            drawGround(g2, rlWireX, rlGroundY);
            g2.fillOval(rlWireX - 3, outputY - 3, 6, 6);

            // --- 4. Draw Input Section (Vin, R1) ---
            String r1Val = r1Field.getText();
            String vinVal = vinField.getText();

            // Vin Source
            int vinX = 80;
            drawSource(g2, vinX, invInputY, vinVal + " V");
            g2.drawString("i_1", vinX + 25, invInputY - 10);

            // Wire from source to R1
            g2.drawLine(vinX + 20, invInputY, 220, invInputY);

            // R1 Resistor
            drawResistorHorizontal(g2, 220, invInputY, 300, invInputY, "R1 = " + r1Val + " \u03A9");

            // Wire from R1 to op-amp
            g2.drawLine(300, invInputY, opAmpX - 20, invInputY);
            g2.fillOval(opAmpX - 23, invInputY - 3, 6, 6); // Dot at inverting input junction

            // --- 5. Draw Non-Inverting Input to Ground ---
            g2.drawLine(opAmpX - 20, nonInvInputY, 350, nonInvInputY);
            g2.drawLine(350, nonInvInputY, 350, nonInvInputY + 60);
            drawGround(g2, 350, nonInvInputY + 60);

            // Labels for IB
            g2.drawString("IB", opAmpX - 50, nonInvInputY - 10);
            g2.drawString("IB", opAmpX - 50, invInputY + 20);
        }

        public void drawOpAmp(Graphics2D g2, int x, int y, int size) {
            int[] xPoints = {x, x, x + size};
            int[] yPoints = {y - size/2, y + size/2, y};
            g2.setColor(Color.BLACK);
            g2.drawPolygon(xPoints, yPoints, 3);

            // Input pins
            g2.drawLine(x - 20, y - 20, x, y - 20);
            g2.drawLine(x - 20, y + 20, x, y + 20);

            // Labels inside triangle
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            g2.drawString("-", x + 10, y - 15);
            g2.drawString("+", x + 10, y + 25);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
        }

        public void drawResistorHorizontal(Graphics2D g2, int x1, int y1, int x2, int y2, String label) {
            int midX = (x1 + x2) / 2;
            int midY = (y1 + y2) / 2;
            int boxWidth = 40;
            int boxHeight = 20;

            // Wire to box
            g2.drawLine(x1, y1, midX - boxWidth/2, midY);
            // Box
            g2.drawRect(midX - boxWidth/2, midY - boxHeight/2, boxWidth, boxHeight);
            // Wire from box
            g2.drawLine(midX + boxWidth/2, midY, x2, y2);

            // Label
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString(label, midX - 40, midY - 20);
        }

        public void drawResistorVertical(Graphics2D g2, int x1, int y1, int x2, int y2, String label) {
            int midX = (x1 + x2) / 2;
            int midY = (y1 + y2) / 2;
            int boxWidth = 20;
            int boxHeight = 40;

            // Wire to box
            g2.drawLine(midX, y1, midX, midY - boxHeight/2);
            // Box
            g2.drawRect(midX - boxWidth/2, midY - boxHeight/2, boxWidth, boxHeight);
            // Wire from box
            g2.drawLine(midX, midY + boxHeight/2, midX, y2);

            // Label
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString(label, midX + 20, midY + 5);
        }

        public void drawSource(Graphics2D g2, int x, int y, String label) {
            g2.setColor(Color.BLACK);
            // Circle
            g2.drawOval(x, y - 15, 30, 30);

            // Sine wave inside
            int cx = x + 15;
            int cy = y;
            g2.drawArc(cx - 8, cy - 8, 16, 16, 0, 180);
            g2.drawArc(cx - 8, cy, 16, 16, 180, 180);

            // Label
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            int labelWidth = g2.getFontMetrics().stringWidth(label);
            g2.drawString(label, x - labelWidth - 5, y + 5);
            g2.drawString("Vin", x + 5, y + 30);
        }

        public void drawGround(Graphics2D g2, int x, int y) {
            g2.drawLine(x, y, x, y + 10);
            g2.drawLine(x - 12, y + 10, x + 12, y + 10);
            g2.drawLine(x - 7, y + 15, x + 7, y + 15);
            g2.drawLine(x - 2, y + 20, x + 2, y + 20);
        }
    }
}