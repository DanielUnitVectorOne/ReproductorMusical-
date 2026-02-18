package com.reproductor.view;

import com.reproductor.controller.AuthController;
import com.reproductor.controller.ReproductorController;
import com.reproductor.controller.SistemaController;
import com.reproductor.model.Cancion;
import com.reproductor.model.Playlist;
import com.reproductor.utils.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Panel del reproductor de música del usuario.
 *
 * FIX #10 — Búsqueda de canción por título (frágil) → por índice (robusta):
 *
 *   ANTES: reproducirSeleccionada() obtenía el título de la fila
 *          seleccionada y luego iteraba TODA la base de datos buscando
 *          la primera canción cuyo título coincidiera:
 *
 *              for (Cancion c : SistemaController...obtenerTodasCanciones()) {
 *                  if (c.getTitulo().equals(titulo)) { ... break; }
 *              }
 *
 *          Problemas:
 *            a) Si dos canciones comparten título, siempre se reproducía
 *               la primera de la lista, nunca la que el usuario seleccionó.
 *            b) Cada reproducción lanzaba una carga innecesaria del archivo
 *               de persistencia completo.
 *            c) Lo mismo ocurría en cargarCancionesDePlaylist() al llenar
 *               la tabla: no había forma de recuperar el objeto Cancion real
 *               a partir de la fila sin buscar otra vez por título.
 *
 *   AHORA: Se mantiene una lista paralela `cancionesActuales` sincronizada
 *          con las filas de la tabla. Al seleccionar la fila N se accede
 *          directamente a cancionesActuales.get(N), sin búsqueda por título
 *          y sin cargas adicionales de disco.
 */
public class ReproductorPanel extends JPanel {

    // ── CAMPOS DE INSTANCIA ────────────────────────────────────────────────────

    private JLabel            lblCancionActual;
    private JLabel            lblArtista;
    private JProgressBar      progressBar;
    private BotonRedondeado   btnPlay;
    private BotonRedondeado   btnNext;
    private BotonRedondeado   btnPrev;
    private DefaultTableModel modeloCanciones;
    private JTable            tablaCanciones;
    private JComboBox<String> comboMisPlaylists;

    private final ReproductorAudio reproductorAudio;
    private final String           username;

    /**
     * FIX #10: lista paralela a las filas de la tabla.
     * cancionesActuales.get(i) == canción correspondiente a la fila i.
     * Sincronizada cada vez que se carga un conjunto de canciones.
     */
    private final List<Cancion> cancionesActuales = new ArrayList<>();

    // ── CONSTRUCTOR ────────────────────────────────────────────────────────────

    public ReproductorPanel() {
        setLayout(null);
        setBackground(Constantes.COLOR_FONDO_MEDIO);
        this.reproductorAudio = new ReproductorAudio();
        this.username = AuthController.getInstancia().getUsuarioActual().getUsername();
        inicializarComponentes();
        cargarMisPlaylists();
    }

    // ── INICIALIZACIÓN DE COMPONENTES ──────────────────────────────────────────

    private void inicializarComponentes() {

        // Título de sección
        JLabel lblTitulo = new JLabel("🎵 Mis Playlists");
        lblTitulo.setFont(Constantes.FUENTE_SUBTITULO);
        lblTitulo.setForeground(Constantes.COLOR_TEXTO);
        lblTitulo.setBounds(30, 15, 200, 30);
        add(lblTitulo);

        // Selector de playlist
        comboMisPlaylists = new JComboBox<>();
        comboMisPlaylists.setBackground(Constantes.COLOR_FONDO_CLARO);
        comboMisPlaylists.setForeground(Constantes.COLOR_TEXTO);
        comboMisPlaylists.setFont(Constantes.FUENTE_NORMAL);
        comboMisPlaylists.setBounds(230, 15, 400, 35);
        comboMisPlaylists.addActionListener(e -> cargarCancionesDePlaylist());
        add(comboMisPlaylists);

        // Botón nueva playlist
        BotonRedondeado btnNuevaPlaylist = new BotonRedondeado("+ Nueva Playlist");
        btnNuevaPlaylist.setBounds(650, 15, 220, 35);
        btnNuevaPlaylist.setColorNormal(Constantes.COLOR_ACENTO);
        btnNuevaPlaylist.setToolTipText("Crear una nueva playlist personal");
        btnNuevaPlaylist.addActionListener(e -> crearNuevaPlaylist());
        add(btnNuevaPlaylist);

        // ── Tabla de canciones ────────────────────────────────────────────────
        String[] columnas = {"♫ Título", "👤 Artista", "⏱ Duración"};
        modeloCanciones = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaCanciones = new JTable(modeloCanciones);
        tablaCanciones.setBackground(Constantes.COLOR_FONDO_CLARO);
        tablaCanciones.setForeground(Constantes.COLOR_TEXTO);
        tablaCanciones.setSelectionBackground(Constantes.COLOR_PRIMARIO);
        tablaCanciones.setRowHeight(35);
        tablaCanciones.setFont(Constantes.FUENTE_NORMAL);
        tablaCanciones.getTableHeader().setBackground(Constantes.COLOR_FONDO_OSCURO);
        tablaCanciones.getTableHeader().setForeground(Constantes.COLOR_TEXTO);
        tablaCanciones.getTableHeader().setFont(Constantes.FUENTE_BOTON);

        // Doble clic para reproducir
        tablaCanciones.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    reproducirSeleccionada();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaCanciones);
        scroll.setBounds(30, 65, 840, 280);
        scroll.setBorder(BorderFactory.createLineBorder(Constantes.COLOR_FONDO_CLARO, 2));
        add(scroll);

