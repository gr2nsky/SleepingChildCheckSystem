import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

class socketAndriodToServer extends Thread{
	private Socket socket;
	private int connectId;
	
	socketAndriodToServer(Socket socket, int connectId){
		this.socket = socket;
		this.connectId = connectId;
	}
	
	public void run() {
		try {
			System.out.println("#socketAndriodToServer start#");
			//inputStream 추출
			InputStream is = socket.getInputStream();
			DataInputStream dis = new DataInputStream(is);
			//접속한 사용자 data 처리 ####auth,id 꼴로 전송받음#### ( 1 원장 2 기사 3 학부모)
			String read = dis.readUTF();
			String[] connData = read.split(",");
			int auth = Integer.parseInt(connData[0]);
			String id = connData[1];
			System.out.println("get data : "+id+","+auth);
			
			//User의 인스턴스 생성해 data 보관
			//socket으로 접속 : 버스기사, 원장, 학부모, 라즈베리 파이
			//접속자를 구분해 주어야 함
			
			User user = new User(socket, auth, id, connectId);
			HashMap<String, User> map = new HashMap<String, User>();
			map.put(id, user);
			Main.connUser.add(map);
			user.start();
		}catch(Exception e){
			
		}
	}
}