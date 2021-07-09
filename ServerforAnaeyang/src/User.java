import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class User extends Thread{
	//###사용자를 구성하는 item 고려해 봐야 함
	Socket socket;
	int auth;
	int connectId;
	String userid;
	DataInputStream dis;
	DataOutputStream dos;
	Querys querys = new Querys();
	
	//getter, setter
	public int getAuth() {
		return auth;
	}
	public void setAuth(int auth) {
		this.auth = auth;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String id) {
		this.userid = id;
	}	
	//user 생성자
	public User(Socket socket, int auth, String id, int connectId){
		this.connectId = connectId;
		this.socket = socket;
		this.auth = auth;
		this.userid = id;
		try {
			InputStream is;
			is = socket.getInputStream();
			dis = new DataInputStream(is);
			OutputStream os = socket.getOutputStream();
			dos = new DataOutputStream(os);
			System.out.println("["+auth+"]"+id+"가 접속했습니다. 접속순서 : "+connectId);
			//최초 1회 정보 송신
			initData(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//사용자로부터 data 수신 : 항시대기
	public void run() {
		try {
			while(true) {
				//data 수신 ####데이터 수신 형식 고려해봐야 함 request#data
				String response = dis.readUTF();
				System.out.println(userid+":"+response);
				String data[] = response.split("#");
				//### 수신된 data에 따른 server 동작 필요
				if(data[0].equals("point")) {
					String point = data[1];
					getPoint(point);
				}
			}
		}catch(Exception e) {
			//user가 접속 종료시 처리 ######사용자를 목록에서 제거
			System.out.println("#퇴장자 처리 실시#");
			int i = 0;
			for(HashMap<String, User> u : Main.connUser) {
				Set key = u.keySet();
				Iterator iterator = key.iterator();
	            String keyName = (String) iterator.next();
	            User user = u.get(keyName);
	            System.out.println(keyName+"의 접속순서는 "+user.connectId+" 였습니다.");
	            if( keyName == getUserid() && user.connectId == this.connectId ) {
	            	Main.connUser.remove(i);
	            	System.out.println(keyName + "님이 소켓접속을 종료하셨습니다.");
	            	return;
	            }
	            i++;
			}
		}
	}
	//접속하자마자 1회성으로 view data 송신
	public void initData(User user) {
		System.out.println("initData 메서드 작동");
		
		int auth = user.getAuth();
		String msg = null;
		
		switch(auth){
			//case of 원장
			case(1):
				System.out.println("initData - manager");
			msg = "initData##"+querys.getManagerField(Main.mysqlConn, userid);
				break;
			//case of 운전사
			case(2):
				System.out.println("initData - Driver");
				msg = "initData##"+querys.getDriverField(Main.mysqlConn, userid);
				break;
			//case of 학부모
			case(3):
				System.out.println("initData - parent");
				msg = "initData##"+querys.getParentField(Main.mysqlConn, userid);
				break;
		}
		SendMsg.SendToClient(msg, this);
	}
	public void getPoint(String point) {
		//기사의 위치정보 업데이트
		querys.LocationSet(Main.mysqlConn, point, userid);
		//기사차량에 탑승한 아이의 부모가 socket접속중이라면, 메세지를 보내 google map을 update
		String msg = "point##"+point;
		System.out.println("getPoint : " + msg);
		ArrayList<String> connUser = querys.parentForChildInBus(Main.mysqlConn, userid);
		for(String parent : connUser) {
			for (HashMap<String, User> hash : Main.connUser) {
				if( hash.get(parent) != null ) {
					SendMsg.SendToClient(msg, hash.get(parent));
				}
			}
		}
	}
	//운행시작시, 주기적으로 gps좌표를 parent에게 전송한다.
	public class returnPosition extends Thread{
		int loop = 0;
		@Override
		public void run() {
			while(loop == 0) {
				String point = Querys.returnPosition(Main.mysqlConn, userid);
				//기사차량에 탑승한 아이의 부모가 socket접속중이라면, 메세지를 보내 google map을 update
				String msg = "point##"+point;
				System.out.println("returnPosition : " + msg);
				ArrayList<String> connUser = querys.parentForChildInBus(Main.mysqlConn, userid);
				for(String parent : connUser) {
					for (HashMap<String, User> hash : Main.connUser) {
						if( hash.get(parent) != null ) {
							SendMsg.SendToClient(msg, hash.get(parent));
						}
					}
				}
				try {
					//전송간격
					sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}







