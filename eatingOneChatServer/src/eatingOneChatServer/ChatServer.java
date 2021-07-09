package eatingOneChatServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;

public class ChatServer {
	
	private ServerSocket server;
	//
	public static ArrayList<Room> room_list;
	//MySQL Connect infomation
	static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://	115.71.239.79/eatingone?characterEncoding=UTF-8&serverTimezone=UTC";
	static final String USERNAME = "root";
	static final String PASSWORD = "dbs2009";
	static Connection conn = null;
	/*
	 드라이버 로딩 -> MySQL 연결 -> 소켓접속 대기 -> 소켓접속시 UserClass 인스턴스 생성, 정보 저장
	 -> 메세지 송수신 대기 -> 메세지 송수신에 따른 작업 처리
	 */
	
	public static void main(String[] args) {		
		//mysql 접속
		try {
			//드라이버 로딩
			Class.forName(JDBC_DRIVER);
			//mysql과 연결
			conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
			System.out.println("\n- MySQL Connection");
		} catch (ClassNotFoundException e) {
			System.out.println("드라이버 로딩 실패 : "+e);
			System.out.println();
		}
		catch(Exception e) {
			System.out.println("에러 : " + e);
		} 
		//caht server open
		new ChatServer();
	}
	
	public ChatServer() {
		try {
			room_list = new ArrayList<>();
			//#포트 개방
			server = new ServerSocket(10022);
			//#접속대기 스레드 실행
			ConnectingThread thread = new ConnectingThread(server, conn);
			thread.start();
		} catch(Exception e) {e.printStackTrace();}
	}

	// 클라이언트에게 메세지 송신
	public synchronized static void SendToClient(String msg, int roomId) {
		Room room;
		room = serchRoom(roomId);
		try {
			for(int i = 0; i < room.size(); i++) {
				UserClass user = room.user_list.get(i);
				user.dos.writeUTF(msg);
			}
		} catch (IOException e) { e.printStackTrace(); }
		
	}
	// 클라이언트가 속한 roomid의 room을 찾아 반환
	public static Room serchRoom(int roomId) {
		for (int i =0; i < room_list.size(); i++)
			if (room_list.get(i).roomId == roomId)
				return room_list.get(i);
		return null;
	}
	// roomId에 해당하는 room에 사용자 정보 추가 (없을시 room 생성)
	public static void enterRoom(int roomId, UserClass user) {
		Room room = serchRoom(roomId);
		if(serchRoom(roomId) == null) {
			room = new Room(roomId);
			room_list.add(room);
		}
		room.add(user);
	}
	//room 제거 (room에 입장한 사용자가 없을시)
	public static void delRoom(Room room) {
		room_list.remove(room);
	}
}
