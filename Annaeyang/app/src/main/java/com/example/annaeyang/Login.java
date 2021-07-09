package com.example.annaeyang;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.annaeyang.Commu.Retrofit.LoginDataBuild;
import com.example.annaeyang.Commu.Retrofit.RetrofitService;
import com.example.annaeyang.Managers.Manager;
import com.example.annaeyang.Parents.Parent;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    EditText ID_ET;
    EditText PW_ET;

    Intent intent;
    int auth = 0; //로그인 상태 default = 0; 1매니저 2드라이버 3학부모
    String TAG = "LoginActivity";

    CheckBox parent_cb;
    CheckBox driver_cb;
    CheckBox manager_cb;
    CheckBox autoLogin;

    //sharedpreference로 계정정보 저장
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ID_ET = (EditText) findViewById(R.id.ID_ET);
        PW_ET = (EditText) findViewById(R.id.PW_ET);

        //checkbox 처리
        parent_cb = (CheckBox) findViewById(R.id.loginForParent);
        driver_cb = (CheckBox) findViewById(R.id.loginForDriver);
        manager_cb = (CheckBox) findViewById(R.id.loginForManager);
        autoLogin = (CheckBox) findViewById(R.id.autoLogin);

        parent_cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                driver_cb.setChecked(false);
                manager_cb.setChecked(false);
            }
        });
        driver_cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent_cb.setChecked(false);
                manager_cb.setChecked(false);
            }
        });
        manager_cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent_cb.setChecked(false);
                driver_cb.setChecked(false);
            }
        });

        pref = getSharedPreferences("pref", MODE_PRIVATE);
        editor = pref.edit();
        checkSP();
    }

    public void checkSP(){
        String getSpId = pref.getString("account", "none");
        if( !getSpId.equals("none") ){
            //"xid", auth+","+xid+","+xpw
            String data[] = getSpId.split(",");
            auth = Integer.parseInt(data[0]);
            String xid = data[1];
            String xpw = data[2];
            retrofit(xid, xpw);
        }
    }

    public void LoginBtn(View v) {
        auth = checkedBox();
        if(auth != 0) {
            String xid = ID_ET.getText().toString();
            String xpw = PW_ET.getText().toString();
            retrofit(xid, xpw);
        }else{
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("로그인 오류");
            alertDialogBuilder
                    .setMessage("체크박스를 선택해 주세요.")
                    .setCancelable(false)
                    .setPositiveButton("확인", null);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }
    public int checkedBox(){
        if(manager_cb.isChecked())
            return 1;
        if(driver_cb.isChecked())
            return 2;
        if(parent_cb.isChecked())
            return 3;
        return 0;
    }

    public void retrofit(final String xid, final String xpw){
        RetrofitService rs = RetrofitService.retrofit.create(RetrofitService.class);
        Call<LoginDataBuild> call = rs.loginData("login.php", xid, xpw, auth);
        call.enqueue(new Callback<LoginDataBuild>() {
            @Override
            public void onResponse(Call<LoginDataBuild> call, Response<LoginDataBuild>response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "fail to  get response. try again", Toast.LENGTH_SHORT).show();
                    editor.remove("account");
                    editor.commit();
                }
                if (response.body() == null) {
                    Toast.makeText(getApplicationContext(), "id/pw가 틀립니다..", Toast.LENGTH_SHORT).show();
                    editor.remove("account");
                    editor.commit();
                }
                //로그인 성공
                else {
                    Toast.makeText(Login.this, response.body().xid, Toast.LENGTH_SHORT).show();
                    //자동로그인 선택시 SharedPreference에 계정정보를 저장해둠.
                    if (autoLogin.isChecked()) {
                        editor.putString("account", auth + "," + xid + "," + xpw);
                        editor.commit();
                    }
                    switch (auth) {
                        case 1:
                            intent = new Intent(getApplicationContext(), Manager.class);
                            intent.putExtra("loginedid", xid);
                            intent.putExtra("auth", auth);
                            break;
                        case 2:
                            intent = new Intent(getApplicationContext(), Manager.class);
                            intent.putExtra("loginedid", xid);
                            intent.putExtra("auth", auth);
                            break;
                        case 3:
                            intent = new Intent(getApplicationContext(), Parent.class);
                            intent.putExtra("loginedid", xid);
                            break;
                    }
                    startActivity(intent);
                    finish();
                }
            }
            @Override
            public void onFailure(Call<LoginDataBuild> call, Throwable t) {
                Toast.makeText(Login.this, "해당 내용을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                Log.e(TAG,"정보 불러오기 실패 :" + t.getMessage() );
                Log.e(TAG,"요청 메시지 :"+call.request());
            }
        });
    }
}
