package spidercore;

import java.io.IOException;
import java.net.SocketTimeoutException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import spiderdb.SpiderDataBaseElement;
import spiderdb.SpiderDataBaseInit;

public class SpiderMain {
	
	static SpiderDataBaseElement elemento = new SpiderDataBaseElement();
	
	public static void main(String[] args) {
		
		SpiderDataBaseInit iniciar = new SpiderDataBaseInit();
		for ( int id = 2; id < 30; id++ ) {
			
			String url = "http://www.mejortorrent.com/secciones.php?sec=descargas&ap=contar&tabla=peliculas&id="
					+ id + "&link_bajar=1";
			
			// Cargar los datos
			elemento.setId(id + "");
			obtenerInfo(url, 60000);
			obtenerInfo("http://www.mejortorrent.com/peli-descargar-torrent-" + id + "-a.html", 60000);
			
			// Insertar el registro
			iniciar.insertElement(elemento);
			System.out.println("Registro insertado: " + id);
			
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
		}
		else {
			// Parsear el td
			pelicula = doc.select("td").eq(52);
			String[] bloque_1 = pelicula.html().split("</b>");
			if (bloque_1[8].contains("DVDRip"))
				// System.out.println("DVDRip");
				elemento.setQuality("DVDRip");
			else
				System.out.println("Formato no reconocido: " + bloque_1[8]);
			String[] bloque_2 = bloque_1[0].split(">");
				// System.out.println("name: " + bloque_2[2]);
				elemento.setName(bloque_2[2]);
			String[] bloque_3 = bloque_1[13].split("<");
				// System.out.println("size: " + bloque_3[0].substring(7));
				elemento.setSize(bloque_3[0].substring(7));
		}
		
	}
	
}