package com.example.annaeyang.Notification;

import android.util.Log;
import com.example.annaeyang.Commu.Retrofit.RetrofitService;
import com.example.annaeyang.Commu.Retrofit.RetrofitServiceString;
import com.google.firebase.messaging.FirebaseMessagingService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseIIDService";

    // [START refresh_token]

    public static void onNewToken(String token, String id) {
        Log.d(TAG, "Refreshed token: " + token);

        sendRegistrationToServer(token, id);
    }

    public static void sendRegistrationToServer(String token, String xid){
        RetrofitServiceString rss = (RetrofitServiceString) RetrofitServiceString.retrofit.create(RetrofitServiceString.class);
        Call<String> call = rss.sendToken("fmc.php", token, xid);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "fail to  get response. try again");
                    return;
                }
                if (response.body() == null) {
                    Log.d(TAG, "responseBody is null");
                    return;
                }
                Log.d(TAG, "responsePhp: " + response);
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "failToResponse: " + t);
            }
        });
    }
}
