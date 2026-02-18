package com.reproductor.view;

import com.reproductor.controller.AuthController;
import com.reproductor.controller.SistemaController;
import com.reproductor.model.Cancion;
import com.reproductor.model.Playlist;
import com.reproductor.model.Usuario;
import com.reproductor.utils.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Panel principal del Administrador.
 *
 * MEJORAS EN ESTA VERSIÓN:
 *  - Dashboard: la tarjeta "Usuarios" ahora muestra el conteo real.
 *  - Dashboard: tabla de los últimos 5 usuarios registrados.
 *  - Se mantienen todos los FIX anteriores (1,2,7,8).
 */
public class PanelAdmin extends JFrame {

    private final LoginAdmin loginAdmin;
    private JDesktopPane     desktopPane;

    public PanelAdmin(LoginAdmin loginAdmin) {
        this.loginAdmin = loginAdmin;
        configurarVentana();
        inicializarComponentes();
    }

    // ── CONFIGURACIÓN ──────────────────────────────────────────────────────────

    private void configurarVentana() {
        setTitle("Panel Administrador");
        setSize(Constantes.ANCHO_VENTANA, Constantes.ALTO_VENTANA);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void inicializarComponentes() {
        add(new BarraTituloPersonalizado(this, "Panel Administrador"), BorderLayout.NORTH);
        add(crearPanelMenu(), BorderLayout.WEST);

        desktopPane = new JDesktopPane();
        desktopPane.setBackground(Constantes.COLOR_FONDO_MEDIO);
        add(desktopPane, BorderLayout.CENTER);

        mostrarDashboard();
    }

    // ── MENÚ LATERAL ───────────────────────────────────────────────────────────

    private JPanel crearPanelMenu() {
        PanelDegradado panelMenu = new PanelDegradado(
                new Color(25, 25, 25), Constantes.COLOR_FONDO_OSCURO, true);
        panelMenu.setPreferredSize(new Dimension(250, Constantes.ALTO_VENTANA));
        panelMenu.setLayout(null);

        JLabel lblTitulo = new JLabel("ADMINISTRADOR");
        lblTitulo.setFont(Constantes.FUENTE_SUBTITULO);
        lblTitulo.setForeground(Constantes.COLOR_PRIMARIO);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBounds(0, 20, 250, 30);
        panelMenu.add(lblTitulo);

        int y = 80, h = 50, gap = 10;

        BotonRedondeado btnDash = new BotonRedondeado("📊 Dashboard");
        btnDash.setBounds(25, y, 200, h);
        btnDash.addActionListener(e -> mostrarDashboard());
        panelMenu.add(btnDash); y += h + gap;

        BotonRedondeado btnCrear = new BotonRedondeado("📋 Crear Playlist");
        btnCrear.setBounds(25, y, 200, h);
        btnCrear.addActionListener(e -> abrirCrearPlaylist());
        panelMenu.add(btnCrear); y += h + gap;

        BotonRedondeado btnPlaylists = new BotonRedondeado("📚 Gestionar Playlists");
        btnPlaylists.setBounds(25, y, 200, h);
        btnPlaylists.addActionListener(e -> mostrarGestionPlaylists());
        panelMenu.add(btnPlaylists); y += h + gap;

        BotonRedondeado btnCanciones = new BotonRedondeado("🎵 Gestionar Canciones");
        btnCanciones.setBounds(25, y, 200, h);
        btnCanciones.addActionListener(e -> mostrarGestionCanciones());
        panelMenu.add(btnCanciones); y += h + gap;

        BotonRedondeado btnReportes = new BotonRedondeado("📄 Reportes");
        btnReportes.setBounds(25, y, 200, h);
        btnReportes.addActionListener(e -> abrirReportes());
        panelMenu.add(btnReportes);

        BotonRedondeado btnCerrar = new BotonRedondeado("🚪 Cerrar Sesión", Constantes.COLOR_ERROR);
        btnCerrar.setBounds(25, Constantes.ALTO_VENTANA - 100, 200, h);
        btnCerrar.addActionListener(e -> cerrarSesion());
        panelMenu.add(btnCerrar);

        return panelMenu;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  DASHBOARD
    // ══════════════════════════════════════════════════════════════════════════

    private void mostrarDashboard() {
        desktopPane.removeAll();
        desktopPane.repaint();

        // FIX #7: JInternalFrame en lugar de JPanel directo al JDesktopPane
        JInternalFrame frame = new JInternalFrame("Dashboard", false, false, false, false);
        frame.setSize(950, 650);
        frame.setLocation(0, 0);
        frame.setBackground(Constantes.COLOR_FONDO_MEDIO);

        JPanel panel = new JPanel(null);
        panel.setBackground(Constantes.COLOR_FONDO_MEDIO);

        // ── Título ────────────────────────────────────────────────────────────
        JLabel lblTitulo = new JLabel("📊 Dashboard del Sistema");
        lblTitulo.setFont(Constantes.FUENTE_TITULO);
        lblTitulo.setForeground(Constantes.COLOR_TEXTO);
        lblTitulo.setBounds(30, 15, 500, 40);
        panel.add(lblTitulo);

        // ── Tarjetas de estadísticas ──────────────────────────────────────────
        int totalCanciones = SistemaController.getInstancia().obtenerTodasCanciones().size();
        int totalPlaylists = SistemaController.getInstancia().obtenerPlaylistsPublicas().size();
        int totalUsuarios  = SistemaController.getInstancia().contarUsuarios();  // ← real ahora

        crearTarjetaDashboard(panel, "🎵 Canciones",  String.valueOf(totalCanciones), 30,  65);
        crearTarjetaDashboard(panel, "📋 Playlists",  String.valueOf(totalPlaylists), 320, 65);
        crearTarjetaDashboard(panel, "👥 Usuarios",   String.valueOf(totalUsuarios),  610, 65);

        // ── Top 5 canciones ───────────────────────────────────────────────────
        JLabel lblTop = new JLabel("🏆 Top 5 Canciones Más Reproducidas");
        lblTop.setFont(Constantes.FUENTE_SUBTITULO);
        lblTop.setForeground(Constantes.COLOR_TEXTO);
        lblTop.setBounds(30, 205, 400, 28);
        panel.add(lblTop);

        String[] colCan = {"#", "Título", "Artista", "Reproducciones"};
        DefaultTableModel modeloCan = new DefaultTableModel(colCan, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tablaCan = crearTabla(modeloCan);
        JScrollPane scrollCan = new JScrollPane(tablaCan);
        scrollCan.setBounds(30, 238, 430, 155);
        panel.add(scrollCan);

        List<Cancion> top5 = SistemaController.getInstancia().obtenerTopCanciones(5);
        int pos = 1;
        for (Cancion c : top5) {
            modeloCan.addRow(new Object[]{
                pos++, c.getTitulo(), c.getArtista(), c.getVecesReproducida()
            });
        }

        // ── Últimos usuarios registrados ──────────────────────────────────────
        JLabel lblUsuarios = new JLabel("🆕 Últimos Usuarios Registrados");
        lblUsuarios.setFont(Constantes.FUENTE_SUBTITULO);
        lblUsuarios.setForeground(Constantes.COLOR_TEXTO);
        lblUsuarios.setBounds(480, 205, 400, 28);
        panel.add(lblUsuarios);

        String[] colUsu = {"Usuario", "Nombre", "Registrado"};
        DefaultTableModel modeloUsu = new DefaultTableModel(colUsu, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tablaUsu = crearTabla(modeloUsu);
        JScrollPane scrollUsu = new JScrollPane(tablaUsu);
        scrollUsu.setBounds(480, 238, 435, 155);
        panel.add(scrollUsu);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        List<Usuario> ultimosUsuarios = SistemaController.getInstancia()
                .obtenerTodosUsuarios()
                .stream()
                // Ordenar: más reciente primero
                .sorted(Comparator.comparing(Usuario::getFechaRegistro).reversed())
                .limit(5)
                .collect(Collectors.toList());

        for (Usuario u : ultimosUsuarios) {
            modeloUsu.addRow(new Object[]{
                u.getUsername(),
                u.getNombre(),
                sdf.format(u.getFechaRegistro())
            });
        }

        // ── Mensaje si no hay usuarios ────────────────────────────────────────
        if (ultimosUsuarios.isEmpty()) {
            modeloUsu.addRow(new Object[]{"—", "Sin usuarios registrados", "—"});
        }

        // ── Separador / nota ──────────────────────────────────────────────────
        JLabel lblNota = new JLabel("Mostrando los últimos 5 registros de cada sección");
        lblNota.setFont(Constantes.FUENTE_PEQUEÑA);
        lblNota.setForeground(Constantes.COLOR_TEXTO_SECUNDARIO);
        lblNota.setHorizontalAlignment(SwingConstants.CENTER);
        lblNota.setBounds(0, 400, 930, 20);
        panel.add(lblNota);

        // ── Top 5 playlists ───────────────────────────────────────────────────
        JLabel lblPl = new JLabel("📋 Playlists Públicas Más Populares");
        lblPl.setFont(Constantes.FUENTE_SUBTITULO);
        lblPl.setForeground(Constantes.COLOR_TEXTO);
        lblPl.setBounds(30, 425, 400, 28);
        panel.add(lblPl);

        String[] colPl = {"Nombre", "Canciones", "Reproducciones"};
        DefaultTableModel modeloPl = new DefaultTableModel(colPl, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tablaPl = crearTabla(modeloPl);
        JScrollPane scrollPl = new JScrollPane(tablaPl);
        scrollPl.setBounds(30, 458, 880, 140);
        panel.add(scrollPl);

        List<Playlist> topPl = SistemaController.getInstancia()
                .obtenerPlaylistsPublicas()
                .stream()
                .sorted(Comparator.comparingInt(Playlist::getVecesReproducida).reversed())
                .limit(5)
                .collect(Collectors.toList());

        for (Playlist p : topPl) {
            modeloPl.addRow(new Object[]{
                p.getNombre(), p.getCantidadCanciones(), p.getVecesReproducida()
            });
        }
        if (topPl.isEmpty()) {
            modeloPl.addRow(new Object[]{"—", "Sin playlists públicas", "—"});
        }

        frame.add(panel);
        frame.setVisible(true);
        desktopPane.add(frame);
        try { frame.setMaximum(true); } catch (Exception ignored) {}
    }

    /** Crea una JTable con el estilo visual del proyecto. */
    private JTable crearTabla(DefaultTableModel modelo) {
        JTable tabla = new JTable(modelo);
        tabla.setBackground(Constantes.COLOR_FONDO_CLARO);
        tabla.setForeground(Constantes.COLOR_TEXTO);
        tabla.setSelectionBackground(Constantes.COLOR_PRIMARIO);
        tabla.setRowHeight(28);
        tabla.setFont(Constantes.FUENTE_NORMAL);
        tabla.getTableHeader().setBackground(Constantes.COLOR_FONDO_OSCURO);
        tabla.getTableHeader().setForeground(Constantes.COLOR_TEXTO);
        tabla.getTableHeader().setFont(Constantes.FUENTE_BOTON);
        return tabla;
    }

    /** Tarjeta de estadística reutilizable. */
    private void crearTarjetaDashboard(JPanel parent, String titulo, String valor, int x, int y) {
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
        lblV.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblV.setForeground(Constantes.COLOR_PRIMARIO);
        lblV.setBounds(20, 50, 210, 50);
        tarjeta.add(lblV);

        parent.add(tarjeta);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  GESTIÓN DE CANCIONES
    // ══════════════════════════════════════════════════════════════════════════

    private void mostrarGestionCanciones() {
        desktopPane.removeAll();
        desktopPane.repaint();

        JInternalFrame frame = new JInternalFrame("Gestionar Canciones", false, true, false, false);
        frame.setSize(900, 600);
        frame.setLocation(20, 20);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Constantes.COLOR_FONDO_MEDIO);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] columnas = {"ID", "Título", "Artista", "Duración", "Reproducciones"};
        DefaultTableModel modeloCanciones = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tablaCanciones = crearTabla(modeloCanciones);
        panel.add(new JScrollPane(tablaCanciones), BorderLayout.CENTER);

        Runnable cargar = () -> {
            modeloCanciones.setRowCount(0);
            for (Cancion c : SistemaController.getInstancia().obtenerTodasCanciones()) {
                modeloCanciones.addRow(new Object[]{
                    c.getId(), c.getTitulo(), c.getArtista(),
                    c.getDuracionFormateada(), c.getVecesReproducida()
                });
            }
        };
        cargar.run();

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setBackground(Constantes.COLOR_FONDO_MEDIO);

        BotonRedondeado btnActualizar = new BotonRedondeado("🔄 Actualizar");
        btnActualizar.addActionListener(e -> cargar.run());
        panelBotones.add(btnActualizar);

        BotonRedondeado btnEditar = new BotonRedondeado("✏️ Editar Canción");
        btnEditar.addActionListener(e -> {
            int fila = tablaCanciones.getSelectedRow();
            if (fila == -1) { JOptionPane.showMessageDialog(frame, "Seleccione una canción."); return; }
            abrirDialogoEditarCancion(frame,
                    (String) modeloCanciones.getValueAt(fila, 0),
                    (String) modeloCanciones.getValueAt(fila, 1),
                    (String) modeloCanciones.getValueAt(fila, 2),
                    cargar);
        });
        panelBotones.add(btnEditar);

        BotonRedondeado btnEliminar = new BotonRedondeado("🗑️ Eliminar", Constantes.COLOR_ERROR);
        btnEliminar.addActionListener(e -> {
            int fila = tablaCanciones.getSelectedRow();
            if (fila == -1) { JOptionPane.showMessageDialog(frame, "Seleccione una canción."); return; }
            if (JOptionPane.showConfirmDialog(frame, "¿Eliminar esta canción?", "Confirmar",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                SistemaController.getInstancia().eliminarCancion(
                        (String) modeloCanciones.getValueAt(fila, 0));
                cargar.run();
            }
        });
        panelBotones.add(btnEliminar);

        panel.add(panelBotones, BorderLayout.SOUTH);
        frame.add(panel);
        frame.setVisible(true);
        desktopPane.add(frame);
    }

    // FIX #1: actualizarCancion() estaba comentada — ahora activa y validada
    private void abrirDialogoEditarCancion(Component parent, String id,
            String tituloActual, String artistaActual, Runnable alGuardar) {

        JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(parent), "Editar Canción", true);
        dialog.setSize(420, 260);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(null);
        dialog.getContentPane().setBackground(Constantes.COLOR_FONDO_MEDIO);

        JLabel lblT = new JLabel("Título:"); lblT.setForeground(Constantes.COLOR_TEXTO);
        lblT.setBounds(20, 20, 100, 25); dialog.add(lblT);

        JTextField txtTitulo = new JTextField(tituloActual);
        txtTitulo.setBackground(Constantes.COLOR_FONDO_CLARO);
        txtTitulo.setForeground(Constantes.COLOR_TEXTO);
        txtTitulo.setCaretColor(Constantes.COLOR_TEXTO);
        txtTitulo.setBounds(20, 45, 375, 35); dialog.add(txtTitulo);

        JLabel lblA = new JLabel("Artista:"); lblA.setForeground(Constantes.COLOR_TEXTO);
        lblA.setBounds(20, 90, 100, 25); dialog.add(lblA);

        JTextField txtArtista = new JTextField(artistaActual);
        txtArtista.setBackground(Constantes.COLOR_FONDO_CLARO);
        txtArtista.setForeground(Constantes.COLOR_TEXTO);
        txtArtista.setCaretColor(Constantes.COLOR_TEXTO);
        txtArtista.setBounds(20, 115, 375, 35); dialog.add(txtArtista);

        BotonRedondeado btnGuardar = new BotonRedondeado("💾 Guardar");
        btnGuardar.setBounds(110, 175, 200, 40);
        btnGuardar.addActionListener(e -> {
            String nt = txtTitulo.getText().trim(), na = txtArtista.getText().trim();
            if (nt.isEmpty() || na.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Título y artista no pueden estar vacíos.");
                return;
            }
            if (SistemaController.getInstancia().actualizarCancion(id, nt, na)) {
                JOptionPane.showMessageDialog(dialog, "✓ Canción actualizada.");
                alGuardar.run(); dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Error al guardar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.add(btnGuardar);
        dialog.setVisible(true);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  GESTIÓN DE PLAYLISTS
    // ══════════════════════════════════════════════════════════════════════════

    private void mostrarGestionPlaylists() {
        desktopPane.removeAll();
        desktopPane.repaint();

        JInternalFrame frame = new JInternalFrame("Gestionar Playlists", false, true, false, false);
        frame.setSize(900, 600);
        frame.setLocation(20, 20);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Constantes.COLOR_FONDO_MEDIO);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] columnas = {"ID", "Nombre", "Canciones", "Reproducciones", "Pública"};
        DefaultTableModel modeloPl = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = crearTabla(modeloPl);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        Runnable cargar = () -> {
            modeloPl.setRowCount(0);
            for (Playlist p : SistemaController.getInstancia().obtenerPlaylistsPublicas()) {
                modeloPl.addRow(new Object[]{
                    p.getId(), p.getNombre(), p.getCantidadCanciones(),
                    p.getVecesReproducida(), p.isEsPublica() ? "Sí" : "No"
                });
            }
        };
        cargar.run();

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setBackground(Constantes.COLOR_FONDO_MEDIO);

        BotonRedondeado btnEditar = new BotonRedondeado("✏️ Editar / Actualizar");
        btnEditar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) { JOptionPane.showMessageDialog(frame, "Seleccione una playlist."); return; }
            abrirDialogoEditarPlaylist(frame, (String) modeloPl.getValueAt(fila, 0), cargar);
        });
        panelBotones.add(btnEditar);

        BotonRedondeado btnEliminar = new BotonRedondeado("🗑️ Eliminar", Constantes.COLOR_ERROR);
        btnEliminar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) return;
            if (JOptionPane.showConfirmDialog(frame, "¿Eliminar esta playlist?", "Confirmar",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                SistemaController.getInstancia().eliminarPlaylist(
                        (String) modeloPl.getValueAt(fila, 0));
                cargar.run();
            }
        });
        panelBotones.add(btnEliminar);

        panel.add(panelBotones, BorderLayout.SOUTH);
        frame.add(panel);
        frame.setVisible(true);
        desktopPane.add(frame);
    }

    // FIX #2: actualizarPlaylist() estaba comentada — ahora activa y validada
    private void abrirDialogoEditarPlaylist(Component parent, String idPlaylist, Runnable onSave) {
        Playlist playlist = SistemaController.getInstancia().obtenerPlaylistPorId(idPlaylist);
        if (playlist == null) {
            JOptionPane.showMessageDialog(parent, "Playlist no encontrada.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(parent), "Editar Playlist", true);
        dialog.setSize(500, 280);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(Constantes.COLOR_FONDO_MEDIO);

        JPanel form = new JPanel(new GridLayout(4, 1, 5, 5));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));
        form.setBackground(Constantes.COLOR_FONDO_MEDIO);

        JLabel lbl = new JLabel("Nombre de la Playlist:");
        lbl.setForeground(Constantes.COLOR_TEXTO);
        JTextField txtNombre = new JTextField(playlist.getNombre());
        txtNombre.setBackground(Constantes.COLOR_FONDO_CLARO);
        txtNombre.setForeground(Constantes.COLOR_TEXTO);
        txtNombre.setCaretColor(Constantes.COLOR_TEXTO);
        JCheckBox chkPublica = new JCheckBox("Es Pública", playlist.isEsPublica());
        chkPublica.setBackground(Constantes.COLOR_FONDO_MEDIO);
        chkPublica.setForeground(Constantes.COLOR_TEXTO);

        form.add(lbl); form.add(txtNombre); form.add(chkPublica);
        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(); btnPanel.setBackground(Constantes.COLOR_FONDO_MEDIO);
        BotonRedondeado btnGuardar = new BotonRedondeado("💾 Guardar Cambios");
        btnGuardar.addActionListener(e -> {
            String nuevoNombre = txtNombre.getText().trim();
            if (nuevoNombre.isEmpty()) { JOptionPane.showMessageDialog(dialog, "El nombre no puede estar vacío."); return; }
            playlist.setNombre(nuevoNombre);
            playlist.setEsPublica(chkPublica.isSelected());
            if (SistemaController.getInstancia().actualizarPlaylist(playlist)) {
                JOptionPane.showMessageDialog(dialog, "✓ Playlist actualizada.");
                onSave.run(); dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Error al guardar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        BotonRedondeado btnCancelar = new BotonRedondeado("Cancelar", Constantes.COLOR_ERROR);
        btnCancelar.addActionListener(e -> dialog.dispose());
        btnPanel.add(btnGuardar); btnPanel.add(btnCancelar);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  CREAR PLAYLIST  (FIX #8: setVisible antes de add)
    // ══════════════════════════════════════════════════════════════════════════

    private void abrirCrearPlaylist() {
        desktopPane.removeAll();
        desktopPane.repaint();

        PanelPersonalizado pp = new PanelPersonalizado("Crear Playlist");
        pp.setBounds(20, 20, 950, 580);
        pp.setContenido(new CrearPlaylist(true, pp));
        pp.getBotonCerrar().addActionListener(e -> { desktopPane.remove(pp); desktopPane.repaint(); });
        pp.setVisible(true);   // FIX #8
        desktopPane.add(pp);
        desktopPane.repaint();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  REPORTES / SESIÓN
    // ══════════════════════════════════════════════════════════════════════════

    private void abrirReportes() {
        desktopPane.removeAll();
        desktopPane.repaint();
        PanelReportes pr = new PanelReportes(true);
        pr.setVisible(true);
        desktopPane.add(pr);
    }

    private void cerrarSesion() {
        AuthController.getInstancia().cerrarSesion();
        loginAdmin.setVisible(true);
        this.dispose();
    }
}