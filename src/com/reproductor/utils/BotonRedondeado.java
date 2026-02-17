package com.reproductor.utils;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

public class BotonRedondeado extends JButton {
    
    private Color colorNormal;
    private Color colorHover;
    private Color colorPresionado;
    private Color colorActual;
    private int radio;
    
    public BotonRedondeado(String texto) {
        this(texto, Constantes.COLOR_PRIMARIO);
    }
    
    public BotonRedondeado(String texto, Color colorNormal) {
        super(texto);
        this.colorNormal = colorNormal;
        this.colorHover = colorNormal.brighter();
        this.colorPresionado = colorNormal.darker();
        this.colorActual = colorNormal;
        this.radio = Constantes.RADIO_BOTON;
        
        configurarEstilo();
        agregarEfectos();
    }
    
    private void configurarEstilo() {
        setForeground(Constantes.COLOR_TEXTO);
        setFont(Constantes.FUENTE_BOTON);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(new EmptyBorder(10, 25, 10, 25));
    }
    
    private void agregarEfectos() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                colorActual = colorHover;
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                colorActual = colorNormal;
                repaint();
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                colorActual = colorPresionado;
                repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                colorActual = colorHover;
                repaint();
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Fondo redondeado
        g2.setColor(colorActual);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radio, radio);
        
        g2.dispose();
        super.paintComponent(g);
    }
    
    public void setColorNormal(Color color) {
        this.colorNormal = color;
        this.colorHover = color.brighter();
        this.colorPresionado = color.darker();
        this.colorActual = color;
        repaint();
    }
    
    public void setRadio(int radio) {
        this.radio = radio;
        repaint();
    }
}