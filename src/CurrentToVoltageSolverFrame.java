import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CurrentToVoltageSolverFrame extends JFrame {

    public JTextField rfField, iinField, vccField;
    public JTextArea resultArea;
    public CircuitDiagramPanel diagramPanel;
    public GridBagConstraints gbc;

    public CurrentToVoltageSolverFrame() {
        super("Current to Voltage Converter Solver");

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
        inputPanel.add(new JLabel("Input Current (Iin) Amps:"), gbcInput);
        gbcInput.gridx = 1;
        iinField = new JTextField("0.001");
        iinField.addActionListener(e -> diagramPanel.repaint());
        iinField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) { diagramPanel.repaint(); }
        });
        inputPanel.add(iinField, gbcInput);

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
        inputPanel.add(new JLabel("Supply Voltage (Vcc) Volts:"), gbcInput);
        gbcInput.gridx = 1;
        vccField = new JTextField("15");
        vccField.addActionListener(e -> diagramPanel.repaint());
        vccField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) { diagramPanel.repaint(); }
        });
        inputPanel.add(vccField, gbcInput);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.30; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(inputPanel, gbc);

        diagramPanel = new CircuitDiagramPanel();
        diagramPanel.setBorder(BorderFactory.createTitledBorder("Circuit Diagram"));
        diagramPanel.setBackground(Color.WHITE);
        diagramPanel.setPreferredSize(new Dimension(650, 450));

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.70;
        add(diagramPanel, gbc);

        JButton calcButton = new JButton("Calculate Output Voltage");
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
            double iin = Double.parseDouble(iinField.getText());
            double rf = Double.parseDouble(rfField.getText());

            double outputVoltage = -iin * rf;

            String formula = String.format(
                    "Vo = -Iin * Rf\n" +
                            "Vo = -(%.6f A) * (%.0f \u03A9)\n" +
                            "Vo = %.4f V",
                    iin, rf, outputVoltage
            );

            resultArea.setText("Result:\n" + formula);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
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

            String vccVal = vccField.getText();

            int topEdgeY = opAmpY - (opAmpSize / 2) + 10;
            int vccTopY = topEdgeY - 40;
            g2.drawLine(opAmpX + 40, topEdgeY, opAmpX + 40, vccTopY);
            g2.drawString("+Vcc = " + vccVal + "V", opAmpX + 50, vccTopY + 15);

            int bottomEdgeY = opAmpY + (opAmpSize / 2) - 10;
            int vccBottomY = bottomEdgeY + 40;
            g2.drawLine(opAmpX + 40, bottomEdgeY, opAmpX + 40, vccBottomY);
            g2.drawString("-Vcc = -" + vccVal + "V", opAmpX + 50, vccBottomY - 5);

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

            String iinVal = iinField.getText();
            int pdX = 150;
            int pdY = invInputY;
            int pdRadius = 25;

            g2.drawOval(pdX - pdRadius, pdY - pdRadius, pdRadius * 2, pdRadius * 2);
            g2.drawLine(pdX - 15, pdY - 5, pdX - 5, pdY - 5);
            g2.drawLine(pdX - 10, pdY - 10, pdX - 10, pdY);
            g2.drawLine(pdX + 5, pdY - 5, pdX + 15, pdY - 5);

            g2.drawLine(pdX + pdRadius, pdY, opAmpX - 20, pdY);

            int pdGroundY = pdY + pdRadius + 40;
            g2.drawLine(pdX, pdY + pdRadius, pdX, pdGroundY);
            drawGround(g2, pdX, pdGroundY);

            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString("Photodiode", pdX - 35, pdY + pdRadius + 35);
            g2.drawString("Iin = " + iinVal + " A", pdX - 30, pdY - pdRadius - 10);
            g2.drawString("B", opAmpX - 35, invInputY - 15);

            g2.drawLine(opAmpX - 20, nonInvInputY, 350, nonInvInputY);
            g2.drawLine(350, nonInvInputY, 350, nonInvInputY + 60);
            drawGround(g2, 350, nonInvInputY + 60);
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