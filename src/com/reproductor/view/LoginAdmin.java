package com.reproductor.view;

import com.reproductor.controller.AuthController;
import com.reproductor.utils.*;
import java.awt.*;
import javax.swing.*;

public class LoginAdmin extends JFrame {
    
    private VentanaPrincipal ventanaPrincipal;
    private CampoTextoModerno txtPassword;
    private BotonRedondeado btnIngresar;
    private BotonRedondeado btnVolver;
    
    public LoginAdmin(VentanaPrincipal ventanaPrincipal) {
        this.ventanaPrincipal = ventanaPrincipal;
        configurarVentana();
        inicializarComponentes();
    }
    
    private void configurarVentana() {
        setTitle("Login Administrador");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }
    
    private void inicializarComponentes() {
        // Barra de título
        BarraTituloPersonalizado barraTitulo = new BarraTituloPersonalizado(this, "Acceso Administrador");
        add(barraTitulo, BorderLayout.NORTH);
        
        // Panel principal
        PanelDegradado panelFondo = new PanelDegradado();
        panelFondo.setLayout(null);
        add(panelFondo, BorderLayout.CENTER);
        
        // Icono
        JLabel lblIcono = new JLabel("🔐");
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        lblIcono.setHorizontalAlignment(SwingConstants.CENTER);
        lblIcono.setBounds(0, 60, 500, 70);
        panelFondo.add(lblIcono);
        
        // Título
        JLabel lblTitulo = new JLabel("Acceso Administrador");
        lblTitulo.setFont(Constantes.FUENTE_TITULO);
        lblTitulo.setForeground(Constantes.COLOR_TEXTO);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBounds(0, 140, 500, 40);
        panelFondo.add(lblTitulo);
        
        // Campo contraseña
        JPasswordField txtPassword = new JPasswordField();
        txtPassword.setBackground(Constantes.COLOR_FONDO_MEDIO);
        txtPassword.setForeground(Constantes.COLOR_TEXTO);
        txtPassword.setCaretColor(Constantes.COLOR_TEXTO);
        txtPassword.setFont(Constantes.FUENTE_NORMAL);
        txtPassword.setBounds(100, 200, 300, 40);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Constantes.COLOR_FONDO_CLARO, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panelFondo.add(txtPassword);
        
        // Botón Ingresar
        btnIngresar = new BotonRedondeado("INGRESAR");
        btnIngresar.setBounds(150, 260, 200, 45);
        btnIngresar.addActionListener(e -> validarLogin(new String(txtPassword.getPassword())));
        panelFondo.add(btnIngresar);
        
        // Botón Volver
        btnVolver = new BotonRedondeado("VOLVER", Constantes.COLOR_FONDO_CLARO);
        btnVolver.setBounds(150, 315, 200, 40);
        btnVolver.addActionListener(e -> volver());
        panelFondo.add(btnVolver);
        
        // Enter para login
        txtPassword.addActionListener(e -> validarLogin(new String(txtPassword.getPassword())));
    }
    
    private void validarLogin(String password) {
        if (AuthController.getInstancia().loginAdmin(password)) {
            PanelAdmin panelAdmin = new PanelAdmin(this);
            panelAdmin.setVisible(true);
            this.setVisible(false);
        } else {
            JOptionPane.showMessageDialog(this,
                "Contraseña incorrecta",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void volver() {
        ventanaPrincipal.setVisible(true);
        this.dispose();
    }
}