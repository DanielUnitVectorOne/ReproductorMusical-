package com.reproductor.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.reproductor.model.Cancion;
import com.reproductor.model.Playlist;
import java.io.FileOutputStream;
import java.util.List;

public class GeneradorPDF {
    
    public static boolean generarReporteUsuario(String username, List<Cancion> topCanciones, 
                                                 List<Playlist> playlists, String rutaSalida) {
        try {
            Document documento = new Document(PageSize.A4);
            PdfWriter.getInstance(documento, new FileOutputStream(rutaSalida));
            documento.open();
            
            // Título
            Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BaseColor.BLACK);
            Paragraph titulo = new Paragraph("Reporte Personal - " + username, fuenteTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            documento.add(titulo);
            
            // Fecha
            Font fuenteNormal = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.DARK_GRAY);
            Paragraph fecha = new Paragraph("Generado: " + new java.util.Date(), fuenteNormal);
            fecha.setAlignment(Element.ALIGN_CENTER);
            fecha.setSpacingAfter(30);
            documento.add(fecha);
            
            // Top Canciones
            Font fuenteSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);
            Paragraph subtitulo1 = new Paragraph("Top Canciones Más Escuchadas", fuenteSubtitulo);
            subtitulo1.setSpacingAfter(15);
            documento.add(subtitulo1);
            
            PdfPTable tablaCanciones = new PdfPTable(3);
            tablaCanciones.setWidthPercentage(100);
            tablaCanciones.setSpacingAfter(25);
            
            // Encabezados
            agregarCeldaEncabezado(tablaCanciones, "Título");
            agregarCeldaEncabezado(tablaCanciones, "Artista");
            agregarCeldaEncabezado(tablaCanciones, "Reproducciones");
            
            // Datos
            for (Cancion c : topCanciones) {
                tablaCanciones.addCell(c.getTitulo());
                tablaCanciones.addCell(c.getArtista());
                tablaCanciones.addCell(String.valueOf(c.getVecesReproducida()));
            }
            
            documento.add(tablaCanciones);
            
            // Playlists
            Paragraph subtitulo2 = new Paragraph("Mis Playlists", fuenteSubtitulo);
            subtitulo2.setSpacingAfter(15);
            documento.add(subtitulo2);
            
            PdfPTable tablaPlaylists = new PdfPTable(2);
            tablaPlaylists.setWidthPercentage(100);
            
            agregarCeldaEncabezado(tablaPlaylists, "Nombre");
            agregarCeldaEncabezado(tablaPlaylists, "Canciones");
            
            for (Playlist p : playlists) {
                tablaPlaylists.addCell(p.getNombre());
                tablaPlaylists.addCell(String.valueOf(p.getCantidadCanciones()));
            }
            
            documento.add(tablaPlaylists);
            
            documento.close();
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean generarReporteGeneral(List<Cancion> topCanciones, 
                                                 List<Playlist> topPlaylists, String rutaSalida) {
        try {
            Document documento = new Document(PageSize.A4);
            PdfWriter.getInstance(documento, new FileOutputStream(rutaSalida));
            documento.open();
            
            Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BaseColor.BLACK);
            Paragraph titulo = new Paragraph("Reporte General del Sistema", fuenteTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(30);
            documento.add(titulo);
            
            // Top Canciones Global
            Font fuenteSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);
            Paragraph sub1 = new Paragraph("Top 10 Canciones Más Reproducidas", fuenteSubtitulo);
            sub1.setSpacingAfter(15);
            documento.add(sub1);
            
            PdfPTable tabla1 = new PdfPTable(4);
            tabla1.setWidthPercentage(100);
            tabla1.setSpacingAfter(25);
            
            agregarCeldaEncabezado(tabla1, "#");
            agregarCeldaEncabezado(tabla1, "Título");
            agregarCeldaEncabezado(tabla1, "Artista");
            agregarCeldaEncabezado(tabla1, "Reproducciones");
            
            int posicion = 1;
            for (Cancion c : topCanciones) {
                tabla1.addCell(String.valueOf(posicion++));
                tabla1.addCell(c.getTitulo());
                tabla1.addCell(c.getArtista());
                tabla1.addCell(String.valueOf(c.getVecesReproducida()));
            }
            
            documento.add(tabla1);
            
            // Top Playlists
            Paragraph sub2 = new Paragraph("Playlists Más Populares", fuenteSubtitulo);
            sub2.setSpacingAfter(15);
            documento.add(sub2);
            
            PdfPTable tabla2 = new PdfPTable(3);
            tabla2.setWidthPercentage(100);
            
            agregarCeldaEncabezado(tabla2, "Nombre");
            agregarCeldaEncabezado(tabla2, "Canciones");
            agregarCeldaEncabezado(tabla2, "Reproducciones");
            
            for (Playlist p : topPlaylists) {
                tabla2.addCell(p.getNombre());
                tabla2.addCell(String.valueOf(p.getCantidadCanciones()));
                tabla2.addCell(String.valueOf(p.getVecesReproducida()));
            }
            
            documento.add(tabla2);
            
            documento.close();
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private static void agregarCeldaEncabezado(PdfPTable tabla, String texto) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, 
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
        celda.setBackgroundColor(new BaseColor(30, 215, 96));
        celda.setPadding(8);
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        tabla.addCell(celda);
    }
}