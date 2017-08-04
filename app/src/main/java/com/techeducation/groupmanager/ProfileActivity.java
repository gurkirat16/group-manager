package com.techeducation.groupmanager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    public static Activity reference;
    LinearLayout LLTechKnown,LLLangKnown, LLProjDetails;
    int user_id_other,user_id;
    TextView username,email,phone,skype,github,branch,year;
    RadioGroup radioGrpDaySch;
    RadioButton rbDayScYes,rbDayScNo;
    ToggleButton toggleSuspend,toggleAccess;
    FloatingActionButton fab;
    JSONObject jsonObjectProfileData = null;
    CardView cardViewSuspend, cardViewAccess;
    ProgressBar progressBar;
    LinearLayout linearLayoutProfile;

    void initViews() {

        user_id = StartActivity.user_id;
        Intent rcv = getIntent();
        user_id_other = rcv.getIntExtra("keyUserId",0);


        LLTechKnown = (LinearLayout)findViewById(R.id.LLTechKnown);
        LLLangKnown = (LinearLayout)findViewById(R.id.LLLangKnown);
        LLProjDetails = (LinearLayout)findViewById(R.id.LLProjDetails);

        cardViewAccess = (CardView)findViewById(R.id.cardViewAccess);
        cardViewSuspend = (CardView)findViewById(R.id.cardViewSuspend);

        username = (TextView) findViewById(R.id.username);
        email = (TextView) findViewById(R.id.email);
        skype = (TextView) findViewById(R.id.skype);
        phone = (TextView) findViewById(R.id.phone);
        github = (TextView) findViewById(R.id.github);
        branch = (TextView)findViewById(R.id.branch);
        year = (TextView)findViewById(R.id.year);
        radioGrpDaySch = (RadioGroup)findViewById(R.id.radioGrpDaySch);
        rbDayScYes = (RadioButton)findViewById(R.id.DaySchYes);
        rbDayScNo = (RadioButton)findViewById(R.id.DaySchNo);

        handler.sendEmptyMessage(100);

        //first condition: when admin is accessing some other's info
        //second condition: when any member is accessing his own info
        if((user_id_other!=0 && StartActivity.access==1) || user_id_other==0){
            Log.i("show","it is admin viewing other user's information");
            fab = (FloatingActionButton) findViewById(R.id.fabEditProfile);
            fab.setVisibility(View.VISIBLE);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(ProfileActivity.this,RegisterActivity.class);
                    i.putExtra("jsonObjectProfileData",jsonObjectProfileData.toString());
                    i.putExtra("callingActivity",102);
                    if(user_id_other==0)
                        i.putExtra("user_id",StartActivity.user_id);
                    else
                        i.putExtra("user_id",user_id_other);
                    startActivity(i);
                }
            });
        }

        //if admin only then can edit profile
        if(StartActivity.access==1){

            Log.i("show","this is admin");
            toggleSuspend = (ToggleButton)findViewById(R.id.toggleSuspend);
            cardViewSuspend.setVisibility(View.VISIBLE);

            toggleAccess = (ToggleButton)findViewById(R.id.toggleAccess);
            cardViewAccess.setVisibility(View.VISIBLE);

            toggleSuspend.setOnClickListener(new View.OnClickListener() {
                int suspend;
                @Override
                public void onClick(View v) {
                    String str = toggleSuspend.getText().toString();
                    Log.i("show","toggle clicked: "+str);

                    if(str.equals(getResources().getString(R.string.toggleSuspendOff))){
                        suspend =0;
                    }else if(str.equals(getResources().getString(R.string.toggleSuspendOn))){
                        suspend=1;
                    }
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, ConnectionUtil.PHP_CHANGE_SUSPEND_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            HashMap<String,String> params = new HashMap<>();
                            params.put("user_id",String.valueOf(user_id_other));
                            params.put("suspend",String.valueOf(suspend));
                            return params;
                        }
                    };

                    AppController.getInstance().addToRequestQueue(stringRequest,"json_obj_req");
                }
            });

            toggleAccess.setOnClickListener(new View.OnClickListener() {
                int access;
                @Override
                public void onClick(View v) {
                    String str = toggleAccess.getText().toString();
                    if(str.equals(getResources().getString(R.string.toggleAccessOff))){
                        access = 3;
                    }else if(str.equals(getResources().getString(R.string.toggleAccessOn))){
                        access = 2;
                    }

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, ConnectionUtil.PHP_CHANGE_ACCESS_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            HashMap<String,String> params = new HashMap<>();
                            params.put("user_id",String.valueOf(user_id_other));
                            params.put("access",String.valueOf(access));
                            return params;
                        }
                    };

                    AppController.getInstance().addToRequestQueue(stringRequest,"json_obj_req");
                }
            });
        }
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        linearLayoutProfile = (LinearLayout)findViewById(R.id.linLayoutProfile);
        linearLayoutProfile.setVisibility(View.GONE);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        reference = this;

        initViews();

    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            JSONObject jsonObject = new JSONObject();
            try {
                if(user_id_other==0){
                    //when user opens his own profile
                    jsonObject.put("user_id",StartActivity.user_id);
                    cardViewAccess.setVisibility(View.GONE);
                    cardViewSuspend.setVisibility(View.GONE);
                }
                else{
                    jsonObject.put("user_id",user_id_other);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ConnectionUtil.PHP_VIEW_PROFILE_URL, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject jsonObject) {
                        try {

                            JSONObject profileObject = jsonObject.getJSONObject("profile");
                            jsonObjectProfileData = profileObject;

                            Log.i("show",profileObject.toString());

                            JSONObject userPersonalInfo = profileObject.getJSONObject("user_personal");
                            String username1 = userPersonalInfo.getString("username");
                            String email1 = userPersonalInfo.getString("email");
                            String phone1 = userPersonalInfo.getString("phone_no");
                            String skype1 = userPersonalInfo.getString("skype_id");
                            String github1 = userPersonalInfo.getString("github");
                            int suspend = userPersonalInfo.getInt("suspend");
                            int access = userPersonalInfo.getInt("access");

                            username.setText(username1);
                            email.setText(email1);
                            phone.setText(phone1);
                            skype.setText(skype1);
                            github.setText(github1);


                            branch.setText(userPersonalInfo.getString("branch"));
                            year.setText(userPersonalInfo.getString("year"));
                            if(userPersonalInfo.getInt("dayscholar")==1){
                                rbDayScYes.setChecked(true);
                            }
                            else{
                                rbDayScNo.setChecked(true);
                            }


                            JSONArray languageArray = profileObject.getJSONArray("languages");
                            JSONArray technologiesArray = profileObject.getJSONArray("technologies");

                            if(languageArray.length()==0){
                                TextView textView = new TextView(ProfileActivity.this);
                                textView.setText("None");
                                textView.setTextSize(18);
                                textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextEventList));
                                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                                textView.setLayoutParams(lparams);
                                LLLangKnown.addView(textView);
                            }else{
                                for(int i=0;i<languageArray.length();i++){
                                    TextView textView = new TextView(getApplicationContext());
                                    textView.setText(languageArray.getString(i));
                                    textView.setTextSize(17);
                                    textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextEventList));
                                    LLLangKnown.addView(textView);
                                }
                            }

                            if(technologiesArray.length()==0){
                                TextView textView = new TextView(ProfileActivity.this);
                                textView.setText("None");
                                textView.setTextSize(18);
                                textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextEventList));
                                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                                textView.setLayoutParams(lparams);
                                LLTechKnown.addView(textView);

                            }else{
                                for(int i=0;i<technologiesArray.length();i++){

                                    TextView textView = new TextView(getApplicationContext());
                                    textView.setText(technologiesArray.getString(i));
                                    textView.setTextSize(17);
                                    textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextEventList));
                                    LLTechKnown.addView(textView);
                                }

                            }

                            JSONArray projectsArray = profileObject.getJSONArray("projects");

                            if(projectsArray.length()==0){
                                TextView textView = new TextView(ProfileActivity.this);
                                textView.setText("None");
                                textView.setTextSize(18);
                                textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextEventList));
                                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                                textView.setLayoutParams(lparams);
                                LLProjDetails.addView(textView);

                            }else{
                                for(int i=0;i<projectsArray.length();i++){
                                    JSONObject projectObject = (JSONObject) projectsArray.get(i);

                                    LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                                    LinearLayout llProjectTitle = new LinearLayout(getApplicationContext());
                                    llProjectTitle.setLayoutParams(lparams);
                                    llProjectTitle.setOrientation(LinearLayout.HORIZONTAL);
                                    llProjectTitle.setWeightSum(1);

                                    TextView txtNumber = new TextView(getApplicationContext());
                                    txtNumber.setTextSize(17);
                                    String number = (i+1)+".";
                                    txtNumber.setText(number);
                                    txtNumber.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.colorTextEventList));

                                    TextView textTitle = new TextView(getApplicationContext());
                                    textTitle.setTextSize(17);
                                    textTitle.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.colorTextEventList));

                                    textTitle.setText(projectObject.getString("proj_title"));


                                    llProjectTitle.addView(txtNumber);
                                    llProjectTitle.addView(textTitle);

                                    LLProjDetails.addView(llProjectTitle);

                                    if(!projectObject.getString("proj_url").equals("")){
                                        LinearLayout llProjectUrl = new LinearLayout(getApplicationContext());
                                        llProjectUrl.setLayoutParams(lparams);

                                        llProjectUrl.setOrientation(LinearLayout.HORIZONTAL);
                                        llProjectUrl.setWeightSum(1);

                                        TextView txtNumber1 = new TextView(getApplicationContext());
                                        txtNumber1.setTextSize(17);
                                        txtNumber1.setText(String.format("%"+number.length()+"s",""));

                                        TextView textUrl = new TextView(getApplicationContext());
                                        textUrl.setTextSize(17);
                                        textUrl.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.colorTextEventList));
                                        textUrl.setText(projectObject.getString("proj_url"));

                                        llProjectUrl.addView(txtNumber1);
                                        llProjectUrl.addView(textUrl);
                                        LLProjDetails.addView(llProjectUrl);
                                    }

                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,20);
                                    View view = new View(getApplicationContext());
                                    view.setLayoutParams(params);
                                    LLProjDetails.addView(view);


                                }
                            }
                            Log.i("show","suspend val : "+suspend);
                            if(StartActivity.access==1){
                                if(suspend==1)
                                    toggleSuspend.setChecked(true);
                                else
                                    toggleSuspend.setChecked(false);
                                if(access==2)
                                    toggleAccess.setChecked(true);
                                else
                                    toggleAccess.setChecked(false);
                            }

                            progressBar.setVisibility(View.GONE);
                            linearLayoutProfile.setVisibility(View.VISIBLE);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });
                AppController.getInstance().addToRequestQueue(jsonObjectRequest, "json_obj_req");




        }
    };

}