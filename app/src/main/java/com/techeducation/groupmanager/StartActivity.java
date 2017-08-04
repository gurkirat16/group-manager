package com.techeducation.groupmanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
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

public class StartActivity extends AppCompatActivity {

    String email;
    public static int access,user_id;
    int suspend;
    TextView appName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        appName = (TextView)findViewById(R.id.appName);

//        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/Oregon.ttf");
//        appName.setTypeface(type);

        setContentView(R.layout.activity_start);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.i("show","inside Runnable Start");
                    if (user_id == 0){
                        Intent i = new Intent(StartActivity.this,LoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();
                    }

                    if(suspend==0){
                        Log.i("show","access   "+access+"");
                        switch (access) {
                            case 1:
                                Intent i = new Intent(StartActivity.this, AdminActivity.class);
                                startActivity(i);
                                break;
                            case 2:
                                Intent j = new Intent(StartActivity.this, StudentPanelActivity.class);
                                startActivity(j);
                                break;
                            case 3:
                                Intent k = new Intent(StartActivity.this, StudentPanelActivity.class);
                                startActivity(k);
                                break;
                        }
                    }
                    else if(suspend==1){
                        Intent i = new Intent(StartActivity.this,LoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }
                }
            },2000);

            Log.i("show","inside Start Activity");
            UserSessionManager session = new UserSessionManager(this);
            HashMap map = session.getUserDetails();
            email = (String) map.get(UserSessionManager.KEY_EMAIL);
            user_id=(int)map.get(UserSessionManager.KEY_USER_ID);

            Log.i("show","map: "+map.toString());

            if (user_id != 0)
                handler.sendEmptyMessage(100);


    }

    Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
        if(msg.what==100){
            Log.i("show","in handler of Start Activity. user_id: "+user_id);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, ConnectionUtil.PHP_GET_LOGIN_VALUES_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.getInt("error")==0){
                            suspend = jsonObject.getInt("suspend");
                            access = jsonObject.getInt("access");
                            Log.i("show","suspend: "+suspend+" access: "+access+" in start activity");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                        Log.i("show","in error repsonse "+error);
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String,String> params = new HashMap<>();
                    params.put("user_id", String.valueOf(user_id));
                    return params;
                }
            };

            AppController.getInstance().addToRequestQueue(stringRequest,"json_obj_req");
        }
        }
    };
}
