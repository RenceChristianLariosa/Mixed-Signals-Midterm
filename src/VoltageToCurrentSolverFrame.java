import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VoltageToCurrentSolverFrame extends JFrame {

    public JTextField r1Field, rfField, viField;
    public JTextArea resultArea;
    public CircuitDiagramPanel diagramPanel;
    public GridBagConstraints gbc;

    public VoltageToCurrentSolverFrame() {
        super("Voltage to Current Converter Solver");

        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Inputs"));
        GridBagConstraints gbcInput = new GridBagConstraints();
        gbcInput.insets = new Insets(5, 5, 5, 5);
        gbcInput.fill = GridBagConstraints.HORIZONTAL;
        gbcInput.weightx = 1.0;

        gbcInput.gridx = 0; gbcInput.gridy = 0;
        inputPanel.add(new JLabel("Input Resistor (R1) Ohms:"), gbcInput);
        gbcInput.gridx = 1;
        r1Field = new JTextField("1000");
        r1Field.addActionListener(e -> diagramPanel.repaint());
        r1Field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) { diagramPanel.repaint(); }
        });
        inputPanel.add(r1Field, gbcInput);

        gbcInput.gridx = 0; gbcInput.gridy = 1;
        inputPanel.add(new JLabel("Feedback Resistor (Rf) Ohms:"), gbcInput);
        gbcInput.gridx = 1;
        rfField = new JTextField("10000");
        rfField.addActionListener(e -> diagramPanel.repaint());
        rfField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) { diagramPanel.repaint(); }
        });
        inputPanel.add(rfField, gbcInput);

        gbcInput.gridx = 0; gbcInput.gridy = 2;
        inputPanel.add(new JLabel("Input Voltage (Vi) Volts:"), gbcInput);
        gbcInput.gridx = 1;
        viField = new JTextField("1");
        viField.addActionListener(e -> diagramPanel.repaint());
        viField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) { diagramPanel.repaint(); }
        });
        inputPanel.add(viField, gbcInput);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.30; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(inputPanel, gbc);

        diagramPanel = new CircuitDiagramPanel();
        diagramPanel.setBorder(BorderFactory.createTitledBorder("Circuit Diagram"));
        diagramPanel.setBackground(Color.WHITE);
        diagramPanel.setPreferredSize(new Dimension(650, 450));

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.70;
        add(diagramPanel, gbc);

        JButton calcButton = new JButton("Calculate Output Current");
        calcButton.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(calcButton, gbc);

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
            double vi = Double.parseDouble(viField.getText());

            if (r1 == 0) throw new ArithmeticException("R1 cannot be zero.");

            double outputCurrent = vi / r1;

            String formula = String.format(
                    "I0 = Vi / R1\n" +
                            "I0 = %.2f V / %.0f \u03A9\n" +
                            "I0 = %.6f A (or %.3f mA)",
                    vi, r1, outputCurrent, outputCurrent * 1000
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

    public class CircuitDiagramPanel extends JPanel {

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(2));
            g2.setFont(new Font("Arial", Font.BOLD, 12));

            int opAmpX = 450;
            int opAmpY = 220;
            int opAmpSize = 80;

            int invInputY = opAmpY - 20;
            int nonInvInputY = opAmpY + 20;
            int outputY = opAmpY;
            int outputX = opAmpX + opAmpSize;

            drawOpAmp(g2, opAmpX, opAmpY, opAmpSize);

            String rfVal = rfField.getText();
            int rfTopWireY = invInputY - 70;
            int rfCenterX = (opAmpX + outputX) / 2;

            g2.drawLine(opAmpX - 20, invInputY, opAmpX - 20, rfTopWireY);
            g2.drawLine(opAmpX - 20, rfTopWireY, rfCenterX - 30, rfTopWireY);
            g2.drawLine(rfCenterX + 30, rfTopWireY, outputX + 20, rfTopWireY);
            g2.drawLine(outputX + 20, rfTopWireY, outputX + 20, outputY);

            drawResistorHorizontal(g2, rfCenterX - 30, rfTopWireY, rfCenterX + 30, rfTopWireY, "Rf = " + rfVal + " \u03A9");

            g2.drawLine(outputX, outputY, outputX + 70, outputY);
            g2.drawString("Vo", outputX + 75, outputY + 5);
            g2.fillOval(outputX + 17, outputY - 3, 6, 6);
            g2.fillOval(opAmpX - 23, invInputY - 3, 6, 6);

            String r1Val = r1Field.getText();
            String viVal = viField.getText();

            int r1X = 280;
            int r1Y = invInputY;
            int r1GroundY = r1Y + 80;

            g2.drawLine(r1X + 40, r1Y, opAmpX - 20, r1Y);

            drawResistorHorizontal(g2, r1X, r1Y, r1X + 40, r1Y, "R1 = " + r1Val + " \u03A9");

            g2.drawLine(r1X, r1Y, r1X - 40, r1Y);
            g2.drawLine(r1X - 40, r1Y, r1X - 40, r1GroundY);
            drawGround(g2, r1X - 40, r1GroundY);

            int viX = 300;
            int viY = nonInvInputY;

            g2.drawString("Vi = " + viVal + " V", viX, viY + 5);

            g2.drawLine(viX + 50, viY, opAmpX - 20, viY);
            g2.fillOval(opAmpX - 23, nonInvInputY - 3, 6, 6);
        }

        public void drawOpAmp(Graphics2D g2, int x, int y, int size) {
            int[] xPoints = {x, x, x + size};
            int[] yPoints = {y - size/2, y + size/2, y};
            g2.setColor(Color.BLACK);
            g2.drawPolygon(xPoints, yPoints, 3);

            g2.drawLine(x - 20, y - 20, x, y - 20);
            g2.drawLine(x - 20, y + 20, x, y + 20);

            g2.setFont(new Font("Arial", Font.BOLD, 16));
            g2.drawString("-", x + 10, y - 15);
            g2.drawString("+", x + 10, y + 25);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
        }

        public void drawResistorHorizontal(Graphics2D g2, int x1, int y1, int x2, int y2, String label) {
            int midX = (x1 + x2) / 2;
            int midY = (y1 + y2) / 2;

            int zigzagWidth = 40;
            int zigzagHeight = 10;
            int startZig = midX - zigzagWidth / 2;

            g2.drawLine(x1, y1, startZig, midY);

            int[] xPoints = new int[7];
            int[] yPoints = new int[7];
            xPoints[0] = startZig;
            yPoints[0] = midY;
            xPoints[1] = startZig + 7;
            yPoints[1] = midY - zigzagHeight;
            xPoints[2] = startZig + 14;
            yPoints[2] = midY + zigzagHeight;
            xPoints[3] = startZig + 21;
            yPoints[3] = midY - zigzagHeight;
            xPoints[4] = startZig + 28;
            yPoints[4] = midY + zigzagHeight;
            xPoints[5] = startZig + 35;
            yPoints[5] = midY - zigzagHeight;
            xPoints[6] = startZig + 40;
            yPoints[6] = midY;
            g2.drawPolyline(xPoints, yPoints, 7);

            g2.drawLine(startZig + zigzagWidth, midY, x2, y2);

            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString(label, midX - 45, midY - 20);
        }

        public void drawGround(Graphics2D g2, int x, int y) {
            g2.drawLine(x, y, x, y + 10);
            g2.drawLine(x - 12, y + 10, x + 12, y + 10);
            g2.drawLine(x - 7, y + 15, x + 7, y + 15);
            g2.drawLine(x - 2, y + 20, x + 2, y + 20);
        }
    }
}