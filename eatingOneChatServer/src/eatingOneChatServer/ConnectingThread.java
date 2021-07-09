package eatingOneChatServer;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;

//#접속대기 스레드//
class ConnectingThread extends Thread{
	
	ServerSocket server;
	Connection conn;
	
	public ConnectingThread(ServerSocket server, Connection conn) {
		this.server = server;
		this.conn = conn;
	}
	
	public void run() {
		try {
			while(true) {
				System.out.println("사용자 접속 대기중");
				Socket socket = server.accept();
				//#사용자 닉네임 처리 스레드 가동, 소켓을 인자로 넘겨줌
				NickNameThread thread = new NickNameThread(socket, conn);
				thread.start();
			}
		} catch(Exception e) {
		}
	}
}