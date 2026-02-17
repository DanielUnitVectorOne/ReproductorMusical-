package com.reproductor.view;

import com.reproductor.controller.AuthController;
import com.reproductor.controller.SistemaController;
import com.reproductor.model.Playlist;
import com.reproductor.model.Cancion;
import com.reproductor.utils.*;
import java.awt.*;
import javax.swing.*;

public class CrearPlaylist extends JInternalFrame {
    
    private boolean esAdmin;
    private CampoTextoModerno txtNombre;
    private JTextArea txtDescripcion;
    private DefaultListModel<Cancion> modeloCanciones;
    
    public CrearPlaylist(boolean esAdmin) {
        super("Crear Playlist", false, true, false, false);
        this.esAdmin = esAdmin;
        setSize(700, 600);
        setLocation(100, 50);
        inicializarComponentes();
    }
    
    private void inicializarComponentes() {
        JPanel panel = new JPanel(null);
        panel.setBackground(Constantes.COLOR_FONDO_MEDIO);
        
        JLabel lblTitulo = new JLabel("Nueva Playlist");
        lblTitulo.setFont(Constantes.FUENTE_TITULO);
        lblTitulo.setForeground(Constantes.COLOR_TEXTO);
        lblTitulo.setBounds(50, 20, 300, 40);
        panel.add(lblTitulo);
        
        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setForeground(Constantes.COLOR_TEXTO);
        lblNombre.setBounds(50, 80, 100, 25);
        panel.add(lblNombre);
        
        txtNombre = new CampoTextoModerno("Nombre de la playlist");
        txtNombre.setBounds(50, 110, 600, 40);
        panel.add(txtNombre);
        
        JLabel lblDesc = new JLabel("Descripción:");
        lblDesc.setForeground(Constantes.COLOR_TEXTO);
        lblDesc.setBounds(50, 160, 100, 25);
        panel.add(lblDesc);
        
        txtDescripcion = new JTextArea();
        txtDescripcion.setBackground(Constantes.COLOR_FONDO_CLARO);
        txtDescripcion.setForeground(Constantes.COLOR_TEXTO);
        txtDescripcion.setFont(Constantes.FUENTE_NORMAL);
        txtDescripcion.setLineWrap(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        scrollDesc.setBounds(50, 190, 600, 80);
        panel.add(scrollDesc);
        
        BotonRedondeado btnCrear = new BotonRedondeado("Crear Playlist");
        btnCrear.setBounds(250, 500, 200, 50);
        btnCrear.addActionListener(e -> crearPlaylist());
        panel.add(btnCrear);
        
        add(panel);
    }
    
    private void crearPlaylist() {
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un nombre");
            return;
        }
        
        String username = AuthController.getInstancia().getUsuarioActual().getUsername();
        Playlist playlist = new Playlist(nombre, descripcion, esAdmin, username);
        
        if (SistemaController.getInstancia().crearPlaylist(playlist)) {
            JOptionPane.showMessageDialog(this, "Playlist creada exitosamente");
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al crear playlist");
        }
    }
}