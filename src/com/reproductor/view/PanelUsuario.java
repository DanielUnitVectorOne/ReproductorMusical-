package com.reproductor.view;

import com.reproductor.controller.AuthController;
import com.reproductor.controller.SistemaController;
import com.reproductor.model.Cancion;
import com.reproductor.model.Playlist;
import com.reproductor.utils.*;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Panel principal del usuario.
 *
 * MEJORA: editarPlaylist() ahora abre un diálogo real que permite
 * renombrar la playlist y refrescar la tabla automáticamente.
 * Se elimina el mensaje "Función de edición en desarrollo".
 */
public class PanelUsuario extends JFrame {

    private final LoginUsuario loginUsuario;
    private JPanel             panelContenido;
    private final String       username;

    public PanelUsuario(LoginUsuario loginUsuario) {
        this.loginUsuario = loginUsuario;
        this.username     = AuthController.getInstancia().getUsuarioActual().getUsername();
        configurarVentana();
        inicializarComponentes();
    }

    // ── CONFIGURACIÓN ──────────────────────────────────────────────────────────

    private void configurarVentana() {
        setTitle("Mi Reproductor");
        setSize(Constantes.ANCHO_VENTANA, Constantes.ALTO_VENTANA);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void inicializarComponentes() {
        add(new BarraTituloPersonalizado(this, "Mi Reproductor - " + username), BorderLayout.NORTH);
        add(crearPanelMenu(), BorderLayout.WEST);

        panelContenido = new JPanel(new BorderLayout());
        panelContenido.setBackground(Constantes.COLOR_FONDO_MEDIO);
        add(panelContenido, BorderLayout.CENTER);

        mostrarReproductor();
    }

    // ── MENÚ LATERAL ───────────────────────────────────────────────────────────

    private JPanel crearPanelMenu() {
        PanelDegradado menu = new PanelDegradado(
                new Color(25, 25, 25), Constantes.COLOR_FONDO_OSCURO, true);
        menu.setPreferredSize(new Dimension(250, Constantes.ALTO_VENTANA));
        menu.setLayout(null);

        JLabel titulo = new JLabel("MI MÚSICA");
        titulo.setFont(Constantes.FUENTE_TITULO);
        titulo.setForeground(Constantes.COLOR_PRIMARIO);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setBounds(0, 20, 250, 40);
        menu.add(titulo);

        int y = 90;

        BotonRedondeado btnReproductor = new BotonRedondeado("🎵 Reproductor");
        btnReproductor.setBounds(25, y, 200, 50);
        btnReproductor.addActionListener(e -> mostrarReproductor());
        menu.add(btnReproductor); y += 60;

        BotonRedondeado btnMisPlaylists = new BotonRedondeado("📋 Mis Playlists");
        btnMisPlaylists.setBounds(25, y, 200, 50);
        btnMisPlaylists.addActionListener(e -> mostrarMisPlaylists());
        menu.add(btnMisPlaylists); y += 60;

        BotonRedondeado btnPublicas = new BotonRedondeado("🌐 Playlists Públicas");
        btnPublicas.setBounds(25, y, 200, 50);
        btnPublicas.addActionListener(e -> mostrarPlaylistsPublicas());
        menu.add(btnPublicas); y += 60;

        BotonRedondeado btnReportes = new BotonRedondeado("📊 Mis Estadísticas");
        btnReportes.setBounds(25, y, 200, 50);
        btnReportes.addActionListener(e -> mostrarEstadisticas());
        menu.add(btnReportes);

        BotonRedondeado btnCerrar = new BotonRedondeado("🚪 Cerrar Sesión", Constantes.COLOR_ERROR);
        btnCerrar.setBounds(25, Constantes.ALTO_VENTANA - 100, 200, 50);
        btnCerrar.addActionListener(e -> cerrarSesion());
        menu.add(btnCerrar);

        return menu;
    }

    // ── REPRODUCTOR ────────────────────────────────────────────────────────────

    private void mostrarReproductor() {
        panelContenido.removeAll();
        panelContenido.add(new ReproductorPanel(), BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    // ── MIS PLAYLISTS ──────────────────────────────────────────────────────────

    private void mostrarMisPlaylists() {
        panelContenido.removeAll();

        JPanel panel = new JPanel(null);
        panel.setBackground(Constantes.COLOR_FONDO_MEDIO);

        JLabel lblTitulo = new JLabel("📋 Gestionar Mis Playlists");
        lblTitulo.setFont(Constantes.FUENTE_TITULO);
        lblTitulo.setForeground(Constantes.COLOR_TEXTO);
        lblTitulo.setBounds(30, 20, 400, 40);
        panel.add(lblTitulo);

        String[] columnas = {"Nombre", "Canciones", "Fecha de creación"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modelo);
        tabla.setBackground(Constantes.COLOR_FONDO_CLARO);
        tabla.setForeground(Constantes.COLOR_TEXTO);
        tabla.setSelectionBackground(Constantes.COLOR_PRIMARIO);
        tabla.setRowHeight(35);
        tabla.setFont(Constantes.FUENTE_NORMAL);
        tabla.getTableHeader().setBackground(Constantes.COLOR_FONDO_OSCURO);
        tabla.getTableHeader().setForeground(Constantes.COLOR_TEXTO);
        tabla.getTableHeader().setFont(Constantes.FUENTE_BOTON);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(30, 80, 880, 380);
        panel.add(scroll);

        // Cargar datos
        List<Playlist> playlists = SistemaController.getInstancia().obtenerPlaylistsUsuario(username);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        for (Playlist p : playlists) {
            modelo.addRow(new Object[]{
                p.getNombre(),
                p.getCantidadCanciones() + " canciones",
                sdf.format(p.getFechaCreacion())
            });
        }

        // ── Botón Editar ──────────────────────────────────────────────────────
        BotonRedondeado btnEditar = new BotonRedondeado("✏️ Editar Playlist");
        btnEditar.setBounds(400, 480, 180, 45);
        btnEditar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(panel, "Selecciona una playlist para editar.");
                return;
            }
            String nombre = (String) modelo.getValueAt(fila, 0);

            // Buscar el objeto Playlist por nombre en la lista ya cargada
            for (Playlist p : playlists) {
                if (p.getNombre().equals(nombre)) {
                    editarPlaylist(p, modelo, fila);
                    break;
                }
            }
        });
        panel.add(btnEditar);

        // ── Botón Eliminar ────────────────────────────────────────────────────
        BotonRedondeado btnEliminar = new BotonRedondeado("🗑️ Eliminar");
        btnEliminar.setBounds(600, 480, 140, 45);
        btnEliminar.setColorNormal(Constantes.COLOR_ERROR);
        btnEliminar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) return;
            String nombre = (String) modelo.getValueAt(fila, 0);
            int confirm = JOptionPane.showConfirmDialog(panel,
                    "¿Eliminar '" + nombre + "'?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                for (Playlist p : playlists) {
                    if (p.getNombre().equals(nombre)) {
                        SistemaController.getInstancia().eliminarPlaylist(p.getId());
                        modelo.removeRow(fila);
                        playlists.remove(p);
                        break;
                    }
                }
            }
        });
        panel.add(btnEliminar);

        panelContenido.add(panel, BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    /**
     * Abre un diálogo real para renombrar la playlist seleccionada.
     * Antes mostraba solo "Función de edición en desarrollo".
     *
     * @param playlist  objeto Playlist a editar (ya cargado, con ID)
     * @param modelo    DefaultTableModel de la tabla, para refrescar la fila
     * @param fila      índice de la fila seleccionada en la tabla
     */
    private void editarPlaylist(Playlist playlist, DefaultTableModel modelo, int fila) {
        JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), "Editar Playlist", true);
        dialog.setSize(420, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(null);
        dialog.getContentPane().setBackground(Constantes.COLOR_FONDO_MEDIO);

        JLabel lbl = new JLabel("Nuevo nombre:");
        lbl.setForeground(Constantes.COLOR_TEXTO);
        lbl.setFont(Constantes.FUENTE_NORMAL);
        lbl.setBounds(20, 25, 150, 25);
        dialog.add(lbl);

        JTextField txtNombre = new JTextField(playlist.getNombre());
        txtNombre.setBackground(Constantes.COLOR_FONDO_CLARO);
        txtNombre.setForeground(Constantes.COLOR_TEXTO);
        txtNombre.setCaretColor(Constantes.COLOR_TEXTO);
        txtNombre.setFont(Constantes.FUENTE_NORMAL);
        txtNombre.setBounds(20, 55, 375, 35);
        dialog.add(txtNombre);

        BotonRedondeado btnGuardar = new BotonRedondeado("💾 Guardar");
        btnGuardar.setBounds(80, 115, 120, 40);
        btnGuardar.addActionListener(e -> {
            String nuevoNombre = txtNombre.getText().trim();
            if (nuevoNombre.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "El nombre no puede estar vacío.");
                return;
            }

            playlist.setNombre(nuevoNombre);
            boolean ok = SistemaController.getInstancia().actualizarPlaylist(playlist);

            if (ok) {
                // Refrescar esa fila de la tabla sin recargar toda la vista
                modelo.setValueAt(nuevoNombre, fila, 0);
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "✓ Playlist renombrada correctamente.");
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Error al guardar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.add(btnGuardar);

        BotonRedondeado btnCancelar = new BotonRedondeado("Cancelar", Constantes.COLOR_ERROR);
        btnCancelar.setBounds(215, 115, 120, 40);
        btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.add(btnCancelar);

        dialog.setVisible(true);
    }

    // ── PLAYLISTS PÚBLICAS ─────────────────────────────────────────────────────

    private void mostrarPlaylistsPublicas() {
        panelContenido.removeAll();

        JPanel panel = new JPanel(null);
        panel.setBackground(Constantes.COLOR_FONDO_MEDIO);

        JLabel lblTitulo = new JLabel("🌐 Playlists Públicas Disponibles");
        lblTitulo.setFont(Constantes.FUENTE_TITULO);
        lblTitulo.setForeground(Constantes.COLOR_TEXTO);
        lblTitulo.setBounds(30, 20, 500, 40);
        panel.add(lblTitulo);

        JLabel lblInfo = new JLabel("Agrega playlists del administrador a tu biblioteca personal");
        lblInfo.setFont(Constantes.FUENTE_NORMAL);
        lblInfo.setForeground(Constantes.COLOR_TEXTO_SECUNDARIO);
        lblInfo.setBounds(30, 60, 600, 25);
        panel.add(lblInfo);

        String[] columnas = {"Nombre", "Canciones", "Creador"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modelo);
        tabla.setBackground(Constantes.COLOR_FONDO_CLARO);
        tabla.setForeground(Constantes.COLOR_TEXTO);
        tabla.setSelectionBackground(Constantes.COLOR_PRIMARIO);
        tabla.setRowHeight(35);
        tabla.setFont(Constantes.FUENTE_NORMAL);
        tabla.getTableHeader().setBackground(Constantes.COLOR_FONDO_OSCURO);
        tabla.getTableHeader().setForeground(Constantes.COLOR_TEXTO);
        tabla.getTableHeader().setFont(Constantes.FUENTE_BOTON);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(30, 100, 880, 380);
        panel.add(scroll);

        List<Playlist> publicas = SistemaController.getInstancia().obtenerPlaylistsPublicas();
        for (Playlist p : publicas) {
            modelo.addRow(new Object[]{
                p.getNombre(), p.getCantidadCanciones() + " canciones", p.getCreadorUsername()
            });
        }

        BotonRedondeado btnAgregar = new BotonRedondeado("➕ Agregar a Mis Playlists");
        btnAgregar.setBounds(350, 500, 240, 50);
        btnAgregar.setColorNormal(Constantes.COLOR_PRIMARIO);
        btnAgregar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) { JOptionPane.showMessageDialog(panel, "Selecciona una playlist."); return; }
            String nombre = (String) modelo.getValueAt(fila, 0);
            for (Playlist p : publicas) {
                if (p.getNombre().equals(nombre)) {
                    Playlist copia = new Playlist(p.getNombre() + " (copia)", username);
                    copia.setEsPublica(false);
                    for (Cancion c : p.getCanciones()) copia.agregarCancion(c);
                    if (SistemaController.getInstancia().crearPlaylist(copia)) {
                        JOptionPane.showMessageDialog(panel, "✓ Playlist agregada a tu biblioteca.",
                                "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    }
                    break;
                }
            }
        });
        panel.add(btnAgregar);

        panelContenido.add(panel, BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    // ── ESTADÍSTICAS ───────────────────────────────────────────────────────────

    private void mostrarEstadisticas() {
        panelContenido.removeAll();

        JPanel panel = new JPanel(null);
        panel.setBackground(Constantes.COLOR_FONDO_MEDIO);

        JLabel lblTitulo = new JLabel("📊 Mis Estadísticas de Reproducción");
        lblTitulo.setFont(Constantes.FUENTE_TITULO);
        lblTitulo.setForeground(Constantes.COLOR_TEXTO);
        lblTitulo.setBounds(30, 20, 500, 40);
        panel.add(lblTitulo);

        List<Playlist> misPlaylists = SistemaController.getInstancia().obtenerPlaylistsUsuario(username);
        int totalPl  = misPlaylists.size();
        int totalCan = misPlaylists.stream().mapToInt(Playlist::getCantidadCanciones).sum();

        crearTarjetaEstadistica(panel, "📋 Playlists",  String.valueOf(totalPl),  30, 100);
        crearTarjetaEstadistica(panel, "🎵 Canciones",  String.valueOf(totalCan), 320, 100);
        crearTarjetaEstadistica(panel, "⏱ Tiempo Total","—",                      610, 100);

        JLabel lblTop = new JLabel("🏆 Top 10 Más Escuchadas");
        lblTop.setFont(Constantes.FUENTE_SUBTITULO);
        lblTop.setForeground(Constantes.COLOR_TEXTO);
        lblTop.setBounds(30, 250, 300, 30);
        panel.add(lblTop);

        String[] columnas = {"#", "Título", "Artista", "Reproducciones"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modelo);
        tabla.setBackground(Constantes.COLOR_FONDO_CLARO);
        tabla.setForeground(Constantes.COLOR_TEXTO);
        tabla.setRowHeight(30);
        tabla.setFont(Constantes.FUENTE_NORMAL);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(30, 290, 880, 200);
        panel.add(scroll);

        List<Cancion> top = SistemaController.getInstancia().obtenerTopCanciones(10);
        int pos = 1;
        for (Cancion c : top) {
            modelo.addRow(new Object[]{pos++, c.getTitulo(), c.getArtista(), c.getVecesReproducida()});
        }

        BotonRedondeado btnPDF = new BotonRedondeado("📄 Descargar Reporte PDF");
        btnPDF.setBounds(350, 510, 240, 45);
        btnPDF.addActionListener(e -> JOptionPane.showMessageDialog(panel, "Generando reporte PDF..."));
        panel.add(btnPDF);

        panelContenido.add(panel, BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    private void crearTarjetaEstadistica(JPanel parent, String titulo, String valor, int x, int y) {
        JPanel tarjeta = new JPanel(null);
        tarjeta.setBackground(Constantes.COLOR_FONDO_CLARO);
        tarjeta.setBounds(x, y, 250, 120);
        tarjeta.setBorder(BorderFactory.createLineBorder(Constantes.COLOR_PRIMARIO, 2));

        JLabel lblT = new JLabel(titulo);
        lblT.setFont(Constantes.FUENTE_NORMAL);
        lblT.setForeground(Constantes.COLOR_TEXTO_SECUNDARIO);
        lblT.setBounds(20, 20, 210, 25);
        tarjeta.add(lblT);

        JLabel lblV = new JLabel(valor);
        lblV.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblV.setForeground(Constantes.COLOR_PRIMARIO);
        lblV.setBounds(20, 50, 210, 50);
        tarjeta.add(lblV);

        parent.add(tarjeta);
    }

    // ── CERRAR SESIÓN ──────────────────────────────────────────────────────────

    private void cerrarSesion() {
        AuthController.getInstancia().cerrarSesion();
        loginUsuario.setVisible(true);
        this.dispose();
    }
} 