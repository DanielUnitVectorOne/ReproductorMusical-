package com.reproductor.model;

import com.reproductor.view.VentanaPrincipal;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;


public class Main {
     public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame ventana = new JFrame("Reproductor Musical");
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.setContentPane(new VentanaPrincipal());
            ventana.pack();
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
        });
    }
}
