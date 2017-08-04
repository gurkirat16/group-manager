package com.techeducation.groupmanager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    int callingActivity;
    int user_id;
    JSONArray arrayTech, arrayLang, arrayProjects;
    JSONObject objProjects, jsonRequestObject, jsonProfileData;
    ImageView imgArrow;
    RadioGroup rGrpYear, rGrpSchHost;
    Button btnRegister;
    EditText eTxtTech, eTxtName, eTxtEmail, eTxtPhone, eTxtSkype, eTxtGithub, eTxtLang, eTxtProjTitle, eTxtProjURL, eTxtPassword;
    ImageButton btnAddTech, btnAddLang, btnAddProj;
    Spinner spinnerBranch;
    LinearLayout linearLayoutTech, linLayoutHoriTech, linearLayoutLang, linLayoutHoriLang, linearLayoutProj, linLayoutVertProj, linLayoutProjTitle;
    ArrayList<EditText> listRefTechETxt, listRefLangETxt, listRefProjTitleETxt, listRefProjURLETxt;
    ArrayList<ImageButton> listRefTechBtn, listRefLangBtn, listRefProjBtn;
    ArrayList<LinearLayout> listRefTechLinLayout, listRefLangLinLayout, listRefProjTitleLinLayout;
    static int idTech, idLang, idProj;

    String[] branches;
    ArrayAdapter<String> dataAdapter;

    void initViews() throws JSONException {
        eTxtName = (EditText) findViewById(R.id.eTxtName);
        eTxtEmail = (EditText) findViewById(R.id.eTxtEmail);
        eTxtPhone = (EditText) findViewById(R.id.eTxtPhone);
        eTxtSkype = (EditText) findViewById(R.id.eTxtSkype);
        eTxtGithub = (EditText) findViewById(R.id.eTxtGithub);
        eTxtPassword = (EditText) findViewById(R.id.eTxtPassword);

        btnRegister = (Button) findViewById(R.id.btnRegister);

        rGrpYear = (RadioGroup) findViewById(R.id.radioGrpYear);
        rGrpSchHost = (RadioGroup) findViewById(R.id.radioGrpSchHost);

        imgArrow = (ImageView) findViewById(R.id.imgArrow);

        spinnerBranch = (Spinner) findViewById(R.id.spinnerBranch);

        eTxtTech = (EditText) findViewById(R.id.eTxtTech);
        btnAddTech = (ImageButton) findViewById(R.id.btnTechAdd);

        eTxtLang = (EditText) findViewById(R.id.eTxtLang);
        btnAddLang = (ImageButton) findViewById(R.id.btnLangAdd);

        eTxtProjTitle = (EditText) findViewById(R.id.eTxtProjTitle);
        eTxtProjURL = (EditText) findViewById(R.id.eTxtProjUrl);
        btnAddProj = (ImageButton) findViewById(R.id.btnAddProj);

        linearLayoutTech = (LinearLayout) findViewById(R.id.linearLayoutTech);
        linLayoutHoriTech = (LinearLayout) findViewById(R.id.linLayoutHorizTech);

        linearLayoutLang = (LinearLayout) findViewById(R.id.linearLayoutLang);
        linLayoutHoriLang = (LinearLayout) findViewById(R.id.linLayoutHorizLang);

        linearLayoutProj = (LinearLayout) findViewById(R.id.linearLayoutProj);
        linLayoutVertProj = (LinearLayout) findViewById(R.id.linLayoutVertProj);
        linLayoutProjTitle = (LinearLayout) findViewById(R.id.linLayoutProjTitle);


        listRefTechETxt = new ArrayList();
        listRefTechBtn = new ArrayList();
        listRefTechLinLayout = new ArrayList();

        listRefLangETxt = new ArrayList();
        listRefLangBtn = new ArrayList();
        listRefLangLinLayout = new ArrayList();

        listRefProjTitleETxt = new ArrayList();
        listRefProjURLETxt = new ArrayList();
        listRefProjBtn = new ArrayList();
        listRefProjTitleLinLayout = new ArrayList();

        setDrawables();

        setSpinnerBranch();

        Intent i =getIntent();
        callingActivity = i.getIntExtra("callingActivity",0);
        //Profile Activity is calling Register Activity
        if(callingActivity==102){
            setTitle("Update Profile");
            user_id = i.getIntExtra("user_id",0);

            jsonProfileData = new JSONObject(i.getStringExtra("jsonObjectProfileData"));
            initData();
        }

        Log.i("show","user id is: "+user_id);

        btnAddTech.setOnClickListener(this);

        btnAddLang.setOnClickListener(this);

        btnAddProj.setOnClickListener(this);

        btnRegister.setOnClickListener(this);
    }
    void initData() throws JSONException {
        btnRegister.setText("SAVE");
        eTxtPassword.setVisibility(View.GONE);
        JSONObject userPersonalInfo = jsonProfileData.getJSONObject("user_personal");
        String username1 = userPersonalInfo.getString("username");
        String email1 = userPersonalInfo.getString("email");
        String phone1 = userPersonalInfo.getString("phone_no");
        String skype1 = userPersonalInfo.getString("skype_id");
        String github1 = userPersonalInfo.getString("github");

        eTxtName.setText(username1);
        eTxtEmail.setText(email1);
        eTxtPhone.setText(phone1);
        eTxtSkype.setText(skype1);
        eTxtGithub.setText(github1);


        if(userPersonalInfo.getString("branch")!=null){
            //compare branch value to values in dataAdapter
            int spinnerPosition = dataAdapter.getPosition(userPersonalInfo.getString("branch"));
            //select that particular branch from spinner
            spinnerBranch.setSelection(spinnerPosition);
        }

        int year = userPersonalInfo.getInt("year");
        RadioButton btn;
        switch (year){
            case 1:
                btn = (RadioButton)findViewById(R.id.radioBtn1stYear);
                btn.setChecked(true);
                break;
            case 2:
                btn = (RadioButton)findViewById(R.id.radioBtn2ndYear);
                btn.setChecked(true);
                break;
            case 3:
                btn = (RadioButton)findViewById(R.id.radioBtn3rdYear);
                btn.setChecked(true);
                break;
            case 4:
                btn = (RadioButton)findViewById(R.id.radioBtn4thYear);
                btn.setChecked(true);
                break;
        }
        int dayScholar = userPersonalInfo.getInt("dayscholar");
        RadioButton btnDaySc;
        if(dayScholar==1){
            btnDaySc = (RadioButton)findViewById(R.id.rbDaySch);
            btnDaySc.setChecked(true);
        }else{
            btnDaySc = (RadioButton)findViewById(R.id.rbHosteler);
            btnDaySc.setChecked(true);
        }

        JSONArray technologiesArray = jsonProfileData.getJSONArray("technologies");
        if(technologiesArray.length()==1){
            eTxtTech.setText(technologiesArray.getString(0));
        }else if(technologiesArray.length()>1){
            eTxtTech.setText(technologiesArray.getString(0));
            for(int index=1;index<technologiesArray.length();index++) {


                EditText eTxtTechMore = (EditText) getLayoutInflater().inflate(R.layout.edit_text_layout, null);
                ImageButton btnDeleteTech = new ImageButton(this);
                LinearLayout linearLayoutMoreTech = new LinearLayout(this);

                btnDeleteTech.setImageResource(R.drawable.minus24);
                btnDeleteTech.setBackgroundColor(Color.TRANSPARENT);

                eTxtTechMore.setLayoutParams(eTxtTech.getLayoutParams());
                btnDeleteTech.setLayoutParams(btnAddTech.getLayoutParams());
                linearLayoutMoreTech.setLayoutParams(linLayoutHoriTech.getLayoutParams());

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER_VERTICAL;
                btnDeleteTech.setLayoutParams(layoutParams);

                eTxtTechMore.setSingleLine();

                eTxtTechMore.setText(technologiesArray.getString(index));

                eTxtTechMore.setId(idTech);
                btnDeleteTech.setId(idTech);
                linearLayoutMoreTech.setId(idTech);
                idTech++;

                listRefTechETxt.add(eTxtTechMore);
                listRefTechBtn.add(btnDeleteTech);
                listRefTechLinLayout.add(linearLayoutMoreTech);

                linearLayoutMoreTech.addView(eTxtTechMore);
                linearLayoutMoreTech.addView(btnDeleteTech);


                linearLayoutTech.addView(linearLayoutMoreTech);

                if (!listRefTechBtn.isEmpty()) {
                    for (int i = 0; i < listRefTechBtn.size(); i++) {

                        listRefTechBtn.get(i).setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                int index = listRefTechBtn.indexOf((ImageButton) v);
                                linearLayoutTech.removeView(listRefTechLinLayout.get(index));
                                listRefTechETxt.remove(index);
                                listRefTechBtn.remove(index);
                                listRefTechLinLayout.remove(index);

                            }
                        });
                    }
                }

            }
        }

        JSONArray languageArray = jsonProfileData.getJSONArray("languages");
        if(languageArray.length()==1){
            eTxtLang.setText(languageArray.getString(0));
        }else if(languageArray.length()>1){
            eTxtLang.setText(languageArray.getString(0));
            for(int index=1;index<languageArray.length();index++){
                EditText eTxtLangMore = (EditText) getLayoutInflater().inflate(R.layout.edit_text_layout, null);
                ImageButton btnDeleteLang = new ImageButton(this);
                LinearLayout linearLayoutMoreLang = new LinearLayout(this);

                btnDeleteLang.setImageResource(R.drawable.minus24);
                btnDeleteLang.setBackgroundColor(Color.TRANSPARENT);

                eTxtLangMore.setLayoutParams(eTxtLang.getLayoutParams());
                btnDeleteLang.setLayoutParams(btnAddLang.getLayoutParams());
                linearLayoutMoreLang.setLayoutParams(linLayoutHoriLang.getLayoutParams());


                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER_VERTICAL;
                btnDeleteLang.setLayoutParams(layoutParams);

                eTxtLangMore.setSingleLine();

                eTxtLangMore.setText(languageArray.getString(index));

                eTxtLangMore.setId(idLang);
                btnDeleteLang.setId(idLang);
                linearLayoutMoreLang.setId(idLang);
                idLang++;

                listRefLangETxt.add(eTxtLangMore);
                listRefLangBtn.add(btnDeleteLang);
                listRefLangLinLayout.add(linearLayoutMoreLang);

                linearLayoutMoreLang.addView(eTxtLangMore);
                linearLayoutMoreLang.addView(btnDeleteLang);

                linearLayoutLang.addView(linearLayoutMoreLang);

                if (!listRefLangBtn.isEmpty()) {
                    for (int i = 0; i < listRefLangBtn.size(); i++) {

                        listRefLangBtn.get(i).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int index = listRefLangBtn.indexOf((ImageButton) v);
                                linearLayoutLang.removeView(listRefLangLinLayout.get(index));
                                listRefLangETxt.remove(index);
                                listRefLangBtn.remove(index);
                                listRefLangLinLayout.remove(index);

                            }
                        });
                    }
                }
            }
        }

        JSONArray projectArray = jsonProfileData.getJSONArray("projects");
        Log.i("show",projectArray.toString());
        if(projectArray.length()==1){
            eTxtProjTitle.setText(projectArray.getJSONObject(0).getString("proj_title"));
            eTxtProjURL.setText(projectArray.getJSONObject(0).getString("proj_url"));
        }else if(projectArray.length()>1){
            eTxtProjTitle.setText(projectArray.getJSONObject(0).getString("proj_title"));
            eTxtProjURL.setText(projectArray.getJSONObject(0).getString("proj_url"));

            for(int index=1;index<projectArray.length();index++){
                JSONObject jsonObjectProject = projectArray.getJSONObject(index);

                EditText eTxtProjTitleMore = (EditText) getLayoutInflater().inflate(R.layout.edit_text_layout, null);
                EditText eTxtProjURLMore = (EditText) getLayoutInflater().inflate(R.layout.edit_text_layout, null);
                ImageView imageView = new ImageView(this);
                ImageButton btnDeleteProj = new ImageButton(this);
                LinearLayout linearLayoutMoreProjTitle = new LinearLayout(this);
                LinearLayout linearLayoutMoreProj = new LinearLayout(this);

                btnDeleteProj.setImageResource(R.drawable.minus24);
                btnDeleteProj.setBackgroundColor(Color.TRANSPARENT);

                eTxtProjTitleMore.setLayoutParams(eTxtProjTitle.getLayoutParams());
                eTxtProjURLMore.setLayoutParams(eTxtProjURL.getLayoutParams());
                imageView.setLayoutParams(imgArrow.getLayoutParams());
                btnDeleteProj.setLayoutParams(btnAddProj.getLayoutParams());
                linearLayoutMoreProjTitle.setLayoutParams(linLayoutProjTitle.getLayoutParams());
                linearLayoutMoreProj.setLayoutParams(linearLayoutProj.getLayoutParams());
                imageView.setImageResource(R.drawable.arrow);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER_VERTICAL;
                btnDeleteProj.setLayoutParams(layoutParams);

                eTxtProjTitleMore.setSingleLine();
                eTxtProjURLMore.setSingleLine();

                eTxtProjTitleMore.setText(jsonObjectProject.getString("proj_title"));
                eTxtProjURLMore.setText(jsonObjectProject.getString("proj_url"));

                eTxtProjTitleMore.setId(idProj);
                eTxtProjURLMore.setId(idProj);
                btnDeleteProj.setId(idProj);
                linearLayoutMoreProjTitle.setId(idProj);
                idTech++;

                listRefProjTitleETxt.add(eTxtProjTitleMore);
                listRefProjURLETxt.add(eTxtProjURLMore);
                listRefProjBtn.add(btnDeleteProj);
                listRefProjTitleLinLayout.add(linearLayoutMoreProjTitle);

                linearLayoutMoreProjTitle.addView(imageView);
                linearLayoutMoreProjTitle.addView(eTxtProjTitleMore);
                linearLayoutMoreProjTitle.addView(btnDeleteProj);

                linearLayoutProj.addView(linearLayoutMoreProjTitle);
                linearLayoutProj.addView(eTxtProjURLMore);

                if (!listRefProjBtn.isEmpty()) {
                    for (int i = 0; i < listRefProjBtn.size(); i++) {

                        listRefProjBtn.get(i).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int index = listRefProjBtn.indexOf((ImageButton) v);
                                linearLayoutProj.removeView(listRefProjTitleLinLayout.get(index));
                                linearLayoutProj.removeView(listRefProjURLETxt.get(index));
                                listRefProjTitleETxt.remove(index);
                                listRefProjURLETxt.remove(index);
                                listRefProjBtn.remove(index);
                                listRefProjTitleLinLayout.remove(index);
                            }
                        });
                    }
                }
            }
        }
    }


    void setSpinnerBranch() {
        branches = new String[]{"Computer Science", "Information Technology", "Electrical Engineering", "Electronics & Communication", "Civil Engineering", "Mechanical Engineering"};
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, branches);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBranch.setAdapter(dataAdapter);

    }

    void setDrawables() {

        Drawable img = this.getResources().getDrawable(R.drawable.user23);
        img.setBounds(0, 0, 40, 40);
        eTxtName.setCompoundDrawables(img, null, null, null);

        img = this.getResources().getDrawable(R.drawable.email);
        img.setBounds(0, 0, 40, 40);
        eTxtEmail.setCompoundDrawables(img, null, null, null);

        img = this.getResources().getDrawable(R.drawable.phone);
        img.setBounds(0, 0, 40, 40);
        eTxtPhone.setCompoundDrawables(img, null, null, null);

        img = this.getResources().getDrawable(R.drawable.skype);
        img.setBounds(0, 0, 40, 40);
        eTxtSkype.setCompoundDrawables(img, null, null, null);

        img = this.getResources().getDrawable(R.drawable.github);
        img.setBounds(0, 0, 40, 40);
        eTxtGithub.setCompoundDrawables(img, null, null, null);

        img = this.getResources().getDrawable(R.drawable.password);
        img.setBounds(0, 0, 40, 40);
        eTxtPassword.setCompoundDrawables(img,null,null,null);

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
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        try {
            initViews();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegister: {
                handler.sendEmptyMessage(100);
                break;
            }

            case R.id.btnTechAdd: {
                boolean flag = true;

                if (eTxtTech.getText().toString().trim().equals("")) {
                    eTxtTech.setError("Field can't be empty");
                    flag = false;
                } else if (!listRefTechETxt.isEmpty()) {
                    for (int i = 0; i < listRefTechETxt.size(); i++) {
                        if (listRefTechETxt.get(i).getText().toString().trim().equals("")) {
                            listRefTechETxt.get(i).setError("Field can't be empty");
                            flag = false;
                        }
                    }
                }
                if (flag) {
                    EditText eTxtTechMore = (EditText) getLayoutInflater().inflate(R.layout.edit_text_layout, null);
                    ImageButton btnDeleteTech = new ImageButton(this);
                    LinearLayout linearLayoutMoreTech = new LinearLayout(this);

                    btnDeleteTech.setImageResource(R.drawable.minus24);
                    btnDeleteTech.setBackgroundColor(Color.TRANSPARENT);

                    eTxtTechMore.setLayoutParams(eTxtTech.getLayoutParams());
                    btnDeleteTech.setLayoutParams(btnAddTech.getLayoutParams());
                    linearLayoutMoreTech.setLayoutParams(linLayoutHoriTech.getLayoutParams());

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.gravity = Gravity.CENTER_VERTICAL;
                    btnDeleteTech.setLayoutParams(layoutParams);

                    eTxtTechMore.setSingleLine();

                    eTxtTechMore.setId(idTech);
                    btnDeleteTech.setId(idTech);
                    linearLayoutMoreTech.setId(idTech);
                    idTech++;

                    listRefTechETxt.add(eTxtTechMore);
                    listRefTechBtn.add(btnDeleteTech);
                    listRefTechLinLayout.add(linearLayoutMoreTech);

                    linearLayoutMoreTech.addView(eTxtTechMore);
                    linearLayoutMoreTech.addView(btnDeleteTech);


                    linearLayoutTech.addView(linearLayoutMoreTech);

                    if (!listRefTechBtn.isEmpty()) {
                        for (int i = 0; i < listRefTechBtn.size(); i++) {

                            listRefTechBtn.get(i).setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    int index = listRefTechBtn.indexOf((ImageButton) v);
                                    linearLayoutTech.removeView(listRefTechLinLayout.get(index));
                                    listRefTechETxt.remove(index);
                                    listRefTechBtn.remove(index);
                                    listRefTechLinLayout.remove(index);

                                }
                            });
                        }
                    }

                }

                break;
            }

            case R.id.btnLangAdd: {

                boolean flag = true;

                if (eTxtLang.getText().toString().trim().equals("")) {
                    eTxtLang.setError("Field can't be empty");
                    flag = false;
                } else if (!listRefLangETxt.isEmpty()) {
                    for (int i = 0; i < listRefLangETxt.size(); i++) {
                        if (listRefLangETxt.get(i).getText().toString().trim().equals("")) {
                            listRefLangETxt.get(i).setError("Field can't be empty");
                            flag = false;
                        }
                    }
                }
                if (flag) {
                    EditText eTxtLangMore = (EditText) getLayoutInflater().inflate(R.layout.edit_text_layout, null);
                    ImageButton btnDeleteLang = new ImageButton(this);
                    LinearLayout linearLayoutMoreLang = new LinearLayout(this);

                    btnDeleteLang.setImageResource(R.drawable.minus24);
                    btnDeleteLang.setBackgroundColor(Color.TRANSPARENT);

                    eTxtLangMore.setLayoutParams(eTxtLang.getLayoutParams());
                    btnDeleteLang.setLayoutParams(btnAddLang.getLayoutParams());
                    linearLayoutMoreLang.setLayoutParams(linLayoutHoriLang.getLayoutParams());


                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.gravity = Gravity.CENTER_VERTICAL;
                    btnDeleteLang.setLayoutParams(layoutParams);

                    eTxtLangMore.setSingleLine();

                    eTxtLangMore.setId(idLang);
                    btnDeleteLang.setId(idLang);
                    linearLayoutMoreLang.setId(idLang);
                    idLang++;

                    listRefLangETxt.add(eTxtLangMore);
                    listRefLangBtn.add(btnDeleteLang);
                    listRefLangLinLayout.add(linearLayoutMoreLang);

                    linearLayoutMoreLang.addView(eTxtLangMore);
                    linearLayoutMoreLang.addView(btnDeleteLang);

                    linearLayoutLang.addView(linearLayoutMoreLang);

                    if (!listRefLangBtn.isEmpty()) {
                        for (int i = 0; i < listRefLangBtn.size(); i++) {

                            listRefLangBtn.get(i).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int index = listRefLangBtn.indexOf((ImageButton) v);
                                    linearLayoutLang.removeView(listRefLangLinLayout.get(index));
                                    listRefLangETxt.remove(index);
                                    listRefLangBtn.remove(index);
                                    listRefLangLinLayout.remove(index);

                                }
                            });
                        }
                    }

                }

                break;
            }

            case R.id.btnAddProj: {
                boolean flag = true;

                if (eTxtProjTitle.getText().toString().trim().equals("")) {
                    eTxtProjTitle.setError("Field can't be empty");
                    flag = false;
                } else if (!listRefProjTitleETxt.isEmpty()) {
                    for (int i = 0; i < listRefProjTitleETxt.size(); i++) {
                        if (listRefProjTitleETxt.get(i).getText().toString().trim().equals("")) {
                            listRefProjTitleETxt.get(i).setError("Field can't be empty");
                            flag = false;
                        }
                    }
                }
                if (flag) {
                    EditText eTxtProjTitleMore = (EditText) getLayoutInflater().inflate(R.layout.edit_text_layout, null);
                    EditText eTxtProjURLMore = (EditText) getLayoutInflater().inflate(R.layout.edit_text_layout, null);
                    ImageView imageView = new ImageView(this);
                    ImageButton btnDeleteProj = new ImageButton(this);
                    LinearLayout linearLayoutMoreProjTitle = new LinearLayout(this);
                    LinearLayout linearLayoutMoreProj = new LinearLayout(this);

                    btnDeleteProj.setImageResource(R.drawable.minus24);
                    btnDeleteProj.setBackgroundColor(Color.TRANSPARENT);

                    eTxtProjTitleMore.setLayoutParams(eTxtProjTitle.getLayoutParams());
                    eTxtProjURLMore.setLayoutParams(eTxtProjURL.getLayoutParams());
                    imageView.setLayoutParams(imgArrow.getLayoutParams());
                    btnDeleteProj.setLayoutParams(btnAddProj.getLayoutParams());
                    linearLayoutMoreProjTitle.setLayoutParams(linLayoutProjTitle.getLayoutParams());
                    linearLayoutMoreProj.setLayoutParams(linearLayoutProj.getLayoutParams());
                    imageView.setImageResource(R.drawable.arrow);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.gravity = Gravity.CENTER_VERTICAL;
                    btnDeleteProj.setLayoutParams(layoutParams);

                    eTxtProjTitleMore.setSingleLine();
                    eTxtProjURLMore.setSingleLine();

                    eTxtProjTitleMore.setId(idProj);
                    eTxtProjURLMore.setId(idProj);
                    btnDeleteProj.setId(idProj);
                    linearLayoutMoreProjTitle.setId(idProj);
                    idTech++;

                    listRefProjTitleETxt.add(eTxtProjTitleMore);
                    listRefProjURLETxt.add(eTxtProjURLMore);
                    listRefProjBtn.add(btnDeleteProj);
                    listRefProjTitleLinLayout.add(linearLayoutMoreProjTitle);

                    linearLayoutMoreProjTitle.addView(imageView);
                    linearLayoutMoreProjTitle.addView(eTxtProjTitleMore);
                    linearLayoutMoreProjTitle.addView(btnDeleteProj);

                    linearLayoutProj.addView(linearLayoutMoreProjTitle);
                    linearLayoutProj.addView(eTxtProjURLMore);

                    if (!listRefProjBtn.isEmpty()) {
                        for (int i = 0; i < listRefProjBtn.size(); i++) {

                            listRefProjBtn.get(i).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int index = listRefProjBtn.indexOf((ImageButton) v);
                                    linearLayoutProj.removeView(listRefProjTitleLinLayout.get(index));
                                    linearLayoutProj.removeView(listRefProjURLETxt.get(index));
                                    listRefProjTitleETxt.remove(index);
                                    listRefProjURLETxt.remove(index);
                                    listRefProjBtn.remove(index);
                                    listRefProjTitleLinLayout.remove(index);
                                }
                            });
                        }
                    }

                }

                break;
            }
        }
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String username, password=null, email, phone, skype, github, branch;
            int year = 0, daysch = 0;
            boolean flag = true;


            username = eTxtName.getText().toString().trim();
            if(callingActivity!=102){
                password = eTxtPassword.getText().toString().trim();
            }
            email = eTxtEmail.getText().toString().trim();
            phone = eTxtPhone.getText().toString().trim();
            skype = eTxtSkype.getText().toString().trim();
            github = eTxtGithub.getText().toString().trim();


            if (username.isEmpty()) {
                eTxtName.setError("Field can't be empty");
                flag = false;
            }
            if (!isValidEmail(email)) {
                eTxtEmail.setError("Invalid Email");
                flag = false;
            }
            if(callingActivity!=102){
                if (password.isEmpty()) {
                    eTxtPassword.setError("Field can't be empty");
                    flag = false;
                }
            }

            if (!(phone.length() == 10)) {
                eTxtPhone.setError("Invalid Phone number");
                flag = false;
            }
            if (skype.isEmpty()) {
                eTxtSkype.setError("Field can't be empty");
                flag = false;
            }
            if (!Patterns.WEB_URL.matcher(github).matches()) {
                eTxtGithub.setError("Invalid URL");
                flag = false;
            }

            if (flag) {
                if (rGrpYear.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getApplicationContext(), "Select Year", Toast.LENGTH_LONG).show();
                    flag = false;
                } else if (rGrpSchHost.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getApplicationContext(), "Select Day Scholar/ Hosteler", Toast.LENGTH_LONG).show();
                    flag = false;
                }

            }

            //validating Skills section
            if (flag) {
                arrayTech = new JSONArray();
                arrayLang = new JSONArray();


                if (!eTxtTech.getText().toString().trim().isEmpty()) {
                    arrayTech.put(eTxtTech.getText().toString().trim());
                }
                if (listRefTechETxt.size() != 0) {
                    for (int i = 0; i < listRefTechETxt.size(); i++) {
                        String str = listRefTechETxt.get(i).getText().toString().trim();
                        if (!str.isEmpty()) {
                            arrayTech.put(str);
                        }
                    }
                }

                if (!eTxtLang.getText().toString().trim().isEmpty()) {
                    arrayLang.put(eTxtLang.getText().toString().trim());
                }
                if (listRefLangETxt.size() != 0) {
                    for (int i = 0; i < listRefLangETxt.size(); i++) {
                        String str = listRefLangETxt.get(i).getText().toString().trim();
                        if (!str.isEmpty()) {
                            arrayLang.put(str);
                        }
                    }
                }

                objProjects = new JSONObject();
                arrayProjects = new JSONArray();

                if (!eTxtProjTitle.getText().toString().trim().isEmpty()) {
                    String title = eTxtProjTitle.getText().toString().trim();
                    String url = eTxtProjURL.getText().toString().trim();
                    try {
                        objProjects.put("ProjectTitle", title);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (!url.isEmpty()) {
                        if (!Patterns.WEB_URL.matcher(url).matches()) {
                            eTxtProjURL.setError("Invalid URL");
                            flag = false;
                        }

                    }

                    if (flag) {
                        try {
                            objProjects.put("ProjectURL", url);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        arrayProjects.put(objProjects);
                    }
                } else {
                    if (!eTxtProjURL.getText().toString().trim().isEmpty()) {
                        eTxtProjTitle.setError("Title is necessary");
                        flag = false;
                    }
                }
                //dynamic project list
                if (listRefProjTitleETxt.size() != 0) {

                    for (int i = 0; i < listRefProjTitleETxt.size(); i++) {
                        objProjects = new JSONObject();
                        String prTitle = listRefProjTitleETxt.get(i).getText().toString().trim();
                        String prUrl = listRefProjURLETxt.get(i).getText().toString().trim();

                        if (!prTitle.isEmpty()) {
                            try {
                                objProjects.put("ProjectTitle", prTitle);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (!prUrl.isEmpty()) {
                                if (!Patterns.WEB_URL.matcher(prUrl).matches()) {
                                    listRefProjURLETxt.get(i).setError("Invalid URL");
                                    flag = false;
                                }

                            }
                            if (flag) {
                                try {
                                    objProjects.put("ProjectURL", prUrl);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                arrayProjects.put(objProjects);
                            }
                        } else if (prTitle.isEmpty()) {
                            if (!prUrl.isEmpty()) {
                                listRefProjTitleETxt.get(i).setError("Title is necessary");
                                flag = false;
                            }
                        }
                    }
                }

            }

            //start
            if (flag) {

                jsonRequestObject = new JSONObject();
                ///object in map needs to be put
                try {
                    jsonRequestObject.put("username", username);
                    jsonRequestObject.put("email", email);
                    if(callingActivity!=102){
                        jsonRequestObject.put("password", password);
                        //1-Admin 2-Access-given Users 3-Normal users
                        jsonRequestObject.put("access", 3);
                    }

                    jsonRequestObject.put("phone", phone);
                    jsonRequestObject.put("skype", skype);
                    jsonRequestObject.put("github", github);

                    jsonRequestObject.put("branch", spinnerBranch.getSelectedItem().toString());

                    switch (rGrpYear.getCheckedRadioButtonId()) {
                        case R.id.radioBtn1stYear:
                            year = 1;
                            break;
                        case R.id.radioBtn2ndYear:
                            year = 2;
                            break;
                        case R.id.radioBtn3rdYear:
                            year = 3;
                            break;
                        case R.id.radioBtn4thYear:
                            year = 4;
                            break;
                    }

                    jsonRequestObject.put("year", year);

                    Log.i("show", "data inserted");

                    switch (rGrpSchHost.getCheckedRadioButtonId()) {
                        case R.id.rbDaySch:
                            daysch = 1;
                            break;
                        case R.id.rbHosteler:
                            daysch = 0;
                            break;
                    }

                    jsonRequestObject.put("dayscholar", daysch);
                    jsonRequestObject.put("technologies", arrayTech);
                    jsonRequestObject.put("languages", arrayLang);
                    jsonRequestObject.put("projects", arrayProjects);

                    if(callingActivity==102)
                        jsonRequestObject.put("user_id", user_id);

                    Log.i("show",jsonRequestObject.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String url;
                if(callingActivity==102){
                    url = ConnectionUtil.PHP_EDIT_PROFILE_URL;
                    Log.i("show","calling activity is : "+callingActivity);

                }else{
                    url = ConnectionUtil.PHP_REGISTER_URL;
                }

                Log.i("show",jsonRequestObject.toString());

                final String email_final = email,password_final=password;
                JsonObjectRequest objRequest = new JsonObjectRequest(Request.Method.POST,url , jsonRequestObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        //Boolean error=jsonObject.getBoolean("error");
                        try {
                            Log.i("show", "json object returned "+jsonObject.toString());

                            int error = jsonObject.getInt("error");
                            if(callingActivity == 102){
                                switch(error){
                                    case 0:
                                        ProfileActivity.reference.finish();
                                        Intent i =new Intent(RegisterActivity.this,ProfileActivity.class);
                                        if(!(user_id == StartActivity.user_id)){
                                            i.putExtra("keyUserId",user_id);}
                                        finish();
                                        startActivity(i);
                                        break;
                                    case 1:
                                        Toast.makeText(RegisterActivity.this,"Could not update profile, please try again later",Toast.LENGTH_SHORT).show();
                                        break;
                                    case 2:
                                        Toast.makeText(RegisterActivity.this,"Could not update profile, please try again later",Toast.LENGTH_SHORT).show();
                                        break;
                                }

                            }else{
                                String msg = jsonObject.getString("msg");
                                Log.i("show","inside response of register");
                                switch (error) {
                                    case 0:
                                        Log.i("show","in register, when user registers");
                                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                        Intent i = new Intent(RegisterActivity.this, StudentPanelActivity.class);
                                        UserSessionManager session = new UserSessionManager(RegisterActivity.this);
                                        session.createLoginSession(email_final,password_final,3,jsonObject.getInt("user_id"));
                                        startActivity(i);
                                        finish();
                                        break;
                                    case 1:
                                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                        break;
                                    case 2:
                                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                        break;
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Log.i("show","error is: "+volleyError.toString());
                            }
                        }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }
                };
                AppController.getInstance().addToRequestQueue(objRequest, "json_obj_req");
            }
        }

    };
}