package com.reproductor.view;

import com.reproductor.controller.AuthController;
import com.reproductor.utils.*;
import java.awt.*;
import javax.swing.*;

public class LoginUsuario extends JFrame {
    
    private VentanaPrincipal ventanaPrincipal;
    private CampoTextoModerno txtUsername;
    private JPasswordField txtPassword;
    
    public LoginUsuario(VentanaPrincipal ventanaPrincipal) {
        this.ventanaPrincipal = ventanaPrincipal;
        configurarVentana();
        inicializarComponentes();
    }
    
    private void configurarVentana() {
        setTitle("Login Usuario");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }
    
    private void inicializarComponentes() {
        // Barra de título
        BarraTituloPersonalizado barraTitulo = new BarraTituloPersonalizado(this, "Inicio de Sesión");
        add(barraTitulo, BorderLayout.NORTH);
        
        // Panel principal
        PanelDegradado panelFondo = new PanelDegradado();
        panelFondo.setLayout(null);
        add(panelFondo, BorderLayout.CENTER);
        
        // Icono
        JLabel lblIcono = new JLabel(new ImageIcon(getClass().getResource("/com/recursos/iconos/add-user.png")));
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        lblIcono.setHorizontalAlignment(SwingConstants.CENTER);
        lblIcono.setBounds(0, 60, 500, 70);
        panelFondo.add(lblIcono);
        
        // Título
        JLabel lblTitulo = new JLabel("Inicio de Sesión");
        lblTitulo.setFont(Constantes.FUENTE_TITULO);
        lblTitulo.setForeground(Constantes.COLOR_TEXTO);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBounds(0, 140, 500, 40);
        panelFondo.add(lblTitulo);
        
        // Campo usuario
        txtUsername = new CampoTextoModerno("Usuario");
        txtUsername.setBounds(100, 200, 300, 40);
        panelFondo.add(txtUsername);
        
        // Campo contraseña
        txtPassword = new JPasswordField();
        txtPassword.setBackground(Constantes.COLOR_FONDO_MEDIO);
        txtPassword.setForeground(Constantes.COLOR_TEXTO);
        txtPassword.setCaretColor(Constantes.COLOR_TEXTO);
        txtPassword.setFont(Constantes.FUENTE_NORMAL);
        txtPassword.setBounds(100, 250, 300, 40);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Constantes.COLOR_FONDO_CLARO, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panelFondo.add(txtPassword);
        
        // Botón Iniciar Sesión
        BotonRedondeado btnLogin = new BotonRedondeado("INICIAR SESIÓN");
        btnLogin.setBounds(150, 310, 200, 45);
        btnLogin.addActionListener(e -> validarLogin());
        panelFondo.add(btnLogin);
        
        // Botón Registrarse
        BotonRedondeado btnRegistro = new BotonRedondeado("REGISTRARSE", Constantes.COLOR_ACENTO);
        btnRegistro.setBounds(150, 365, 200, 40);
        btnRegistro.addActionListener(e -> abrirRegistro());
        panelFondo.add(btnRegistro);
        
        // Botón Volver
        BotonRedondeado btnVolver = new BotonRedondeado("VOLVER", Constantes.COLOR_FONDO_CLARO);
        btnVolver.setBounds(150, 415, 200, 40);
        btnVolver.addActionListener(e -> volver());
        panelFondo.add(btnVolver);
        
        // Enter para login
        txtPassword.addActionListener(e -> validarLogin());
    }
    
    private void validarLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Por favor complete todos los campos",
                "Campos vacíos",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (AuthController.getInstancia().loginUsuario(username, password)) {
            PanelUsuario panelUsuario = new PanelUsuario(this);
            panelUsuario.setVisible(true);
            this.setVisible(false);
        } else {
            JOptionPane.showMessageDialog(this,
                Constantes.ERROR_LOGIN,
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void abrirRegistro() {
        RegistroUsuario registro = new RegistroUsuario(this);
        registro.setVisible(true);
        this.setVisible(false);
    }
    
    private void volver() {
        ventanaPrincipal.setVisible(true);
        this.dispose();
    }
}