        // ── Panel de reproducción ─────────────────────────────────────────────
        JPanel panelReproduccion = new JPanel(null);
        panelReproduccion.setBackground(Constantes.COLOR_FONDO_OSCURO);
        panelReproduccion.setBounds(30, 360, 840, 180);
        panelReproduccion.setBorder(
                BorderFactory.createLineBorder(Constantes.COLOR_PRIMARIO, 2));
        add(panelReproduccion);

        lblCancionActual = new JLabel("♫ Selecciona una canción");
        lblCancionActual.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblCancionActual.setForeground(Constantes.COLOR_TEXTO);
        lblCancionActual.setHorizontalAlignment(SwingConstants.CENTER);
        lblCancionActual.setBounds(0, 30, 840, 30);
        panelReproduccion.add(lblCancionActual);

        lblArtista = new JLabel("");
        lblArtista.setFont(Constantes.FUENTE_NORMAL);
        lblArtista.setForeground(Constantes.COLOR_TEXTO_SECUNDARIO);
        lblArtista.setHorizontalAlignment(SwingConstants.CENTER);
        lblArtista.setBounds(0, 65, 840, 25);
        panelReproduccion.add(lblArtista);

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setBackground(Constantes.COLOR_FONDO_CLARO);
        progressBar.setForeground(Constantes.COLOR_PRIMARIO);
        progressBar.setBounds(100, 105, 640, 10);
        panelReproduccion.add(progressBar);

        // Controles de reproducción
        int centroX = 420;
        int yCtrl   = 130;

        btnPrev = new BotonRedondeado("⏮ Anterior");
        btnPrev.setBounds(centroX - 220, yCtrl, 140, 40);
        btnPrev.setFont(Constantes.FUENTE_BOTON);
        btnPrev.setToolTipText("Canción anterior");
        btnPrev.addActionListener(e -> anterior());
        panelReproduccion.add(btnPrev);

        btnPlay = new BotonRedondeado("▶ Reproducir");
        btnPlay.setBounds(centroX - 70, yCtrl, 140, 40);
        btnPlay.setFont(Constantes.FUENTE_BOTON);
        btnPlay.setColorNormal(Constantes.COLOR_PRIMARIO);
        btnPlay.setToolTipText("Reproducir / Pausar");
        btnPlay.addActionListener(e -> togglePlay());
        panelReproduccion.add(btnPlay);

