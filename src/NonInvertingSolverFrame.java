import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NonInvertingSolverFrame extends JFrame {

    public JTextField rfField;
    public JTextField r1Field;
    public JTextField vinField;
    public JTextArea resultArea;
    public CircuitDiagramPanel diagramPanel;
    public GridBagConstraints gbc;

    public NonInvertingSolverFrame() {
        super("Non-Inverting Op-Amp Solver");

        setSize(950, 600);
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
        inputPanel.add(new JLabel("Feedback Resistor (Rf) Ohms:"), gbcInput);
        gbcInput.gridx = 1;
        rfField = new JTextField("10000");
        rfField.addActionListener(e -> diagramPanel.repaint());
        rfField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) { diagramPanel.repaint(); }
        });
        inputPanel.add(rfField, gbcInput);

        gbcInput.gridx = 0; gbcInput.gridy = 1;
        inputPanel.add(new JLabel("Input Resistor (R1) Ohms:"), gbcInput);
        gbcInput.gridx = 1;
        r1Field = new JTextField("1000");
        r1Field.addActionListener(e -> diagramPanel.repaint());
        r1Field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) { diagramPanel.repaint(); }
        });
        inputPanel.add(r1Field, gbcInput);

        gbcInput.gridx = 0; gbcInput.gridy = 2;
        inputPanel.add(new JLabel("Input Voltage (Vin) Volts:"), gbcInput);
        gbcInput.gridx = 1;
        vinField = new JTextField("1");
        vinField.addActionListener(e -> diagramPanel.repaint());
        vinField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) { diagramPanel.repaint(); }
        });
        inputPanel.add(vinField, gbcInput);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.35; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(inputPanel, gbc);

        // --- 2. Diagram Panel ---
        diagramPanel = new CircuitDiagramPanel();
        diagramPanel.setBorder(BorderFactory.createTitledBorder("Circuit Diagram"));
        diagramPanel.setBackground(Color.WHITE);
        diagramPanel.setPreferredSize(new Dimension(550, 350));

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.65;
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
            double rf = Double.parseDouble(rfField.getText());
            double r1 = Double.parseDouble(r1Field.getText());
            double vin = Double.parseDouble(vinField.getText());

            if (r1 == 0) throw new ArithmeticException("R1 cannot be zero.");

            double outputVoltage = (1 + (rf / r1)) * vin;

            String formula = String.format("Vo = (1 + Rf/R1) * Vin\n" +
                    "Vo = (1 + %.2f/%.2f) * %.2f\n" +
                    "Vo = %.4f V", rf, r1, vin, outputVoltage);

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

            // Op-Amp Geometry
            int opAmpX = w - 180;
            int opAmpY = h / 2;
            int opAmpSize = 80;
            int nonInvertingInputY = opAmpY + 25;
            int invertingInputY = opAmpY - 25;
            int outputY = opAmpY;
            int outputX = opAmpX + opAmpSize;

            // Draw Op-Amp Symbol
            drawOpAmp(g2, opAmpX, opAmpY, opAmpSize);

            // --- Draw Feedback Resistor (Rf) ---
            int rfWireY = invertingInputY - 60;
            String rfVal = rfField.getText();

            // Draw the complete feedback wire first
            g2.drawLine(opAmpX - 20, invertingInputY, opAmpX - 20, rfWireY);
            g2.drawLine(opAmpX - 20, rfWireY, outputX + 20, rfWireY);
            g2.drawLine(outputX + 20, rfWireY, outputX + 20, outputY);
            g2.fillOval(outputX + 17, outputY - 3, 6, 6);

            // Draw Rf on top of the wire
            drawResistor(g2, opAmpX - 20, rfWireY, outputX + 20, rfWireY, "Rf = " + rfVal + " \u03A9");

            // Draw output wire
            g2.drawLine(outputX, outputY, outputX + 60, outputY);
            g2.drawString("Vo", outputX + 65, outputY + 5);

            // --- Draw Input Section (R1 and Vin) ---
            String r1Val = r1Field.getText();
            String vinVal = vinField.getText();

            // 1. Draw Vin source to non-inverting (+) input
            int vinStartX = 40;
            drawOpenTerminalSource(g2, vinStartX, nonInvertingInputY, vinVal + " V");
            g2.drawLine(vinStartX + 15, nonInvertingInputY, opAmpX, nonInvertingInputY);

            // 2. Draw R1 from inverting (-) input to ground
            int turnPointX = opAmpX - 120;
            int r1GroundY = invertingInputY + 100;

            // Draw the complete R1 wire first (from op-amp node to turn point, then down to ground)
            g2.drawLine(opAmpX - 20, invertingInputY, turnPointX, invertingInputY);
            g2.drawLine(turnPointX, invertingInputY, turnPointX, r1GroundY);
            drawGround(g2, turnPointX, r1GroundY);

            // Draw R1 on top of the horizontal wire section
            // We place it between the op-amp node and the turn point
            drawResistor(g2, opAmpX - 20, invertingInputY, turnPointX, invertingInputY, "R1 = " + r1Val + " \u03A9");
        }

        public void drawOpAmp(Graphics2D g2, int x, int y, int size) {
            int[] xPoints = {x, x, x + size};
            int[] yPoints = {y - size/2, y + size/2, y};
            g2.setColor(Color.BLACK);
            g2.drawPolygon(xPoints, yPoints, 3);

            g2.drawString("-", x + 10, y - 15);
            g2.drawString("+", x + 10, y + 25);
        }

        // Bulletproof resistor drawing: Erases the background line, then draws the zigzag
        public void drawResistor(Graphics2D g2, int x1, int y1, int x2, int y2, String label) {
            int midX = (x1 + x2) / 2;
            int midY = (y1 + y2) / 2;

            int zigzagWidth = 30;
            int zigzagHeight = 10;
            int startZig = midX - zigzagWidth / 2;

            // 1. Erase the straight line under the zigzag by drawing a white rectangle
            // We add a little padding (2px) to ensure it fully covers the line
            g2.setColor(getBackground());
            g2.fillRect(startZig - 2, midY - zigzagHeight - 2, zigzagWidth + 4, (zigzagHeight * 2) + 4);

            // 2. Draw the zigzag in black
            g2.setColor(Color.BLACK);
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
            xPoints[6] = startZig + 30;
            yPoints[6] = midY;

            g2.drawPolyline(xPoints, yPoints, 7);

            // 3. Draw Label
            g2.setFont(new Font("Arial", Font.PLAIN, 11));
            g2.drawString(label, midX - 30, midY - 15);
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