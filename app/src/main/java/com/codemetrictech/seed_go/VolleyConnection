package com.codemetrictech.seed_go;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyConnection {

    private static VolleyConnection connection;
    private RequestQueue rq;
    private Context mcontext;

    private VolleyConnection(Context context){
        mcontext = context;
        rq = getRequestQue();
    }

    public RequestQueue getRequestQue(){

        if(rq == null){
            rq = Volley.newRequestQueue(mcontext.getApplicationContext());
        }
        return rq;
    }

    public static synchronized VolleyConnection getInstance(Context context){

        if(connection == null){
            connection = new VolleyConnection(context);
        }
        return connection;
    }

    public <T> void addRequestQue(Request<T> request) {rq.add(request); }

}
