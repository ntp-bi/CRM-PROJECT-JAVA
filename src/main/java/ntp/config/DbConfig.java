package ntp.config;

import java.sql.Connection;
import java.sql.DriverManager;

public class DbConfig {
	private static String DRIVER_NAME = "com.mysql.cj.jdbc.Driver";
	private static String URL = "jdbc:mysql://127.0.0.1:3306/crm-project1";
	private static String USER_NAME = "root";
	private static String PASSWORD = "";

	public static Connection getMySQLConnection() {
		Connection connection = null;
		try {
			Class.forName(DRIVER_NAME);
			connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
		} catch (Exception e) {
			System.out.println("Lỗi khi kết nối vào database | " + e.getMessage());
			e.printStackTrace();
		}
		return connection;
	}
}
