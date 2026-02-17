package com.reproductor.view;

import com.reproductor.controller.AuthController;
import com.reproductor.controller.SistemaController;
import com.reproductor.model.Playlist;
import com.reproductor.utils.*;
import java.awt.*;
import javax.swing.*;

public class PanelUsuario extends JFrame {
    
    private LoginUsuario loginUsuario;
    private JDesktopPane desktopPane;
    
    public PanelUsuario(LoginUsuario loginUsuario) {
        this.loginUsuario = loginUsuario;
        configurarVentana();
        inicializarComponentes();
    }
    
    private void configurarVentana() {
        setTitle("Mi Reproductor");
        setSize(Constantes.ANCHO_VENTANA, Constantes.ALTO_VENTANA);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }
    
    private void inicializarComponentes() {
        add(new BarraTituloPersonalizado(this, "Mi Reproductor"), BorderLayout.NORTH);
        add(crearPanelMenu(), BorderLayout.WEST);
        
        desktopPane = new JDesktopPane();
        desktopPane.setBackground(Constantes.COLOR_FONDO_MEDIO);
        add(desktopPane, BorderLayout.CENTER);
        
        mostrarReproductor();
    }
    
    private JPanel crearPanelMenu() {
        PanelDegradado menu = new PanelDegradado(new Color(25, 25, 25), Constantes.COLOR_FONDO_OSCURO, true);
        menu.setPreferredSize(new Dimension(250, Constantes.ALTO_VENTANA));
        menu.setLayout(null);
        
        JLabel titulo = new JLabel("MI MÚSICA");
        titulo.setFont(Constantes.FUENTE_SUBTITULO);
        titulo.setForeground(Constantes.COLOR_PRIMARIO);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setBounds(0, 20, 250, 30);
        menu.add(titulo);
        
        int y = 80;
        
        BotonRedondeado btnReproductor = new BotonRedondeado("🎵 Reproductor");
        btnReproductor.setBounds(25, y, 200, 50);
        btnReproductor.addActionListener(e -> mostrarReproductor());
        menu.add(btnReproductor);
        y += 60;
        
        BotonRedondeado btnPlaylists = new BotonRedondeado("📋 Mis Playlists");
        btnPlaylists.setBounds(25, y, 200, 50);
        btnPlaylists.addActionListener(e -> mostrarMisPlaylists());
        menu.add(btnPlaylists);
        y += 60;
        
        BotonRedondeado btnPublicas = new BotonRedondeado("🌐 Playlists Públicas");
        btnPublicas.setBounds(25, y, 200, 50);
        btnPublicas.addActionListener(e -> mostrarPlaylistsPublicas());
        menu.add(btnPublicas);
        y += 60;
        
        BotonRedondeado btnReportes = new BotonRedondeado("📊 Mis Reportes");
        btnReportes.setBounds(25, y, 200, 50);
        btnReportes.addActionListener(e -> mostrarReportes());
        menu.add(btnReportes);
        
        BotonRedondeado btnCerrar = new BotonRedondeado("🚪 Cerrar Sesión", Constantes.COLOR_ERROR);
        btnCerrar.setBounds(25, Constantes.ALTO_VENTANA - 100, 200, 50);
        btnCerrar.addActionListener(e -> cerrarSesion());
        menu.add(btnCerrar);
        
        return menu;
    }
    
    private void mostrarReproductor() {
        desktopPane.removeAll();
        desktopPane.repaint();
        
        ReproductorPanel reproductor = new ReproductorPanel();
        desktopPane.add(reproductor);
        reproductor.setVisible(true);
    }
    
    private void mostrarMisPlaylists() {
        desktopPane.removeAll();
        desktopPane.repaint();
        
        // Mostrar playlists del usuario
        JInternalFrame frame = new JInternalFrame("Mis Playlists", false, true, false, false);
        frame.setSize(800, 500);
        frame.setLocation(50, 50);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Constantes.COLOR_FONDO_MEDIO);
        
        JLabel lbl = new JLabel("Aquí van tus playlists personales");
        lbl.setForeground(Constantes.COLOR_TEXTO);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lbl, BorderLayout.CENTER);
        
        frame.add(panel);
        frame.setVisible(true);
        desktopPane.add(frame);
    }
    
    private void mostrarPlaylistsPublicas() {
        // Similar pero con playlists públicas
    }
    
    private void mostrarReportes() {
        desktopPane.removeAll();
        desktopPane.repaint();
        
        PanelReportes reportes = new PanelReportes(false);
        desktopPane.add(reportes);
        reportes.setVisible(true);
    }
    
    private void cerrarSesion() {
        AuthController.getInstancia().cerrarSesion();
        loginUsuario.setVisible(true);
        this.dispose();
    }
}