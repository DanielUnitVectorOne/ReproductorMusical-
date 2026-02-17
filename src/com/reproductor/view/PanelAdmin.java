package com.reproductor.view;

import com.reproductor.controller.AuthController;
import com.reproductor.controller.SistemaController;
import com.reproductor.model.Cancion;
import com.reproductor.model.Playlist;
import com.reproductor.utils.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class PanelAdmin extends JFrame {
    
    private LoginAdmin loginAdmin;
    private JDesktopPane desktopPane;
    private DefaultTableModel modeloPlaylists;
    private JTable tablaPlaylists;
    
    public PanelAdmin(LoginAdmin loginAdmin) {
        this.loginAdmin = loginAdmin;
        configurarVentana();
        inicializarComponentes();
    }
    
    private void configurarVentana() {
        setTitle("Panel Administrador");
        setSize(Constantes.ANCHO_VENTANA, Constantes.ALTO_VENTANA);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }
    
    private void inicializarComponentes() {
        // Barra de título
        BarraTituloPersonalizado barraTitulo = new BarraTituloPersonalizado(this, "Panel Administrador");
        add(barraTitulo, BorderLayout.NORTH);
        
        // Panel lateral (menú)
        JPanel panelMenu = crearPanelMenu();
        add(panelMenu, BorderLayout.WEST);
        
        // Área de trabajo (MDI)
        desktopPane = new JDesktopPane();
        desktopPane.setBackground(Constantes.COLOR_FONDO_MEDIO);
        add(desktopPane, BorderLayout.CENTER);
        
        // Mostrar panel de bienvenida por defecto
        mostrarPanelBienvenida();
    }
    
    private JPanel crearPanelMenu() {
        PanelDegradado panelMenu = new PanelDegradado(
            new Color(25, 25, 25),
            Constantes.COLOR_FONDO_OSCURO,
            true
        );
        panelMenu.setPreferredSize(new Dimension(250, Constantes.ALTO_VENTANA));
        panelMenu.setLayout(null);
        
        // Título del menú
        JLabel lblTitulo = new JLabel("ADMINISTRADOR");
        lblTitulo.setFont(Constantes.FUENTE_SUBTITULO);
        lblTitulo.setForeground(Constantes.COLOR_PRIMARIO);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBounds(0, 20, 250, 30);
        panelMenu.add(lblTitulo);
        
        int y = 80;
        int altura = 50;
        int espacio = 10;
        
        // Botón crear playlist
        BotonRedondeado btnCrearPlaylist = new BotonRedondeado("📋 Crear Playlist");
        btnCrearPlaylist.setBounds(25, y, 200, altura);
        btnCrearPlaylist.addActionListener(e -> abrirCrearPlaylist());
        panelMenu.add(btnCrearPlaylist);
        y += altura + espacio;
        
        // Botón ver playlists
        BotonRedondeado btnVerPlaylists = new BotonRedondeado("📚 Gestionar Playlists");
        btnVerPlaylists.setBounds(25, y, 200, altura);
        btnVerPlaylists.addActionListener(e -> mostrarGestionPlaylists());
        panelMenu.add(btnVerPlaylists);
        y += altura + espacio;
        
        // Botón reportes
        BotonRedondeado btnReportes = new BotonRedondeado("📊 Reportes");
        btnReportes.setBounds(25, y, 200, altura);
        btnReportes.addActionListener(e -> abrirReportes());
        panelMenu.add(btnReportes);
        y += altura + espacio;
        
        // Botón cerrar sesión (abajo)
        BotonRedondeado btnCerrarSesion = new BotonRedondeado("🚪 Cerrar Sesión", Constantes.COLOR_ERROR);
        btnCerrarSesion.setBounds(25, Constantes.ALTO_VENTANA - 100, 200, altura);
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
        panelMenu.add(btnCerrarSesion);
        
        return panelMenu;
    }
    
    private void mostrarPanelBienvenida() {
        JInternalFrame frame = new JInternalFrame("Bienvenido", false, true, false, false);
        frame.setSize(700, 500);
        frame.setLocation(50, 50);
        
        PanelDegradado panel = new PanelDegradado();
        panel.setLayout(null);
        
        JLabel lblBienvenida = new JLabel("¡Bienvenido, Administrador!");
        lblBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblBienvenida.setForeground(Constantes.COLOR_TEXTO);
        lblBienvenida.setHorizontalAlignment(SwingConstants.CENTER);
        lblBienvenida.setBounds(0, 150, 700, 50);
        panel.add(lblBienvenida);
        
        JLabel lblInfo = new JLabel("Gestiona playlists y visualiza estadísticas del sistema");
        lblInfo.setFont(Constantes.FUENTE_NORMAL);
        lblInfo.setForeground(Constantes.COLOR_TEXTO_SECUNDARIO);
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
        lblInfo.setBounds(0, 210, 700, 25);
        panel.add(lblInfo);
        
        frame.add(panel);
        frame.setVisible(true);
        desktopPane.add(frame);
    }
    
    private void abrirCrearPlaylist() {
        // Limpiar desktop
        desktopPane.removeAll();
        desktopPane.repaint();
        
        CrearPlaylist ventana = new CrearPlaylist(true); // true = admin
        desktopPane.add(ventana);
        ventana.setVisible(true);
    }
    
    private void mostrarGestionPlaylists() {
        desktopPane.removeAll();
        desktopPane.repaint();
        
        JInternalFrame frame = new JInternalFrame("Gestionar Playlists", false, true, false, false);
        frame.setSize(900, 600);
        frame.setLocation(20, 20);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Constantes.COLOR_FONDO_MEDIO);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Tabla de playlists
        String[] columnas = {"ID", "Nombre", "Canciones", "Reproducciones", "Pública"};
        modeloPlaylists = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaPlaylists = new JTable(modeloPlaylists);
        tablaPlaylists.setBackground(Constantes.COLOR_FONDO_CLARO);
        tablaPlaylists.setForeground(Constantes.COLOR_TEXTO);
        tablaPlaylists.setSelectionBackground(Constantes.COLOR_PRIMARIO);
        tablaPlaylists.setRowHeight(30);
        
        JScrollPane scroll = new JScrollPane(tablaPlaylists);
        scroll.getViewport().setBackground(Constantes.COLOR_FONDO_CLARO);
        panel.add(scroll, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setBackground(Constantes.COLOR_FONDO_MEDIO);
        
        BotonRedondeado btnActualizar = new BotonRedondeado("🔄 Actualizar");
        btnActualizar.addActionListener(e -> cargarPlaylists());
        panelBotones.add(btnActualizar);
        
        BotonRedondeado btnEliminar = new BotonRedondeado("🗑️ Eliminar", Constantes.COLOR_ERROR);
        btnEliminar.addActionListener(e -> eliminarPlaylistSeleccionada());
        panelBotones.add(btnEliminar);
        
        panel.add(panelBotones, BorderLayout.SOUTH);
        
        frame.add(panel);
        frame.setVisible(true);
        desktopPane.add(frame);
        
        cargarPlaylists();
    }
    
    private void cargarPlaylists() {
        modeloPlaylists.setRowCount(0);
        List<Playlist> playlists = SistemaController.getInstancia().obtenerPlaylistsPublicas();
        
        for (Playlist p : playlists) {
            modeloPlaylists.addRow(new Object[]{
                p.getId(),
                p.getNombre(),
                p.getCantidadCanciones(),
                p.getVecesReproducida(),
                p.isEsPublica() ? "Sí" : "No"
            });
        }
    }
    
    private void eliminarPlaylistSeleccionada() {
        int fila = tablaPlaylists.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una playlist");
            return;
        }
        
        String id = (String) modeloPlaylists.getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Eliminar esta playlist?",
            "Confirmar",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            SistemaController.getInstancia().eliminarPlaylist(id);
            cargarPlaylists();
        }
    }
    
    private void abrirReportes() {
        desktopPane.removeAll();
        desktopPane.repaint();
        
        PanelReportes panelReportes = new PanelReportes(true); // true = admin
        desktopPane.add(panelReportes);
        panelReportes.setVisible(true);
    }
    
    private void cerrarSesion() {
        AuthController.getInstancia().cerrarSesion();
        loginAdmin.setVisible(true);
        this.dispose();
    }
}