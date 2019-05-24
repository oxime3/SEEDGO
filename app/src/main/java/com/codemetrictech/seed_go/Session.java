package com.codemetrictech.seed_go;

import android.content.Context;
import android.content.SharedPreferences;

public class Session {
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private Context context;

    Session(Context context){
        this.context = context;
        sp = context.getSharedPreferences("swen_forums", Context.MODE_PRIVATE);
        editor = sp.edit();

    }

    void setLoggedin(boolean isLoggedIn){
        editor.putBoolean("loggedInMode", isLoggedIn);
        editor.commit();
    }

    public boolean isLoggedIn(){
        return sp.getBoolean("loggedInMode", false);
    }


    void setSavedUsername(String username){
        editor.putString("UserID", username);
        editor.commit();
    }

    public String getSavedUsername(){
        return sp.getString("UserID", "User Not Logged In");
    }

}