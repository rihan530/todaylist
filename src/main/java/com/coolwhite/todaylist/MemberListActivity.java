package com.coolwhite.todaylist;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class MemberListActivity extends AppCompatActivity {

    RbPreference pref;
    String group_idx="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        pref = new RbPreference(this);

        group_idx= getIntent().getStringExtra("idx");


        new getGroupMember().execute();
    }







    public class GoodsAdapter extends ArrayAdapter<GroupMemberData> {//리스스뷰 어댑터
        private ArrayList<GroupMemberData> items;
        GroupMemberData fInfo;

        public GoodsAdapter(Context context, int textViewResourseId, ArrayList<GroupMemberData> items) {
            super(context, textViewResourseId, items);
            this.items = items;
        }

        public View getView(int position, View convertView, ViewGroup parent) {//

            View v = convertView;
            fInfo = items.get(position);

            LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.listview_member, null); //리스트뷰 뷰 불러온다.



            TextView title = (TextView) v.findViewById(R.id.title);//이름
            title.setText(fInfo.getMem_id());


            TextView reg_group = (TextView) v.findViewById(R.id.reg_group);//이름
            if(fInfo.getTag().equals("1")){
                reg_group.setText("멤버");

            }else{
                reg_group.setText("멤버승인");
            }


            reg_group.setTag(position);
            reg_group.setOnClickListener(new View.OnClickListener() {// ����Ʈ��Ŭ�� ��
                // BookDetailActivity��
                // �̵�

                @Override
                public void onClick(View v) { //리스트뷰 클릭시 그룹멤버인지 체크
                    int pos = (Integer) v.getTag();
                    idxx = items.get(pos).getIdx();
                    if(items.get(pos).getTag().equals("1")){
                        Toast.makeText(MemberListActivity.this , "이미 그룹의 회원입니다." , Toast.LENGTH_SHORT).show();
                    }else{
                        new groupmember().execute();
                    }


                }
            });




            return v;
        }
    }

    String idxx="";

    ArrayList<GroupMemberData> mdata = new ArrayList<>();


    private class getGroupMember extends AsyncTask<Void, Void, Void> { //그룹 인덱스를 넘겨서  내가 그룹에 속했는지 , 신청중인지 , 신청안했는지 체크한다.
        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(MemberListActivity.this, "", "데이터 처리중....", true);
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
            URL url = new URL("http://planmanager.cafe24.com/app/group_member_list.php");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setDefaultUseCaches(false);
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            StringBuffer buffer = new StringBuffer();
            buffer.append("group_idx").append("=").append(group_idx).append("&");


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
            mdata.clear();
            try {

                JSONArray jAr = new JSONArray(data); //json 배열을 파싱하여 Arraylist로 만든다.


                for (int i = 0; i < jAr.length(); i++) {
                    JSONObject student = jAr.getJSONObject(i);
                    String idx = student.getString("idx");
                    String mem_id = student.getString("mem_id");
                    String tag = student.getString("tag");


                    mdata.add(new GroupMemberData(idx, mem_id, tag));  //json 배열을 파싱하여 Arraylist로 만든다.

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

            GoodsAdapter mAdapter = new GoodsAdapter(MemberListActivity.this, R.layout.listview_member, mdata);//Arraylist로 리스트뷰에 데이터에 연결해준다.

            TextView nodata = (TextView) findViewById(R.id.nodata);


            if (mdata.size() == 0) {
                nodata.setVisibility(View.VISIBLE);
            } else {
                nodata.setVisibility(View.GONE);
            }
            s_list.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();


        }


    }


    //그룹멤버 승인하기
    private class groupmember extends AsyncTask<Void, Void, Void> {

        private String flag;
        private String message;
        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(MemberListActivity.this, "", "데이터 처리중....", true);

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
            URL url = new URL("http://planmanager.cafe24.com/app/group_member_update.php"); //서버에 회원가입 데이터를 보내서 db에 저장한다.
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setDefaultUseCaches(false);
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            StringBuffer buffer = new StringBuffer();
            buffer.append("idx").append("=").append(idxx).append("&");
            buffer.append("tag").append("=").append(1).append("&");



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

                Toast.makeText(MemberListActivity.this, "승인 완료되었습니다.", Toast.LENGTH_SHORT).show();
                new getGroupMember().execute();


            } else {
                Toast.makeText(MemberListActivity.this, "에러 발생하였습니다.", Toast.LENGTH_SHORT).show();
            }





        }
    }
}
