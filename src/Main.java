import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
    public static void main(String[] args) {
       mainFrame frame=new mainFrame();

       frame.inverting.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               new InvertingSolverFrame();
           }
       });
       frame.non_inverting.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               new NonInvertingSolverFrame();
           }
       });
       frame.differential.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
                new DifferentialSolverFrame();
           }
       });
        frame.integrator.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new IntegratorSolverFrame();
            }
        });
       frame.current_to_voltage.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {

           }
       });
       frame.voltage_to_current.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {

           }
       });
    }
}