package com.reproductor.dao;

import com.reproductor.model.Cancion;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DAO para gestionar la persistencia de canciones.
 *
 * FIX #9: Se eliminó el método obtenerTodasCanciones() que era
 *         idéntico a cargarCanciones() y estaba duplicado, generando
 *         confusión sobre cuál usar. El método canónico es
 *         cargarCanciones(); quienes lo llamaban externamente ya
 *         pasan por SistemaController.obtenerTodasCanciones() que
 *         internamente llama a cargarCanciones().
 */
public class CancionDAO {

    private static final String ARCHIVO = "datos/canciones.dat";

    // ── GUARDAR LISTA COMPLETA ─────────────────────────────────────────────────

    public boolean guardarCanciones(List<Cancion> canciones) {
        ObjectOutputStream oos = null;
        try {
            File directorio = new File("datos");
            if (!directorio.exists()) {
                directorio.mkdir();
            }

            oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO));
            oos.writeObject(canciones);

            System.out.println("✅ " + canciones.size() + " canciones guardadas");
            return true;

        } catch (IOException e) {
            System.err.println("❌ Error al guardar canciones: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            cerrarStream(oos);
        }
    }

    // ── CARGAR LISTA COMPLETA ──────────────────────────────────────────────────

    /**
     * Carga todas las canciones desde el archivo de persistencia.
     * Es el único método de lectura masiva; elimina la duplicación
     * que antes existía con obtenerTodasCanciones().
     */
    public List<Cancion> cargarCanciones() {
        ObjectInputStream ois = null;
        try {
            File archivo = new File(ARCHIVO);
            if (!archivo.exists()) {
                System.out.println("📄 Archivo de canciones no existe. Lista vacía.");
                return new ArrayList<>();
            }

            ois = new ObjectInputStream(new FileInputStream(ARCHIVO));

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
            cerrarStream(ois);
        }
    }

    // ── GUARDAR UNA CANCIÓN (UPSERT) ───────────────────────────────────────────

    /**
     * Agrega una canción nueva o actualiza una existente (por ID).
     */
    public boolean guardarCancion(Cancion cancion) {
        List<Cancion> canciones = cargarCanciones();

        for (int i = 0; i < canciones.size(); i++) {
            if (canciones.get(i).getId().equals(cancion.getId())) {
                canciones.set(i, cancion);
                System.out.println("🔄 Canción actualizada: " + cancion.getTitulo());
                return guardarCanciones(canciones);
            }
        }

        canciones.add(cancion);
        System.out.println("➕ Nueva canción: " + cancion.getTitulo());
        return guardarCanciones(canciones);
    }

    // ── BÚSQUEDAS ──────────────────────────────────────────────────────────────

    public Cancion buscarCancion(String id) {
        return cargarCanciones().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Cancion> buscarCancionesPorTitulo(String titulo) {
        String busqueda = titulo.toLowerCase();
        return cargarCanciones().stream()
                .filter(c -> c.getTitulo().toLowerCase().contains(busqueda))
                .collect(Collectors.toList());
    }

    public List<Cancion> buscarCancionesPorArtista(String artista) {
        String busqueda = artista.toLowerCase();
        return cargarCanciones().stream()
                .filter(c -> c.getArtista().toLowerCase().contains(busqueda))
                .collect(Collectors.toList());
    }

    public List<Cancion> buscarCancionesPorGenero(String genero) {
        return cargarCanciones().stream()
                .filter(c -> c.getGenero() != null && c.getGenero().equalsIgnoreCase(genero))
                .collect(Collectors.toList());
    }

    // ── TOP CANCIONES ──────────────────────────────────────────────────────────

    public List<Cancion> obtenerTopCanciones(int limite) {
        return cargarCanciones().stream()
                .sorted(Comparator.comparingInt(Cancion::getVecesReproducida).reversed())
                .limit(limite)
                .collect(Collectors.toList());
    }

    // ── ELIMINAR ───────────────────────────────────────────────────────────────

    public boolean eliminarCancion(String id) {
        List<Cancion> canciones = cargarCanciones();
        boolean eliminado = canciones.removeIf(c -> c.getId().equals(id));

        if (eliminado) {
            System.out.println("🗑️ Canción eliminada: " + id);
            return guardarCanciones(canciones);
        }
        return false;
    }

    // ── ESTADÍSTICAS ───────────────────────────────────────────────────────────

    public int contarCanciones() {
        return cargarCanciones().size();
    }

    public int contarReproduccionesTotales() {
        return cargarCanciones().stream()
                .mapToInt(Cancion::getVecesReproducida)
                .sum();
    }

    // ── UTILIDAD INTERNA ───────────────────────────────────────────────────────

    private void cerrarStream(Closeable stream) {
        if (stream != null) {
            try { stream.close(); } catch (IOException ignored) {}
        }
    }
}