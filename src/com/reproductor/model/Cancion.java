package com.reproductor.model;

import java.io.Serializable;
import java.util.Objects;


public class Cancion implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String titulo;
    private String artista;
    private String album;
    private int duracionSegundos;
    private String rutaArchivo;
    private String genero;
    private int vecesReproducida;
    

    public Cancion() {
        this.vecesReproducida = 0;
    }
    

    public Cancion(String id, String titulo, String artista, String album, 
                   int duracionSegundos, String rutaArchivo, String genero) {
        this.id = id;
        this.titulo = titulo;
        this.artista = artista;
        this.album = album;
        this.duracionSegundos = duracionSegundos;
        this.rutaArchivo = rutaArchivo;
        this.genero = genero;
        this.vecesReproducida = 0;
    }
    
    // Constructor simple
    public Cancion(String titulo, String artista, String rutaArchivo) {
        this.id = generarId();
        this.titulo = titulo;
        this.artista = artista;
        this.rutaArchivo = rutaArchivo;
        this.vecesReproducida = 0;
    }
    

    private String generarId() {
        return "SONG_" + System.currentTimeMillis();
    }
    

    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public String getArtista() {
        return artista;
    }
    
    public void setArtista(String artista) {
        this.artista = artista;
    }
    
    public String getAlbum() {
        return album;
    }
    
    public void setAlbum(String album) {
        this.album = album;
    }
    
    public int getDuracionSegundos() {
        return duracionSegundos;
    }
    
    public void setDuracionSegundos(int duracionSegundos) {
        this.duracionSegundos = duracionSegundos;
    }
    
    public String getRutaArchivo() {
        return rutaArchivo;
    }
    
    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }
    
    public String getGenero() {
        return genero;
    }
    
    public void setGenero(String genero) {
        this.genero = genero;
    }
    
    public int getVecesReproducida() {
        return vecesReproducida;
    }
    
    public void setVecesReproducida(int vecesReproducida) {
        this.vecesReproducida = vecesReproducida;
    }
    

    public void incrementarReproducciones() {
        this.vecesReproducida++;
    }
    
    public String getDuracionFormateada() {
        int minutos = duracionSegundos / 60;
        int segundos = duracionSegundos % 60;
        return String.format("%d:%02d", minutos, segundos);
    }
    
    @Override
    public String toString() {
        return titulo + " - " + artista;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cancion cancion = (Cancion) o;
        return Objects.equals(id, cancion.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}