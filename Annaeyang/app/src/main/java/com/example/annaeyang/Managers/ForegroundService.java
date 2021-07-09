package com.example.annaeyang.Managers;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.annaeyang.Commu.SocketCommu;
import com.example.annaeyang.R;

public class ForegroundService extends Service {

    SocketCommu socketCommu;
    //기사 좌표를 구하기 위핸 LocationManager
    LocationManager locationManager;
    LocationListener locationListener;
    boolean isNetworkEnabled;
    public String point; // 기사의 위치좌표를 저장

    //로그인 계정확인용
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public ForegroundService() { }
    public boolean loop = true;
    String TAG = "LocationService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service 생성");
        loop = true;
        socketCommu = Manager.socketCommu;

        //좌표계산
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        //기존 좌표를 구한 시간으로부터 1초 이후 + 1m 이상 이동시 리스너 발동
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                //위도와 경도를 받아 server에 송신
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                point = Double.toString(lat) + "," + Double.toString(lng);
                socketCommu.send("point#" + point);
                Log.d("LocationService", "latitude: " + lat + ", longitude: " + lng);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("onStatusChanged", "ok");
            }

            public void onProviderEnabled(String provider) {
                Log.d("onProviderEnabled", "ok");
            }

            public void onProviderDisabled(String provider) {
                Log.d("onProviderDisabled", "ok");
            }
        };

        startForegroundService();
        sendLocationInfo();
    }

    void startForegroundService() {
        Log.i(TAG, "startFroegroundService 호풀");
        Intent notificationIntent = new Intent(this, Manager.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_service);

        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "snwodeer_service_channel";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "SnowDeer Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);

            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(this);
        }
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContent(remoteViews)
                .setContentIntent(pendingIntent);

        startForeground(1, builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void sendLocationInfo() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, locationListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service 종료");
        locationManager.removeUpdates(locationListener);
    }
}
