package com.reproductor.controller;

import com.reproductor.dao.CancionDAO;
import com.reproductor.dao.PlaylistDAO;
import com.reproductor.dao.EstadisticaDAO;
import com.reproductor.dao.UsuarioDAO;
import com.reproductor.model.Cancion;
import com.reproductor.model.Playlist;
import com.reproductor.model.Estadistica;
import com.reproductor.model.Usuario;
import java.util.List;

/**
 * Controlador principal del sistema (Singleton).
 *
 * CAMBIOS RESPECTO A LA VERSIÓN ANTERIOR:
 *  - Se agregó UsuarioDAO como dependencia para exponer métodos de
 *    consulta de usuarios desde la capa de vista (dashboard de admin).
 *  - Se eliminó buscarPlaylist() duplicado de obtenerPlaylistPorId().
 *  - Se agregó obtenerTodosUsuarios() y contarUsuarios() para el dashboard.
 */
public class SistemaController {

    private static SistemaController instancia;
    private final CancionDAO     cancionDAO;
    private final PlaylistDAO    playlistDAO;
    private final EstadisticaDAO estadisticaDAO;
    private final UsuarioDAO     usuarioDAO;

    private SistemaController() {
        this.cancionDAO     = new CancionDAO();
        this.playlistDAO    = new PlaylistDAO();
        this.estadisticaDAO = new EstadisticaDAO();
        this.usuarioDAO     = new UsuarioDAO();
    }

    public static SistemaController getInstancia() {
        if (instancia == null) {
            instancia = new SistemaController();
        }
        return instancia;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  CANCIONES
    // ════════════════════════════════════════════════════════════════════════

    public boolean agregarCancion(Cancion cancion) {
        return cancionDAO.guardarCancion(cancion);
    }

    public List<Cancion> obtenerTodasCanciones() {
        return cancionDAO.cargarCanciones();
    }

    public List<Cancion> obtenerTopCanciones(int limite) {
        return cancionDAO.obtenerTopCanciones(limite);
    }

    public Cancion obtenerCancionPorId(String id) {
        return cancionDAO.buscarCancion(id);
    }

    public boolean actualizarCancion(String id, String nuevoTitulo, String nuevoArtista) {
        Cancion c = cancionDAO.buscarCancion(id);
        if (c != null) {
            c.setTitulo(nuevoTitulo);
            c.setArtista(nuevoArtista);
            return cancionDAO.guardarCancion(c);
        }
        return false;
    }

    public boolean eliminarCancion(String id) {
        return cancionDAO.eliminarCancion(id);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  PLAYLISTS
    // ════════════════════════════════════════════════════════════════════════

    public boolean crearPlaylist(Playlist playlist) {
        return playlistDAO.guardarPlaylist(playlist);
    }

    public List<Playlist> obtenerPlaylistsPublicas() {
        return playlistDAO.cargarPlaylistsPublicas();
    }

    public List<Playlist> obtenerPlaylistsUsuario(String username) {
        return playlistDAO.cargarPlaylistsDeUsuario(username);
    }

    public Playlist obtenerPlaylistPorId(String id) {
        return playlistDAO.buscarPlaylist(id);
    }

    public boolean actualizarPlaylist(Playlist playlist) {
        return playlistDAO.guardarPlaylist(playlist);
    }

    public boolean eliminarPlaylist(String id) {
        return playlistDAO.eliminarPlaylist(id);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  USUARIOS  ← NUEVO
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Devuelve todos los usuarios registrados en el sistema.
     * Usado por el dashboard del administrador para mostrar la tabla
     * de usuarios recientes.
     */
    public List<Usuario> obtenerTodosUsuarios() {
        return usuarioDAO.obtenerTodosUsuarios();
    }

    /**
     * Devuelve el total de usuarios registrados (para la tarjeta del dashboard).
     */
    public int contarUsuarios() {
        return usuarioDAO.contarUsuarios();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  ESTADÍSTICAS
    // ════════════════════════════════════════════════════════════════════════

    public void registrarReproduccionCancion(String cancionId, String username) {
        Estadistica est = new Estadistica(cancionId, username, 0);
        estadisticaDAO.registrarReproduccion(est);

        Cancion cancion = cancionDAO.buscarCancion(cancionId);
        if (cancion != null) {
            cancion.incrementarReproducciones();
            cancionDAO.guardarCancion(cancion);
        }
    }

    public void registrarReproduccionPlaylist(String playlistId, String username) {
        Estadistica est = new Estadistica(playlistId, username);
        estadisticaDAO.registrarReproduccion(est);

        Playlist playlist = playlistDAO.buscarPlaylist(playlistId);
        if (playlist != null) {
            playlist.incrementarReproducciones();
            playlistDAO.guardarPlaylist(playlist);
        }
    }

    public List<Estadistica> obtenerEstadisticasUsuario(String username) {
        return estadisticaDAO.obtenerEstadisticasUsuario(username);
    }

    public List<Estadistica> obtenerEstadisticasRecientes(int dias) {
        return estadisticaDAO.obtenerEstadisticasRecientes(dias);
    }
}