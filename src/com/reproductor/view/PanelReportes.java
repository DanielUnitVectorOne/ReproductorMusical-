package com.reproductor.view;

import com.reproductor.controller.AuthController;
import com.reproductor.controller.SistemaController;
import com.reproductor.model.Cancion;
import com.reproductor.utils.*;
import java.awt.*;
import javax.swing.*;
import java.util.List;
import java.io.File;

public class PanelReportes extends JInternalFrame {
    
    private boolean esAdmin;
    
    public PanelReportes(boolean esAdmin) {
        super("Reportes y Estadísticas", false, true, false, false);
        this.esAdmin = esAdmin;
        setSize(900, 600);
        setLocation(20, 20);
        inicializarComponentes();
    }
    
    private void inicializarComponentes() {
        JPanel panel = new JPanel(null);
        panel.setBackground(Constantes.COLOR_FONDO_MEDIO);
        
        JLabel lblTitulo = new JLabel(esAdmin ? "Reportes Generales" : "Mis Reportes");
        lblTitulo.setFont(Constantes.FUENTE_TITULO);
        lblTitulo.setForeground(Constantes.COLOR_TEXTO);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBounds(0, 30, 900, 40);
        panel.add(lblTitulo);
        
        // Top Canciones
        JLabel lblTop = new JLabel("Top 10 Canciones Más Escuchadas");
        lblTop.setFont(Constantes.FUENTE_SUBTITULO);
        lblTop.setForeground(Constantes.COLOR_TEXTO);
        lblTop.setBounds(50, 100, 400, 30);
        panel.add(lblTop);
        
        JTextArea txtTop = new JTextArea();
        txtTop.setEditable(false);
        txtTop.setBackground(Constantes.COLOR_FONDO_CLARO);
        txtTop.setForeground(Constantes.COLOR_TEXTO);
        txtTop.setFont(Constantes.FUENTE_NORMAL);
        
        List<Cancion> top = SistemaController.getInstancia().obtenerTopCanciones(10);
        StringBuilder sb = new StringBuilder();
        int pos = 1;
        for (Cancion c : top) {
            sb.append(pos++).append(". ")
              .append(c.getTitulo()).append(" - ")
              .append(c.getArtista()).append(" (")
              .append(c.getVecesReproducida()).append(" reproducciones)\n");
        }
        txtTop.setText(sb.toString());
        
        JScrollPane scroll = new JScrollPane(txtTop);
        scroll.setBounds(50, 140, 800, 300);
        panel.add(scroll);
        
        // Botón descargar PDF
        BotonRedondeado btnPDF = new BotonRedondeado("📄 Descargar PDF");
        btnPDF.setBounds(350, 470, 200, 50);
        btnPDF.addActionListener(e -> generarPDF());
        panel.add(btnPDF);
        
        add(panel);
    }
    
    private void generarPDF() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar Reporte");
        chooser.setSelectedFile(new File("reporte.pdf"));
        
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String ruta = chooser.getSelectedFile().getAbsolutePath();
            if (!ruta.endsWith(".pdf")) {
                ruta += ".pdf";
            }
            
            List<Cancion> top = SistemaController.getInstancia().obtenerTopCanciones(10);
            
            if (esAdmin) {
                GeneradorPDF.generarReporteGeneral(top, null, ruta);
            } else {
                String username = AuthController.getInstancia().getUsuarioActual().getUsername();
                GeneradorPDF.generarReporteUsuario(username, top, null, ruta);
            }
            
            JOptionPane.showMessageDialog(this, "PDF generado exitosamente en:\n" + ruta);
        }
    }
}