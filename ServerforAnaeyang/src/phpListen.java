import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class phpListen extends Thread{
	ServerSocket listenSock = null;
	Socket sock = null;
		
	phpListen(){}
		
	public void run(){
		try {
			while(true) {
				listenSock = new ServerSocket(10023);
				System.out.println("phpListen 가동");
				while(true) {
					sock = listenSock.accept();
					BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
					String line="";
					while( (line=br.readLine()) != null ){
						bw.write("PHP said : "+line+"\n");
						System.out.println();
						SendMsg.setride(line);
						bw.flush();
					}
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
