package com.reproductor.dao;

import com.reproductor.model.Playlist;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DAO para gestionar playlists
 */
public class PlaylistDAO {
    
    private static final String ARCHIVO = "datos/playlists.dat";
    
    /**
     * Guarda todas las playlists
     */
    public boolean guardarPlaylists(List<Playlist> playlists) {
        ObjectOutputStream oos = null;
        try {
            File directorio = new File("datos");
            if (!directorio.exists()) {
                directorio.mkdir();
            }
            
            FileOutputStream fos = new FileOutputStream(ARCHIVO);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(playlists);
            
            System.out.println("✅ " + playlists.size() + " playlists guardadas");
            return true;
            
        } catch (IOException e) {
            System.err.println("❌ Error al guardar playlists: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (oos != null) oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Carga todas las playlists
     */
    public List<Playlist> cargarPlaylists() {
        ObjectInputStream ois = null;
        try {
            File archivo = new File(ARCHIVO);
            if (!archivo.exists()) {
                System.out.println("📄 Archivo de playlists no existe. Lista vacía.");
                return new ArrayList<>();
            }
            
            FileInputStream fis = new FileInputStream(ARCHIVO);
            ois = new ObjectInputStream(fis);
            
            @SuppressWarnings("unchecked")
            List<Playlist> playlists = (List<Playlist>) ois.readObject();
            
            System.out.println("✅ " + playlists.size() + " playlists cargadas");
            return playlists;
            
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Error al cargar playlists: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            try {
                if (ois != null) ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Guarda una sola playlist (agrega o actualiza)
     */
    public boolean guardarPlaylist(Playlist playlist) {
        List<Playlist> playlists = cargarPlaylists();
        
        // Buscar si ya existe (por ID)
        for (int i = 0; i < playlists.size(); i++) {
            if (playlists.get(i).getId().equals(playlist.getId())) {
                playlists.set(i, playlist);
                System.out.println("🔄 Playlist actualizada: " + playlist.getNombre());
                return guardarPlaylists(playlists);
            }
        }
        
        // Si no existe, agregar
        playlists.add(playlist);
        System.out.println("➕ Nueva playlist: " + playlist.getNombre());
        return guardarPlaylists(playlists);
    }
    
    /**
     * Obtiene solo las playlists públicas (creadas por admin)
     */
    public List<Playlist> cargarPlaylistsPublicas() {
        List<Playlist> todas = cargarPlaylists();
        return todas.stream()
                .filter(Playlist::isEsPublica)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene las playlists de un usuario específico
     */
    public List<Playlist> cargarPlaylistsDeUsuario(String username) {
        List<Playlist> todas = cargarPlaylists();
        return todas.stream()
                .filter(p -> p.getCreadorUsername().equals(username))
                .collect(Collectors.toList());
    }
    
    /**
     * Busca una playlist por ID
     */
    public Playlist buscarPlaylist(String id) {
        List<Playlist> playlists = cargarPlaylists();
        return playlists.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Busca playlists por nombre (búsqueda parcial)
     */
    public List<Playlist> buscarPlaylistsPorNombre(String nombre) {
        List<Playlist> todas = cargarPlaylists();
        String nombreBusqueda = nombre.toLowerCase();
        
        return todas.stream()
                .filter(p -> p.getNombre().toLowerCase().contains(nombreBusqueda))
                .collect(Collectors.toList());
    }
    
    /**
     * Elimina una playlist
     */
    public boolean eliminarPlaylist(String id) {
        List<Playlist> playlists = cargarPlaylists();
        boolean eliminado = playlists.removeIf(p -> p.getId().equals(id));
        
        if (eliminado) {
            System.out.println("🗑️ Playlist eliminada");
            return guardarPlaylists(playlists);
        }
        
        return false;
    }
    
    /**
     * Obtiene las playlists más reproducidas
     */
    public List<Playlist> obtenerTopPlaylists(int limite) {
        List<Playlist> todas = cargarPlaylists();
        return todas.stream()
                .sorted((p1, p2) -> Integer.compare(p2.getVecesReproducida(), p1.getVecesReproducida()))
                .limit(limite)
                .collect(Collectors.toList());
    }
    
    /**
     * Cuenta total de playlists
     */
    public int contarPlaylists() {
        return cargarPlaylists().size();
    }
    
    /**
     * Cuenta playlists públicas
     */
    public int contarPlaylistsPublicas() {
        return cargarPlaylistsPublicas().size();
    }
    
    /**
     * Cuenta playlists de un usuario
     */
    public int contarPlaylistsUsuario(String username) {
        return cargarPlaylistsDeUsuario(username).size();
    }
}