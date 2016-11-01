package spiderdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SpiderDataBaseInit {
	
	private Connection conn = null;
	
	public SpiderDataBaseInit () {
		System.out.println("Iniciando módulo de base de datos...");
		openConnection();
		createTable();
		System.out.println("Fin de la inicialización");
	}
	
	/**
	 * Abre la conexión con la base de datos, si no existe la crea.
	 */
	private void openConnection () {
		
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			System.out.println("No se ha encontrado el driver de Sqlite");
			e.printStackTrace();
		}
		
		System.out.println("Conectando...");
		
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:spiderdat.db");
		} catch (SQLException e) {
			System.out.println("Error al intentar acceder a la base de datos");
			e.printStackTrace();
		}
		System.out.println("Base de datos abierta");
	} // Fin del método public void openConnection ()
	
	private void createTable () {
		
		Statement stmt = null;
		
		try {
			if ( conn.isClosed() )
				openConnection();
		} catch (SQLException e) {
			System.out.println("Error al intentar comprobar la conexión con la base de datos");
			e.printStackTrace();
		}
		
		try {
			stmt = conn.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS MOVIES " +
					"(ID      INT      PRIMARY KEY NOT NULL, " +
					" NAME    TEXT                 NOT NULL, " + 
					" QUALITY CHAR(15)             NOT NULL, " + 
					" LINK    TEXT                 NOT NULL, " + 
					" SIZE    CHAR(10)             NOT NULL)";
			System.out.println("Resultado de ejecución: " + stmt.executeUpdate(sql));
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			System.out.println("Error al crear la sentencia de inicialización");
			e.printStackTrace();
		}
		
	} // Fin del método public void createTable ()
	
	public void insertElement (SpiderDataBaseElement elemento) {
		
		Statement stmt = null;
		
		try {
			if ( conn.isClosed() )
				openConnection();
		} catch (SQLException e) {
			System.out.println("Error al intentar comprobar la conexión con la base de datos");
			e.printStackTrace();
		}
		
		try {
			stmt = conn.createStatement();
			String sql = "INSERT OR REPLACE INTO MOVIES " +
					"(ID, NAME, QUALITY, LINK, SIZE) " +
					"VALUES ('" + elemento.getId() + "', '" +
								elemento.getName() + "', '" +
								elemento.getQuality() + "', '" +
								elemento.getLink() + "', '" +
								elemento.getSize() + "')";
			System.out.println("Resultado de ejecución: " + stmt.executeUpdate(sql));
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			System.out.println("Error al insertar el registro en la base de datos");
			e.printStackTrace();
		}
		
	}

}