        btnNext = new BotonRedondeado("Siguiente ⏭");
        btnNext.setBounds(centroX + 80, yCtrl, 140, 40);
        btnNext.setFont(Constantes.FUENTE_BOTON);
        btnNext.setToolTipText("Siguiente canción");
        btnNext.addActionListener(e -> siguiente());
        panelReproduccion.add(btnNext);
    }

    // ── CARGA DE PLAYLISTS ─────────────────────────────────────────────────────

    private void cargarMisPlaylists() {
        comboMisPlaylists.removeAllItems();
        comboMisPlaylists.addItem("-- Todas mis canciones --");

        List<Playlist> misPlaylists =
                SistemaController.getInstancia().obtenerPlaylistsUsuario(username);
        for (Playlist p : misPlaylists) {
            comboMisPlaylists.addItem(p.getNombre());
        }

        if (comboMisPlaylists.getItemCount() > 1) {
            comboMisPlaylists.setSelectedIndex(1);
        } else {
            comboMisPlaylists.setSelectedIndex(0);
        }
    }

    // ── CARGA DE CANCIONES ─────────────────────────────────────────────────────

    /**
     * FIX #10: además de llenar la tabla, se sincroniza cancionesActuales.
     * La posición i en la tabla corresponde exactamente a cancionesActuales.get(i).
     */
    private void cargarCancionesDePlaylist() {
        modeloCanciones.setRowCount(0);
        cancionesActuales.clear(); // ← sincronización de la lista paralela

        String seleccion = (String) comboMisPlaylists.getSelectedItem();
        if (seleccion == null) return;

        List<Cancion> canciones;

        if (seleccion.equals("-- Todas mis canciones --")) {
            canciones = SistemaController.getInstancia().obtenerTodasCanciones();
        } else {
            canciones = new ArrayList<>();
            List<Playlist> playlists =
                    SistemaController.getInstancia().obtenerPlaylistsUsuario(username);
            for (Playlist p : playlists) {
                if (p.getNombre().equals(seleccion)) {
                    canciones = p.getCanciones();
                    break;
                }
            }
        }

        for (Cancion c : canciones) {
            cancionesActuales.add(c); // mantener sincronización con la tabla
            modeloCanciones.addRow(new Object[]{
                c.getTitulo(),
                c.getArtista(),
                c.getDuracionFormateada()
            });
        }
    }

    // ── NUEVA PLAYLIST ─────────────────────────────────────────────────────────

    private void crearNuevaPlaylist() {
        String nombre = JOptionPane.showInputDialog(this,
                "Nombre de la nueva playlist:",
                "Nueva Playlist",
                JOptionPane.QUESTION_MESSAGE);

        if (nombre != null && !nombre.trim().isEmpty()) {
            Playlist nueva = new Playlist(nombre.trim(), username);
            nueva.setEsPublica(false);

            if (SistemaController.getInstancia().crearPlaylist(nueva)) {
                JOptionPane.showMessageDialog(this,
                        "✓ Playlist '" + nombre.trim() + "' creada exitosamente.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarMisPlaylists();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error al crear la playlist.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ── REPRODUCCIÓN ───────────────────────────────────────────────────────────

    /**
     * FIX #10: obtiene la canción directamente de cancionesActuales por índice.
     * Ya no itera toda la base de datos buscando por título, eliminando el bug
     * de canciones con nombres duplicados.
     */
    private void reproducirSeleccionada() {
        int fila = tablaCanciones.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una canción de la lista.");
            return;
        }
        if (fila >= cancionesActuales.size()) {
            // La lista y la tabla están desincronizadas (no debería ocurrir)
            JOptionPane.showMessageDialog(this,
                    "Error interno: lista de canciones desincronizada. "
                  + "Selecciona otra playlist e inténtalo de nuevo.");
            return;
        }

        // FIX #10: acceso O(1) por índice, sin búsqueda por título
        Cancion c = cancionesActuales.get(fila);

        reproductorAudio.reproducir(c.getRutaArchivo());
        ReproductorController.getInstancia().reproducirCancion(c);

        lblCancionActual.setText("♫ " + c.getTitulo());
        lblArtista.setText("👤 " + c.getArtista());
        btnPlay.setText("⏸ Pausar");
    }

    private void togglePlay() {
        if (reproductorAudio.isReproduciendo()) {
            reproductorAudio.pausar();
            btnPlay.setText("▶ Reproducir");
        } else if (reproductorAudio.isPausado()) {
            reproductorAudio.reanudar();
            btnPlay.setText("⏸ Pausar");
        } else {
            reproducirSeleccionada();
        }
    }

    private void siguiente() {
        int filaActual = tablaCanciones.getSelectedRow();
        if (filaActual >= 0 && filaActual < tablaCanciones.getRowCount() - 1) {
            int siguienteFila = filaActual + 1;
            tablaCanciones.setRowSelectionInterval(siguienteFila, siguienteFila);
            reproducirSeleccionada();
        }
    }

    private void anterior() {
        int filaActual = tablaCanciones.getSelectedRow();
        if (filaActual > 0) {
            int anteriorFila = filaActual - 1;
            tablaCanciones.setRowSelectionInterval(anteriorFila, anteriorFila);
            reproducirSeleccionada();
        }
    }
}