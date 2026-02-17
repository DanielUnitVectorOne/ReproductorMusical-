package com.reproductor.view;

import com.reproductor.controller.AuthController;
import com.reproductor.utils.*;
import java.awt.*;
import javax.swing.*;

public class RegistroUsuario extends JFrame {
    
    private LoginUsuario loginUsuario;
    private CampoTextoModerno txtUsername;
    private CampoTextoModerno txtNombre;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    
    public RegistroUsuario(LoginUsuario loginUsuario) {
        this.loginUsuario = loginUsuario;
        configurarVentana();
        inicializarComponentes();
    }
    
    private void configurarVentana() {
        setTitle("Registro de Usuario");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }
    
    private void inicializarComponentes() {
        // Barra de título
        BarraTituloPersonalizado barraTitulo = new BarraTituloPersonalizado(this, "Registro");
        add(barraTitulo, BorderLayout.NORTH);
        
        // Panel principal
        PanelDegradado panelFondo = new PanelDegradado();
        panelFondo.setLayout(null);
        add(panelFondo, BorderLayout.CENTER);
        
        // Título
        JLabel lblTitulo = new JLabel("Crear Cuenta");
        lblTitulo.setFont(Constantes.FUENTE_TITULO);
        lblTitulo.setForeground(Constantes.COLOR_TEXTO);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBounds(0, 60, 500, 40);
        panelFondo.add(lblTitulo);
        
        // Subtítulo
        JLabel lblSub = new JLabel("Complete los datos para registrarse");
        lblSub.setFont(Constantes.FUENTE_NORMAL);
        lblSub.setForeground(Constantes.COLOR_TEXTO_SECUNDARIO);
        lblSub.setHorizontalAlignment(SwingConstants.CENTER);
        lblSub.setBounds(0, 100, 500, 25);
        panelFondo.add(lblSub);
        
        // Campo nombre completo
        txtNombre = new CampoTextoModerno("Nombre completo");
        txtNombre.setBounds(100, 150, 300, 40);
        panelFondo.add(txtNombre);
        
        // Campo username
        txtUsername = new CampoTextoModerno("Nombre de usuario");
        txtUsername.setBounds(100, 200, 300, 40);
        panelFondo.add(txtUsername);
        
        // Campo contraseña
        txtPassword = crearCampoPassword();
        txtPassword.setBounds(100, 250, 300, 40);
        panelFondo.add(txtPassword);
        
        // Campo confirmar contraseña
        txtConfirmPassword = crearCampoPassword();
        txtConfirmPassword.setBounds(100, 300, 300, 40);
        panelFondo.add(txtConfirmPassword);
        
        // Botón Registrarse
        BotonRedondeado btnRegistrar = new BotonRedondeado("REGISTRARSE");
        btnRegistrar.setBounds(150, 360, 200, 45);
        btnRegistrar.addActionListener(e -> registrar());
        panelFondo.add(btnRegistrar);
        
        // Botón Volver
        BotonRedondeado btnVolver = new BotonRedondeado("YA TENGO CUENTA", Constantes.COLOR_FONDO_CLARO);
        btnVolver.setBounds(150, 415, 200, 40);
        btnVolver.addActionListener(e -> volver());
        panelFondo.add(btnVolver);
    }
    
    private JPasswordField crearCampoPassword() {
        JPasswordField campo = new JPasswordField();
        campo.setBackground(Constantes.COLOR_FONDO_MEDIO);
        campo.setForeground(Constantes.COLOR_TEXTO);
        campo.setCaretColor(Constantes.COLOR_TEXTO);
        campo.setFont(Constantes.FUENTE_NORMAL);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Constantes.COLOR_FONDO_CLARO, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return campo;
    }
    
    private void registrar() {
        String nombre = txtNombre.getText().trim();
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());
        
        // Validaciones
        if (nombre.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Por favor complete todos los campos",
                "Campos vacíos",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (password.length() < 4) {
            JOptionPane.showMessageDialog(this,
                "La contraseña debe tener al menos 4 caracteres",
                "Contraseña débil",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                "Las contraseñas no coinciden",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Intentar registrar
        if (AuthController.getInstancia().registrarUsuario(username, password, nombre)) {
            JOptionPane.showMessageDialog(this,
                Constantes.EXITO_REGISTRO,
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
            volver();
        } else {
            JOptionPane.showMessageDialog(this,
                "El nombre de usuario ya existe",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void volver() {
        loginUsuario.setVisible(true);
        this.dispose();
    }
}