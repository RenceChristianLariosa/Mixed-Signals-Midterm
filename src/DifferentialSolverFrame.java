import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DifferentialSolverFrame extends JFrame {

    public JTextField r1Field, rfField, r2Field, r3Field, v1Field, v2Field, rlField;
    public JTextArea resultArea;
    public CircuitDiagramPanel diagramPanel;
    public GridBagConstraints gbc;

    public DifferentialSolverFrame() {
        super("Differential Op-Amp Solver");

        setSize(1150, 700);
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
        gbcInput.insets = new Insets(4, 4, 4, 4);
        gbcInput.fill = GridBagConstraints.HORIZONTAL;
        gbcInput.weightx = 1.0;

        int row = 0;
        addInputField(inputPanel, gbcInput, row++, "Input Resistor R1 (Ohms):", "10000", "R1");
        addInputField(inputPanel, gbcInput, row++, "Feedback Resistor Rf (Ohms):", "10000", "Rf");
        addInputField(inputPanel, gbcInput, row++, "Input Resistor R2 (Ohms):", "10000", "R2");
        addInputField(inputPanel, gbcInput, row++, "Ground Resistor R3 (Ohms):", "10000", "R3");
        addInputField(inputPanel, gbcInput, row++, "Load Resistor RL (Ohms):", "10000", "RL");
        addInputField(inputPanel, gbcInput, row++, "Input Voltage V1 (V):", "1", "V1");
        addInputField(inputPanel, gbcInput, row++, "Input Voltage V2 (V):", "1", "V2");

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.30; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(inputPanel, gbc);

        // --- 2. Diagram Panel ---
        diagramPanel = new CircuitDiagramPanel();
        diagramPanel.setBorder(BorderFactory.createTitledBorder("Circuit Diagram"));
        diagramPanel.setBackground(Color.WHITE);
        diagramPanel.setPreferredSize(new Dimension(750, 450));

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.70;
        add(diagramPanel, gbc);

        // --- 3. Calculate Button ---
        JButton calcButton = new JButton("Calculate Output Voltage");
        calcButton.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(calcButton, gbc);

        // --- 4. Result Area ---
        resultArea = new JTextArea(5, 20);
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

    public void addInputField(JPanel panel, GridBagConstraints gbc, int row, String label, String defaultValue, String name) {
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        JTextField field = new JTextField(defaultValue);
        field.setName(name);
        field.addActionListener(e -> diagramPanel.repaint());
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) { diagramPanel.repaint(); }
        });
        panel.add(field, gbc);

        switch (name) {
            case "R1": r1Field = field; break;
            case "Rf": rfField = field; break;
            case "R2": r2Field = field; break;
            case "R3": r3Field = field; break;
            case "RL": rlField = field; break;
            case "V1": v1Field = field; break;
            case "V2": v2Field = field; break;
        }
    }

    public void calculate() {
        try {
            double r1 = Double.parseDouble(r1Field.getText());
            double rf = Double.parseDouble(rfField.getText());
            double r2 = Double.parseDouble(r2Field.getText());
            double r3 = Double.parseDouble(r3Field.getText());
            double v1 = Double.parseDouble(v1Field.getText());
            double v2 = Double.parseDouble(v2Field.getText());

            if (r1 == 0) throw new ArithmeticException("R1 cannot be zero.");
            if (r2 + r3 == 0) throw new ArithmeticException("R2 + R3 cannot be zero.");

            double term1 = ((r1 + rf) / r1) * (r3 / (r2 + r3)) * v2;
            double term2 = (rf / r1) * v1;
            double outputVoltage = term1 - term2;

            String formula = String.format(
                    "Vo = [(R1+Rf)/R1] * [R3/(R2+R3)] * V2 - [Rf/R1] * V1\n" +
                            "Vo = [(%.0f+%.0f)/%.0f] * [%.0f/(%.0f+%.0f)] * %.2f - [%.0f/%.0f] * %.2f\n" +
                            "Vo = %.4f V",
                    r1, rf, r1, r3, r2, r3, v2, rf, r1, v1, outputVoltage
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

            int w = getWidth();
            int h = getHeight();

            // --- Op-Amp Geometry ---
            int opAmpX = w - 200;
            int opAmpY = h / 2 - 30;
            int opAmpSize = 80;
            int invertingInputY = opAmpY - 25;
            int nonInvertingInputY = opAmpY + 25;
            int outputY = opAmpY;
            int outputX = opAmpX + opAmpSize;

            drawOpAmp(g2, opAmpX, opAmpY, opAmpSize);

            // --- 1. Draw Feedback Resistor (Rf) ---
            int rfWireY = invertingInputY - 70;
            String rfVal = rfField.getText();

            drawResistor(g2, opAmpX - 20, rfWireY, outputX + 20, rfWireY, "Rf = " + rfVal + " \u03A9");
            g2.drawLine(opAmpX - 20, invertingInputY, opAmpX - 20, rfWireY);
            g2.drawLine(outputX + 20, rfWireY, outputX + 20, outputY);
            g2.fillOval(outputX + 17, outputY - 3, 6, 6);

            // --- 2. Draw Output and Load Resistor (RL) ---
            String rlVal = rlField.getText();
            int rlWireX = outputX + 70;
            int rlGroundY = outputY + 130;

            g2.drawLine(outputX, outputY, rlWireX, outputY);
            drawResistorVertical(g2, rlWireX, outputY + 40, rlWireX, outputY + 100, "RL = " + rlVal + " \u03A9");
            g2.drawLine(rlWireX, outputY, rlWireX, outputY + 40);
            g2.drawLine(rlWireX, outputY + 100, rlWireX, rlGroundY);
            drawGround(g2, rlWireX, rlGroundY);

            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString("v_out", rlWireX + 10, outputY - 5);

            // --- 3. Draw Input 1 (v1, R1) ---
            String r1Val = r1Field.getText();
            String v1Val = v1Field.getText();
            int v1StartX = 60;
            int v1StartY = nonInvertingInputY + 80;

            drawOpenTerminalSource(g2, v1StartX, v1StartY, "v1 = " + v1Val + " V");
            g2.drawLine(v1StartX + 15, v1StartY, v1StartX + 60, v1StartY);
            g2.drawLine(v1StartX + 60, v1StartY, v1StartX + 60, invertingInputY);
            g2.drawLine(v1StartX + 60, invertingInputY, opAmpX - 140, invertingInputY);

            drawResistor(g2, opAmpX - 140, invertingInputY, opAmpX - 20, invertingInputY, "R1 = " + r1Val + " \u03A9");

            // --- 4. Draw Input 2 (v2, R2) ---
            String r2Val = r2Field.getText();
            String v2Val = v2Field.getText();
            int v2StartX = 60;
            int v2StartY = nonInvertingInputY + 130;

            drawOpenTerminalSource(g2, v2StartX, v2StartY, "v2 = " + v2Val + " V");
            g2.drawLine(v2StartX + 15, v2StartY, v2StartX + 80, v2StartY);
            g2.drawLine(v2StartX + 80, v2StartY, v2StartX + 80, nonInvertingInputY);
            g2.drawLine(v2StartX + 80, nonInvertingInputY, opAmpX - 140, nonInvertingInputY);

            drawResistor(g2, opAmpX - 140, nonInvertingInputY, opAmpX - 20, nonInvertingInputY, "R2 = " + r2Val + " \u03A9");

            // --- 5. Draw R3 from v_b to ground ---
            String r3Val = r3Field.getText();
            int r3X = opAmpX - 50;
            int r3GroundY = nonInvertingInputY + 100;

            drawResistorVertical(g2, r3X, nonInvertingInputY + 40, r3X, nonInvertingInputY + 100, "R3 = " + r3Val + " \u03A9");
            g2.drawLine(r3X, nonInvertingInputY, r3X, nonInvertingInputY + 40);
            g2.drawLine(r3X, nonInvertingInputY + 100, r3X, r3GroundY);
            drawGround(g2, r3X, r3GroundY);

            g2.drawLine(opAmpX - 20, nonInvertingInputY, opAmpX, nonInvertingInputY);

            // Connection dots
            g2.fillOval(r3X - 3, nonInvertingInputY - 3, 6, 6);
            g2.fillOval(opAmpX - 23, invertingInputY - 3, 6, 6);

            // --- Node Labels (Adjusted to avoid overlap) ---
            g2.setFont(new Font("Arial", Font.PLAIN, 12));
            // Move v_a to the right, close to the op-amp, and above the wire
            g2.drawString("v_a", opAmpX - 35, invertingInputY - 15);
            // Move v_b to the right, close to the op-amp, and above the wire
            g2.drawString("v_b", opAmpX - 35, nonInvertingInputY - 15);
        }

        public void drawOpAmp(Graphics2D g2, int x, int y, int size) {
            int[] xPoints = {x, x, x + size};
            int[] yPoints = {y - size/2, y + size/2, y};
            g2.setColor(Color.BLACK);
            g2.drawPolygon(xPoints, yPoints, 3);

            g2.drawString("-", x + 10, y - 15);
            g2.drawString("+", x + 10, y + 25);
        }

        public void drawResistor(Graphics2D g2, int x1, int y1, int x2, int y2, String label) {
            g2.setColor(Color.BLACK);
            int midX = (x1 + x2) / 2;
            int midY = (y1 + y2) / 2;

            int zigzagWidth = 30;
            int zigzagHeight = 10;
            int startZig = midX - zigzagWidth / 2;
            int endZig = startZig + zigzagWidth;

            g2.drawLine(x1, y1, startZig, midY);

            int[] xPoints = new int[7];
            int[] yPoints = new int[7];
            xPoints[0] = startZig;
            yPoints[0] = midY;
            xPoints[1] = startZig + 5;
            yPoints[1] = midY - zigzagHeight;
            xPoints[2] = startZig + 10;
            yPoints[2] = midY + zigzagHeight;
            xPoints[3] = startZig + 15;
            yPoints[3] = midY - zigzagHeight;
            xPoints[4] = startZig + 20;
            yPoints[4] = midY + zigzagHeight;
            xPoints[5] = startZig + 25;
            yPoints[5] = midY - zigzagHeight;
            xPoints[6] = endZig;
            yPoints[6] = midY;

            g2.drawPolyline(xPoints, yPoints, 7);
            g2.drawLine(endZig, midY, x2, y2);

            // Adjusted label position: 20 pixels above the wire to clear the zigzag peaks
            g2.setFont(new Font("Arial", Font.PLAIN, 11));
            g2.drawString(label, midX - 30, midY - 20);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
        }

        public void drawResistorVertical(Graphics2D g2, int x1, int y1, int x2, int y2, String label) {
            g2.setColor(Color.BLACK);
            int midX = (x1 + x2) / 2;
            int midY = (y1 + y2) / 2;

            int zigzagHeight = 30;
            int zigzagWidth = 10;
            int startZig = midY - zigzagHeight / 2;
            int endZig = startZig + zigzagHeight;

            g2.drawLine(midX, y1, midX, startZig);

            int[] xPoints = new int[7];
            int[] yPoints = new int[7];
            xPoints[0] = midX;
            yPoints[0] = startZig;
            xPoints[1] = midX - zigzagWidth;
            yPoints[1] = startZig + 5;
            xPoints[2] = midX + zigzagWidth;
            yPoints[2] = startZig + 10;
            xPoints[3] = midX - zigzagWidth;
            yPoints[3] = startZig + 15;
            xPoints[4] = midX + zigzagWidth;
            yPoints[4] = startZig + 20;
            xPoints[5] = midX - zigzagWidth;
            yPoints[5] = startZig + 25;
            xPoints[6] = midX;
            yPoints[6] = endZig;

            g2.drawPolyline(xPoints, yPoints, 7);
            g2.drawLine(midX, endZig, midX, y2);

            g2.setFont(new Font("Arial", Font.PLAIN, 11));
            g2.drawString(label, midX + 15, midY + 5);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
        }

        public void drawOpenTerminalSource(Graphics2D g2, int x, int y, String label) {
            g2.setColor(Color.BLACK);
            g2.drawOval(x, y - 5, 10, 10);

            g2.setFont(new Font("Arial", Font.BOLD, 12));
            int labelWidth = g2.getFontMetrics().stringWidth(label);
            g2.drawString(label, x - labelWidth - 5, y + 5);
        }

        public void drawGround(Graphics2D g2, int x, int y) {
            g2.drawLine(x, y, x, y + 10);
            g2.drawLine(x - 10, y + 10, x + 10, y + 10);
            g2.drawLine(x - 6, y + 15, x + 6, y + 15);
            g2.drawLine(x - 2, y + 20, x + 2, y + 20);
        }
    }
}