package com.techeducation.groupmanager;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
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

public class EventsHistoryActivity extends AppCompatActivity {

    RecyclerView rvEventsHistory;
    List<EventsFeed> eventsFeedList;
    EventsAdapter eventsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rvEventsHistory = (RecyclerView)findViewById(R.id.rvEventsHistory);
        rvEventsHistory.setLayoutManager(new LinearLayoutManager(this));
        rvEventsHistory.setItemAnimator(new DefaultItemAnimator());
        rvEventsHistory.setHasFixedSize(true);
        handler.sendEmptyMessage(100);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:{
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 100) {

                StringRequest request = new StringRequest(Request.Method.POST, ConnectionUtil.PHP_FETCH_EVENTS_HISTORY, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject object = new JSONObject(response);
                            if (object.getInt("error") == 1) {
                                Toast.makeText(EventsHistoryActivity.this, object.getString("msg"), Toast.LENGTH_SHORT).show();
                            } else if (object.getInt("error") == 2) {
                                Toast.makeText(EventsHistoryActivity.this, object.getString("msg"), Toast.LENGTH_SHORT).show();
                            } else {
                                JSONArray eventsArr = object.getJSONArray("events");
                                Log.i("showobj1","admin event array "+eventsArr.toString());
                                eventsFeedList = new ArrayList<>();

                                for (int i = 0; i < eventsArr.length(); i++) {
                                    JSONObject jsonObject = eventsArr.getJSONObject(i);
                                    eventsFeedList.add(new EventsFeed(jsonObject.getInt("id"), jsonObject.getString("title"), jsonObject.getString("venue"), jsonObject.getString("date"), jsonObject.getString("time"), jsonObject.getString("description")));

                                    Log.i("showobj1","admin array i "+i);
                                }

                                eventsAdapter = new EventsAdapter(eventsFeedList);
                                rvEventsHistory.setAdapter(eventsAdapter);

                                Log.i("showobj1","admin list events "+eventsFeedList.toString());

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
}
