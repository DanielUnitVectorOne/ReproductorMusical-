package com.reproductor.main;

import com.reproductor.view.VentanaPrincipal;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    
    public static void main(String[] args) {
        // Configurar Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Iniciar aplicación
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        });
    }
}
