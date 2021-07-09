package com.example.annaeyang.Commu.Retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetrofitService {
    //로그인 : id와 pw를 받아 해당하는 계정 있는지
    @FormUrlEncoded
    @POST("{page}") Call <LoginDataBuild> loginData(
            @Path("page") String page,
            @Field("xid") String xid,
            @Field("xpw") String xpw,
            @Field("auth") int auth);

    Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://g2rnsky.vps.phps.kr/Anaeyang/")
            .addConverterFactory(GsonConverterFactory.create(gson))
    //.addConverterFactory(ScalarsConverterFactory.create())  // String 등 처리시
            .build();
}