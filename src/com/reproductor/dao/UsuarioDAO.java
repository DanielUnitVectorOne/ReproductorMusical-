package com.reproductor.dao;

import com.reproductor.model.Usuario;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gestionar la persistencia de usuarios
 * Guarda y carga usuarios desde archivo usando serialización
 */
public class UsuarioDAO {
    
    private static final String ARCHIVO = "datos/usuarios.dat";
    
    /**
     * Guarda la lista completa de usuarios en el archivo
     */
    public boolean guardarUsuarios(List<Usuario> usuarios) {
        ObjectOutputStream oos = null;
        try {
            // Crear carpeta "datos" si no existe
            File directorio = new File("datos");
            if (!directorio.exists()) {
                directorio.mkdir();
                System.out.println("📁 Carpeta 'datos' creada");
            }
            
            FileOutputStream fos = new FileOutputStream(ARCHIVO);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(usuarios);
            
            System.out.println("✅ " + usuarios.size() + " usuarios guardados en " + ARCHIVO);
            return true;
            
        } catch (IOException e) {
            System.err.println("❌ Error al guardar usuarios: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Carga la lista de usuarios desde el archivo
     */
    public List<Usuario> cargarUsuarios() {
        ObjectInputStream ois = null;
        try {
            File archivo = new File(ARCHIVO);
            
            if (!archivo.exists()) {
                System.out.println("📄 Archivo " + ARCHIVO + " no existe. Retornando lista vacía.");
                return new ArrayList<>();
            }
            
            FileInputStream fis = new FileInputStream(ARCHIVO);
            ois = new ObjectInputStream(fis);
            
            @SuppressWarnings("unchecked")
            List<Usuario> usuarios = (List<Usuario>) ois.readObject();
            
            System.out.println("✅ " + usuarios.size() + " usuarios cargados desde " + ARCHIVO);
            return usuarios;
            
        } catch (FileNotFoundException e) {
            System.out.println("📄 Archivo no encontrado. Retornando lista vacía.");
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Error al cargar usuarios: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Guarda un solo usuario (agrega o actualiza en la lista existente)
     */
    public boolean guardarUsuario(Usuario usuario) {
        List<Usuario> usuarios = cargarUsuarios();
        
        // Verificar si el usuario ya existe (por username)
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getUsername().equals(usuario.getUsername())) {
                usuarios.set(i, usuario); // Actualizar
                System.out.println("🔄 Usuario actualizado: " + usuario.getUsername());
                return guardarUsuarios(usuarios);
            }
        }
        
        // Si no existe, agregar
        usuarios.add(usuario);
        System.out.println("➕ Nuevo usuario agregado: " + usuario.getUsername());
        return guardarUsuarios(usuarios);
    }
    
    /**
     * Busca un usuario por username
     */
    public Usuario buscarUsuario(String username) {
        List<Usuario> usuarios = cargarUsuarios();
        
        for (Usuario u : usuarios) {
            if (u.getUsername().equals(username)) {
                return u;
            }
        }
        
        return null; // No encontrado
    }
    
    /**
     * Valida credenciales de login
     */
    public Usuario validarCredenciales(String username, String password) {
        Usuario usuario = buscarUsuario(username);
        
        if (usuario != null && usuario.getPassword().equals(password)) {
            return usuario;
        }
        
        return null;
    }
    
    /**
     * Elimina un usuario
     */
    public boolean eliminarUsuario(String username) {
        List<Usuario> usuarios = cargarUsuarios();
        
        boolean eliminado = usuarios.removeIf(u -> u.getUsername().equals(username));
        
        if (eliminado) {
            System.out.println("🗑️ Usuario eliminado: " + username);
            return guardarUsuarios(usuarios);
        }
        
        System.out.println("⚠️ Usuario no encontrado: " + username);
        return false;
    }
    
    /**
     * Obtiene todos los usuarios
     */
    public List<Usuario> obtenerTodosUsuarios() {
        return cargarUsuarios();
    }
    
    /**
     * Cuenta total de usuarios registrados
     */
    public int contarUsuarios() {
        return cargarUsuarios().size();
    }
    
    /**
     * Verifica si existe un usuario con ese username
     */
    public boolean existeUsuario(String username) {
        return buscarUsuario(username) != null;
    }
}