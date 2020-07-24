package com.coolwhite.todaylist;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolwhite.todaylist.R;

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
import java.text.NumberFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RbPreference pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = new RbPreference(this);


        //로그아웃 처리

        Button logout = (Button) findViewById(R.id.logout);//로그아웃 처리
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adialog = new AlertDialog.Builder(MainActivity.this
                );
                adialog.setMessage("로그아웃 하시겠습니까?").setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        pref.put(RbPreference.ISLOGIN, false);


                        Intent moveintent = new Intent(MainActivity.this, LogInActivity.class);
                        moveintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(moveintent);
                        finish();
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = adialog.create();
                alert.show();

            }
        });


        //그룹 등록하기
        Button insert = (Button) findViewById(R.id.insert);
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gt = new Intent(MainActivity.this , GroupInputActivity.class);
                startActivityForResult(gt,0);

            }
        });

        //그룹리스트 불러온다

        new getGroupList().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //startActivityForResult 처리후 호출
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0 && resultCode==RESULT_OK){
            new getGroupList().execute();  //그룹 등록후 여기가 호출되어 그룹 리스트 다시 불러온다.
        }
    }

    ArrayList<GroupData> gdata = new ArrayList<>();


    private class getGroupList extends AsyncTask<Void, Void, Void> {
        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(MainActivity.this, "", "데이터 처리중....", true);
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
            URL url = new URL("http://planmanager.cafe24.com/app/group_list.php");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setDefaultUseCaches(false);
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            StringBuffer buffer = new StringBuffer();

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
            gdata.clear();
            try {

                JSONArray jAr = new JSONArray(data); //json 배열을 파싱하여 Arraylist로 만든다.


                for (int i = 0; i < jAr.length(); i++) {
                    JSONObject student = jAr.getJSONObject(i);
                    String idx = student.getString("idx");
                    String make_id = student.getString("make_id");
                    String title = student.getString("title");
                    String contents = student.getString("contents");
                    String reg_date = student.getString("reg_date");



                    gdata.add(new GroupData(idx,make_id,title,reg_date,contents));  //json 배열을 파싱하여 Arraylist로 만든다.

                }
            } catch (JSONException e) {
                Log.d("tag", "Parse Error " +e.toString());
            }

        }

        protected void onPostExecute(Void result) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
            ListView s_list = (ListView) findViewById(R.id.s_list);

            GoodsAdapter mAdapter = new GoodsAdapter(MainActivity.this, R.layout.listview_group, gdata);//Arraylist로 리스트뷰에 데이터에 연결해준다.

            TextView nodata = (TextView) findViewById(R.id.nodata);


            if(gdata.size()==0){
                nodata.setVisibility(View.VISIBLE);
            }else{
                nodata.setVisibility(View.GONE);
            }
            s_list.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();



        }
    }

    public class GoodsAdapter extends ArrayAdapter<GroupData> {//리스스뷰 어댑터
        private ArrayList<GroupData> items;
        GroupData fInfo;

        public GoodsAdapter(Context context, int textViewResourseId, ArrayList<GroupData> items) {
            super(context, textViewResourseId, items);
            this.items = items;
        }

        public View getView(int position, View convertView, ViewGroup parent) {//

            View v = convertView;
            fInfo = items.get(position);

            LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.listview_group, null); //리스트뷰 뷰 불러온다.



            TextView title = (TextView) v.findViewById(R.id.title);//이름
            title.setText(fInfo.getTitle());

            TextView contents = (TextView) v.findViewById(R.id.contents);//가격
            contents.setText(fInfo.getContents());

            TextView reg_date = (TextView) v.findViewById(R.id.reg_date);//수정 버튼 누를시 처리
            reg_date.setText(fInfo.getReg_date().substring(0,10));

            TextView reg_group = (TextView) v.findViewById(R.id.reg_group);
            reg_group.setTag(position);
            reg_group.setOnClickListener(new View.OnClickListener() {// ����Ʈ��Ŭ�� ��
                // BookDetailActivity��
                // �̵�

                @Override
                public void onClick(View v) { //버튼 클릭시 그룹멤버인지 체크
                    int pos = (Integer) v.getTag();
                    group_idx = items.get(pos).getIdx();
                    new getGroupMember().execute();


                }
            });


            v.setTag(position);
            v.setOnClickListener(new View.OnClickListener() {// ����Ʈ��Ŭ�� ��
                // BookDetailActivity��
                // �̵�

                @Override
                public void onClick(View v) { //리스트뷰 클릭시 그룹멤버인지 체크
                    int pos = (Integer) v.getTag();
                    idxx=pos;
                    group_idx = items.get(pos).getIdx();
                    new getGroupMember1().execute();


                }
            });


            return v;
        }
    }

    String group_idx="";
    int idxx=0;

    ArrayList<GroupMemberData> mdata = new ArrayList<>();


    private class getGroupMember extends AsyncTask<Void, Void, Void> { //그룹 인덱스를 넘겨서  내가 그룹에 속했는지 , 신청중인지 , 신청안했는지 체크한다.
        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(MainActivity.this, "", "데이터 처리중....", true);
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



                    mdata.add(new GroupMemberData(idx,mem_id,tag));  //json 배열을 파싱하여 Arraylist로 만든다.

                }
            } catch (JSONException e) {
                Log.d("tag", "Parse Error " +e.toString());
            }

        }

        protected void onPostExecute(Void result) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }

            int idx=-1;

            for(int i = 0 ; i < mdata.size(); i++){ //그룹멤버중에 내 아이디가 있는지 확인한다
                if(mdata.get(i).getMem_id().equals(pref.getValue(RbPreference.MEM_ID,""))){
                    idx=i;
                    break;
                }
            }

            if(idx==-1){ //그룹중에 신청아이디가 없으므로 그룹신청 팝업을 띄운다.
                AlertDialog.Builder adialog = new AlertDialog.Builder(MainActivity.this
                );
                adialog.setMessage("그룹에 가입 하시겠습니까?").setPositiveButton("가입", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        new groupmember().execute();

                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = adialog.create();
                alert.show();
            }else{ //그룹 멤버이므로 신청중이면 신청중 아니면 이미 그룹에 가입되었다고 알린다.
                if(mdata.get(idx).getTag().equals("1")){
                    Toast.makeText(MainActivity.this , "이미 그룹의 회원입니다." , Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this , "그룹 가입 신청중입니다." , Toast.LENGTH_SHORT).show();
                }

            }


        }
    }


    private class getGroupMember1 extends AsyncTask<Void, Void, Void> { //그룹 인덱스를 넘겨서  내가 그룹에 속했는지 , 신청중인지 , 신청안했는지 체크한다.
        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(MainActivity.this, "", "데이터 처리중....", true);
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



                    mdata.add(new GroupMemberData(idx,mem_id,tag));  //json 배열을 파싱하여 Arraylist로 만든다.

                }
            } catch (JSONException e) {
                Log.d("tag", "Parse Error " +e.toString());
            }

        }

        protected void onPostExecute(Void result) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }

            int idx=-1;

            for(int i = 0 ; i < mdata.size(); i++){ //그룹멤버중에 내 아이디가 있는지 확인한다
                if(mdata.get(i).getMem_id().equals(pref.getValue(RbPreference.MEM_ID,""))){
                    idx=i;
                    break;
                }
            }

            if(idx==-1){ //그룹중에 신청아이디가 없으므로 그룹신청 팝업을 띄운다.
                AlertDialog.Builder adialog = new AlertDialog.Builder(MainActivity.this
                );
                adialog.setMessage("그룹에 가입 하시겠습니까?").setPositiveButton("가입", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        new groupmember().execute();

                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = adialog.create();
                alert.show();
            }else{ //그룹 멤버이므로 신청중이면 신청중 아니면 이미 그룹에 가입되었다고 알린다.
                if(mdata.get(idx).getTag().equals("1")){ //그룹 멤버이면 일정 엑티비티로 이돈한다.
                  //그룹의 회원이면 상세 내용을 본다.

                    Intent gt = null;

					gt = new Intent(MainActivity.this, GroupSchedeulActivity.class);
					Bundle ex = new Bundle();
					ex.putSerializable("list", gdata.get(idxx)); //그룹의 정보를 다 넘긴다.
					gt.putExtras(ex);

					startActivityForResult(gt,1);

                }else{
                    Toast.makeText(MainActivity.this , "그룹 가입 신청중입니다." , Toast.LENGTH_SHORT).show();
                }

            }


        }
    }


    //그룹신청하기
    private class groupmember extends AsyncTask<Void, Void, Void> {

        private String flag;
        private String message;
        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(MainActivity.this, "", "데이터 처리중....", true);

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
            URL url = new URL("http://planmanager.cafe24.com/app/group_member_insert.php"); //서버에 회원가입 데이터를 보내서 db에 저장한다.
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setDefaultUseCaches(false);
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            StringBuffer buffer = new StringBuffer();
            buffer.append("mem_id").append("=").append(pref.getValue(RbPreference.MEM_ID,"")).append("&");
            buffer.append("group_idx").append("=").append(group_idx).append("&");
            buffer.append("tag").append("=").append(0).append("&");



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

                Toast.makeText(MainActivity.this, "그룹 신청 완료되었습니다.", Toast.LENGTH_SHORT).show();


            } else {
                Toast.makeText(MainActivity.this, "에러 발생하였습니다.", Toast.LENGTH_SHORT).show();
            }





        }
    }
}
