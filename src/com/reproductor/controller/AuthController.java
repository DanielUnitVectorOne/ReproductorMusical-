package com.reproductor.controller;

import com.reproductor.dao.UsuarioDAO;
import com.reproductor.model.Usuario;
import com.reproductor.utils.Constantes;

/**
 * Controlador de autenticación (Singleton).
 *
 * FIX #11: ya no accede directamente a Constantes.PASSWORD_ADMIN
 *          (campo público hardcodeado). Usa el método
 *          Constantes.verificarPasswordAdmin(password) para mantener
 *          la lógica de comparación encapsulada y facilitar una
 *          futura migración a contraseñas hasheadas.
 */
public class AuthController {

    private static AuthController instancia;
    private final UsuarioDAO usuarioDAO;
    private Usuario usuarioActual;

    private AuthController() {
        this.usuarioDAO  = new UsuarioDAO();
        this.usuarioActual = null;
    }

    public static AuthController getInstancia() {
        if (instancia == null) {
            instancia = new AuthController();
        }
        return instancia;
    }

    // ── LOGIN ADMINISTRADOR ────────────────────────────────────────────────────

    public boolean loginAdmin(String password) {
        // FIX #11: uso de método encapsulado en lugar de campo público
        if (Constantes.verificarPasswordAdmin(password)) {
            usuarioActual = new Usuario("admin", password, "Administrador", true);
            return true;
        }
        return false;
    }

    // ── LOGIN USUARIO ──────────────────────────────────────────────────────────

    public boolean loginUsuario(String username, String password) {
        Usuario usuario = usuarioDAO.validarCredenciales(username, password);
        if (usuario != null) {
            usuarioActual = usuario;
            return true;
        }
        return false;
    }

    // ── REGISTRO ───────────────────────────────────────────────────────────────

    public boolean registrarUsuario(String username, String password, String nombre) {
        if (usuarioDAO.buscarUsuario(username) != null) {
            return false; // ya existe
        }
        Usuario nuevoUsuario = new Usuario(username, password, nombre, false);
        return usuarioDAO.guardarUsuario(nuevoUsuario);
    }

    // ── SESIÓN ─────────────────────────────────────────────────────────────────

    public void cerrarSesion() {
        usuarioActual = null;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public boolean esAdmin() {
        return usuarioActual != null && usuarioActual.isEsAdministrador();
    }

    public boolean haySesionActiva() {
        return usuarioActual != null;
    }
}