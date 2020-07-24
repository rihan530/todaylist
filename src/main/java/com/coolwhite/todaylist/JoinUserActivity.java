//사용자 회원가입
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

public class JoinUserActivity extends Activity {

    EditText id;
    EditText pass;

    EditText name;
    EditText nick;
    EditText email;


    boolean isCheck=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_user);

        id = (EditText) findViewById(R.id.id);//아이디
        name = (EditText) findViewById(R.id.name);//이름
        pass = (EditText) findViewById(R.id.pass);//비밀번호
        nick = (EditText) findViewById(R.id.nick);//닉네임

        email = (EditText) findViewById(R.id.email);//이메일


        Button join = (Button) findViewById(R.id.join);

        join.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(id.getText().toString())) {
                    Toast.makeText(JoinUserActivity.this, "아이디를 입력하세요", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(pass.getText().toString())) {
                    Toast.makeText(JoinUserActivity.this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(name.getText().toString())) {
                    Toast.makeText(JoinUserActivity.this, "이름을 입력하세요", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(email.getText().toString())) {
                    Toast.makeText(JoinUserActivity.this, "이메일을 입력하세요", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(nick.getText().toString())) {
                    Toast.makeText(JoinUserActivity.this, "닉네임을 입력하세요", Toast.LENGTH_SHORT).show();
                }else {
                    //서버에 데이터 저장
                    new joinTask().execute(); //http://namdaefood.cafe24.com/app/join.php 호출
                }

            }
        });


    }




    private class joinTask extends AsyncTask<Void, Void, Void> {

        private String flag;
        private String message;
        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(JoinUserActivity.this, "", "회원가입 처리중....", true);

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
            URL url = new URL("http://planmanager.cafe24.com/app/join.php"); //서버에 회원가입 데이터를 보내서 db에 저장한다.
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setDefaultUseCaches(false);
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            StringBuffer buffer = new StringBuffer();
            buffer.append("mem_id").append("=").append(id.getText().toString()).append("&");
            buffer.append("name").append("=").append(name.getText().toString()).append("&");
            buffer.append("e_mail").append("=").append(email.getText().toString()).append("&");
            buffer.append("pass").append("=").append(pass.getText().toString()).append("&");
            buffer.append("nick_name").append("=").append(nick.getText().toString()).append("&");

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
                Toast.makeText(JoinUserActivity.this, "회원가입 완료 되었습니다. 로그인해주세요.", Toast.LENGTH_SHORT).show();
                finish();

            } else {
                Toast.makeText(JoinUserActivity.this, "중복된 아이디 이거나 에러 발생하였습니다.", Toast.LENGTH_SHORT).show();
            }

        }
    }


}
