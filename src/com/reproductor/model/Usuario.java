package com.reproductor.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Usuario implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String username;
    private String password;
    private String nombre;
    private Date fechaRegistro;
    private boolean esAdministrador;
    private List<Playlist> playlistsPersonales;
    
    public Usuario() {
        this.playlistsPersonales = new ArrayList<>();
        this.fechaRegistro = new Date();
    }
    

    public Usuario(String username, String password, String nombre, boolean esAdministrador) {
        this.username = username;
        this.password = password;
        this.nombre = nombre;
        this.esAdministrador = esAdministrador;
        this.fechaRegistro = new Date();
        this.playlistsPersonales = new ArrayList<>();
    }
    
    public Usuario(String username, String password, String nombre) {
        this(username, password, nombre, false);
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public Date getFechaRegistro() {
        return fechaRegistro;
    }
    
    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
    
    public boolean isEsAdministrador() {
        return esAdministrador;
    }
    
    public void setEsAdministrador(boolean esAdministrador) {
        this.esAdministrador = esAdministrador;
    }
    
    public List<Playlist> getPlaylistsPersonales() {
        return playlistsPersonales;
    }
    
    public void setPlaylistsPersonales(List<Playlist> playlistsPersonales) {
        this.playlistsPersonales = playlistsPersonales;
    }
    
    public void agregarPlaylist(Playlist playlist) {
        this.playlistsPersonales.add(playlist);
    }
    
    public void eliminarPlaylist(Playlist playlist) {
        this.playlistsPersonales.remove(playlist);
    }
    
    @Override
    public String toString() {
        return "Usuario{" +
                "username='" + username + '\'' +
                ", nombre='" + nombre + '\'' +
                ", esAdmin=" + esAdministrador +
                ", playlists=" + playlistsPersonales.size() +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(username, usuario.username);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}