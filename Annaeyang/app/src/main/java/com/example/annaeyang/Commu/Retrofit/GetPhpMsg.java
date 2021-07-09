package com.example.annaeyang.Commu.Retrofit;

import com.google.gson.annotations.SerializedName;

public class GetPhpMsg {

    @SerializedName("msg")
    public String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
