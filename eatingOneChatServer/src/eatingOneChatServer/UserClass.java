package eatingOneChatServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
//#사용자 정보를 묶어 관리하는 클래스
class UserClass extends Thread{
	String userid;
	Socket socket;
	DataInputStream dis;
	DataOutputStream dos;
	int roomId;
	Connection conn;
	//인자로 받은 유저 정보를 적용
	public UserClass(String userid, Socket socket, Connection conn, int roomId) {
		try {
			this.conn = conn;
			this.userid = userid;
			this.socket = socket;
			this.roomId = roomId;
			InputStream is = socket.getInputStream();
			dis = new DataInputStream(is);
			OutputStream os = socket.getOutputStream();
			dos = new DataOutputStream(os);
			
		} catch(Exception e) {e.printStackTrace();}
	}

	//저장되있는 유저중 하나로부터 메세지를 수신받음. 항시대기
	public void run() {
		try {
			while(true) {
				//메세지 수신
				String msg = dis.readUTF();
				//날자계산 해 같이 전송
				String time = Utillity.getTime();
				
				ChatServer.SendToClient(userid + "%div%" + msg + "%div%" + time, roomId);
				System.out.println(userid + " : " + msg + ", " + time);
				//채팅내역 서버에 전송
				MySQLconn.saveToMySQL(userid, msg, time, conn);
			}
			//유저가 chat방을 나가면, 해당 위치에서 처리해야함
		} catch(Exception e) {
			System.out.println(userid + "님이 퇴장하였습니다.");
			Room room = ChatServer.serchRoom(roomId);
			room.del(this);
			if(room.size() == 0)
				ChatServer.delRoom(room);
		}
	}
}
