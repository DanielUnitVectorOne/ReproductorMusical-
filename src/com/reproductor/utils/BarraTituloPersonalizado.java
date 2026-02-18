package com.reproductor.utils;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class BarraTituloPersonalizado extends JPanel {
    
    private Point puntoInicial;
    private JFrame ventana;
    
    public BarraTituloPersonalizado(JFrame ventana, String titulo) {
        this.ventana = ventana;
        setLayout(null);
        setBackground(Constantes.COLOR_FONDO_OSCURO);
        setPreferredSize(new Dimension(Constantes.ANCHO_VENTANA, Constantes.ALTURA_BARRA_TITULO));
        
        agregarTitulo(titulo);
        agregarBotones();
        hacerArrastrable();
    }
    
    private void agregarTitulo(String titulo) {
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setForeground(Constantes.COLOR_TEXTO);
        lblTitulo.setFont(Constantes.FUENTE_NORMAL);
        lblTitulo.setBounds(15, 0, 300, Constantes.ALTURA_BARRA_TITULO);
        add(lblTitulo);
    }
    
    private void agregarBotones() {
        // Botón cerrar
        JButton btnCerrar = crearBotonControl("", Constantes.COLOR_ERROR);
        ImageIcon iconoCerrar = new ImageIcon(getClass().getResource("/com/recursos/iconos/cerrar-ventana-96_1.png"));
        btnCerrar.setIcon(iconoCerrar);
        btnCerrar.setBounds(getPreferredSize().width - 50, 0, 50, Constantes.ALTURA_BARRA_TITULO);
        btnCerrar.addActionListener(e -> System.exit(0));

        add(btnCerrar);
        
        
        // Botón minimizar
        JButton btnMinimizar = crearBotonControl("", Constantes.COLOR_FONDO_CLARO);
        ImageIcon iconoMinimizar = new ImageIcon(getClass().getResource("/com/recursos/iconos/menos (1).png"));
        btnMinimizar.setIcon(iconoMinimizar);
        btnMinimizar.setBounds(getPreferredSize().width - 100, 0, 50, Constantes.ALTURA_BARRA_TITULO);
        btnMinimizar.addActionListener(e -> ventana.setState(Frame.ICONIFIED));
        add(btnMinimizar);
    }
    
    private JButton crearBotonControl(String texto, Color colorHover) {
        JButton btn = new JButton(texto);
        btn.setForeground(Constantes.COLOR_TEXTO);
        btn.setBackground(Constantes.COLOR_FONDO_OSCURO);
        btn.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 20));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        /*btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(colorHover);
                btn.setOpaque(true);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setOpaque(false);
            }
        });
        */
        return btn;
    }
    
    private void hacerArrastrable() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                puntoInicial = e.getPoint();
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point ubicacionActual = ventana.getLocation();
                ventana.setLocation(
                    ubicacionActual.x + e.getX() - puntoInicial.x,
                    ubicacionActual.y + e.getY() - puntoInicial.y
                );
            }
        });
    }
}