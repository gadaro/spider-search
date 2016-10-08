package spidercore;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import spiderdb.SpiderDataBaseInit;

public class SpiderMain {
	
	public static void main(String[] args) {
		
		SpiderDataBaseInit iniciar = new SpiderDataBaseInit();
		iniciar.createTable();
		
		// TODO Utilizar un matcher replacer
		for ( int id = 2; id < 10; id++ ) {
			String url = "http://xxx"
					+ id + "&link_bajar=1";
			
			System.out.println(obtenerTorrent(url, 5000));
		}
		
    }

	public static String obtenerTorrent(String url, int timeout) {
		
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
		else {
			pelicula = doc.select("[href*=/uploads/]");
		}
		
		return pelicula.toString();
	}
	
}