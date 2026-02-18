package com.reproductor.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PanelPersonalizado extends JPanel {
    
    private String titulo;
    private JPanel panelContenido;
    private JButton btnCerrar;
    
    public PanelPersonalizado(String titulo) {
        this.titulo = titulo;
        setLayout(new BorderLayout());
        setBackground(Constantes.COLOR_FONDO_MEDIO);
        setBorder(BorderFactory.createLineBorder(Constantes.COLOR_FONDO_CLARO, 2));
        
        inicializarComponentes();
    }
    
    private void inicializarComponentes() {
        // Barra superior personalizada
        JPanel barraSuperior = new JPanel(new BorderLayout());
        barraSuperior.setBackground(Constantes.COLOR_FONDO_OSCURO);
        barraSuperior.setPreferredSize(new Dimension(0, 40));
        
        JLabel lblTitulo = new JLabel("  " + titulo);
        lblTitulo.setFont(Constantes.FUENTE_SUBTITULO);
        lblTitulo.setForeground(Constantes.COLOR_TEXTO);
        barraSuperior.add(lblTitulo, BorderLayout.WEST);
        
        // Botón cerrar personalizado
        btnCerrar = new JButton("✕");
        btnCerrar.setFont(new Font("Arial", Font.PLAIN, 18));
        btnCerrar.setForeground(Constantes.COLOR_TEXTO);
        btnCerrar.setBackground(Constantes.COLOR_FONDO_OSCURO);
        btnCerrar.setBorderPainted(false);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setPreferredSize(new Dimension(40, 40));
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnCerrar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnCerrar.setBackground(Constantes.COLOR_ERROR);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btnCerrar.setBackground(Constantes.COLOR_FONDO_OSCURO);
            }
        });
        
        barraSuperior.add(btnCerrar, BorderLayout.EAST);
        add(barraSuperior, BorderLayout.NORTH);
        
        // Panel de contenido
        panelContenido = new JPanel();
        panelContenido.setBackground(Constantes.COLOR_FONDO_MEDIO);
        add(panelContenido, BorderLayout.CENTER);
    }
    
    public JPanel getPanelContenido() {
        return panelContenido;
    }
    
    public void setContenido(JPanel contenido) {
        remove(panelContenido);
        panelContenido = contenido;
        add(panelContenido, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
    
    public JButton getBotonCerrar() {
        return btnCerrar;
    }
}