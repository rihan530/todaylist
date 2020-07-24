//그룹 등록
package com.coolwhite.todaylist;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;


public class GroupInputActivity extends Activity {

	RbPreference pref;

	EditText name;
	EditText contents;




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_input);
		pref = new RbPreference(this);

		name = (EditText) findViewById(R.id.name);//그룹명
		contents = (EditText) findViewById(R.id.contents);// 소개




		Button update = (Button) findViewById(R.id.update);

		update.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(name.getText().toString())) {
					Toast.makeText(GroupInputActivity.this, "그룹명 입력하세요", Toast.LENGTH_SHORT).show();
				}
				else if (TextUtils.isEmpty(contents.getText().toString())) {
					Toast.makeText(GroupInputActivity.this, "소개를 입력하세요", Toast.LENGTH_SHORT).show();
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
			mProgressDialog = ProgressDialog.show(GroupInputActivity.this, "", "데이터 처리중....", true);
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
			URL url = new URL("http://planmanager.cafe24.com/app/group_insert.php"); //서버에  데이터를 보내서 db에 저장한다.
			HttpURLConnection http = (HttpURLConnection) url.openConnection();

			http.setDefaultUseCaches(false);
			http.setRequestMethod("POST");
			http.setDoOutput(true);
			http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
			StringBuffer buffer = new StringBuffer();
			buffer.append("make_id").append("=").append(pref.getValue(RbPreference.MEM_ID,"")).append("&");
			buffer.append("title").append("=").append(name.getText().toString()).append("&");
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
				Toast.makeText(GroupInputActivity.this, "등록 완료 되었습니다.", Toast.LENGTH_SHORT).show();
				setResult(RESULT_OK);
				finish();

			} else {
				Toast.makeText(GroupInputActivity.this, "에러 발생하였습니다.", Toast.LENGTH_SHORT).show();
			}

		}
	}





}
