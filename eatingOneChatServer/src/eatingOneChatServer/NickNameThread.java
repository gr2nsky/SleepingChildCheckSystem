package eatingOneChatServer;

import java.io.DataInputStream;
import java.io.InputStream;
import java.net.Socket;
import java.sql.Connection;

//#닉네임 처리 스레드
class NickNameThread extends Thread{
	private Socket socket;
	Connection conn;
	
	//인자로 받은 소켓을 적용
	public NickNameThread(Socket socket, Connection conn) {
		this.socket = socket;
		this.conn = conn;
	}
	
	public void run() {
		try {
			//스트림 추출
			InputStream is = socket.getInputStream();
			DataInputStream dis = new DataInputStream(is);
			
			//사용자 정보 수신 userId+ "&" +Integer.toString(roomId)
			String[] connMsg = dis.readUTF().split("&");
			String userid = connMsg[0];
			int roomId = Integer.parseInt(connMsg[1]);
			
			//해당 접속정보를 묶어 보관
			UserClass user = new UserClass(userid, socket, conn, roomId);
			user.start();
			//ChatServer.user_list.add(user);
			ChatServer.enterRoom(roomId, user);
			//서버 룸에 입장인원 정보 추가
			System.out.println(userid + "님이 입장하였습니다.");
			
		} catch(Exception e) {e.printStackTrace();}
	}
}