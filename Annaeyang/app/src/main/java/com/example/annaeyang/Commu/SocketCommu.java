package com.example.annaeyang.Commu;

import com.example.annaeyang.Managers.Manager;
import com.example.annaeyang.Parents.Parent;
import com.example.annaeyang.commonInfo.ServerInfo;
import com.example.annaeyang.commonInfo.UserInfo;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class SocketCommu {

    String TAG = "SocketCommuLog";
    //어플 종료시 스레드를 종료시켜주는 스위치
    boolean isRunning = false;
    //채팅방에 접속해있는지 상태를 알려주는 스위치
    public static boolean isConnect = false;
    //서버 소켓 객체
    public static Socket member_socket;
    // 1 원장 2 기사 3 학부모 0 초기값
    int auth = 0;
    String userId;
    //핸들러 선언
    Handler initHandler;

    public SocketCommu(String userId, int auth){
        this.userId = userId;
        this.auth = auth;
        ConnServer();
    }
    public void ConnServer(){
        ConnectionThread thread = new ConnectionThread(userId);
        thread.start();
    }


    public class ConnectionThread extends Thread{

        public ConnectionThread(String userid){
            userId = userid;
        }

        @Override
        public void run(){
            try{
                Log.i(TAG, "Run"+"["+auth+"]"+userId);
                //호스트서버의 ip와 포트로 접속
                final Socket socket = new Socket(ServerInfo.serverIp, 10022);
                member_socket = socket;
                //스트림 추출
                OutputStream os = socket.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                //접속 최초에 사용자 정보를 서버에 송신
                dos.writeUTF(auth+","+userId);
                //접속상태를 true로 셋팅
                isConnect = true;
                isRunning = true;
                //메세지 수신 대기 스레드 가동 (소켓 객체를 전달)
                receptionThread thread = new receptionThread(socket);
                thread.start();
            } catch (Exception e) {
                Log.e(TAG, "error : "+e);
            }
        }
    }
    //서버로부터 메세지 수신
    class receptionThread extends Thread{
        Socket socket;
        DataInputStream dis;

        public receptionThread(Socket socket){
            try{
                this.socket = socket;
                //input스트림을 socket으로부터 추출
                InputStream is = socket.getInputStream();
                dis = new DataInputStream(is);
            }catch (Exception e) {e.printStackTrace();}
        }
        @Override
        public void run(){
            try{
                //어플 종료시 isRunning을 false로 바꿔주어, 앱 종료후에 스레드 종료
                while(isRunning){
                    //서버로부터 메세지 수신, odaer##msg
                    final String data[] = dis.readUTF().split("##");
                    Log.i(TAG, data[0]+data[1]);
                    String order = data[0];
                    String msg = data[1];
                    //최초로 수신되는 view item 정보
                    if(order.equals("initData"))
                        initData(msg);

                    //이후 view에 알맞은 핸들러 처리
                    switch (auth){
                        case 1 :

                            break;
                        case 2 :
                            if(data[0].equals("rideChange")) {
                                Log.i(TAG,"기사뷰 변경중");
                                String useData[] = data[1].split(",");
                                final String cName = useData[0];
                                final String ride = useData[1];
                                initHandler = new Handler(Looper.getMainLooper());
                                initHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Manager.changeRide(cName, ride);
                                    }
                                });
                            }
                            break;
                        case 3 :
                            if(data[0].equals("point")){
                                final String loc = data[1];
                                initHandler = new Handler(Looper.getMainLooper());
                                initHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Parent.changeLoc(loc);
                                    }
                                });
                            }
                            if(data[0].equals("rideChange")) {
                                final String datas = data[1];
                                Log.i(TAG, msg);
                                initHandler = new Handler(Looper.getMainLooper());
                                initHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Parent.setState(datas);
                                    }
                                });
                            }
                            break;
                    }
                }
            }catch (Exception e) { e.printStackTrace(); }
        }
    }
    public static void send(String msg){
        requestToServer thread = new requestToServer(member_socket, msg);
        thread.start();
    }

    //서버에 request
    public static class requestToServer extends Thread{
        Socket socket;
        String msg;
        DataOutputStream dos;

        public requestToServer(Socket socket, String msg){
            try{
                //인자로 받은 소켓과 메세지 선언 및 outputStream 추출
                this.socket = socket;
                this.msg = msg;
                OutputStream os = socket.getOutputStream();
                dos = new DataOutputStream(os);
            } catch (Exception e ) { e.printStackTrace();}
        }
        @Override
        public void run(){
            try {
                //서버에 메세지 전송
                dos.writeUTF(msg);
            } catch (IOException e) {e.printStackTrace();}
        }
    }

    //사용자 정보 캡슐화 후 리턴
    UserInfo userinfo(){
        UserInfo user = new UserInfo();
        user.setUserId(userId);
        user.setAuth(auth);
        return user;
    }

    //앱 종료시 소켓 접속 해제 및 스레드 종료
    public void exitChat(){
        try{
            isConnect = false;
            isRunning = false;
            member_socket.close();
        }catch (Exception e) {e.printStackTrace();}
    }

    //initData 핸들링
    public void initData(final String msg){
        switch (auth){
            case 1 :
                Log.i(TAG, msg);
                initHandler = new Handler(Looper.getMainLooper());
                initHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Manager.driversStateSet(msg);
                    }
                });
                break;
            case 2 :
                Log.i(TAG, msg);
                initHandler = new Handler(Looper.getMainLooper());
                initHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Manager.driverStateSet(msg);
                    }
                });
                break;
            case 3 :
                Log.i(TAG, msg);
                initHandler = new Handler(Looper.getMainLooper());
                initHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Parent.setState(msg);
                    }
                });
        }
    }

}
