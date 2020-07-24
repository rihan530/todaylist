//시작 엑티비티
package com.coolwhite.todaylist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


public class IntroActivity extends Activity  {

    RbPreference pref;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_intro);

        pref = new RbPreference(this);

        mHandler1.sendEmptyMessageDelayed(0, 1500); // 1.5초후에 이동


    }

    Handler mHandler = new Handler();


    Handler mHandler1 = new Handler() {
        public void handleMessage(Message msg) {  //로그인 되어있으면 MainActivity.class 이동 , 아니면 LogInActivity.class
            if (pref.getValue(RbPreference.ISLOGIN,false)){//ISLOGIN 은 전역변수로, 자동 로그인 체크하면 true로 세팅된다.
                Intent moveintent = new Intent(IntroActivity.this, MainActivity.class);
                startActivity(moveintent);
                finish();
            } else {
                Intent moveintent = new Intent(IntroActivity.this, LogInActivity.class);
                startActivity(moveintent);
                finish();

            }
        }
    };



}


