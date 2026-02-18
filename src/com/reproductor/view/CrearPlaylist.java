package com.reproductor.view;

import com.reproductor.controller.AuthController;
import com.reproductor.controller.SistemaController;
import com.reproductor.model.Playlist;
import com.reproductor.model.Cancion;
import com.reproductor.utils.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.List;

/**
 * Panel para crear playlists nuevas.
 *
 * FIX #5 — agregarCancion() vs actualizarCancion() al editar:
 *   ANTES: editarCancionSeleccionada() modificaba título/artista en
 *          el objeto Cancion y luego llamaba a
 *          SistemaController.agregarCancion(cancion). Esto funcionaba
 *          de casualidad porque el DAO hace upsert, pero semánticamente
 *          es incorrecto: "agregar" implica que la canción no existe;
 *          "actualizar" transmite la intención real.
 *
 *   AHORA: se llama a SistemaController.actualizarCancion(id, titulo,
 *          artista), que es el método específico para edición y hace
 *          explícita la intención. Además, se valida que los campos
 *          no queden vacíos antes de guardar.
 */
public class CrearPlaylist extends JPanel {

    private final boolean           esAdmin;
    private final PanelPersonalizado panelPersonalizado;

    private CampoTextoModerno   txtNombre;
    private JTextArea           txtDescripcion;
    private DefaultTableModel   modeloDisponibles;
    private DefaultTableModel   modeloSeleccionadas;
    private JTable              tablaDisponibles;
    private JTable              tablaSeleccionadas;

    public CrearPlaylist(boolean esAdmin, PanelPersonalizado panelPersonalizado) {
        this.esAdmin            = esAdmin;
        this.panelPersonalizado = panelPersonalizado;
        setLayout(null);
        setBackground(Constantes.COLOR_FONDO_MEDIO);
        inicializarComponentes();
        cargarCancionesDisponibles();
    }

    // ── COMPONENTES ────────────────────────────────────────────────────────────

