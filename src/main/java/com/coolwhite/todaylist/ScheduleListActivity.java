//일정리스트
package com.coolwhite.todaylist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

public class ScheduleListActivity extends AppCompatActivity {

    RbPreference pref;
    String group_idx="";
    String day="";

    ArrayList<ScheduleData> mScList = new ArrayList<ScheduleData>(); // 스케쥴

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_detail);

        pref = new RbPreference(this);

        group_idx= getIntent().getStringExtra("group_idx");
        day= getIntent().getStringExtra("day");

        TextView main_txt = (TextView) findViewById(R.id.main_txt);
        main_txt.setText("일정 ("+day+")");

        Button insert = (Button)findViewById(R.id.insert);
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gt = new Intent(ScheduleListActivity.this,
                        ScheduleInputActivity.class); // ScheduleInfoActvity로
//
                gt.putExtra("day", day);

                gt.putExtra("group_idx",group_idx);
                startActivityForResult(gt, 0);
            }
        });
        new getSchedule().execute();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //startActivityForResult 처리후 호출
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0 && resultCode==RESULT_OK){
            new getSchedule().execute();
        }
    }




    public class GoodsAdapter extends ArrayAdapter<ScheduleData> {//리스스뷰 어댑터
        private ArrayList<ScheduleData> items;
        ScheduleData fInfo;

        public GoodsAdapter(Context context, int textViewResourseId, ArrayList<ScheduleData> items) {
            super(context, textViewResourseId, items);
            this.items = items;
        }

        public View getView(int position, View convertView, ViewGroup parent) {//

            View v = convertView;
            fInfo = items.get(position);

            LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.listview_sc, null); //리스트뷰 뷰 불러온다.



            TextView title = (TextView) v.findViewById(R.id.title);//이름
            title.setText(fInfo.getContents());

            TextView reg_id = (TextView) v.findViewById(R.id.reg_id);//이름
            reg_id.setText("등록자 : " +fInfo.getReg_id() +", 등록일 : " +fInfo.getReg_date());
            TextView update = (TextView) v.findViewById(R.id.update);//이름


            TextView tag = (TextView) v.findViewById(R.id.tag);//태그
            tag.setText("일정");
            update.setVisibility(View.VISIBLE);



            update.setTag(position);
            update.setOnClickListener(new View.OnClickListener() {// ����Ʈ��Ŭ�� ��
                // BookDetailActivity��
                // �̵�

                @Override
                public void onClick(View v) { //리스트뷰 클릭시 그룹멤버인지 체크
                    int pos = (Integer) v.getTag();
                    idxx = items.get(pos).getIdx();
                    if(items.get(pos).getReg_id().equals(pref.getValue(RbPreference.MEM_ID,""))){
                        Intent gt = new Intent(ScheduleListActivity.this,
                                ScheduleUpdateActivity.class); // ScheduleInfoActvity로
//
                        gt.putExtra("idx", idxx);
                        gt.putExtra("txt", items.get(pos).getContents());

                        gt.putExtra("group_idx",group_idx);
                        startActivityForResult(gt, 0);
                    }else{
                       Toast.makeText(ScheduleListActivity.this , "자기가 쓴글만 수정 가능합니다." , Toast.LENGTH_SHORT).show();
                    }




                }
            });

            v.setTag(position);
            v.setOnClickListener(new View.OnClickListener() {// ����Ʈ��Ŭ�� ��
                // BookDetailActivity��
                // �̵�

                @Override
                public void onClick(View v) { //리스트뷰 클릭시 그룹멤버인지 체크
                    int pos = (Integer) v.getTag();
                    idxx = items.get(pos).getIdx();
                    Intent gt = new Intent(ScheduleListActivity.this,
                            ScheduleDetailActivity.class); // ScheduleInfoActvity로
//
                    gt.putExtra("idx", idxx);
                    gt.putExtra("txt", items.get(pos).getContents());

                    gt.putExtra("group_idx",group_idx);
                    startActivityForResult(gt, 0);




                }
            });




            return v;
        }
    }

    String idxx="";



    private class getSchedule extends AsyncTask<Void, Void, Void> { //그룹의 날짜별 일정을 가져온다.
        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(ScheduleListActivity.this, "", "데이터 처리중....", true);
            }

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                getStore();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private void getStore() throws UnsupportedEncodingException, IOException {
            URL url = new URL("http://planmanager.cafe24.com/app/board_list.php");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setDefaultUseCaches(false);
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            StringBuffer buffer = new StringBuffer();
            buffer.append("group_idx").append("=").append(group_idx).append("&");
            if(day.length()<=10){
                day = day+" 00:00:00";
            }
            buffer.append("sc_date").append("=").append(day).append("&");

            Log.d("myLog" , "buffer " + buffer.toString());


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
            Pasingversiondata(builder.toString()); //리턴된 json 값을 파싱한다.
        }

        private void Pasingversiondata(String data) {
            mScList.clear();
            try {

                JSONArray jAr = new JSONArray(data); //json 배열을 파싱하여 Arraylist로 만든다.


                for (int i = 0; i < jAr.length(); i++) {
                    JSONObject student = jAr.getJSONObject(i);
                    String idx = student.getString("idx");
                    String group_idx = student.getString("group_idx");
                    String reg_id = student.getString("reg_id");
                    String reg_date = student.getString("reg_date");
                    String contents = student.getString("contents");
                    String sc_date = student.getString("sc_date");
                    String tag = student.getString("tag");


                    mScList.add(new ScheduleData(idx, group_idx, reg_id, reg_date, contents, sc_date, tag));  //json 배열을 파싱하여 Arraylist로 만든다.

                }
            } catch (JSONException e) {
                Log.d("tag", "Parse Error " + e.toString());
            }

        }

        protected void onPostExecute(Void result) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }

            ListView s_list = (ListView) findViewById(R.id.s_list);

            GoodsAdapter mAdapter = new GoodsAdapter(ScheduleListActivity.this, R.layout.listview_member, mScList);//Arraylist로 리스트뷰에 데이터에 연결해준다.

            TextView nodata = (TextView) findViewById(R.id.nodata);


            if (mScList.size() == 0) {
                nodata.setVisibility(View.VISIBLE);
            } else {
                nodata.setVisibility(View.GONE);
            }
            s_list.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }


    }
}
