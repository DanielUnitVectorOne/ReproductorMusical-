package com.reproductor.model;

import java.io.Serializable;
import java.util.Date;


public class Estadistica implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String cancionId;
    private String playlistId;
    private String username;
    private Date fechaReproduccion;
    private int duracionReproduccion; 
    

    public Estadistica() {
        this.fechaReproduccion = new Date();
    }
    

    public Estadistica(String cancionId, String username, int duracionReproduccion) {
        this.cancionId = cancionId;
        this.username = username;
        this.duracionReproduccion = duracionReproduccion;
        this.fechaReproduccion = new Date();
    }
    

    public Estadistica(String playlistId, String username) {
        this.playlistId = playlistId;
        this.username = username;
        this.fechaReproduccion = new Date();
    }
    

    public String getCancionId() {
        return cancionId;
    }
    
    public void setCancionId(String cancionId) {
        this.cancionId = cancionId;
    }
    
    public String getPlaylistId() {
        return playlistId;
    }
    
    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public Date getFechaReproduccion() {
        return fechaReproduccion;
    }
    
    public void setFechaReproduccion(Date fechaReproduccion) {
        this.fechaReproduccion = fechaReproduccion;
    }
    
    public int getDuracionReproduccion() {
        return duracionReproduccion;
    }
    
    public void setDuracionReproduccion(int duracionReproduccion) {
        this.duracionReproduccion = duracionReproduccion;
    }
    
    @Override
    public String toString() {
        return "Estadistica{" +
                "cancion='" + cancionId + '\'' +
                ", usuario='" + username + '\'' +
                ", fecha=" + fechaReproduccion +
                '}';
    }
}