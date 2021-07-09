package com.example.annaeyang.Managers;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.annaeyang.Commu.SocketCommu;
import com.example.annaeyang.R;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.annaeyang.Parents.Parent.location;


public class Manager extends AppCompatActivity {

    static String TAG = "ManagerAc";

    static Context context;
    LinearLayout DriverTab;
    LinearLayout managerTab;
    LayoutInflater inflater;
    public static ListView riding;
    public static ListView notRiding;

    String loginedid;
    int auth = 0;

    static TextView busNum_d;
    static TextView rided;
    static Button driverButton;

    //공통
    public static SocketCommu socketCommu;
    Handler mHandler = new Handler();
    public static ArrayList<String> rideArr;
    public static ArrayList<String> notRideArr;
    public static ArrayAdapter ridingAdapter;
    public static ArrayAdapter notRidingAdapter;

    //원장용
    public static Spinner spinner;
    public static CustomSpinnerAdapter adapter;
    static String driverName;
    public static TextView timeTV;
    public static TextView busNum_m;
    public static TextView ridedNum_m;
    Timer timer;
    static boolean settingManager = false;
    SocketCommu socketCommu2;

    //기사용
    int driving = 0; // 1:운행중 0 비운행중
    //sharedpreference로 운전자의 운행값을 저장
    SharedPreferences pref;
    SharedPreferences.Editor editor;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        //하단 탑승/미탑승 context 적용
        context = getApplicationContext();
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        riding = (ListView) findViewById(R.id.riding);
        notRiding = (ListView) findViewById(R.id.notRiding);
        //리스트뷰에 adapter 장착
        rideArr = new ArrayList<String>();
        notRideArr = new ArrayList<String>();
        ridingAdapter = new ArrayAdapter(context, R.layout.listview_item, rideArr);
        notRidingAdapter = new ArrayAdapter(context, R.layout.listview_item, notRideArr);
        riding.setAdapter(ridingAdapter);
        notRiding.setAdapter(notRidingAdapter);
        //intent로 login한 auth, id 가져옴
        Intent intent = getIntent();
        auth = intent.getExtras().getInt("auth");
        loginedid = intent.getExtras().getString("loginedid");
        Log.i(TAG, "[" + auth + "]" + loginedid);
        tab(auth);

