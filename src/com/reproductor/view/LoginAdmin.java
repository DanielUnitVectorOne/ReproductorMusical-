package com.reproductor.view;

import com.reproductor.controller.AuthController;
import com.reproductor.utils.*;
import java.awt.*;
import javax.swing.*;

/**
 * Ventana de login para el administrador.
 *
 * FIX #6 — Variable shadowing de txtPassword:
 *   ANTES: La clase declaraba un campo de instancia
 *          "private CampoTextoModerno txtPassword" que nunca se
 *          inicializaba. Dentro de inicializarComponentes() se creaba
 *          una variable LOCAL "JPasswordField txtPassword" con el
 *          mismo nombre, ocultando (shadowing) al campo. Esto
 *          compilaba sin error porque el lambda de "Enter para login"
 *          capturaba la variable local; pero el campo de instancia
 *          quedaba null y cualquier acceso externo a él hubiera
 *          producido NullPointerException.
 *
 *   AHORA: El campo de instancia se declara correctamente como
 *          JPasswordField (el tipo real que se necesita), se inicializa
 *          dentro de inicializarComponentes() asignándolo a this.txtPassword,
 *          y no existe ninguna variable local con el mismo nombre.
 */
public class LoginAdmin extends JFrame {

    private final VentanaPrincipal ventanaPrincipal;

    // FIX #6: tipo correcto (JPasswordField) y sin variable local que lo oculte
    private JPasswordField txtPassword;
    private BotonRedondeado btnIngresar;
    private BotonRedondeado btnVolver;

    public LoginAdmin(VentanaPrincipal ventanaPrincipal) {
        this.ventanaPrincipal = ventanaPrincipal;
        configurarVentana();
        inicializarComponentes();
    }

    // ── CONFIGURACIÓN ──────────────────────────────────────────────────────────

    private void configurarVentana() {
        setTitle("Login Administrador");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    // ── COMPONENTES ────────────────────────────────────────────────────────────

    private void inicializarComponentes() {
        // Barra de título personalizada
        BarraTituloPersonalizado barraTitulo =
                new BarraTituloPersonalizado(this, "Acceso Administrador");
        add(barraTitulo, BorderLayout.NORTH);

        // Panel principal con degradado
        PanelDegradado panelFondo = new PanelDegradado();
        panelFondo.setLayout(null);
        add(panelFondo, BorderLayout.CENTER);

        // Ícono
        JLabel lblIcono = new JLabel(
        new ImageIcon(getClass().getResource("/com/recursos/iconos/cuenta.png")));
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

        // ── Campo contraseña ──────────────────────────────────────────────────
        // FIX #6: se asigna directamente al campo de instancia (this.txtPassword),
        //         sin crear ninguna variable local con el mismo nombre.
        txtPassword = new JPasswordField();
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
        btnIngresar.addActionListener(e -> validarLogin());
        panelFondo.add(btnIngresar);

        // Botón Volver
        btnVolver = new BotonRedondeado("VOLVER", Constantes.COLOR_FONDO_CLARO);
        btnVolver.setBounds(150, 315, 200, 40);
        btnVolver.addActionListener(e -> volver());
        panelFondo.add(btnVolver);

        // Enter también valida el login
        txtPassword.addActionListener(e -> validarLogin());
    }

    // ── LÓGICA ─────────────────────────────────────────────────────────────────

    private void validarLogin() {
        // FIX #6: ahora txtPassword es el campo de instancia correctamente
        //         inicializado; ya no hay ambigüedad.
        String password = new String(txtPassword.getPassword());

        if (AuthController.getInstancia().loginAdmin(password)) {
            PanelAdmin panelAdmin = new PanelAdmin(this);
            panelAdmin.setVisible(true);
            this.setVisible(false);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Contraseña incorrecta",
                    "Error de acceso",
                    JOptionPane.ERROR_MESSAGE);
            txtPassword.setText(""); // limpiar campo tras error
            txtPassword.requestFocus();
        }
    }

    private void volver() {
        ventanaPrincipal.setVisible(true);
        this.dispose();
    }
}