package spidercore;

import java.io.IOException;
import java.net.SocketTimeoutException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import spiderdb.SpiderDataBaseElement;
import spiderdb.SpiderDataBaseInit;

/**
 * 
 * @author Gabe
 *
 */
public class SpiderMain {
	
	static SpiderDataBaseElement elemento = new SpiderDataBaseElement();
	static final int PROFUNDIDAD = 50;
	static final int ID_0 = 2;
	static final String NOT_VALID_ID = "NOT_VALID_ID";
	
	public static void main(String[] args) {
		
		SpiderDataBaseInit bbddinit = new SpiderDataBaseInit();
		
		int comienzo = bbddinit.checkLastElement();
		
		// Si devuelve 0, la base de datos está recién creada o hubo un error, se comienza desde ID_0.
		// Se aumenta uno al comienzo para que continúe con el registro siguiente al último insertado.
		if ( comienzo == 0 )
			comienzo = ID_0;
		else
			comienzo ++;
		
		int fin = comienzo + PROFUNDIDAD;
		
		for ( int id = comienzo; id < fin; id++ ) {
			
			String url = "http://www.mejortorrent.com/secciones.php?sec=descargas&ap=contar&tabla=peliculas&id="
					+ id + "&link_bajar=1";
			
			// Cargar los datos
			elemento.setId(id + "");
			obtenerInfo(url, 60000);
			obtenerInfo("http://www.mejortorrent.com/peli-descargar-torrent-" + id + "-a.html", 60000);
			
			// Insertar el registro
			if (elemento.getName() != NOT_VALID_ID) {
				bbddinit.insertElement(elemento);
				System.out.println("Registro insertado: " + id);
			}
			
		}
		
		System.out.println("Fin del programa");
		
    }

	public static void obtenerInfo(String url, int timeout) {
		
		Document doc = null;
		Elements pelicula = null;
		
		//  The default timeout is 3 seconds (3000 millis)
		
		try {
			doc = Jsoup.connect(url)
					.timeout(timeout)
					.get();
		} catch (SocketTimeoutException timeOutEx) {
			System.out.println("Se ha superado el tiempo de espera");
			timeOutEx.printStackTrace();
		} catch (IOException ioEx) {
			System.out.println("Error al obtener el documento");
			ioEx.printStackTrace();
		}
		
		if ( doc.body().text().contains("Too many connections") )
			System.out.println("La base de datos de la página está colapsada");
		else if ( url.contains("secciones.php") ) {
			pelicula = doc.select("[href*=/uploads/]");
			// System.out.println("link: " + pelicula.attr("href"));
			elemento.setLink("www.mejortorrent.com" + pelicula.attr("href"));
		} else {
			// Parsear el td
			pelicula = doc.select("td").eq(52);
			String[] bloque_1 = pelicula.html().split("</b>");
			
			//System.out.println("Tamaño a examinar: " + bloque_1.length);
			
			if ( bloque_1.length > 1 ) {
				if (bloque_1[8].contains("DVDRip"))
					elemento.setQuality("DVDRip");
				else if (bloque_1[8].contains("DVDscreener"))
					elemento.setQuality("DVDscreener");
				else {
					System.out.println("Formato no reconocido: " + bloque_1[8]);
					elemento.setQuality("Unknown");
				}
				
				String[] bloque_2 = bloque_1[0].split(">");
					// System.out.println("name: " + bloque_2[2]);
					elemento.setName(bloque_2[2]);
				String[] bloque_3 = bloque_1[13].split("<");
					// System.out.println("size: " + bloque_3[0].substring(7));
					elemento.setSize(bloque_3[0].substring(7));
			} else {
				System.out.println("Registro corrupto en la página");
				elemento.setName(NOT_VALID_ID);
			}
		}

	}
	
}