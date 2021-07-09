package com.example.annaeyang.Commu.Retrofit;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetrofitServiceString {

    @FormUrlEncoded
    @POST("{page}")
    Call<String> sendToken(
            @Path("page") String page,
            @Field("token") String token,
            @Field("xid") String xid);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://g2rnsky.vps.phps.kr/Anaeyang/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build();

}
