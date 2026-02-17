package com.reproductor.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class CampoTextoModerno extends JTextField {
    
    private String placeholder;
    private Color colorBorde;
    private Color colorFocus;
    private boolean tieneFoco;
    
    public CampoTextoModerno(String placeholder) {
        this.placeholder = placeholder;
        this.colorBorde = Constantes.COLOR_FONDO_CLARO;
        this.colorFocus = Constantes.COLOR_PRIMARIO;
        this.tieneFoco = false;
        
        configurarEstilo();
        agregarEfectosFoco();
    }
    
    private void configurarEstilo() {
        setBackground(Constantes.COLOR_FONDO_MEDIO);
        setForeground(Constantes.COLOR_TEXTO);
        setCaretColor(Constantes.COLOR_TEXTO);
        setFont(Constantes.FUENTE_NORMAL);
        setBorder(new EmptyBorder(10, 15, 10, 15));
        setOpaque(false);
    }
    
    private void agregarEfectosFoco() {
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                tieneFoco = true;
                repaint();
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                tieneFoco = false;
                repaint();
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Fondo
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        
        // Borde
        g2.setColor(tieneFoco ? colorFocus : colorBorde);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
        
        g2.dispose();
        super.paintComponent(g);
    }
    
    @Override
    protected void paintBorder(Graphics g) {
        // No pintar borde (ya lo hacemos en paintComponent)
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        
        // Placeholder
        if (getText().isEmpty() && !tieneFoco) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Constantes.COLOR_TEXTO_SECUNDARIO);
            g2.setFont(getFont());
            g2.drawString(placeholder, getInsets().left, g.getFontMetrics().getMaxAscent() + getInsets().top);
        }
    }
    
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }
}