        //소켓 접속
        socketCommu = new SocketCommu(loginedid, auth);
        controlTimTextView();
        if(auth == 2){
            findSharedPreference();
        }
    }

    /**@param auth 원장1 기사2 학부모3 null값0*/
    public void tab(int auth) {
        if (auth == 1) {
            managerTab = (LinearLayout) findViewById(R.id.detachedLayout);
            inflater.inflate(R.layout.manager_tab, managerTab, true);
            setManagerInterface();

        } else {
            DriverTab = (LinearLayout) findViewById(R.id.detachedLayout);
            inflater.inflate(R.layout.driver_tab, DriverTab, true);
            setDeriverInterface();
        }
    }

    //driverTab item adjust
    public void setDeriverInterface() {
        //shard preference
        timeTV = (TextView) findViewById(R.id.timeTV);
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        editor = pref.edit();
        //layout item
        busNum_d = (TextView) findViewById(R.id.busNum_d);
        rided = (TextView) findViewById(R.id.ridedNum);
        driverButton = (Button) findViewById(R.id.driverButton);
    }

    //MnagerTab item adjust
    public void setManagerInterface() {
        timeTV = (TextView) findViewById(R.id.timeTV);
        ridedNum_m = (TextView) findViewById(R.id.ridedNum_m);
        busNum_m = (TextView) findViewById(R.id.busNum_m);
        spinner = (Spinner) findViewById(R.id.spinner);
        adapter = new CustomSpinnerAdapter();
        spinner.setAdapter(adapter);
        adapter.addItem("선택해주세요.", "-");
        adapter.notifyDataSetChanged();
        spinner.setSelection(0);
        socketCommu2 = new SocketCommu(null, 0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    if (socketCommu2.isConnect) {
                        socketCommu2.exitChat();
                    }
                    String xid = adapter.getXid(position);
                    Toast.makeText(Manager.this, xid, Toast.LENGTH_SHORT).show();
                    driverName = adapter.getName(position);
                    socketCommu2 = new SocketCommu(xid, 2);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //아무것도 선택 안됬을 때.
            }
        });
    }

    //################이하 Driver
    public static void driverStateSet(String data) {
        if (settingManager == true) {
            Log.i(TAG, "driverStateSet 작동");
            String busnum;
            String msg[] = data.split("#");
            busnum = msg[0];
            String name[] = msg[1].split(",");
            String ride[] = msg[2].split(",");

            busNum_m.setText(busnum);


            notRidingAdapter.clear();
            ridingAdapter.clear();

            for (int i = 0; i < name.length; i++) {
                if (Integer.parseInt(ride[i]) == 0) {
                    notRidingAdapter.add(name[i]);
                    notRidingAdapter.notifyDataSetChanged();
                } else {
                    ridingAdapter.add(name[i]);
                    ridingAdapter.notifyDataSetChanged();
                }
            }
            ridedNum_m.setText(Integer.toString(ridingAdapter.getCount()));
            return;
        }
        Log.i(TAG, "driverStateSet 작동");
        String busnum;
        String msg[] = data.split("#");
        busnum = msg[0];
        String name[] = msg[1].split(",");
        String ride[] = msg[2].split(",");

        busNum_d.setText(busnum);

        for (int i = 0; i < name.length; i++) {

            if (Integer.parseInt(ride[i]) == 0) {
                notRidingAdapter.add(name[i]);
                notRidingAdapter.notifyDataSetChanged();
            } else {
                ridingAdapter.add(name[i]);
                ridingAdapter.notifyDataSetChanged();
            }
        }
        rided.setText(Integer.toString(ridingAdapter.getCount()));
    }

    //하단 리스트뷰 수정 (changeRide)
    public static void changeRide(String cName, String ride) {
        Log.i(TAG, "changeRide작동");
        if (ride.equals("0")) {
            for (int i = 0; i < notRideArr.size(); i++) {
                if (notRideArr.get(i).equals(cName)) {
                    notRideArr.remove(i);
                    break;
                }
            }
            for (int i = 0; i < rideArr.size(); i++) {
                if (rideArr.get(i).equals(cName)) {
                    rideArr.remove(i);
                    break;
                }
            }
            notRideArr.add(cName);
        } else {
            for (int i = 0; i < rideArr.size(); i++) {
                if (rideArr.get(i).equals(cName)) {
                    rideArr.remove(i);
                    break;
                }
            }
            for (int i = 0; i < notRideArr.size(); i++) {
                if (notRideArr.get(i).equals(cName)) {
                    notRideArr.remove(i);
                }
            }
            rideArr.add(cName);
        }
        ridingAdapter.notifyDataSetChanged();
        notRidingAdapter.notifyDataSetChanged();
    }

    //운행시작 버튼
    public void driveBtn(View v) {
        if(driving==0){
            Log.i(TAG,"운행시작");
            driverButton.setText("운행 종료");
            driving = 1;
            delSharedPreference();
            editor.putInt("driving", driving);
            editor.commit();
            //서비스 실행
            Intent intent = new Intent(this, ForegroundService.class);
            Log.e("service", "서비스인텐스 선언");
            if (Build.VERSION.SDK_INT >= 26){
                try {
                    startForegroundService(intent);
                }catch (SecurityException e){
                    e.printStackTrace();
                }
            Log.e("service", "서비스인텐스 포어그라운드서비수");}
            else{

                startService(intent);
            Log.e("service", "서비스인텐스 걍 서바수");}
        }
        else {

            if (!rideArr.isEmpty()) {
                Toast.makeText(context, "내리지 않은 아이가 있습니다!", Toast.LENGTH_SHORT).show();
                return;
            }

            //서비스 종료
            Intent intent = new Intent(this, ForegroundService.class);
            stopService(intent);

            driverButton.setText("운행 시작");
            Log.i(TAG,"운행종료");
            driving = 0;
            //리스너 호출 종료

            delSharedPreference();
            editor.putInt("driving", driving);
            editor.commit();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkAcc(){
        //권한확인
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            //권한을 허용하지 않았다면
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //다이얼로그 선언
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                // 제목셋팅
                alertDialogBuilder.setTitle("접근권한 오류");
                // AlertDialog 셋팅
                alertDialogBuilder
                        .setMessage("해당 기능을 이용하기 위해서는 권한 허용이 필수적입니다. 프로그램을 종료합니다.")
                        .setCancelable(false)
                        .setPositiveButton("확인",
                                new DialogInterface.OnClickListener() {
                                    public void onClick( DialogInterface dialog, int id) {
                                        // 앱을 종료한다
                                        finish();
                                    }
                                });
                // 다이얼로그 생성
                AlertDialog alertDialog = alertDialogBuilder.create();
                // 다이얼로그 보여주기
                alertDialog.show();
            }
            return;
        }
    }
    //sharedPrefernce에 저장된 driving key 가 있는지 확인하고, 있다면 삭제한다.
    public void delSharedPreference(){
        if(pref.getInt("driving", -1) == -1) {
            editor.remove("driving");
            editor.commit();
        }
    }
    //최초시작지 sharedPreference에 저장된 driving key를 있는지 확인하고,
    //운행중이였다면, 포어그라운드 서비스 실행
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void findSharedPreference(){
        if(pref.getInt("driving", -1) == 1) {
            driving = 1;
            //서비스 실행
            Intent intent = new Intent(this, ForegroundService.class);
            Log.e("service", "서[비스인텐스 선언");
            if (Build.VERSION.SDK_INT >= 26){
                try {
                    startForegroundService(intent);
                }catch (SecurityException e){
                    e.printStackTrace();
                }
                Log.e("service", "서비스인텐스 포어그라운드서비수");
            }
            else{
                startService(intent);
                Log.e("service", "서비스인텐스 걍 서바수");
            }
            driverButton.setText("운행 종료");
        }
    }


    //원장용
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            Date rightNow = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");
            String dateString[] = formatter.format(rightNow).split(":");
            String hour = dateString[0];
            String min = dateString[1];
            if(Integer.parseInt(hour)>13){
                hour = "PM " + (Integer.parseInt(hour)-12);
            }
            else{
                hour = "AM " + (Integer.parseInt(hour));
            }
            timeTV.setText(hour + " : " + min);
        }
    };

    class MainTimerTask extends TimerTask {
        public void run() {
            mHandler.post(mUpdateTimeTask);
        }
    }

    public static void driversStateSet(String msg){
        //최초작동시엔 spinner등 조기를 채우고,이후엔 driver의 정보 업데이트.
        if(settingManager==false) {
            Log.i(TAG, "driversStateSet 작동");
            // name,xid#name,xid...
            String datas[] = msg.split("#");
            for (String st : datas) {
                String data[] = st.split(",");
                adapter.addItem(data[0], data[1]);
            }
            adapter.notifyDataSetChanged();
            settingManager=true;
        }

    }

    //공용
    public void controlTimTextView(){
        MainTimerTask timerTask = new MainTimerTask();
        timer = new Timer();
        timer.schedule(timerTask, 500, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(auth==3)
            timer.cancel();
    }
    @Override
    protected void onPause() {
        if(auth==3)
            timer.cancel();
        super.onPause();
    }

    @Override
    protected void onResume() {
        MainTimerTask timerTask = new MainTimerTask();
        if(auth==3)
            timer.schedule(timerTask, 500, 3000);
        super.onResume();
    }
}
