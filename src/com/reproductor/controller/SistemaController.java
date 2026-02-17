package com.reproductor.controller;

import com.reproductor.dao.CancionDAO;
import com.reproductor.dao.PlaylistDAO;
import com.reproductor.dao.EstadisticaDAO;
import com.reproductor.model.Cancion;
import com.reproductor.model.Playlist;
import com.reproductor.model.Estadistica;
import java.util.List;

public class SistemaController {
    
    private static SistemaController instancia;
    private CancionDAO cancionDAO;
    private PlaylistDAO playlistDAO;
    private EstadisticaDAO estadisticaDAO;
    
    private SistemaController() {
        this.cancionDAO = new CancionDAO();
        this.playlistDAO = new PlaylistDAO();
        this.estadisticaDAO = new EstadisticaDAO();
    }
    
    public static SistemaController getInstancia() {
        if (instancia == null) {
            instancia = new SistemaController();
        }
        return instancia;
    }
    
    // CANCIONES
    public boolean agregarCancion(Cancion cancion) {
        return cancionDAO.guardarCancion(cancion);
    }
    
    public List<Cancion> obtenerTodasCanciones() {
        return cancionDAO.cargarCanciones();
    }
    
    public List<Cancion> obtenerTopCanciones(int limite) {
        return cancionDAO.obtenerTopCanciones(limite);
    }
    
    public boolean eliminarCancion(String id) {
        return cancionDAO.eliminarCancion(id);
    }
    
    // PLAYLISTS
    public boolean crearPlaylist(Playlist playlist) {
        return playlistDAO.guardarPlaylist(playlist);
    }
    
    public List<Playlist> obtenerPlaylistsPublicas() {
        return playlistDAO.cargarPlaylistsPublicas();
    }
    
    public List<Playlist> obtenerPlaylistsUsuario(String username) {
        return playlistDAO.cargarPlaylistsDeUsuario(username);
    }
    
    public boolean actualizarPlaylist(Playlist playlist) {
        return playlistDAO.guardarPlaylist(playlist);
    }
    
    public boolean eliminarPlaylist(String id) {
        return playlistDAO.eliminarPlaylist(id);
    }
    
    public Playlist buscarPlaylist(String id) {
        return playlistDAO.buscarPlaylist(id);
    }
    
    // ESTADÍSTICAS
    public void registrarReproduccionCancion(String cancionId, String username) {
        Estadistica est = new Estadistica(cancionId, username, 0);
        estadisticaDAO.registrarReproduccion(est);
        
        // Incrementar contador en la canción
        Cancion cancion = cancionDAO.buscarCancion(cancionId);
        if (cancion != null) {
            cancion.incrementarReproducciones();
            cancionDAO.guardarCancion(cancion);
        }
    }
    
    public void registrarReproduccionPlaylist(String playlistId, String username) {
        Estadistica est = new Estadistica(playlistId, username);
        estadisticaDAO.registrarReproduccion(est);
        
        // Incrementar contador en la playlist
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