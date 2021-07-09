import java.net.ServerSocket;
import java.net.Socket;

public class ConnectingThread extends Thread{
	ServerSocket server;
	int i = 0;
	
	//접속대기
	ConnectingThread(ServerSocket server){
		this.server = server;
	}
	public void run() {
		try {
			while(true) {
				System.out.println("사용자 접속 대기");
				Socket socket = server.accept();
				socketAndriodToServer SAT = new socketAndriodToServer(socket, i);
				SAT.start();
				i++;
			}
		}catch(Exception e) {
			
		}
	}
}
