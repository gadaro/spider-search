package spidercore;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import spiderdb.SpiderDataBaseElement;
import spiderdb.SpiderDataBaseInit;

public class SpiderMain {
	
	static SpiderDataBaseElement elemento = new SpiderDataBaseElement();
	
	public static void main(String[] args) {
		
		// SpiderDataBaseInit iniciar = new SpiderDataBaseInit();
		for ( int id = 2; id < 3; id++ ) {
			
			String url = "http://www.mejortorrent.com/secciones.php?sec=descargas&ap=contar&tabla=peliculas&id="
					+ id + "&link_bajar=1";
			
			// Cargar los datos
			elemento.setId(id + "");
			obtenerInfo(url, 5000);
			obtenerInfo("http://www.mejortorrent.com/peli-descargar-torrent-" + id + "-a.html", 5000);
		}
		
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
			System.out.println(pelicula.attr("href"));
		}
		else {
			// Parsear el td
			pelicula = doc.select("td").eq(52);
			Iterator<Element> itr = pelicula.iterator();
			while(itr.hasNext()) {
		         Element e = itr.next();
		         System.out.println(e.html());
		      }
		}
		
	}
	
}