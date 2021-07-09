package com.example.annaeyang.Parents;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.annaeyang.Commu.SocketCommu;
import com.example.annaeyang.Notification.MyFirebaseInstanceIDService;
import com.example.annaeyang.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;


public class Parent extends AppCompatActivity implements OnMapReadyCallback {

    public static TextView boardingTV;
    public static TextView dNameTV;
    public static TextView busNumTV;
    public static TextView phoneTV;
    public static TextView rideTimeText;
    public static TextView rideTimeTV;
    public static TextView rideDateTV;
    public static TextView cName;

    public static String busNum;
    public static String ride;
    public static String bName;
    public static String phone;
    public static String location;

    static Context context;

    String loginedid;
    static GoogleMap mMap;

    String TAG = "ParentLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parents);

        context = getApplicationContext();


        boardingTV = (TextView) findViewById(R.id.boardingTV);
        dNameTV = (TextView) findViewById(R.id.bName);
        busNumTV = (TextView) findViewById(R.id.busNum);
        phoneTV = (TextView) findViewById(R.id.phone);
        rideTimeText = (TextView) findViewById(R.id.rideTimeText);
        rideDateTV = (TextView) findViewById(R.id.rideDateTV);
        rideTimeTV = (TextView) findViewById(R.id.rideTimeTV);
        cName = (TextView) findViewById(R.id.cName);

        //intent로 login한 id 가져옴
        Intent intent = getIntent();
        loginedid = intent.getExtras().getString("loginedid");

        //알람채널 생성
        createNotificationChannel();

        //google maps 선언
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //firebase에 토픽 등록 및 token 가져오기
        FirebaseMessaging.getInstance().subscribeToTopic("parent");
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Log.d(TAG, token);
                        MyFirebaseInstanceIDService.onNewToken(token, loginedid);
                    }
                });
        //소켓 접속
        SocketCommu socketCommu = new SocketCommu(loginedid, 3);
    }
    public static void setState(String msg) {
        //rs.getString("name")+"#"+rs.getString("student_busNum")+"#"+rs.getString("ride")+"#"+rs.getString("name")+"#"+rs.getString("phone")+"#"+rs.getString("location");
        String msgs[] = msg.split("#");
        //차량 번호 핸들링
        busNum = msgs[0];
        if(msgs[1].equals("0")) {
            rideTimeText.setText("하차 시간");
            ride = "미탑승";
        }
        else {
            rideTimeText.setText("탑승 시간");
            ride = "탑승중";
        }
        //child name set
        cName.setText(msgs[2]);
        //탑승/하차의 사간/날자 핸들링
        String dates[] = msgs[3].split(" ");
        String date= dates[0];
        String times[] = dates[1].split(":");
        if (Integer.parseInt(times[0]) > 12)
            times[0]= "PM "+ (Integer.parseInt(times[0])-12);
        else
            times[0]="AM " + (Integer.parseInt(times[0]));
        bName = msgs[4];
        //핸드폰 번호를 000-0000-0000 형식으로 변경해 textView에 적용
        phone = msgs[5];
        String p1 = phone.substring(0,3);
        String p2 = phone.substring(3, 7);
        String p3 = phone.substring(7, 11);
        phone = p1+"-"+p2+"-"+p3;
        phoneTV.setText(phone);

        location = msgs[6];
        if(!location.equals("null")) {
            String[] loc = location.split(",");
            float spotX = Float.parseFloat(loc[0]);
            float spotY = Float.parseFloat(loc[1]);
            initLoc(spotX, spotY);

        rideTimeTV.setText(times[0]+" : "+times[1]);
        rideDateTV.setText(date);
        boardingTV.setText(ride);
        dNameTV.setText(bName);
        busNumTV.setText(busNum);

        }
        else{
            Toast.makeText(context, "현재 운행중이 아닙니다. 가장 마지막으로 인식된 지역을 지도에 표시합니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void initLoc(float spotX, float spotY){
        mMap.clear();
        LatLng mapPoin = new LatLng (spotX, spotY);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(mapPoin);
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapPoin, 16));
    }

    public static void changeLoc(String loc){
        String st[] = loc.split(",");
        double v1 = Double.parseDouble(st[0]);
        double v2 = Double.parseDouble(st[1]);

        Log.e("changeLoc", Double.toString(v1) + "," + Double.toString(v2));

        mMap.clear();
        LatLng mapPoin = new LatLng (v1, v2);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(mapPoin);
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapPoin, 16));
    }

    public static void changeRide(String ride){
        if(ride.equals("0")){
            boardingTV.setText("미탑승");
        }else{
            boardingTV.setText("탑승중");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng SEOUL = new LatLng(37.56, 126.97);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(SEOUL);
        markerOptions.title("미운행중");
        markerOptions.snippet("현재는 운행중이 아닙니다.");
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 16));
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Anaeyang";
            String description = "Anaeyang";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("anaeyang", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }
    //전화번호 터치시 통화걸기로 넘어감
    public void callDriver(View view){
        String tel = "tel:";
        String phone = phoneTV.getText().toString();
        String nums[] = phone.split("-");
        for(String s : nums)
            tel += s;
        Intent tt = new Intent(Intent.ACTION_DIAL, Uri.parse(tel));
        startActivity(tt);
    }
}
