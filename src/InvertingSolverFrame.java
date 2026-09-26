import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InvertingSolverFrame extends JFrame {

    public JTextField rfField;
    public JRadioButton singleInputRadio, multiInputRadio;
    public JSpinner numInputsSpinner;
    public JPanel inputPanel;
    public JTextArea resultArea;
    public ButtonGroup modeGroup;
    public CircuitDiagramPanel diagramPanel;

    public GridBagConstraints gbc;

    public InvertingSolverFrame() {
        super("Inverting Op-Amp Solver");

        setSize(950, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel modeLabel = new JLabel("Select Calculation Mode:");
        modeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        addComponent(modeLabel, 0, 0, 2);

        singleInputRadio = new JRadioButton("Single Input (Vo = -Rf/R1 * Vi)", true);
        multiInputRadio = new JRadioButton("Multiple Inputs (Summing Amplifier)");

        modeGroup = new ButtonGroup();
        modeGroup.add(singleInputRadio);
        modeGroup.add(multiInputRadio);

        addComponent(singleInputRadio, 0, 1, 2);
        addComponent(multiInputRadio, 0, 2, 2);

        ActionListener modeListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateInputFields();
            }
        };
        singleInputRadio.addActionListener(modeListener);
        multiInputRadio.addActionListener(modeListener);

        addComponent(new JLabel("Feedback Resistor (Rf) in Ohms:"), 0, 3, 1);
        rfField = new JTextField("10000");

        rfField.addActionListener(e -> diagramPanel.repaint());
        rfField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) { diagramPanel.repaint(); }
        });
        addComponent(rfField, 1, 3, 1);

        JLabel numInputsLabel = new JLabel("Number of Inputs (Multi-mode):");
        addComponent(numInputsLabel, 0, 4, 1);

        SpinnerModel spinnerModel = new SpinnerNumberModel(2, 2, 6, 1);
        numInputsSpinner = new JSpinner(spinnerModel);
        numInputsSpinner.setEnabled(false);

        numInputsSpinner.addChangeListener(e -> updateInputFields());
        addComponent(numInputsSpinner, 1, 4, 1);

        JPanel mainContentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.insets = new Insets(5, 5, 5, 5);
        gbcMain.fill = GridBagConstraints.BOTH;
        gbcMain.weighty = 1.0;

        inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Inputs"));
        JScrollPane scrollPane = new JScrollPane(inputPanel);
        scrollPane.setPreferredSize(new Dimension(300, 350));

        gbcMain.gridx = 0; gbcMain.gridy = 0; gbcMain.weightx = 0.35;
        mainContentPanel.add(scrollPane, gbcMain);

        diagramPanel = new CircuitDiagramPanel();
        diagramPanel.setBorder(BorderFactory.createTitledBorder("Circuit Diagram"));
        diagramPanel.setBackground(Color.WHITE);
        diagramPanel.setPreferredSize(new Dimension(550, 350));

        gbcMain.gridx = 1; gbcMain.gridy = 0; gbcMain.weightx = 0.65;
        mainContentPanel.add(diagramPanel, gbcMain);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(mainContentPanel, gbc);

        updateInputFields();

        JButton calcButton = new JButton("Calculate Output Voltage");
        calcButton.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridy = 6;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(calcButton, gbc);

        resultArea = new JTextArea(4, 20);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.BOLD, 12));
        resultArea.setBackground(new Color(240, 240, 240));
        gbc.gridy = 7;
        add(new JScrollPane(resultArea), gbc);

        calcButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculate();
            }
        });

        setVisible(true);
    }

    public void addComponent(Component comp, int x, int y, int width) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        add(comp, gbc);
    }

    public void updateInputFields() {
        inputPanel.removeAll();
        GridBagConstraints gbcInput = new GridBagConstraints();
        gbcInput.insets = new Insets(5, 5, 5, 5);
        gbcInput.fill = GridBagConstraints.HORIZONTAL;
        gbcInput.weightx = 1.0;

        if (singleInputRadio.isSelected()) {
            numInputsSpinner.setEnabled(false);

            gbcInput.gridx = 0; gbcInput.gridy = 0; gbcInput.gridwidth = 1;
            JCheckBox includeR1 = new JCheckBox("Use Resistor R1", true);
            includeR1.setName("CB_R1");
            inputPanel.add(includeR1, gbcInput);

            gbcInput.gridx = 1;
            JTextField r1Field = new JTextField("1000");
            r1Field.setName("R1");

            includeR1.addActionListener(e -> {
                r1Field.setEnabled(includeR1.isSelected());
                diagramPanel.repaint();
            });
            r1Field.addActionListener(e -> diagramPanel.repaint());
            r1Field.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) { diagramPanel.repaint(); }
            });
            inputPanel.add(r1Field, gbcInput);

            gbcInput.gridx = 0; gbcInput.gridy = 1; gbcInput.gridwidth = 1;
            inputPanel.add(new JLabel("Input Voltage (V1) Volts:"), gbcInput);

            gbcInput.gridx = 1;
            JTextField v1Field = new JTextField("1");
            v1Field.setName("V1");
            v1Field.addActionListener(e -> diagramPanel.repaint());
            v1Field.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) { diagramPanel.repaint(); }
            });
            inputPanel.add(v1Field, gbcInput);

        } else {
            numInputsSpinner.setEnabled(true);
            int numInputs = (int) numInputsSpinner.getValue();

            for (int i = 1; i <= numInputs; i++) {
                int row = (i - 1) * 3;

                gbcInput.gridx = 0; gbcInput.gridy = row; gbcInput.gridwidth = 1;
                JCheckBox includeResistor = new JCheckBox("Use Resistor R" + i, true);
                includeResistor.setName("CB_R" + i);
                inputPanel.add(includeResistor, gbcInput);

                gbcInput.gridx = 1;
                JTextField rField = new JTextField("1000");
                rField.setName("R" + i);

                includeResistor.addActionListener(e -> {
                    rField.setEnabled(includeResistor.isSelected());
                    diagramPanel.repaint();
                });
                rField.addActionListener(e -> diagramPanel.repaint());
                rField.addFocusListener(new java.awt.event.FocusAdapter() {
                    public void focusLost(java.awt.event.FocusEvent evt) { diagramPanel.repaint(); }
                });
                inputPanel.add(rField, gbcInput);

                gbcInput.gridx = 0; gbcInput.gridy = row + 1;
                inputPanel.add(new JLabel("Input Voltage V" + i + " (V):"), gbcInput);

                gbcInput.gridx = 1;
                JTextField vField = new JTextField("1");
                vField.setName("V" + i);
                vField.addActionListener(e -> diagramPanel.repaint());
                vField.addFocusListener(new java.awt.event.FocusAdapter() {
                    public void focusLost(java.awt.event.FocusEvent evt) { diagramPanel.repaint(); }
                });
                inputPanel.add(vField, gbcInput);

                if (i < numInputs) {
                    gbcInput.gridx = 0; gbcInput.gridy = row + 2; gbcInput.gridwidth = 2;
                    inputPanel.add(new JSeparator(), gbcInput);
                }
            }
        }

        inputPanel.revalidate();
        inputPanel.repaint();
        diagramPanel.repaint();
    }

    public void calculate() {
        try {
            double rf = Double.parseDouble(rfField.getText());
            if (rf <= 0) throw new NumberFormatException("Rf must be > 0");

            double outputVoltage = 0;
            StringBuilder formula = new StringBuilder();

            if (singleInputRadio.isSelected()) {
                Component[] comps = inputPanel.getComponents();
                double r1 = 0, v1 = 0;
                boolean useR1 = true;

                for (Component c : comps) {
                    if (c instanceof JTextField) {
                        JTextField tf = (JTextField) c;
                        if ("R1".equals(tf.getName())) r1 = Double.parseDouble(tf.getText());
                        if ("V1".equals(tf.getName())) v1 = Double.parseDouble(tf.getText());
                    } else if (c instanceof JCheckBox) {
                        if ("CB_R1".equals(c.getName())) useR1 = ((JCheckBox) c).isSelected();
                    }
                }

                if (useR1) {
                    if (r1 == 0) throw new ArithmeticException("R1 cannot be zero if selected.");
                    outputVoltage = -(rf / r1) * v1;
                    formula.append("Vo = -(").append(rf).append(" / ").append(r1).append(") * ").append(v1);
                } else {
                    outputVoltage = -v1;
                    formula.append("Vo = -(No R) * ").append(v1);
                }

            } else {
                int numInputs = (int) numInputsSpinner.getValue();
                double[] r = new double[numInputs + 1];
                double[] v = new double[numInputs + 1];
                boolean[] useResistor = new boolean[numInputs + 1];

                Component[] comps = inputPanel.getComponents();

                for (Component c : comps) {
                    if (c instanceof JTextField) {
                        JTextField tf = (JTextField) c;
                        String name = tf.getName();
                        if (name != null && name.startsWith("R")) {
                            int idx = Integer.parseInt(name.substring(1));
                            r[idx] = Double.parseDouble(tf.getText());
                        }
                        if (name != null && name.startsWith("V")) {
                            int idx = Integer.parseInt(name.substring(1));
                            v[idx] = Double.parseDouble(tf.getText());
                        }
                    } else if (c instanceof JCheckBox) {
                        JCheckBox cb = (JCheckBox) c;
                        String name = cb.getName();
                        if (name != null && name.startsWith("CB_R")) {
                            int idx = Integer.parseInt(name.substring(4));
                            useResistor[idx] = cb.isSelected();
                        }
                    }
                }

                formula.append("Vo = -[ ");
                boolean firstTerm = true;

                for (int i = 1; i <= numInputs; i++) {
                    double termValue = 0;

                    if (useResistor[i]) {
                        if (r[i] == 0) throw new ArithmeticException("R" + i + " cannot be zero if selected.");
                        termValue = (rf / r[i]) * v[i];

                        if (!firstTerm) formula.append(" + ");
                        formula.append("(").append(rf).append("/").append(r[i]).append(")*").append(v[i]);
                    } else {
                        termValue = v[i];
                        if (!firstTerm) formula.append(" + ");
                        formula.append("(No R)*").append(v[i]);
                    }

                    outputVoltage -= termValue;
                    firstTerm = false;
                }
                formula.append(" ]");
            }

            resultArea.setText("Result:\n" + formula.toString() + "\n\nOutput Voltage (Vo) = " + String.format("%.4f", outputVoltage) + " V");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (ArithmeticException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Math Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isResistorUsed(String name) {
        for (Component c : inputPanel.getComponents()) {
            if (c instanceof JCheckBox && name.equals(c.getName())) {
                return ((JCheckBox) c).isSelected();
            }
        }
        return true;
    }

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

            int opAmpX = w - 180;
            int opAmpY = h / 2;
            int opAmpSize = 80;
            int invertingInputY = opAmpY - 25;
            int outputY = opAmpY;
            int outputX = opAmpX + opAmpSize;

            drawOpAmp(g2, opAmpX, opAmpY, opAmpSize);

            int rfWireY = invertingInputY - 60;
            String rfVal = rfField.getText();

            g2.drawLine(opAmpX - 20, invertingInputY, opAmpX - 20, rfWireY);
            drawResistor(g2, opAmpX - 20, rfWireY, outputX + 20, rfWireY, "Rf = " + rfVal + " \u03A9");
            g2.drawLine(outputX + 20, rfWireY, outputX + 20, outputY);
            g2.fillOval(outputX + 17, outputY - 3, 6, 6);

            g2.drawLine(outputX, outputY, outputX + 60, outputY);
            g2.drawString("Vo", outputX + 65, outputY + 5);

            if (singleInputRadio.isSelected()) {
                drawSingleInputCircuit(g2, opAmpX, invertingInputY);
            } else {
                drawMultiInputCircuit(g2, opAmpX, invertingInputY);
            }
        }

        public void drawOpAmp(Graphics2D g2, int x, int y, int size) {
            int[] xPoints = {x, x, x + size};
            int[] yPoints = {y - size/2, y + size/2, y};
            g2.setColor(Color.BLACK);
            g2.drawPolygon(xPoints, yPoints, 3);

            g2.drawString("-", x + 10, y - 15);
            g2.drawString("+", x + 10, y + 25);

            int nonInvertingInputY = y + 25;
            int groundWireLength = 70;
            int groundY = nonInvertingInputY + groundWireLength;

            g2.drawLine(x, nonInvertingInputY, x - 30, nonInvertingInputY);
            g2.drawLine(x - 30, nonInvertingInputY, x - 30, groundY);
            drawGround(g2, x - 30, groundY);
        }

        public void drawSingleInputCircuit(Graphics2D g2, int opAmpX, int invertingInputY) {
            String r1 = "1000", v1 = "1";
            boolean useR1 = true;

            for (Component c : inputPanel.getComponents()) {
                if (c instanceof JTextField) {
                    JTextField tf = (JTextField) c;
                    if ("R1".equals(tf.getName())) r1 = tf.getText();
                    if ("V1".equals(tf.getName())) v1 = tf.getText();
                } else if (c instanceof JCheckBox) {
                    if ("CB_R1".equals(c.getName())) useR1 = ((JCheckBox) c).isSelected();
                }
            }

            int startX = 40;

            drawOpenTerminalSource(g2, startX, invertingInputY, v1 + " V");

            g2.drawLine(startX + 15, invertingInputY, startX + 50, invertingInputY);

            if (useR1) {
                drawResistor(g2, startX + 50, invertingInputY, opAmpX - 20, invertingInputY, "R1 = " + r1 + " \u03A9");
            } else {
                g2.drawLine(startX + 50, invertingInputY, opAmpX - 20, invertingInputY);
                g2.drawString("(No R)", startX + 60, invertingInputY - 5);
            }

            g2.drawLine(opAmpX - 20, invertingInputY, opAmpX, invertingInputY);
            g2.fillOval(opAmpX - 23, invertingInputY - 3, 6, 6);
        }

        public void drawMultiInputCircuit(Graphics2D g2, int opAmpX, int invertingInputY) {
            int numInputs = (int) numInputsSpinner.getValue();
            int startX = 30;
            int verticalBusX = opAmpX - 60;

            double[] r = new double[numInputs + 1];
            double[] v = new double[numInputs + 1];
            boolean[] useResistor = new boolean[numInputs + 1];

            for (Component c : inputPanel.getComponents()) {
                if (c instanceof JTextField) {
                    JTextField tf = (JTextField) c;
                    String name = tf.getName();
                    if (name != null && name.startsWith("R")) {
                        try { r[Integer.parseInt(name.substring(1))] = Double.parseDouble(tf.getText()); } catch(Exception e){}
                    }
                    if (name != null && name.startsWith("V")) {
                        try { v[Integer.parseInt(name.substring(1))] = Double.parseDouble(tf.getText()); } catch(Exception e){}
                    }
                } else if (c instanceof JCheckBox) {
                    JCheckBox cb = (JCheckBox) c;
                    String name = cb.getName();
                    if (name != null && name.startsWith("CB_R")) {
                        useResistor[Integer.parseInt(name.substring(4))] = cb.isSelected();
                    }
                }
            }

            int spacing = 60;
            int totalHeight = (numInputs - 1) * spacing;
            int startY = invertingInputY - totalHeight / 2;

            g2.drawLine(verticalBusX, startY, verticalBusX, startY + totalHeight);
            g2.drawLine(verticalBusX, invertingInputY, opAmpX, invertingInputY);
            g2.fillOval(opAmpX - 23, invertingInputY - 3, 6, 6);

            for (int i = 1; i <= numInputs; i++) {
                int currentY = startY + (i - 1) * spacing;

                drawOpenTerminalSource(g2, startX, currentY, String.format("%.1f V", v[i]));
                g2.drawLine(startX + 15, currentY, startX + 50, currentY);

                if (useResistor[i]) {
                    drawResistor(g2, startX + 50, currentY, verticalBusX, currentY, "R" + i + " = " + String.format("%.0f", r[i]) + " \u03A9");
                } else {
                    g2.drawLine(startX + 50, currentY, verticalBusX, currentY);
                    g2.drawString("(No R)", startX + 60, currentY - 5);
                }

                g2.fillOval(verticalBusX - 3, currentY - 3, 6, 6);
            }
        }

        public void drawResistor(Graphics2D g2, int x1, int y1, int x2, int y2, String label) {
            g2.setColor(Color.BLACK);
            int midX = (x1 + x2) / 2;
            int midY = (y1 + y2) / 2;

            int zigzagWidth = 30;
            int zigzagHeight = 10;
            int startZig = midX - zigzagWidth / 2;

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
            xPoints[6] = startZig + 30;
            yPoints[6] = midY;

            g2.drawPolyline(xPoints, yPoints, 7);
            g2.drawLine(startZig + zigzagWidth, midY, x2, y2);

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