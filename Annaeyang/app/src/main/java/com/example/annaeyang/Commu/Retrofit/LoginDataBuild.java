package com.example.annaeyang.Commu.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginDataBuild {

    @SerializedName("xid")
    @Expose
    public String xid;
    @SerializedName("auth")
    @Expose
    public int auth;
}
