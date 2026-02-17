package com.reproductor.dao;

import com.reproductor.model.Estadistica;
import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DAO para estadísticas de reproducción
 */
public class EstadisticaDAO {
    
    private static final String ARCHIVO = "datos/estadisticas.dat";
    
    /**
     * Guarda todas las estadísticas
     */
    public boolean guardarEstadisticas(List<Estadistica> estadisticas) {
        ObjectOutputStream oos = null;
        try {
            File directorio = new File("datos");
            if (!directorio.exists()) {
                directorio.mkdir();
            }
            
            FileOutputStream fos = new FileOutputStream(ARCHIVO);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(estadisticas);
            
            System.out.println("✅ " + estadisticas.size() + " estadísticas guardadas");
            return true;
            
        } catch (IOException e) {
            System.err.println("❌ Error al guardar estadísticas: " + e.getMessage());
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
     * Carga todas las estadísticas
     */
    public List<Estadistica> cargarEstadisticas() {
        ObjectInputStream ois = null;
        try {
            File archivo = new File(ARCHIVO);
            if (!archivo.exists()) {
                System.out.println("📄 Archivo de estadísticas no existe. Lista vacía.");
                return new ArrayList<>();
            }
            
            FileInputStream fis = new FileInputStream(ARCHIVO);
            ois = new ObjectInputStream(fis);
            
            @SuppressWarnings("unchecked")
            List<Estadistica> estadisticas = (List<Estadistica>) ois.readObject();
            
            System.out.println("✅ " + estadisticas.size() + " estadísticas cargadas");
            return estadisticas;
            
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Error al cargar estadísticas: " + e.getMessage());
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
     * Registra una nueva reproducción
     */
    public boolean registrarReproduccion(Estadistica estadistica) {
        List<Estadistica> estadisticas = cargarEstadisticas();
        estadisticas.add(estadistica);
        return guardarEstadisticas(estadisticas);
    }
    
    /**
     * Obtiene estadísticas de un usuario específico
     */
    public List<Estadistica> obtenerEstadisticasUsuario(String username) {
        List<Estadistica> todas = cargarEstadisticas();
        return todas.stream()
                .filter(e -> e.getUsername().equals(username))
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene estadísticas de los últimos N días
     */
    public List<Estadistica> obtenerEstadisticasRecientes(int dias) {
        List<Estadistica> todas = cargarEstadisticas();
        Date fechaLimite = new Date(System.currentTimeMillis() - (dias * 24L * 60 * 60 * 1000));
        
        return todas.stream()
                .filter(e -> e.getFechaReproduccion().after(fechaLimite))
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene estadísticas de hoy
     */
    public List<Estadistica> obtenerEstadisticasHoy() {
        List<Estadistica> todas = cargarEstadisticas();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date inicioHoy = cal.getTime();
        
        return todas.stream()
                .filter(e -> e.getFechaReproduccion().after(inicioHoy))
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene estadísticas de esta semana
     */
    public List<Estadistica> obtenerEstadisticasSemana() {
        return obtenerEstadisticasRecientes(7);
    }
    
    /**
     * Obtiene estadísticas de este mes
     */
    public List<Estadistica> obtenerEstadisticasMes() {
        return obtenerEstadisticasRecientes(30);
    }
    
    /**
     * Cuenta reproducciones de una canción específica
     */
    public int contarReproduccionesCancion(String cancionId) {
        List<Estadistica> todas = cargarEstadisticas();
        return (int) todas.stream()
                .filter(e -> e.getCancionId() != null && e.getCancionId().equals(cancionId))
                .count();
    }
    
    /**
     * Cuenta reproducciones de una playlist específica
     */
    public int contarReproduccionesPlaylist(String playlistId) {
        List<Estadistica> todas = cargarEstadisticas();
        return (int) todas.stream()
                .filter(e -> e.getPlaylistId() != null && e.getPlaylistId().equals(playlistId))
                .count();
    }
    
    /**
     * Obtiene total de reproducciones del sistema
     */
    public int contarReproduccionesTotales() {
        return cargarEstadisticas().size();
    }
    
    /**
     * Limpia estadísticas antiguas (más de X días)
     */
    public boolean limpiarEstadisticasAntiguas(int diasAntiguos) {
        List<Estadistica> todas = cargarEstadisticas();
        Date fechaLimite = new Date(System.currentTimeMillis() - (diasAntiguos * 24L * 60 * 60 * 1000));
        
        List<Estadistica> filtradas = todas.stream()
                .filter(e -> e.getFechaReproduccion().after(fechaLimite))
                .collect(Collectors.toList());
        
        int eliminadas = todas.size() - filtradas.size();
        System.out.println("🗑️ " + eliminadas + " estadísticas antiguas eliminadas");
        
        return guardarEstadisticas(filtradas);
    }
}