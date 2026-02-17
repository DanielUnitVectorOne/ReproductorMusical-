package com.reproductor.utils;

import java.awt.Color;
import java.awt.Font;

public class Constantes {
    
    // COLORES
    public static final Color COLOR_PRIMARIO = new Color(30, 215, 96);
    public static final Color COLOR_FONDO_OSCURO = new Color(18, 18, 18);
    public static final Color COLOR_FONDO_MEDIO = new Color(40, 40, 40);
    public static final Color COLOR_FONDO_CLARO = new Color(60, 60, 60);
    public static final Color COLOR_TEXTO = new Color(255, 255, 255);
    public static final Color COLOR_TEXTO_SECUNDARIO = new Color(179, 179, 179);
    public static final Color COLOR_ACENTO = new Color(29, 185, 84);
    public static final Color COLOR_ERROR = new Color(244, 67, 54);
    public static final Color COLOR_HOVER = new Color(50, 50, 50);
    
    // FUENTES
    public static final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 32);
    public static final Font FUENTE_SUBTITULO = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FUENTE_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FUENTE_BOTON = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FUENTE_PEQUEÑA = new Font("Segoe UI", Font.PLAIN, 12);
    
    // DIMENSIONES
    public static final int ANCHO_VENTANA = 1200;
    public static final int ALTO_VENTANA = 700;
    public static final int ALTURA_BARRA_TITULO = 40;
    public static final int RADIO_BOTON = 20;
    
    // RUTAS
    public static final String RUTA_ICONOS = "/resources/icons/";
    public static final String PASSWORD_ADMIN = "admin123";
    
    // MENSAJES
    public static final String BIENVENIDA = "REPRODUCTOR MUSICAL";
    public static final String ERROR_LOGIN = "Usuario o contraseña incorrectos";
    public static final String ERROR_REGISTRO = "Error al registrar usuario";
    public static final String EXITO_REGISTRO = "Usuario registrado exitosamente";
}