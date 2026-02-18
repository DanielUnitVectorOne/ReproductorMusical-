package com.reproductor.view;

import com.reproductor.controller.AuthController;
import com.reproductor.controller.SistemaController;
import com.reproductor.model.Cancion;
import com.reproductor.model.Playlist;
import com.reproductor.utils.*;
import java.awt.*;
import java.io.File;
import java.util.List;
import javax.swing.*;

/**
 * Panel interno de reportes y estadísticas.
 *
 * FIX #3 — NullPointerException al generar PDF:
 *   ANTES: generarPDF() pasaba null como parámetro de playlists:
 *
 *       GeneradorPDF.generarReporteGeneral(top, null, ruta);
 *       GeneradorPDF.generarReporteUsuario(username, top, null, ruta);
 *
 *          GeneradorPDF itera esas listas con un for-each, lo que
 *          lanzaba NullPointerException en el momento de pulsar el
 *          botón "Descargar PDF", haciendo que la función no sirviera.
 *
 *   AHORA:
 *       - Para el admin se pasan las playlists públicas del sistema.
 *       - Para el usuario se pasan sus playlists personales.
 *       - Se verifica el resultado del método y se muestra un mensaje
 *         de error si GeneradorPDF devuelve false.
 *       - Se añade el import de Playlist (faltaba) y de AuthController.
 */
public class PanelReportes extends JInternalFrame {

    private final boolean esAdmin;

    public PanelReportes(boolean esAdmin) {
        super("Reportes y Estadísticas", false, true, false, false);
        this.esAdmin = esAdmin;
        setSize(900, 600);
        setLocation(20, 20);
        inicializarComponentes();
    }

    // ── COMPONENTES ────────────────────────────────────────────────────────────

    private void inicializarComponentes() {
        JPanel panel = new JPanel(null);
        panel.setBackground(Constantes.COLOR_FONDO_MEDIO);

        // Título
        JLabel lblTitulo = new JLabel(esAdmin ? "Reportes Generales" : "Mis Reportes");
        lblTitulo.setFont(Constantes.FUENTE_TITULO);
        lblTitulo.setForeground(Constantes.COLOR_TEXTO);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBounds(0, 30, 900, 40);
        panel.add(lblTitulo);

        // ── Top canciones ─────────────────────────────────────────────────────
        JLabel lblTop = new JLabel("Top 10 Canciones Más Escuchadas");
        lblTop.setFont(Constantes.FUENTE_SUBTITULO);
        lblTop.setForeground(Constantes.COLOR_TEXTO);
        lblTop.setBounds(50, 100, 400, 30);
        panel.add(lblTop);

        JTextArea txtTop = new JTextArea();
        txtTop.setEditable(false);
        txtTop.setBackground(Constantes.COLOR_FONDO_CLARO);
        txtTop.setForeground(Constantes.COLOR_TEXTO);
        txtTop.setFont(Constantes.FUENTE_NORMAL);

        List<Cancion> top = SistemaController.getInstancia().obtenerTopCanciones(10);
        StringBuilder sb  = new StringBuilder();
        int pos = 1;
        for (Cancion c : top) {
            sb.append(pos++).append(". ")
              .append(c.getTitulo()).append(" — ")
              .append(c.getArtista()).append("  (")
              .append(c.getVecesReproducida()).append(" reproducciones)\n");
        }
        if (sb.length() == 0) {
            sb.append("Aún no hay canciones reproducidas.");
        }
        txtTop.setText(sb.toString());

        JScrollPane scroll = new JScrollPane(txtTop);
        scroll.setBounds(50, 140, 800, 290);
        panel.add(scroll);

        // ── Botón PDF ─────────────────────────────────────────────────────────
        BotonRedondeado btnPDF = new BotonRedondeado("📄 Descargar PDF");
        btnPDF.setBounds(350, 460, 200, 50);
        btnPDF.addActionListener(e -> generarPDF());
        panel.add(btnPDF);

        add(panel);
    }

    // ── GENERACIÓN DE PDF ──────────────────────────────────────────────────────

    private void generarPDF() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar Reporte PDF");
        chooser.setSelectedFile(new File("reporte.pdf"));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return; // el usuario canceló
        }

        String ruta = chooser.getSelectedFile().getAbsolutePath();
        if (!ruta.toLowerCase().endsWith(".pdf")) {
            ruta += ".pdf";
        }

        // FIX #3: obtener las listas reales en lugar de pasar null
        List<Cancion> topCanciones = SistemaController.getInstancia().obtenerTopCanciones(10);
        boolean exito;

        if (esAdmin) {
            // Para el admin: playlists públicas del sistema
            List<Playlist> topPlaylists =
                    SistemaController.getInstancia().obtenerPlaylistsPublicas();

            exito = GeneradorPDF.generarReporteGeneral(topCanciones, topPlaylists, ruta);

        } else {
            // Para el usuario: sus playlists personales
            String username = AuthController.getInstancia().getUsuarioActual().getUsername();
            List<Playlist> misPlaylists =
                    SistemaController.getInstancia().obtenerPlaylistsUsuario(username);

            exito = GeneradorPDF.generarReporteUsuario(username, topCanciones, misPlaylists, ruta);
        }

        if (exito) {
            JOptionPane.showMessageDialog(this,
                    "✓ PDF generado correctamente en:\n" + ruta,
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "❌ Error al generar el PDF.\n"
                  + "Verifica que la librería iText esté en el classpath.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}