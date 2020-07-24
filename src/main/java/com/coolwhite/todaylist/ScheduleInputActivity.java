//일정등록
package com.coolwhite.todaylist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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


public class ScheduleInputActivity extends Activity {

	RbPreference pref;

	TextView type;
	EditText contents;
	String group_idx="";
	String day="";
	String tag="";

	String arrays[] ={"일정"};
	String arraysIdx[] ={"1" };


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_memo_input);
		pref = new RbPreference(this);
		group_idx= getIntent().getStringExtra("group_idx");
		day= getIntent().getStringExtra("day");


		type = (TextView) findViewById(R.id.type);//타입
		contents = (EditText) findViewById(R.id.contents);// 소개

		type.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {


				AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleInputActivity.this);
				builder.setTitle("선택하세요.");
				builder.setItems(arrays, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int pos) {
						dialog.dismiss();
						type.setText(arrays[pos]);
						tag = arraysIdx[pos];
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});


		Button update = (Button) findViewById(R.id.update);

		update.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(type.getText().toString())) {
					Toast.makeText(ScheduleInputActivity.this, "타입을 입력하세요", Toast.LENGTH_SHORT).show();
				}
				else if (TextUtils.isEmpty(contents.getText().toString())) {
					Toast.makeText(ScheduleInputActivity.this, "내용을 입력하세요", Toast.LENGTH_SHORT).show();
				} else {
					new updates().execute();

				}

			}
		});




	}



	private class updates extends AsyncTask<Void, Void, Void> {//  데이터 입력

		private String flag;
		private String message;
		ProgressDialog mProgressDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog = ProgressDialog.show(ScheduleInputActivity.this, "", "데이터 처리중....", true);
		}

		@Override
		protected Void doInBackground(Void... params) {

			try {
				getList();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		private void getList() throws UnsupportedEncodingException, IOException {
			URL url = new URL("http://planmanager.cafe24.com/app/board_write.php"); //서버에  데이터를 보내서 db에 저장한다.
			HttpURLConnection http = (HttpURLConnection) url.openConnection();

			http.setDefaultUseCaches(false);
			http.setRequestMethod("POST");
			http.setDoOutput(true);
			http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
			StringBuffer buffer = new StringBuffer();
			buffer.append("reg_id").append("=").append(pref.getValue(RbPreference.MEM_ID,"")).append("&");
			buffer.append("tag").append("=").append(tag).append("&");
			buffer.append("group_idx").append("=").append(group_idx).append("&");
			buffer.append("sc_date").append("=").append(day).append("&");

			buffer.append("contents").append("=").append(contents.getText().toString()).append("&");



			OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF-8");
			PrintWriter writer = new PrintWriter(outStream);
			writer.write(buffer.toString());
			writer.flush();

			InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF-8");
			BufferedReader reader = new BufferedReader(tmp);
			StringBuilder builder = new StringBuilder();
			String str;
			while ((str = reader.readLine()) != null) {
				builder.append(str);
			}
			Pasingversiondata1(builder.toString()); //결과값 파싱
		}

		private void Pasingversiondata1(String data) {

			flag = data;


		}

		protected void onPostExecute(Void result) {
			mProgressDialog.dismiss();
			if (flag.equals("ok")) {
				Toast.makeText(ScheduleInputActivity.this, "등록 완료 되었습니다.", Toast.LENGTH_SHORT).show();
				setResult(RESULT_OK);
				finish();

			} else {
				Toast.makeText(ScheduleInputActivity.this, "에러 발생하였습니다.", Toast.LENGTH_SHORT).show();
			}

		}
	}





}
