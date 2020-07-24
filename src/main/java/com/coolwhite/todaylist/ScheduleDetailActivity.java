
//일정 상세보기
package com.coolwhite.todaylist;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;


public class ScheduleDetailActivity extends Activity {

	RbPreference pref;


	TextView contents;
	String group_idx="";
	String day="";
	String tag="";

	String arrays[] ={"일정" , "파일"};
	String arraysIdx[] ={"1" , "0"};

	String idx="";
	String txt="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_memo_detail);
		pref = new RbPreference(this);

		txt= getIntent().getStringExtra("txt");



		contents = (TextView) findViewById(R.id.contents);// 소개
		contents.setText(txt);




	}







}
