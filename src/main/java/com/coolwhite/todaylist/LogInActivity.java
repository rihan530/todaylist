//로그인
package com.coolwhite.todaylist;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class LogInActivity extends Activity {
    EditText id;
    EditText pass;
    RbPreference pref;

    boolean isCheck=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pref= new RbPreference(this);

        id = (EditText) findViewById(R.id.id);//아이디
        pass = (EditText) findViewById(R.id.pass);//비밀번호


        Button login = (Button)findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(id.getText().toString())) {
                    Toast.makeText(LogInActivity.this, "아이디 입력하세요", Toast.LENGTH_SHORT).show();
                }else  if (TextUtils.isEmpty(pass.getText().toString())) {
                    Toast.makeText(LogInActivity.this , "비밀번호를 입력하세요" , Toast.LENGTH_SHORT).show();
                }else{
                    //서버에 로그인체크
                    new loginTask().execute(); //http://planmanager.cafe24.com/app/login_proc.php 호출
                }

            }
        });

        Button join_user = (Button)findViewById(R.id.join_user);//회원 가입이동
        join_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gt = new Intent(LogInActivity.this , JoinUserActivity.class);
                startActivity(gt);

            }
        });
//
//
//
        final ImageView auto_img = (ImageView)findViewById(R.id.auto_img);//자동 로그인 체크
        auto_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isCheck=!isCheck;

                if(isCheck){
                    auto_img.setBackgroundResource(R.drawable.check_on);
                }else{
                    auto_img.setBackgroundResource(R.drawable.check);
                }

            }
        });




    }



    private class loginTask extends AsyncTask<Void, Void, Void> {

        private String flag;
        private String message;
        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(LogInActivity.this, "", "로그인 처리중....", true);
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
            URL url; //서버에 로그인 요청한다.
            url = new URL("http://planmanager.cafe24.com/app/login_proc.php");


            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setDefaultUseCaches(false);
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            StringBuffer buffer = new StringBuffer();
            buffer.append("mem_id").append("=").append(id.getText().toString()).append("&");//아이디와 패스워드를 서버에 전송한다.
            buffer.append("pass").append("=").append(pass.getText().toString()).append("&");


            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF-8");
            PrintWriter writer = new PrintWriter(outStream);
            writer.write(buffer.toString());
            writer.flush();

            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(tmp);
            StringBuilder builder = new StringBuilder();
            String str; //서버에서 처리한 결과를 str에 저장
            while ((str = reader.readLine()) != null) {
                builder.append(str);
            }
            Pasingversiondata1(builder.toString()); //결과값 파싱
        }

        private void Pasingversiondata1(String data) {

                flag = data; //결과값 저장

        }

        protected void onPostExecute(Void result) {
            mProgressDialog.dismiss();
            if (flag.equals("error")) {
                Toast.makeText(LogInActivity.this , "아이디/비번이 맞지않습니다." , Toast.LENGTH_SHORT).show();

            } else {  //로그인 성공하면 전역변수에 값 저장 . 전역변수는 계속 사용되어 지는것이기 때문에 미리 저장해 둔다.
                pref.put(RbPreference.MEM_ID , id.getText().toString());//회원아이디
                pref.put(RbPreference.MEM_PASS, pass.getText().toString());//회원비밀번호
                pref.put(RbPreference.ISLOGIN  , isCheck);//자동 로그인여부

                Intent gt = new Intent(LogInActivity.this, MainActivity.class);//메인 화면 이동
                startActivity(gt);
                finish();

            }

        }
    }



}
