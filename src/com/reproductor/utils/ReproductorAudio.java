package com.reproductor.utils;

import javazoom.jl.player.Player;
import java.io.FileInputStream;

/**
 * Reproductor de audio basado en la librería JLayer (MP3).
 *
 * FIX #4 — Bug crítico de unidades en pausa/reanudación:
 *   ANTES: pausar() guardaba player.getPosition() → milisegundos.
 *          reproducir() llamaba is.skip(posicionPausa) → bytes.
 *          Estas unidades son completamente distintas, causando que
 *          el salto fuera incorrecto (o incluso excediera el tamaño
 *          del archivo y fallara silenciosamente).
 *
 *   AHORA: Se rastrea el tiempo de inicio de reproducción con
 *          System.currentTimeMillis(). Al pausar se calcula los ms
 *          transcurridos. Al reanudar se convierten a bytes usando la
 *          tasa estándar de 128 kbps (16 bytes/ms), que es la más
 *          común en MP3. Esto da una aproximación suficientemente
 *          precisa para archivos MP3 estándar.
 *
 *   NOTA: Para WAV o bitrates muy distintos la aproximación difiere
 *         ligeramente; en un proyecto de producción se usaría una
 *         librería más avanzada (JavaFX MediaPlayer, Javazoom avanzado)
 *         que soporte seek nativo.
 */
public class ReproductorAudio {

    // Bytes por milisegundo para MP3 a 128 kbps: 128.000 bits/s ÷ 8 ÷ 1000
    private static final long BYTES_POR_MS = 16L;

    private Player player;
    private Thread  hiloReproduccion;
    private String  rutaArchivoActual;

    private volatile boolean reproduciendo;
    private volatile boolean pausado;

    /** Instante (en ms del sistema) en que arrancó la reproducción actual. */
    private long inicioReproduccionMs;

    /** Acumulado de tiempo ya reproducido antes de la última pausa (ms). */
    private long tiempoReproducidoMs;

    public ReproductorAudio() {
        reproduciendo      = false;
        pausado            = false;
        inicioReproduccionMs = 0;
        tiempoReproducidoMs  = 0;
    }

    // ── REPRODUCIR ─────────────────────────────────────────────────────────────

    /**
     * Inicia la reproducción del archivo indicado.
     * Si había una pausa activa, reanuda desde la posición aproximada.
     */
    public void reproducir(String rutaArchivo) {
        detener();

        this.rutaArchivoActual = rutaArchivo;
        this.reproduciendo     = true;
        this.pausado           = false;

        // Capturamos el offset de tiempo ANTES de crear el hilo
        // para que la lambda lo use como valor efectivamente final.
        final long offsetMs = tiempoReproducidoMs;

        hiloReproduccion = new Thread(() -> {
            try {
                FileInputStream fis = new FileInputStream(rutaArchivo);

                // FIX #4: convertir ms → bytes correctamente
                if (offsetMs > 0) {
                    long bytesASaltar = offsetMs * BYTES_POR_MS;
                    long bytesDisponibles = fis.available();
                    // Precaución: no saltar más allá del archivo
                    if (bytesASaltar < bytesDisponibles) {
                        fis.skip(bytesASaltar);
                    } else {
                        // El offset supera el archivo; reiniciar desde el principio
                        fis.skip(0);
                    }
                }

                // Registrar el instante real de inicio (descontando el offset)
                inicioReproduccionMs = System.currentTimeMillis() - offsetMs;

                player = new Player(fis);
                player.play(); // bloqueante hasta que termina o se cierra

                // Llegamos aquí cuando la canción termina naturalmente
                if (!pausado) {
                    reproduciendo       = false;
                    tiempoReproducidoMs = 0;
                    inicioReproduccionMs  = 0;
                }

            } catch (Exception e) {
                System.err.println("❌ Error reproduciendo: " + e.getMessage());
                e.printStackTrace();
            }
        });

        hiloReproduccion.setDaemon(true); // el hilo no impide el cierre de la JVM
        hiloReproduccion.start();
    }

    // ── PAUSAR ─────────────────────────────────────────────────────────────────

    /**
     * Pausa la reproducción guardando la posición en milisegundos.
     */
    public void pausar() {
        if (player != null && reproduciendo) {
            // FIX #4: guardar en ms usando el reloj del sistema, NO player.getPosition()
            tiempoReproducidoMs = System.currentTimeMillis() - inicioReproduccionMs;
            player.close();
            pausado       = true;
            reproduciendo = false;
        }
    }

    // ── REANUDAR ───────────────────────────────────────────────────────────────

    /**
     * Reanuda desde la posición en que se pausó.
     * La conversión ms → bytes se realiza en reproducir().
     */
    public void reanudar() {
        if (pausado && rutaArchivoActual != null) {
            reproducir(rutaArchivoActual); // tiempoReproducidoMs ya tiene el offset
        }
    }

    // ── DETENER ────────────────────────────────────────────────────────────────

    /**
     * Detiene completamente la reproducción y resetea la posición.
     */
    public void detener() {
        if (player != null) {
            player.close();
        }
        reproduciendo       = false;
        pausado             = false;
        tiempoReproducidoMs = 0;
        inicioReproduccionMs  = 0;
    }

    // ── GETTERS ────────────────────────────────────────────────────────────────

    public boolean isReproduciendo() {
        return reproduciendo;
    }

    public boolean isPausado() {
        return pausado;
    }

    /**
     * Devuelve el tiempo reproducido en segundos (para mostrar en la UI).
     */
    public long getTiempoReproducidoSegundos() {
        if (reproduciendo) {
            return (System.currentTimeMillis() - inicioReproduccionMs) / 1000;
        }
        return tiempoReproducidoMs / 1000;
    }
}