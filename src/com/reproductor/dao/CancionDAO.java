package com.reproductor.dao;

import com.reproductor.model.Cancion;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DAO para gestionar canciones
 */
public class CancionDAO {
    
    private static final String ARCHIVO = "datos/canciones.dat";
    
    /**
     * Guarda todas las canciones
     */
    public boolean guardarCanciones(List<Cancion> canciones) {
        ObjectOutputStream oos = null;
        try {
            File directorio = new File("datos");
            if (!directorio.exists()) {
                directorio.mkdir();
            }
            
            FileOutputStream fos = new FileOutputStream(ARCHIVO);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(canciones);
            
            System.out.println("✅ " + canciones.size() + " canciones guardadas");
            return true;
            
        } catch (IOException e) {
            System.err.println("❌ Error al guardar canciones: " + e.getMessage());
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
     * Carga todas las canciones
     */
    public List<Cancion> cargarCanciones() {
        ObjectInputStream ois = null;
        try {
            File archivo = new File(ARCHIVO);
            if (!archivo.exists()) {
                System.out.println("📄 Archivo de canciones no existe. Lista vacía.");
                return new ArrayList<>();
            }
            
            FileInputStream fis = new FileInputStream(ARCHIVO);
            ois = new ObjectInputStream(fis);
            
            @SuppressWarnings("unchecked")
            List<Cancion> canciones = (List<Cancion>) ois.readObject();
            
            System.out.println("✅ " + canciones.size() + " canciones cargadas");
            return canciones;
            
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Error al cargar canciones: " + e.getMessage());
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
     * Guarda una sola canción (agrega o actualiza)
     */
    public boolean guardarCancion(Cancion cancion) {
        List<Cancion> canciones = cargarCanciones();
        
        // Buscar si ya existe (por ID)
        for (int i = 0; i < canciones.size(); i++) {
            if (canciones.get(i).getId().equals(cancion.getId())) {
                canciones.set(i, cancion);
                System.out.println("🔄 Canción actualizada: " + cancion.getTitulo());
                return guardarCanciones(canciones);
            }
        }
        
        // Si no existe, agregar
        canciones.add(cancion);
        System.out.println("➕ Nueva canción: " + cancion.getTitulo());
        return guardarCanciones(canciones);
    }
    
    /**
     * Busca una canción por ID
     */
    public Cancion buscarCancion(String id) {
        List<Cancion> canciones = cargarCanciones();
        return canciones.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Busca canciones por título (búsqueda parcial)
     */
    public List<Cancion> buscarCancionesPorTitulo(String titulo) {
        List<Cancion> todas = cargarCanciones();
        String tituloBusqueda = titulo.toLowerCase();
        
        return todas.stream()
                .filter(c -> c.getTitulo().toLowerCase().contains(tituloBusqueda))
                .collect(Collectors.toList());
    }
    
    /**
     * Busca canciones por artista
     */
    public List<Cancion> buscarCancionesPorArtista(String artista) {
        List<Cancion> todas = cargarCanciones();
        String artistaBusqueda = artista.toLowerCase();
        
        return todas.stream()
                .filter(c -> c.getArtista().toLowerCase().contains(artistaBusqueda))
                .collect(Collectors.toList());
    }
    
    /**
     * Busca canciones por género
     */
    public List<Cancion> buscarCancionesPorGenero(String genero) {
        List<Cancion> todas = cargarCanciones();
        
        return todas.stream()
                .filter(c -> c.getGenero() != null && c.getGenero().equalsIgnoreCase(genero))
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene las canciones más reproducidas (Top)
     */
    public List<Cancion> obtenerTopCanciones(int limite) {
        List<Cancion> canciones = cargarCanciones();
        return canciones.stream()
                .sorted(Comparator.comparingInt(Cancion::getVecesReproducida).reversed())
                .limit(limite)
                .collect(Collectors.toList());
    }
    
    /**
     * Elimina una canción
     */
    public boolean eliminarCancion(String id) {
        List<Cancion> canciones = cargarCanciones();
        boolean eliminado = canciones.removeIf(c -> c.getId().equals(id));
        
        if (eliminado) {
            System.out.println("🗑️ Canción eliminada");
            return guardarCanciones(canciones);
        }
        
        return false;
    }
    
    /**
     * Obtiene todas las canciones
     */
    public List<Cancion> obtenerTodasCanciones() {
        return cargarCanciones();
    }
    
    /**
     * Cuenta total de canciones
     */
    public int contarCanciones() {
        return cargarCanciones().size();
    }
    
    /**
     * Obtiene estadísticas de reproducciones totales
     */
    public int contarReproduccionesTotales() {
        List<Cancion> canciones = cargarCanciones();
        return canciones.stream()
                .mapToInt(Cancion::getVecesReproducida)
                .sum();
    }
}