package com.reproductor.controller;

import com.reproductor.model.Cancion;
import com.reproductor.model.Playlist;
import java.util.ArrayList;
import java.util.List;

public class ReproductorController {
    
    private static ReproductorController instancia;
    private Cancion cancionActual;
    private Playlist playlistActual;
    private List<Cancion> colaReproduccion;
    private int indiceActual;
    private boolean reproduciendo;
    private boolean repetir;
    private boolean aleatorio;
    
    private ReproductorController() {
        this.colaReproduccion = new ArrayList<>();
        this.indiceActual = 0;
        this.reproduciendo = false;
        this.repetir = false;
        this.aleatorio = false;
    }
    
    public static ReproductorController getInstancia() {
        if (instancia == null) {
            instancia = new ReproductorController();
        }
        return instancia;
    }
    
    public void reproducirCancion(Cancion cancion) {
        this.cancionActual = cancion;
        this.reproduciendo = true;
        
        // Registrar reproducción
        String username = AuthController.getInstancia().getUsuarioActual().getUsername();
        SistemaController.getInstancia().registrarReproduccionCancion(cancion.getId(), username);
    }
    
    public void reproducirPlaylist(Playlist playlist) {
        this.playlistActual = playlist;
        this.colaReproduccion = new ArrayList<>(playlist.getCanciones());
        this.indiceActual = 0;
        
        if (!colaReproduccion.isEmpty()) {
            reproducirCancion(colaReproduccion.get(0));
        }
        
        // Registrar reproducción de playlist
        String username = AuthController.getInstancia().getUsuarioActual().getUsername();
        SistemaController.getInstancia().registrarReproduccionPlaylist(playlist.getId(), username);
    }
    
    public void siguiente() {
        if (colaReproduccion.isEmpty()) return;
        
        if (aleatorio) {
            indiceActual = (int) (Math.random() * colaReproduccion.size());
        } else {
            indiceActual++;
            if (indiceActual >= colaReproduccion.size()) {
                if (repetir) {
                    indiceActual = 0;
                } else {
                    pausar();
                    return;
                }
            }
        }
        
        reproducirCancion(colaReproduccion.get(indiceActual));
    }
    
    public void anterior() {
        if (colaReproduccion.isEmpty()) return;
        
        indiceActual--;
        if (indiceActual < 0) {
            indiceActual = repetir ? colaReproduccion.size() - 1 : 0;
        }
        
        reproducirCancion(colaReproduccion.get(indiceActual));
    }
    
    public void pausar() {
        this.reproduciendo = false;
    }
    
    public void reanudar() {
        this.reproduciendo = true;
    }
    
    public void toggleRepetir() {
        this.repetir = !this.repetir;
    }
    
    public void toggleAleatorio() {
        this.aleatorio = !this.aleatorio;
    }
    
    // Getters
    public Cancion getCancionActual() {
        return cancionActual;
    }
    
    public boolean isReproduciendo() {
        return reproduciendo;
    }
    
    public boolean isRepetir() {
        return repetir;
    }
    
    public boolean isAleatorio() {
        return aleatorio;
    }
    
    public List<Cancion> getColaReproduccion() {
        return colaReproduccion;
    }
}