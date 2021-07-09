import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;

public class SendMsg {

	// 클라이언트에게 메세지 송신
	public synchronized static void SendToClient(String msg, User user) {
		try {
			user.dos.writeUTF(msg);
		} catch (IOException e) { e.printStackTrace(); }
		
	}
	
	//php
	public static void setride(String datas) {
		System.out.println("setride 가동");
		Querys querys = new Querys();
		
		String split[] = datas.split(",");
		String token = split[0];
		String ride = split[1];
		
		String parent = querys.changeRideParent(Main.mysqlConn, token);
		String rideData = querys.getParentField(Main.mysqlConn, parent);

		String msg = "rideChange##"+rideData;
		for (HashMap<String, User> hash : Main.connUser) {
			if( hash.get(parent) != null ) {
				SendMsg.SendToClient(msg, hash.get(parent));
				System.out.println("setride:"+parent+" 찾음 : "+msg);
			}
		}
		
		String result = querys.changeRideDriver(Main.mysqlConn, parent);
		String datas2[] = result.split(",");
		String driverId = datas2[0];
		String cName = datas2[1]; 
		
		String msg2 = "rideChange##"+cName+','+ride;
		for (HashMap<String, User> hash : Main.connUser) {
			if( hash.get(driverId) != null ) {
				SendMsg.SendToClient(msg2, hash.get(driverId));
				System.out.println("setride:"+driverId+","+cName+" 찾음 : "+msg);
			}
		}
	}
	
}
