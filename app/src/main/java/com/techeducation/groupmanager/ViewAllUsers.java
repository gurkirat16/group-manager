package com.techeducation.groupmanager;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewAllUsers extends AppCompatActivity {

    ArrayList<AllUsersBean> allUsersDataList;
    AllUsersAdapter adapter;
    RecyclerView recyclerView;
    EditText eTxtSearch;

    void initViews() {


        recyclerView = (RecyclerView) findViewById(R.id.recyclerAllUsers);
        eTxtSearch = (EditText) findViewById(R.id.eTxtSearch);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        handler.sendEmptyMessage(100);
        eTxtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.listUpdate(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_users);
        initViews();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            final int allUsers=1;

            if(msg.what==100){
                StringRequest stringRequest = new StringRequest(Request.Method.POST, ConnectionUtil.PHP_LIST_OF_USERS_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {

                            JSONObject jsonObject = new JSONObject(s);
                            JSONArray userArray = jsonObject.getJSONArray("userArray");

                            allUsersDataList = new ArrayList();
                            Log.i("show",userArray.toString());

                            for(int i=0;i<userArray.length();i++){
                                JSONObject jsonObject1 = userArray.getJSONObject(i);
                                allUsersDataList.add(new AllUsersBean(jsonObject1.getInt("user_id"),jsonObject1.getString("username")));
                            }

                            adapter = new AllUsersAdapter(allUsersDataList);
                            recyclerView.setAdapter(adapter);

                            recyclerView.addOnItemTouchListener(
                                    new RecyclerItemClickListener(getApplicationContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                                        @Override public void onItemClick(View view, int position) {
                                            Intent i = new Intent(getApplicationContext(),ProfileActivity.class);
                                            AllUsersBean allUsersBean = (AllUsersBean) allUsersDataList.get(position);
                                            i.putExtra("keyUserId",allUsersBean.getuserid());
                                            startActivity(i);

                                        }

                                        @Override public void onLongItemClick(View view, int position) {

                                        }
                                    })
                            );
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"Some Connectivity Error",Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.i("show",volleyError.toString());
                        Toast.makeText(getApplicationContext(),"Some Connectivity Error",Toast.LENGTH_LONG).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<>();
                        params.put("allUsers",String.valueOf(allUsers));
                        params.put("user_id",String.valueOf(StartActivity.user_id));
                        return params;
                    }
                };
                AppController.getInstance().addToRequestQueue(stringRequest, "json_obj_req");
            }

        }
    };

}
