import java.sql.Connection;
import java.sql.DriverManager;

public class DBMS {	
	Connection DBA() {
		Connection conn = null;
		final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
		final String DB_URL = "jdbc:mysql://115.71.239.79:3306/Wookinder?characterEncoding=UTF-8&serverTimezone=UTC";
		final String USERNAME = "cap";
		final String PASSWORD = "06q5dVPLtCRNuYzN";
		//mysql 접속
		try {
			//드라이버 로딩
			Class.forName(JDBC_DRIVER);
			//mysql과 연결
			conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
			System.out.println("\n- MySQL Connection");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("드라이버 로딩 실패");
		}
		catch(Exception e) {
			System.out.println("에러 : " + e);
		} 
		return conn;
	}
}
