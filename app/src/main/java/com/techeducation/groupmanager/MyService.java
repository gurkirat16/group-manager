package com.techeducation.groupmanager;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyService extends Service {
    public static String BROADCAST_ACTION = "com.techeducation.groupmanager.suspendvalue";
    Handler handler = new Handler();
    Intent intent;

    public MyService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
        Log.i("show","oncreate service");
    }

    @Override
    public void onStart(Intent intent, int startId) {
       handler.removeCallbacks(queryForSuspend);
        Log.i("show","onStart before service");
        handler.postDelayed(queryForSuspend,1000);
        Log.i("show","onStart after service");
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private Runnable queryForSuspend = new Runnable() {
        int suspend,access;
        @Override
        public void run() {
            Log.i("show","inside queryforSuspend");
            StringRequest stringRequest = new StringRequest(Request.Method.POST, ConnectionUtil.PHP_GET_LOGIN_VALUES_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.i("show","inside string request");
                        JSONObject jsonObject = new JSONObject(response);
                        int error = jsonObject.getInt("error");
                        if(error==0){
                            suspend = jsonObject.getInt("suspend");
                            access = jsonObject.getInt("access");
                            intent.putExtra("suspend",suspend);
                            intent.putExtra("access",access);
                            Log.i("show","Suspend in service: "+suspend);
                            Log.i("show","Access in service: "+access);
                        }

                    } catch (JSONException e) {
                        Log.i("show",e.toString());
                       Toast.makeText(getApplicationContext(),"Connection error",Toast.LENGTH_SHORT).show();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("show",error.toString());
                   Toast.makeText(getApplicationContext(),"Connection error",Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String,String> params = new HashMap<>();
                    params.put("user_id",String.valueOf(StartActivity.user_id));
                    return params;
                }
            };
            AppController.getInstance().addToRequestQueue(stringRequest,"json_obj_req");
            sendBroadcast(intent);
            handler.postDelayed(this, 1000);

        }
    };

    @Override
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
    }
}
