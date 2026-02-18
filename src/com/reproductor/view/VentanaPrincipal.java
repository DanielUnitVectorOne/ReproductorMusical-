package com.reproductor.view;

import com.reproductor.utils.*;
import java.awt.*;
import javax.swing.*;

public class VentanaPrincipal extends JFrame {
    
    private PanelDegradado panelFondo;
    private BotonRedondeado btnAdmin;
    private BotonRedondeado btnUsuario;
    
    public VentanaPrincipal() {
        configurarVentana();
        inicializarComponentes();
    }
    
    private void configurarVentana() {
        setTitle("Reproductor Musical");
        setSize(Constantes.ANCHO_VENTANA, Constantes.ALTO_VENTANA);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true); // Sin barra de título default
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }
    
    private void inicializarComponentes() {
        // Barra de título personalizada
        BarraTituloPersonalizado barraTitulo = new BarraTituloPersonalizado(this, "Reproductor Musical");
        add(barraTitulo, BorderLayout.NORTH);
        
        // Panel principal con degradado
        panelFondo = new PanelDegradado(
            Constantes.COLOR_FONDO_OSCURO,
            new Color(30, 30, 30),
            true
        );
        panelFondo.setLayout(null);
        add(panelFondo, BorderLayout.CENTER);
        
        // Logo/Título
        JLabel lblBienvenida = new JLabel("REPRODUCTOR MUSICAL");
        lblBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 48));
        lblBienvenida.setForeground(Constantes.COLOR_TEXTO);
        lblBienvenida.setHorizontalAlignment(SwingConstants.CENTER);
        lblBienvenida.setBounds(0, 100, Constantes.ANCHO_VENTANA, 60);
        panelFondo.add(lblBienvenida);
        
        // Subtítulo
        JLabel lblSubtitulo = new JLabel("Seleccione su tipo de sesión");
        lblSubtitulo.setFont(Constantes.FUENTE_SUBTITULO);
        lblSubtitulo.setForeground(Constantes.COLOR_TEXTO_SECUNDARIO);
        lblSubtitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblSubtitulo.setBounds(0, 170, Constantes.ANCHO_VENTANA, 30);
        panelFondo.add(lblSubtitulo);
        
        // Botón Administrador
        btnAdmin = new BotonRedondeado("ADMINISTRADOR", Constantes.COLOR_PRIMARIO);
        ImageIcon iconoAdm = new ImageIcon(getClass().getResource("/com/recursos/iconos/admin1.png"));
        btnAdmin.setIcon(iconoAdm);
        btnAdmin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnAdmin.setBounds(400, 300, 400, 60);
        btnAdmin.addActionListener(e -> abrirLoginAdmin());
        panelFondo.add(btnAdmin);
        
        // Botón Usuario
        btnUsuario = new BotonRedondeado("USUARIO", Constantes.COLOR_ACENTO);
        ImageIcon iconoUsu = new ImageIcon(getClass().getResource("/com/recursos/iconos/users.png"));
        btnUsuario.setIcon(iconoUsu);
        btnUsuario.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnUsuario.setBounds(400, 380, 400, 60);
        btnUsuario.addActionListener(e -> abrirLoginUsuario());
        panelFondo.add(btnUsuario);
        
        // Mensaje inferior
        JLabel lblInfo = new JLabel("Desarrollado con ♥ para Programación");
        lblInfo.setFont(Constantes.FUENTE_PEQUEÑA);
        lblInfo.setForeground(Constantes.COLOR_TEXTO_SECUNDARIO);
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
        lblInfo.setBounds(0, Constantes.ALTO_VENTANA - 80, Constantes.ANCHO_VENTANA, 20);
        panelFondo.add(lblInfo);
    }
    
    private void abrirLoginAdmin() {
        LoginAdmin loginAdmin = new LoginAdmin(this);
        loginAdmin.setVisible(true);
        this.setVisible(false);
    }
    
    private void abrirLoginUsuario() {
        LoginUsuario loginUsuario = new LoginUsuario(this);
        loginUsuario.setVisible(true);
        this.setVisible(false);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        });
    }
}
