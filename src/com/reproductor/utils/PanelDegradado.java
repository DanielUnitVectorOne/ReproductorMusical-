package com.reproductor.utils;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

public class PanelDegradado extends JPanel {
    
    private Color colorInicio;
    private Color colorFin;
    private boolean vertical;
    
    public PanelDegradado() {
        this(Constantes.COLOR_FONDO_OSCURO, Constantes.COLOR_FONDO_MEDIO, true);
    }
    
    public PanelDegradado(Color inicio, Color fin, boolean vertical) {
        this.colorInicio = inicio;
        this.colorFin = fin;
        this.vertical = vertical;
        setOpaque(false);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        int ancho = getWidth();
        int alto = getHeight();
        
        GradientPaint gp;
        if (vertical) {
            gp = new GradientPaint(0, 0, colorInicio, 0, alto, colorFin);
        } else {
            gp = new GradientPaint(0, 0, colorInicio, ancho, 0, colorFin);
        }
        
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, ancho, alto);
    }
    
    public void setColorInicio(Color color) {
        this.colorInicio = color;
        repaint();
    }
    
    public void setColorFin(Color color) {
        this.colorFin = color;
        repaint();
    }
}