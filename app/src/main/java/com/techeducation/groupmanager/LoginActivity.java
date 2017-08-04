package com.techeducation.groupmanager;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.R.attr.password;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    EditText etxtemail, etxtpass;
    TextView tvRegister;
    Button btnlogin;
    CheckBox chkboxPass;
    UserSessionManager session;

    void initViews(){
        chkboxPass = (CheckBox)findViewById(R.id.chkboxShwPass);
        tvRegister = (TextView)findViewById(R.id.register);
        etxtpass = (EditText)findViewById(R.id.password);
        etxtemail = (EditText)findViewById(R.id.emailid);
        btnlogin = (Button)findViewById(R.id.login);

        session = new UserSessionManager(getApplicationContext());

        btnlogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);

        chkboxPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    etxtpass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    etxtpass.setSelection(etxtpass.getText().length());
                }else{
                    etxtpass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    etxtpass.setSelection(etxtpass.getText().length());
                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(R.layout.activity_login);
        initViews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login :

                if(!isValidEmail(etxtemail.getText().toString().trim())){
                    etxtemail.setError("Invalid Email");
                }
                else if(etxtpass.getText().toString().isEmpty()){
                    etxtpass.setError("Invalid Password");
                }else {
                    handler.sendEmptyMessage(100);
                }
                break;

            case R.id.register :
                etxtpass.setText("");
                etxtemail.setText("");
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.putExtra("callingActivity",101);
                startActivity(intent);

                break;
        }
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            final String email = etxtemail.getText().toString().trim();
            final String password = etxtpass.getText().toString().trim();


            try {
                JSONObject loginObject = new JSONObject();
                loginObject.put("email",email);
                loginObject.put("password",password);

                JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.POST, ConnectionUtil.PHP_LOGIN_URL, loginObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {

                    Log.i("show","in response");
                    Log.i("show",jsonObject.toString());

                    try {
                        int status = jsonObject.getInt("error");
                        Log.i("show","status "+status);

                        switch(status){
                            case 0:
                                if(jsonObject.getInt("suspend")==1){
                                    Toast.makeText(LoginActivity.this, "Your access is suspended by admin", Toast.LENGTH_SHORT).show();
                                }else{
                                    Log.i("show","in switch status "+status);
                                    int access = jsonObject.getInt("access");
                                    int user_id = jsonObject.getInt("user_id");
                                    Log.i("show","before access "+access+" user_id "+user_id);
                                    session.createLoginSession(email,password,access,user_id);
                                    Log.i("show","access "+access+" user_id "+user_id);
                                    if(access==1){
                                        Intent i = new Intent(getApplicationContext(), AdminActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                        finish();
                                    }
                                    else if(access==2 || access==3){
                                        Intent i = new Intent(getApplicationContext(), StudentPanelActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                        finish();
                                    }
                                }


                                break;
                            case 1:
                                Toast.makeText(getApplicationContext(),jsonObject.getString("msg"),Toast.LENGTH_LONG).show();
                                break;
                            case 2:
                                Toast.makeText(getApplicationContext(),jsonObject.getString("msg"),Toast.LENGTH_LONG).show();
                                break;
                            case 3:
                                Toast.makeText(getApplicationContext(),jsonObject.getString("msg"),Toast.LENGTH_LONG).show();
                                break;
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.i("show",volleyError.toString());
                }
            });
            AppController.getInstance().addToRequestQueue(jsonObjRequest,"req_login");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        System.exit(0);
    }
}