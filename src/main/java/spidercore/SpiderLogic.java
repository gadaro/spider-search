/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spidercore;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import spiderdb.SpiderDataBaseElement;
import spiderdb.SpiderDataBaseInit;
import spidergui.SpiderFrame;

/**
 *
 * @author gdrodriguez
 */
public class SpiderLogic {

    SpiderDataBaseElement element = new SpiderDataBaseElement();
    SpiderFrame mainFrame = SpiderFrame.getInstance(); //Si se activa no funciona el dispose...
    
    static final int ID_0 = 2;
    static final String NOT_VALID_ID = "NOT_VALID_ID";

    /**
     * Bucle de lógica principal, inicia la BBDD y revisa las películas.
     * @param depth Profundidad de peticiones, número de películas a indexar.
     */
    public void execute(int depth) {

        SpiderDataBaseInit bbddinit = new SpiderDataBaseInit();

        int start = bbddinit.checkLastElement();

        // Si devuelve 0, la base de datos está recién creada o hubo un error, se comienza desde ID_0.
        // Se aumenta uno al start para que continúe con el registro siguiente al último insertado.
        if (start == 0) {
            start = ID_0;
        } else {
            start++;
        }

        int end = start + depth;

        for (int id = start; id < end; id++) {

            String url = "http://www.mejortorrent.com/secciones.php?sec=descargas&ap=contar&tabla=peliculas&id="
                    + id + "&link_bajar=1";

            // Cargar los datos
            element.setId(id + "");
            getInfo(url, 60000);
            getInfo("http://www.mejortorrent.com/peli-descargar-torrent-" + id + "-a.html", 60000);

            // Insertar el registro
            if (!element.getName().equals(NOT_VALID_ID)) {
                bbddinit.insertElement(element);
                downloadTorrentFile(element.getLink(), element.getName());
                //mainFrame.addStatusAreaText("Registro insertado: " + id + "\n" );
            }

        }
    }

    /**
     * Método que parsea las diferentes páginas utilizando Jsoup y obtiene la 
     * información necesaria de cada película y la almacena en una instancia de
     * la clase SpiderDataBaseElement.
     * @param url Dirección a parsear.
     * @param timeout Tiempo de espera antes de dar el link por caído.
     */
    private void getInfo(String url, int timeout) {

        Document doc = null;
        Elements pelicula = null;

        //  The default timeout is 3 seconds (3000 millis)
        try {
            doc = Jsoup.connect(url)
                    .timeout(timeout)
                    .get();
        } catch (SocketTimeoutException timeOutEx) {
            // mainFrame.setStatusBarText("Se ha superado el tiempo de espera");
            timeOutEx.printStackTrace();
        } catch (IOException ioEx) {
            // mainFrame.setStatusBarText("Error al obtener el documento");
            ioEx.printStackTrace();
        }

        if (doc.body().text().contains("Too many connections")) {
            // mainFrame.setStatusBarText("La base de datos de la página está colapsada");
        } else if (url.contains("secciones.php")) {
            pelicula = doc.select("[href*=/uploads/]");
            // System.out.println("link: " + pelicula.attr("href"));
            element.setLink("www.mejortorrent.com" + pelicula.attr("href"));
        } else {
            // Parsear el td
            pelicula = doc.select("td").eq(52);
            String[] bloque_1 = pelicula.html().split("</b>");

            //System.out.println("Tamaño a examinar: " + bloque_1.length);
            if (bloque_1.length > 1) {
                if (bloque_1[8].contains("DVDRip")) {
                    element.setQuality("DVDRip");
                } else if (bloque_1[8].contains("DVDscreener")) {
                    element.setQuality("DVDscreener");
                } else {
                    // mainFrame.setStatusBarText("Formato no reconocido: " + bloque_1[8]);
                    element.setQuality("Unknown");
                }

                String[] bloque_2 = bloque_1[0].split(">");
                // System.out.println("name: " + bloque_2[2]);
                element.setName(bloque_2[2]);
                String[] bloque_3 = bloque_1[13].split("<");
                // System.out.println("size: " + bloque_3[0].substring(7));
                element.setSize(bloque_3[0].substring(7));
            } else {
                // mainFrame.setStatusBarText("Registro corrupto en la página");
                element.setName(NOT_VALID_ID);
            }
        }
    }
    
    /**
     * Método que descarga el fichero torrent y lo almacena localmente en el 
     * directorio especificado en la pestaña de Configuración.
     * @param url Dirección web final del fichero torrent.
     * @param name Nombre de la película, posteriormente se utiliza para renombrar 
     * el fichero torrent a minúsculas y sin espacios.
     */
    void downloadTorrentFile (String url, String name) {
        try {
            URL direccion = new URL("http://" + url);
            try {
                ReadableByteChannel rbc = Channels.newChannel(direccion.openStream());
                FileOutputStream fos = new FileOutputStream(mainFrame.getGuiConfigPath().getText() +
                        "\\" + name.replaceAll(" ", "_").toLowerCase() + ".torrent");
                fos.getChannel().transferFrom(rbc, 0, Integer.MAX_VALUE);
                fos.close();
                rbc.close();
            } catch (IOException ex) {
                Logger.getLogger(SpiderLogic.class.getName()).log(Level.SEVERE, null, ex);
            }            
        } catch (MalformedURLException ex) {
            Logger.getLogger(SpiderLogic.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
