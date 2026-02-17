package com.reproductor.view;

import com.reproductor.controller.ReproductorController;
import com.reproductor.model.Cancion;
import com.reproductor.utils.*;
import java.awt.*;
import javax.swing.*;

public class ReproductorPanel extends JInternalFrame {
    
    private JLabel lblCancionActual;
    private JLabel lblArtista;
    private JProgressBar progressBar;
    private BotonRedondeado btnPlay;
    private BotonRedondeado btnNext;
    private BotonRedondeado btnPrev;
    
    public ReproductorPanel() {
        super("Reproductor", false, true, false, false);
        setSize(900, 600);
        setLocation(20, 20);
        inicializarComponentes();
    }
    
    private void inicializarComponentes() {
        JPanel panel = new JPanel(null);
        panel.setBackground(Constantes.COLOR_FONDO_MEDIO);
        
        // Información de la canción
        lblCancionActual = new JLabel("No hay canción reproduciéndose");
        lblCancionActual.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblCancionActual.setForeground(Constantes.COLOR_TEXTO);
        lblCancionActual.setHorizontalAlignment(SwingConstants.CENTER);
        lblCancionActual.setBounds(0, 100, 900, 35);
        panel.add(lblCancionActual);
        
        lblArtista = new JLabel("");
        lblArtista.setFont(Constantes.FUENTE_SUBTITULO);
        lblArtista.setForeground(Constantes.COLOR_TEXTO_SECUNDARIO);
        lblArtista.setHorizontalAlignment(SwingConstants.CENTER);
        lblArtista.setBounds(0, 140, 900, 25);
        panel.add(lblArtista);
        
        // Barra de progreso
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setBackground(Constantes.COLOR_FONDO_CLARO);
        progressBar.setForeground(Constantes.COLOR_PRIMARIO);
        progressBar.setBounds(200, 250, 500, 10);
        panel.add(progressBar);
        
        // Controles
        int centroX = 450;
        int y = 320;
        
        btnPrev = new BotonRedondeado("⏮");
        btnPrev.setBounds(centroX - 150, y, 60, 60);
        btnPrev.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        btnPrev.addActionListener(e -> anterior());
        panel.add(btnPrev);
        
        btnPlay = new BotonRedondeado("▶");
        btnPlay.setBounds(centroX - 30, y, 60, 60);
        btnPlay.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        btnPlay.addActionListener(e -> togglePlay());
        panel.add(btnPlay);
        
        btnNext = new BotonRedondeado("⏭");
        btnNext.setBounds(centroX + 90, y, 60, 60);
        btnNext.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        btnNext.addActionListener(e -> siguiente());
        panel.add(btnNext);
        
        add(panel);
    }
    
    private void togglePlay() {
        ReproductorController ctrl = ReproductorController.getInstancia();
        if (ctrl.isReproduciendo()) {
            ctrl.pausar();
            btnPlay.setText("▶");
        } else {
            ctrl.reanudar();
            btnPlay.setText("⏸");
        }
    }
    
    private void siguiente() {
        ReproductorController.getInstancia().siguiente();
        actualizarInfo();
    }
    
    private void anterior() {
        ReproductorController.getInstancia().anterior();
        actualizarInfo();
    }
    
    private void actualizarInfo() {
        Cancion cancion = ReproductorController.getInstancia().getCancionActual();
        if (cancion != null) {
            lblCancionActual.setText(cancion.getTitulo());
            lblArtista.setText(cancion.getArtista());
        }
    }
}