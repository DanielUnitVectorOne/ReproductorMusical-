package com.reproductor.utils;

import java.awt.Color;
import java.awt.Font;

/**
 * Constantes globales del sistema.
 *
 * FIX #11: PASSWORD_ADMIN ya no es una constante pública hardcodeada
 *          con un nombre obvio. Se mantiene en una sola clase pero se
 *          añade advertencia clara. En un sistema real debería estar
 *          hasheada (BCrypt, SHA-256) y almacenada en archivo externo.
 */
public class Constantes {

    // ── COLORES ────────────────────────────────────────────────────────────────
    public static final Color COLOR_PRIMARIO          = new Color(30, 215, 96);
    public static final Color COLOR_FONDO_OSCURO      = new Color(18, 18, 18);
    public static final Color COLOR_FONDO_MEDIO       = new Color(40, 40, 40);
    public static final Color COLOR_FONDO_CLARO       = new Color(60, 60, 60);
    public static final Color COLOR_TEXTO             = new Color(255, 255, 255);
    public static final Color COLOR_TEXTO_SECUNDARIO  = new Color(179, 179, 179);
    public static final Color COLOR_ACENTO            = new Color(29, 185, 84);
    public static final Color COLOR_ERROR             = new Color(244, 67, 54);
    public static final Color COLOR_HOVER             = new Color(50, 50, 50);

    // ── FUENTES ────────────────────────────────────────────────────────────────
    public static final Font FUENTE_TITULO    = new Font("Segoe UI", Font.BOLD,  32);
    public static final Font FUENTE_SUBTITULO = new Font("Segoe UI", Font.BOLD,  20);
    public static final Font FUENTE_NORMAL    = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FUENTE_BOTON     = new Font("Segoe UI", Font.BOLD,  14);
    public static final Font FUENTE_PEQUEÑA   = new Font("Segoe UI", Font.PLAIN, 12);

    // ── DIMENSIONES ────────────────────────────────────────────────────────────
    public static final int ANCHO_VENTANA       = 1200;
    public static final int ALTO_VENTANA        = 700;
    public static final int ALTURA_BARRA_TITULO = 40;
    public static final int RADIO_BOTON         = 20;

    // ── AUTENTICACIÓN ──────────────────────────────────────────────────────────
    /**
     * ADVERTENCIA DE SEGURIDAD: En producción esta contraseña debe estar
     * hasheada (BCrypt) y cargada desde un archivo de configuración externo,
     * nunca embebida en el código fuente. Para este proyecto académico se
     * deja aquí como valor único de configuración.
     */
    static final String CLAVE_ADMIN = "admin123";

    /**
     * Verifica si la contraseña proporcionada corresponde al administrador.
     * Centralizar la comparación aquí facilita migrar a hash en el futuro.
     */
    public static boolean verificarPasswordAdmin(String password) {
        return CLAVE_ADMIN.equals(password);
    }

    // ── MENSAJES ───────────────────────────────────────────────────────────────
    public static final String BIENVENIDA       = "REPRODUCTOR MUSICAL";
    public static final String ERROR_LOGIN      = "Usuario o contraseña incorrectos";
    public static final String ERROR_REGISTRO   = "Error al registrar usuario";
    public static final String EXITO_REGISTRO   = "Usuario registrado exitosamente";
}