    private void inicializarComponentes() {

        // Subtítulo
        JLabel lblSubtitulo = new JLabel("Completa la información de la playlist");
        lblSubtitulo.setFont(Constantes.FUENTE_NORMAL);
        lblSubtitulo.setForeground(Constantes.COLOR_TEXTO_SECUNDARIO);
        lblSubtitulo.setBounds(30, 10, 400, 25);
        add(lblSubtitulo);

        // ── Nombre ────────────────────────────────────────────────────────────
        JLabel lblNombre = new JLabel("Nombre de la Playlist:");
        lblNombre.setForeground(Constantes.COLOR_TEXTO);
        lblNombre.setFont(Constantes.FUENTE_NORMAL);
        lblNombre.setBounds(30, 45, 200, 25);
        add(lblNombre);

        txtNombre = new CampoTextoModerno("Ej: Rock Clásico");
        txtNombre.setBounds(30, 70, 400, 35);
        add(txtNombre);

        // ── Descripción ───────────────────────────────────────────────────────
        JLabel lblDesc = new JLabel("Descripción (opcional):");
        lblDesc.setForeground(Constantes.COLOR_TEXTO);
        lblDesc.setFont(Constantes.FUENTE_NORMAL);
        lblDesc.setBounds(450, 45, 200, 25);
        add(lblDesc);

        txtDescripcion = new JTextArea();
        txtDescripcion.setBackground(Constantes.COLOR_FONDO_CLARO);
        txtDescripcion.setForeground(Constantes.COLOR_TEXTO);
        txtDescripcion.setFont(Constantes.FUENTE_NORMAL);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        scrollDesc.setBounds(450, 70, 470, 60);
        add(scrollDesc);

        // ── Tabla canciones disponibles ───────────────────────────────────────
        JLabel lblDisponibles = new JLabel("🎵 Biblioteca de Música");
        lblDisponibles.setFont(Constantes.FUENTE_SUBTITULO);
        lblDisponibles.setForeground(Constantes.COLOR_TEXTO);
        lblDisponibles.setBounds(30, 145, 300, 25);
        add(lblDisponibles);

        String[] columnasDisp = {"Título", "Artista", "Archivo"};
        modeloDisponibles = new DefaultTableModel(columnasDisp, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaDisponibles = new JTable(modeloDisponibles);
        tablaDisponibles.setBackground(Constantes.COLOR_FONDO_CLARO);
        tablaDisponibles.setForeground(Constantes.COLOR_TEXTO);
        tablaDisponibles.setSelectionBackground(Constantes.COLOR_PRIMARIO);
        tablaDisponibles.setRowHeight(28);
        tablaDisponibles.getTableHeader().setBackground(Constantes.COLOR_FONDO_OSCURO);
        tablaDisponibles.getTableHeader().setForeground(Constantes.COLOR_TEXTO);
        tablaDisponibles.getColumnModel().getColumn(2).setPreferredWidth(150);

        JScrollPane scrollDisp = new JScrollPane(tablaDisponibles);
        scrollDisp.setBounds(30, 175, 380, 280);
        add(scrollDisp);

        // ── Botones centrales ─────────────────────────────────────────────────
        BotonRedondeado btnAgregar = new BotonRedondeado("➤");
        btnAgregar.setBounds(425, 260, 50, 50);
        btnAgregar.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btnAgregar.setToolTipText("Agregar canción a la playlist");
        btnAgregar.addActionListener(e -> agregarCancion());
        add(btnAgregar);

        BotonRedondeado btnQuitar = new BotonRedondeado("◄");
        btnQuitar.setBounds(425, 320, 50, 50);
        btnQuitar.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btnQuitar.setColorNormal(Constantes.COLOR_ERROR);
        btnQuitar.setToolTipText("Quitar canción de la playlist");
        btnQuitar.addActionListener(e -> quitarCancion());
        add(btnQuitar);

        // ── Tabla canciones seleccionadas ─────────────────────────────────────
        JLabel lblSeleccionadas = new JLabel("📋 Canciones en la Playlist");
        lblSeleccionadas.setFont(Constantes.FUENTE_SUBTITULO);
        lblSeleccionadas.setForeground(Constantes.COLOR_TEXTO);
        lblSeleccionadas.setBounds(490, 145, 300, 25);
        add(lblSeleccionadas);

        String[] columnasSel = {"Título", "Artista"};
        modeloSeleccionadas = new DefaultTableModel(columnasSel, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaSeleccionadas = new JTable(modeloSeleccionadas);
        tablaSeleccionadas.setBackground(Constantes.COLOR_FONDO_CLARO);
        tablaSeleccionadas.setForeground(Constantes.COLOR_TEXTO);
        tablaSeleccionadas.setSelectionBackground(Constantes.COLOR_PRIMARIO);
        tablaSeleccionadas.setRowHeight(28);
        tablaSeleccionadas.getTableHeader().setBackground(Constantes.COLOR_FONDO_OSCURO);
        tablaSeleccionadas.getTableHeader().setForeground(Constantes.COLOR_TEXTO);

        JScrollPane scrollSel = new JScrollPane(tablaSeleccionadas);
        scrollSel.setBounds(490, 175, 430, 280);
        add(scrollSel);

        // ── Botones inferiores ────────────────────────────────────────────────
        BotonRedondeado btnAgregarArchivos = new BotonRedondeado("📁 Agregar Archivos MP3");
        btnAgregarArchivos.setBounds(30, 470, 200, 40);
        btnAgregarArchivos.setColorNormal(Constantes.COLOR_ACENTO);
        btnAgregarArchivos.setToolTipText("Buscar archivos MP3 en tu computadora");
        btnAgregarArchivos.addActionListener(e -> agregarArchivosMP3());
        add(btnAgregarArchivos);

        BotonRedondeado btnEditarCancion = new BotonRedondeado("✏️ Editar Info");
        btnEditarCancion.setBounds(240, 470, 170, 40);
        btnEditarCancion.setColorNormal(Constantes.COLOR_FONDO_CLARO);
        btnEditarCancion.setToolTipText("Editar nombre y artista de la canción seleccionada");
        btnEditarCancion.addActionListener(e -> editarCancionSeleccionada());
        add(btnEditarCancion);

        BotonRedondeado btnCrear = new BotonRedondeado("✓ Crear Playlist");
        btnCrear.setBounds(620, 470, 180, 45);
        btnCrear.setToolTipText("Guardar la playlist");
        btnCrear.addActionListener(e -> crearPlaylist());
        add(btnCrear);

        BotonRedondeado btnCancelar = new BotonRedondeado("✗ Cancelar", Constantes.COLOR_ERROR);
        btnCancelar.setBounds(810, 470, 110, 45);
        btnCancelar.setToolTipText("Cancelar y cerrar");
        btnCancelar.addActionListener(e -> cerrarPanel());
        add(btnCancelar);
    }

    // ── AGREGAR ARCHIVOS MP3 ───────────────────────────────────────────────────

    private void agregarArchivosMP3() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar archivos MP3");
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(
                new FileNameExtensionFilter("Archivos de Audio (*.mp3, *.wav)", "mp3", "wav"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File[] archivos = fileChooser.getSelectedFiles();
            int agregados = 0;

            for (File archivo : archivos) {
                String nombreArchivo = archivo.getName();
                String titulo = nombreArchivo.substring(0, nombreArchivo.lastIndexOf('.'));

                Cancion nuevaCancion = new Cancion(titulo, "Desconocido", archivo.getAbsolutePath());
                nuevaCancion.setDuracionSegundos(180); // valor por defecto

                if (SistemaController.getInstancia().agregarCancion(nuevaCancion)) {
                    agregados++;
                }
            }

            cargarCancionesDisponibles();
            JOptionPane.showMessageDialog(this,
                    agregados + " archivo(s) nuevo(s) agregado(s) a la biblioteca.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ── EDITAR CANCIÓN SELECCIONADA ────────────────────────────────────────────

    private void editarCancionSeleccionada() {
        int fila = tablaDisponibles.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una canción para editar.");
            return;
        }

        String tituloActual  = (String) modeloDisponibles.getValueAt(fila, 0);
        String artistaActual = (String) modeloDisponibles.getValueAt(fila, 1);

        // Buscar la canción para obtener su ID
        List<Cancion> canciones = SistemaController.getInstancia().obtenerTodasCanciones();
        Cancion cancionAEditar  = null;
        for (Cancion c : canciones) {
            if (c.getTitulo().equals(tituloActual)) {
                cancionAEditar = c;
                break;
            }
        }

        if (cancionAEditar == null) {
            JOptionPane.showMessageDialog(this, "No se encontró la canción en la base de datos.");
            return;
        }

        // Capturamos el ID antes del lambda (debe ser effectively final)
        final String idCancion = cancionAEditar.getId();

        // ── Diálogo de edición ────────────────────────────────────────────────
        JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), "Editar Canción", true);
        dialog.setSize(450, 260);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(null);
        dialog.getContentPane().setBackground(Constantes.COLOR_FONDO_MEDIO);

        JLabel lblTit = new JLabel("Título:");
        lblTit.setForeground(Constantes.COLOR_TEXTO);
        lblTit.setBounds(30, 30, 100, 25);
        dialog.add(lblTit);

        CampoTextoModerno txtTitulo = new CampoTextoModerno("");
        txtTitulo.setText(tituloActual);
        txtTitulo.setBounds(30, 55, 390, 35);
        dialog.add(txtTitulo);

        JLabel lblArt = new JLabel("Artista:");
        lblArt.setForeground(Constantes.COLOR_TEXTO);
        lblArt.setBounds(30, 100, 100, 25);
        dialog.add(lblArt);

        CampoTextoModerno txtArtista = new CampoTextoModerno("");
        txtArtista.setText(artistaActual);
        txtArtista.setBounds(30, 125, 390, 35);
        dialog.add(txtArtista);

        BotonRedondeado btnGuardar = new BotonRedondeado("💾 Guardar");
        btnGuardar.setBounds(125, 185, 200, 40);
        btnGuardar.addActionListener(e -> {
            String nuevoTitulo  = txtTitulo.getText().trim();
            String nuevoArtista = txtArtista.getText().trim();

            // Validación: campos no vacíos
            if (nuevoTitulo.isEmpty() || nuevoArtista.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "El título y el artista no pueden estar vacíos.");
                return;
            }

            // FIX #5: usar actualizarCancion() en lugar de agregarCancion()
            //         para expresar la intención correcta de EDICIÓN.
            boolean ok = SistemaController.getInstancia()
                    .actualizarCancion(idCancion, nuevoTitulo, nuevoArtista);

            if (ok) {
                cargarCancionesDisponibles();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "✓ Canción actualizada correctamente.");
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Error al actualizar la canción.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.add(btnGuardar);

        dialog.setVisible(true);
    }

    // ── CARGA Y MANIPULACIÓN DE TABLA ─────────────────────────────────────────

    private void cargarCancionesDisponibles() {
        modeloDisponibles.setRowCount(0);
        List<Cancion> canciones = SistemaController.getInstancia().obtenerTodasCanciones();

        for (Cancion c : canciones) {
            String nombreArchivo = new File(c.getRutaArchivo()).getName();
            modeloDisponibles.addRow(new Object[]{
                c.getTitulo(),
                c.getArtista(),
                nombreArchivo
            });
        }
    }

    private void agregarCancion() {
        int fila = tablaDisponibles.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una canción de la biblioteca.");
            return;
        }

        String titulo  = (String) modeloDisponibles.getValueAt(fila, 0);
        String artista = (String) modeloDisponibles.getValueAt(fila, 1);

        // Verificar duplicado en la lista de seleccionadas
        for (int i = 0; i < modeloSeleccionadas.getRowCount(); i++) {
            if (modeloSeleccionadas.getValueAt(i, 0).equals(titulo)) {
                JOptionPane.showMessageDialog(this, "Esta canción ya está en la playlist.");
                return;
            }
        }

        modeloSeleccionadas.addRow(new Object[]{titulo, artista});
    }

    private void quitarCancion() {
        int fila = tablaSeleccionadas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una canción para quitar.");
            return;
        }
        modeloSeleccionadas.removeRow(fila);
    }

    // ── CREAR PLAYLIST ─────────────────────────────────────────────────────────

    private void crearPlaylist() {
        String nombre      = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un nombre para la playlist.");
            return;
        }

        if (modeloSeleccionadas.getRowCount() == 0) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Crear playlist vacía?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
        }

        String   username = AuthController.getInstancia().getUsuarioActual().getUsername();
        Playlist playlist = new Playlist(nombre, descripcion, esAdmin, username);

        // Asociar canciones seleccionadas (búsqueda por título → obtener objeto completo)
        List<Cancion> todasCanciones = SistemaController.getInstancia().obtenerTodasCanciones();
        for (int i = 0; i < modeloSeleccionadas.getRowCount(); i++) {
            String titulo = (String) modeloSeleccionadas.getValueAt(i, 0);
            for (Cancion c : todasCanciones) {
                if (c.getTitulo().equals(titulo)) {
                    playlist.agregarCancion(c);
                    break;
                }
            }
        }

        if (SistemaController.getInstancia().crearPlaylist(playlist)) {
            JOptionPane.showMessageDialog(this,
                    "✓ Playlist '" + nombre + "' creada con "
                    + playlist.getCantidadCanciones() + " canción(es).");
            cerrarPanel();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar la playlist.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── CERRAR PANEL ───────────────────────────────────────────────────────────

    private void cerrarPanel() {
        Container parent = getParent();
        if (parent != null) {
            parent.remove(this);
            parent.revalidate();
            parent.repaint();
        }
    }
}