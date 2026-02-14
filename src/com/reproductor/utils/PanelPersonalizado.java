package com.reproductor.utils;

import javax.swing.JPanel;
import java.awt.*;

/**
 * Panel personalizado con gradiente y bordes redondeados 
 * PanelGradiente panel = new PanelGradiente();
 */
public class PanelPersonalizado extends JPanel {

    private Color colorInicio;
    private Color colorFin;
    
    // Configuración de bordes
    private int radio = 0;
    private boolean bordeRedondeado = false;
    
    // Dirección del gradiente
    private Orientacion orientacion = Orientacion.VERTICAL;
    
    // Enum para la orientación
    public enum Orientacion {
        VERTICAL,    // De arriba hacia abajo
        HORIZONTAL,  // De izquierda a derecha
        DIAGONAL     // Diagonal
    }
    
    public PanelPersonalizado(Color colorInicio, Color colorFin, int radio, Orientacion orientacion) {
        this.colorInicio = colorInicio;
        this.colorFin = colorFin;
        this.radio = radio;
        this.bordeRedondeado = radio > 0;
        this.orientacion = orientacion;
        configurarPanel();
    }
    

    private void configurarPanel() {
        setOpaque(false);  
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                            RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, 
                            RenderingHints.VALUE_RENDER_QUALITY);
        
        // Crear el gradiente según la orientación
        GradientPaint gradiente = crearGradiente();
        g2d.setPaint(gradiente);
        
        // Dibujar el fondo
        if (bordeRedondeado) {

            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), radio, radio);
        } else {
            // Rectangular normal
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        
        g2d.dispose();
    }
    
    /**
     * Crea el gradiente según la orientación configurada
     */
    private GradientPaint crearGradiente() {
        switch (orientacion) {
            case HORIZONTAL:
                return new GradientPaint(
                    0, 0, colorInicio,
                    getWidth(), 0, colorFin
                );
            case DIAGONAL:
                return new GradientPaint(
                    0, 0, colorInicio,
                    getWidth(), getHeight(), colorFin
                );
            case VERTICAL:
            default:
                return new GradientPaint(
                    0, 0, colorInicio,
                    0, getHeight(), colorFin
                );
        }
    }
    
    public Color getColorInicio() {
        return colorInicio;
    }
    
    public void setColorInicio(Color colorInicio) {
        this.colorInicio = colorInicio;
        repaint();
    }
    
    public Color getColorFin() {
        return colorFin;
    }
    
    public void setColorFin(Color colorFin) {
        this.colorFin = colorFin;
        repaint();
    }
    
    public int getRadio() {
        return radio;
    }
    
    public void setRadio(int radio) {
        this.radio = radio;
        this.bordeRedondeado = radio > 0;
        repaint();
    }
    
    public boolean isBordeRedondeado() {
        return bordeRedondeado;
    }
    
    public void setBordeRedondeado(boolean bordeRedondeado) {
        this.bordeRedondeado = bordeRedondeado;
        if (!bordeRedondeado) {
            this.radio = 0;
        }
        repaint();
    }
    
    public Orientacion getOrientacion() {
        return orientacion;
    }
    
    public void setOrientacion(Orientacion orientacion) {
        this.orientacion = orientacion;
        repaint();
    }
    

    public void setGradiente(Color inicio, Color fin) {
        this.colorInicio = inicio;
        this.colorFin = fin;
        repaint();
    }
}