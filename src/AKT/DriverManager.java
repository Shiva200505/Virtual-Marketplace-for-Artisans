package AKT;

import java.sql.Connection;
import java.sql.SQLException;

public class DriverManager {

	public static Connection getConnection(String url, String user, String password) throws SQLException {
		// Forward the request to the real java.sql.DriverManager
		return java.sql.DriverManager.getConnection(url, user, password);
	}

}
