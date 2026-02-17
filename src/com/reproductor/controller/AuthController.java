package com.reproductor.controller;

import com.reproductor.dao.UsuarioDAO;
import com.reproductor.model.Usuario;
import com.reproductor.utils.Constantes;

public class AuthController {
    
    private static AuthController instancia;
    private UsuarioDAO usuarioDAO;
    private Usuario usuarioActual;
    
    private AuthController() {
        this.usuarioDAO = new UsuarioDAO();
        this.usuarioActual = null;
    }
    
    public static AuthController getInstancia() {
        if (instancia == null) {
            instancia = new AuthController();
        }
        return instancia;
    }
    
    public boolean loginAdmin(String password) {
        if (Constantes.PASSWORD_ADMIN.equals(password)) {
            // Crear usuario admin temporal
            usuarioActual = new Usuario("admin", password, "Administrador", true);
            return true;
        }
        return false;
    }
    
    public boolean loginUsuario(String username, String password) {
        Usuario usuario = usuarioDAO.validarCredenciales(username, password);
        if (usuario != null) {
            usuarioActual = usuario;
            return true;
        }
        return false;
    }
    
    public boolean registrarUsuario(String username, String password, String nombre) {
        // Verificar si ya existe
        if (usuarioDAO.buscarUsuario(username) != null) {
            return false;
        }
        
        Usuario nuevoUsuario = new Usuario(username, password, nombre, false);
        return usuarioDAO.guardarUsuario(nuevoUsuario);
    }
    
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