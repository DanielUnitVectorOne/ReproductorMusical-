package com.reproductor.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Playlist implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String nombre;
    private String descripcion;
    private List<Cancion> canciones;
    private Date fechaCreacion;
    private boolean esPublica; 
    private String creadorUsername; 
    private int vecesReproducida;
    

    public Playlist() {
        this.canciones = new ArrayList<>();
        this.fechaCreacion = new Date();
        this.vecesReproducida = 0;
    }
    

    public Playlist(String nombre, String descripcion, boolean esPublica, String creadorUsername) {
        this.id = generarId();
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.canciones = new ArrayList<>();
        this.fechaCreacion = new Date();
        this.esPublica = esPublica;
        this.creadorUsername = creadorUsername;
        this.vecesReproducida = 0;
    }
    

    public Playlist(String nombre, String creadorUsername) {
        this(nombre, "", false, creadorUsername);
    }
    
    private String generarId() {
        return "PL_" + System.currentTimeMillis();
    }
    

    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public List<Cancion> getCanciones() {
        return canciones;
    }
    
    public void setCanciones(List<Cancion> canciones) {
        this.canciones = canciones;
    }
    
    public Date getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public boolean isEsPublica() {
        return esPublica;
    }
    
    public void setEsPublica(boolean esPublica) {
        this.esPublica = esPublica;
    }
    
    public String getCreadorUsername() {
        return creadorUsername;
    }
    
    public void setCreadorUsername(String creadorUsername) {
        this.creadorUsername = creadorUsername;
    }
    
    public int getVecesReproducida() {
        return vecesReproducida;
    }
    
    public void setVecesReproducida(int vecesReproducida) {
        this.vecesReproducida = vecesReproducida;
    }
    

    public void agregarCancion(Cancion cancion) {
        this.canciones.add(cancion);
    }
    
    public void eliminarCancion(Cancion cancion) {
        this.canciones.remove(cancion);
    }
    
    public int getCantidadCanciones() {
        return canciones.size();
    }
    
    public int getDuracionTotal() {
        return canciones.stream()
                .mapToInt(Cancion::getDuracionSegundos)
                .sum();
    }
    
    public void incrementarReproducciones() {
        this.vecesReproducida++;
    }
    
    @Override
    public String toString() {
        return nombre + " (" + canciones.size() + " canciones)";
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Playlist playlist = (Playlist) o;
        return Objects.equals(id, playlist.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}