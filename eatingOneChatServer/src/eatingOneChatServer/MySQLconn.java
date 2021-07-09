package eatingOneChatServer;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class MySQLconn extends Thread{
	PreparedStatement psmt = null;
	String sql = null;
	
	public MySQLconn(String userid, String msg, String time, Connection conn) {
		try {
			if(conn == null) {
				System.out.println("mysql 연결 에러");
				return;
			}
			sql = "INSERT INTO chatSaveTest (sender, msg, time) values (?, ?, ?)";
			
			psmt = conn.prepareStatement(sql.toString());
			psmt.setString(1, userid);
			psmt.setString(2, msg);
			psmt.setString(3, time);
			psmt.executeUpdate();
			
			psmt.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	//받은 메세지를 mysql 서버에 저장
	public static void saveToMySQL(String userid, String msg, String time, Connection conn) {
		MySQLconn thread = new MySQLconn(userid, msg, time, conn);
		thread.start();
	}
}

