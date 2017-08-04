package com.techeducation.groupmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentPanelActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView recyclerView, rvEventStudents;
    UserSessionManager session;
    TextView txtUserStats,txtRecentUsers;
    List recentUsersDataList;
    ArrayList<EventsFeed> eventsFeedList;
    CardView cardViewRecentUsers;
    AllUsersAdapter adapter;
    LinearLayout noNetLayout, netAvailableLayout;
    Button tryAgain;
    Intent intent;
    EventsAdapter eventsAdapter;

    void initViews(){
        txtUserStats = (TextView)findViewById(R.id.userStats);
        txtRecentUsers = (TextView)findViewById(R.id.txtViewRecentUsers);
        cardViewRecentUsers = (CardView)findViewById(R.id.cardRecentUsers);

        intent = new Intent(StudentPanelActivity.this, MyService.class);
        //user stats
        handler.sendEmptyMessage(100);

        rvEventStudents = (RecyclerView)findViewById(R.id.rvEventsStudents);
        rvEventStudents.setLayoutManager(new LinearLayoutManager(this));
        rvEventStudents.setItemAnimator(new DefaultItemAnimator());
        rvEventStudents.setHasFixedSize(true);
        //fetch events feed
        handler.sendEmptyMessage(400);


        if (StartActivity.access == 3) {
            txtRecentUsers.setVisibility(View.GONE);
            cardViewRecentUsers.setVisibility(View.GONE);
        } else {
            recyclerView = (RecyclerView) findViewById(R.id.recyclerRecentUsers);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            //recent user list
            handler.sendEmptyMessage(200);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_student_panel);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        noNetLayout = (LinearLayout)findViewById(R.id.noNetLayout);
        netAvailableLayout = (LinearLayout)findViewById(R.id.netAvailableLayout);
        tryAgain = (Button) findViewById(R.id.btnTryAgain);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            netAvailableLayout.setVisibility(View.VISIBLE);
            noNetLayout.setVisibility(View.GONE);
            initViews();
        } else {
            Toast.makeText(StudentPanelActivity.this, "Not Connected", Toast.LENGTH_SHORT).show();
            netAvailableLayout.setVisibility(View.GONE);
            noNetLayout.setVisibility(View.VISIBLE);
        }

        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo Info = connectivityManager.getActiveNetworkInfo();

                if(Info != null && Info.isConnectedOrConnecting()) {
                    Toast.makeText(StudentPanelActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                    netAvailableLayout.setVisibility(View.VISIBLE);
                    noNetLayout.setVisibility(View.GONE);
                    initViews();
                } else {
                    Toast.makeText(StudentPanelActivity.this, "Not Connected", Toast.LENGTH_SHORT).show();
                    netAvailableLayout.setVisibility(View.GONE);
                    noNetLayout.setVisibility(View.VISIBLE);
                }
            }
        });

    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.viewUsers) {
            handler.sendEmptyMessage(300);

        } else if (id == R.id.evUserHistory) {
            Intent i =new Intent(StudentPanelActivity.this,EventsHistoryActivity.class);
            startActivity(i);

        } else if (id == R.id.myProfile) {
            Intent i =new Intent(StudentPanelActivity.this,ProfileActivity.class);
            startActivity(i);

        } else if (id == R.id.logout) {
            session = new UserSessionManager(getApplicationContext());
            session.logoutUser();
            unregisterReceiver(receiver);
            stopService(intent);

            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(MyService.BROADCAST_ACTION));
        startService(intent);

    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==100){
                StringRequest strUserCountRequest = new StringRequest(Request.Method.POST, ConnectionUtil.PHP_COUNT_USERS_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            switch (jsonObject.getInt("error")){
                                case 0:
                                    txtUserStats.setText(String.valueOf(jsonObject.getInt("num_rows")));
                                    break;
                                case 1:
                                    Toast.makeText(StudentPanelActivity.this,jsonObject.getString("msg"),Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });
                AppController.getInstance().addToRequestQueue(strUserCountRequest, "json_obj_req");
            }
            else if(msg.what==200){
                final int allUsers = 0;
                StringRequest stringRequest = new StringRequest(Request.Method.POST, ConnectionUtil.PHP_LIST_OF_USERS_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {

                            JSONObject jsonObject = new JSONObject(s);
                            JSONArray userArray = jsonObject.getJSONArray("userArray");

                            Log.i("show",userArray.toString());
                            recentUsersDataList = new ArrayList();
                            for(int i=0;i<userArray.length();i++){
                                JSONObject jsonObject1 = userArray.getJSONObject(i);
                                recentUsersDataList.add(new AllUsersBean(jsonObject1.getInt("user_id"),jsonObject1.getString("username")));

                                adapter = new AllUsersAdapter(recentUsersDataList);
                                recyclerView.setAdapter(adapter);
                            }

                            recyclerView.addOnItemTouchListener(
                                    new RecyclerItemClickListener(getApplicationContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                                        @Override public void onItemClick(View view, int position) {
                                            Intent i = new Intent(getApplicationContext(),ProfileActivity.class);
                                            AllUsersBean allUsersBean = (AllUsersBean) recentUsersDataList.get(position);
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
            else if(msg.what==300){
                Intent i = new Intent(StudentPanelActivity.this,ViewAllUsers.class);
                startActivity(i);
            } else if(msg.what == 400) {

                StringRequest request = new StringRequest(Request.Method.POST, ConnectionUtil.PHP_FETCH_FEED, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject object = new JSONObject(response);
                            if(object.getInt("error") == 1){
                                Toast.makeText(StudentPanelActivity.this,object.getString("msg"),Toast.LENGTH_SHORT).show();
                            } else {
                                JSONArray eventsArr = object.getJSONArray("events");
                                eventsFeedList = new ArrayList<>();

                                for (int i = 0; i < eventsArr.length(); i++) {
                                    JSONObject jsonObject = eventsArr.getJSONObject(i);
                                    eventsFeedList.add(new EventsFeed(jsonObject.getInt("id"), jsonObject.getString("title"), jsonObject.getString("venue"), jsonObject.getString("date"), jsonObject.getString("time"), jsonObject.getString("description")));
                                }
                                eventsAdapter = new EventsAdapter(eventsFeedList);
                                rvEventStudents.setAdapter(eventsAdapter);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("event", error.toString());
                        Toast.makeText(getApplicationContext(), "Some Connectivity Error", Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }
                };

                AppController.getInstance().addToRequestQueue(request, "json_obj_req");
            }
        }
    };


    @Override
    protected void onDestroy() {
        Log.i("show","onDestroy studentpanel");
        stopService(intent);
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            System.exit(0);
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(MyService.BROADCAST_ACTION)){
                int val = intent.getIntExtra("integer",0);
                int suspend = intent.getIntExtra("suspend", 0);
                Log.i("show", "suspend in receiver: " + suspend);
                Log.i("show", "val in receiver: " + val);
                if (suspend == 1) {
                    UserSessionManager sessionManager = new UserSessionManager(context);
                    sessionManager.logoutUser();
                    finish();
                    Toast.makeText(context, "Your access is suspended by admin", Toast.LENGTH_SHORT).show();
                    //finish the current activity here
                }
            }

        }
    };
}