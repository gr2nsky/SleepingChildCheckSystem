import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
	
	public static ArrayList<HashMap<String, User>> connUser = new ArrayList<HashMap<String, User>>();

	public static void main(String[] args) {
			Main a = new Main();
			a.Server();
	}
	
	static Connection mysqlConn;
	private ServerSocket server;
	static ArrayList<User> userList;
	
	void Server() {
		//1. 서버 가동시 MySQL 접속
		DBMS dbms = new DBMS();
		mysqlConn = dbms.DBA();
		//2. 포트개방, 각 클래스 참조 및 전역변수 생성/선언
		try {
			server = new ServerSocket(10022);
			//3. 사용자 접속 대기
			
			ConnectingThread ct = new ConnectingThread(server);
			ct.start();
			
			phpListen pl = new phpListen();
			pl.start();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
