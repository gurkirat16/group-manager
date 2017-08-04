package com.techeducation.groupmanager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PostEventActivity extends AppCompatActivity {
    Button btnChooseDate,btnChooseTime,postEvent;
    TextView txtDate,txtTime;
    Calendar calendar;
    EditText etxtEventName, etxtEventVenue, etxtEventDesc;
    boolean validation = true;

    void initViews(){

        calendar= Calendar.getInstance();
        txtDate=(TextView)findViewById(R.id.txtDate);
        btnChooseDate=(Button)findViewById(R.id.btnChooseDate);
        btnChooseTime=(Button)findViewById(R.id.btnChooseTime);
        txtTime=(TextView)findViewById(R.id.txtTime);
        postEvent = (Button)findViewById(R.id.btnpost);
        etxtEventName = (EditText)findViewById(R.id.eventName);
        etxtEventVenue = (EditText)findViewById(R.id.eventVenue);
        etxtEventDesc = (EditText)findViewById(R.id.eventDesc);

        initTimeAndDate();

        btnChooseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(PostEventActivity.this,time,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true).show();
            }
        });
        btnChooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(PostEventActivity.this,date,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        postEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if(networkInfo != null && networkInfo.isConnectedOrConnecting()){
                    handler.sendEmptyMessage(100);
                    Toast.makeText(PostEventActivity.this, "Event Posted", Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(PostEventActivity.this, "Not Connected", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home: {
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Post Event");
        initViews();
    }
    void  initTimeAndDate(){
        Calendar c =Calendar.getInstance();
        txtTime.setText(String.format("%02d:%02d",c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE)));
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = sdf.format(c.getTime());
        txtDate.setText(formattedDate);
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            calendar.set(year, month, dayOfMonth);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = sdf.format(calendar.getTime());
            txtDate.setText(formattedDate);
        }
    };
    TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener(){
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            txtTime.setText(String.format("%02d:%02d",hourOfDay,minute));
        }
    };

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String title, venue, date, time, description;

            title = etxtEventName.getText().toString().trim();
            venue = etxtEventVenue.getText().toString().trim();
            date = txtDate.getText().toString().trim();
            time = txtTime.getText().toString().trim();
            description = etxtEventDesc.getText().toString().trim();

            if(title.isEmpty()){
                validation = false;
                etxtEventName.setError("Required Field");
            }
            if(venue.isEmpty()){
                validation = false;
                etxtEventVenue.setError("Required Field");
            }
            if(date.isEmpty()){
                validation = false;
                txtDate.setError("Required Field");
            }
            if(time.isEmpty()){
                validation = false;
                txtTime.setError("Required Field");
            }
            if(description.isEmpty()){
                validation = false;
                etxtEventDesc.setError("Required Field");
            }

            if(validation){
                JSONObject jsoneventObject = new JSONObject();
                try {
                    jsoneventObject.put("title", title);
                    jsoneventObject.put("venue", venue);
                    jsoneventObject.put("date", date);
                    jsoneventObject.put("time", time);
                    jsoneventObject.put("description", description);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.i("eventObject", "sentObject: " + jsoneventObject.toString());

                JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, ConnectionUtil.PHP_POST_EVENT_URL, jsoneventObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("eventObject", "receivedObj: " + response.toString());
                        try {
                            int error = response.getInt("error");
                            Log.i("eventObject", "err no. from server: " + error);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("eventObject", "error in errListener: " + error.toString());
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }
                };
                AppController.getInstance().addToRequestQueue(req, "json_obj_req");
            }
        }
    };
}