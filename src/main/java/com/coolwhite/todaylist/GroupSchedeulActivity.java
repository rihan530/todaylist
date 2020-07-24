//그룹 스케줄
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;


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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class GroupSchedeulActivity extends AppCompatActivity {

    RbPreference pref;
    GroupData gdata;
    private CalendarAdapter mCalendarAdapter;
    Calendar mThisMonthCalendar;
    GridView mGvCalendar;
    ArrayList<DayInfo> mDayList = new ArrayList<DayInfo>(); // 달력 데이터
    ArrayList<ScheduleData> mScList = new ArrayList<ScheduleData>(); // 스케쥴
    int yearNow;
    int monNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        pref = new RbPreference(this);

        gdata = (GroupData)getIntent().getSerializableExtra("list");


        SimpleDateFormat df = new SimpleDateFormat("yyyy", Locale.KOREA);
        String str_date = df.format(new Date());

        SimpleDateFormat df1 = new SimpleDateFormat("MM", Locale.KOREA);
        String str_date1 = df1.format(new Date());

        mThisMonthCalendar = Calendar.getInstance();// 캘린더 객체 얻어옮

        yearNow = Integer.parseInt(str_date);//오늘날짜
        monNow = Integer.parseInt(str_date1);

        TextView main_txt = (TextView) findViewById(R.id.main_txt);
        main_txt.setText(gdata.getTitle());


        Button group_mem = (Button) findViewById(R.id.group_mem);
        group_mem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gt = new Intent(GroupSchedeulActivity.this, MemberListActivity.class);
                gt.putExtra("idx" ,gdata.getIdx());
                startActivity(gt);
            }
        }); //그룹멥버 보기


        setButtonCalendar(); //버튼 세팅

        new getSchedule().execute();//이번달의 그룹의 일정을 불러온다.
    }

    private void setButtonCalendar() { // 캘린더에 이전달 다음달 버튼 처리
        Button btn_pre = (Button) findViewById(R.id.s_pre);
        btn_pre.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mThisMonthCalendar.add(Calendar.MONTH, -1);

                yearNow = mThisMonthCalendar.get(Calendar.YEAR);
                monNow = mThisMonthCalendar.get(Calendar.MONTH) + 1;
                new getSchedule().execute();

            }
        });

        Button btn_next = (Button) findViewById(R.id.s_next);
        btn_next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mThisMonthCalendar.add(Calendar.MONTH, 1);

                yearNow = mThisMonthCalendar.get(Calendar.YEAR);
                monNow = mThisMonthCalendar.get(Calendar.MONTH) + 1;
                new getSchedule().execute();

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //startActivityForResult 처리후 호출
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==11) {
            new getSchedule().execute();
        }
    }

    private class getSchedule extends AsyncTask<Void, Void, Void> { //그룹의 스케줄을 받아온다.
        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(GroupSchedeulActivity.this, "", "데이터 처리중....", true);
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
            buffer.append("group_idx").append("=").append(gdata.getIdx()).append("&");
            buffer.append("year").append("=").append(yearNow).append("&");
            buffer.append("month").append("=").append(monNow).append("&");


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


                    mScList.add(new ScheduleData(idx, group_idx,reg_id,reg_date,contents,sc_date, tag));  //json 배열을 파싱하여 Arraylist로 만든다.

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

            setCal();
            setCalendar();
            getCalendar(mThisMonthCalendar);

            ListView s_list = (ListView) findViewById(R.id.s_list);

            GoodsAdapter mAdapter = new GoodsAdapter(GroupSchedeulActivity.this, R.layout.listview_member, mScList);//Arraylist로 리스트뷰에 데이터에 연결해준다.

            s_list.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            setListViewHeightBasedOnChildren(s_list);

        }


    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


    public class GoodsAdapter extends ArrayAdapter<ScheduleData> {//리스트뷰 어댑터
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
            update.setVisibility(View.GONE);


            v.setTag(position);
            v.setOnClickListener(new View.OnClickListener() {// ����Ʈ��Ŭ�� ��
                // BookDetailActivity��
                // �̵�

                @Override
                public void onClick(View v) { //리스트뷰 클릭시 그룹멤버인지 체크
                    int pos = (Integer) v.getTag();
                    idxx = items.get(pos).getIdx();
                    Intent gt = new Intent(GroupSchedeulActivity.this,
                            ScheduleDetailActivity.class); // ScheduleInfoActvity로
//
                    gt.putExtra("idx", idxx);
                    gt.putExtra("txt", items.get(pos).getContents());


                    startActivityForResult(gt, 0);




                }
            });





            return v;
        }
    }

    String idxx="";

    private void setCalendar() { // 이번달 년 월 출력
        mThisMonthCalendar.set(Calendar.DAY_OF_MONTH, 1);


        TextView s_today = (TextView) findViewById(R.id.s_today);

        s_today.setText(mThisMonthCalendar.get(Calendar.YEAR) + ". "
                + (mThisMonthCalendar.get(Calendar.MONTH) + 1) + "월");

    }
    public static int SUNDAY = 1;

    private void getCalendar(Calendar calendar) {// 이번달의 달력 값들을 세팅해준다
        int lastMonthStartDay;
        int dayOfMonth;
        int thisMonthLastDay;

        mDayList.clear();

        // 이번달 시작일의 요일을 구한다. 시작일이 일요일인 경우 인덱스를 1(일요일)에서 8(다음주 일요일)로 바꾼다.)
        dayOfMonth = calendar.get(Calendar.DAY_OF_WEEK);
        thisMonthLastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        calendar.add(Calendar.MONTH, -1);

        // 지난달의 마지막 일자를 구한다.
        lastMonthStartDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        calendar.add(Calendar.MONTH, 1);

        if (dayOfMonth == SUNDAY) {
            dayOfMonth += 7;
        }

        lastMonthStartDay -= (dayOfMonth - 1) - 1;

        DayInfo day = null;

        for (int i = 0; i < dayOfMonth - 1; i++) { // 지날달의 남은 일자들을 구해서 mDayList
            // add 시켜준다
            int date = lastMonthStartDay + i;
            day = new DayInfo(Integer.toString(date),
                    String.valueOf(mThisMonthCalendar.get(Calendar.MONTH) + 1),
                    String.valueOf(mThisMonthCalendar.get(Calendar.YEAR)),
                    false, "0", "");  //DayInfo 클래스는 CalendarAdapter 에서 쓰이며 캘린더 그리드 뷰에 하나하나의 내용을 나타낸다.


            mDayList.add(day);
        }
        for (int i = 1; i <= thisMonthLastDay; i++) { // 이번달 날짜들을 mDayList add
            // 시켜준다
            String days = "";


            days = Integer.toString(i);
            String mon = "";
            int tempMon = mThisMonthCalendar.get(Calendar.MONTH) + 1;
            if (tempMon < 10) {
                mon = "0" + tempMon;
            } else {
                mon = Integer.toString(tempMon);
            }

            // 이번달 날짜중에 일정 값이 있는 날짜가 있는지 체크한다(내용  표시용 태그)
            int idx = checkDate(days);
            String memo = "";
            String cate = "";

            if (idx == -1) {
                memo = "";
                cate = "0";
            } else {
                memo = mScList.get(idx).getContents(); //내용
                cate = mScList.get(idx).getContents(); //내용저장
            }

            day = new DayInfo(Integer.toString(i),
                    String.valueOf(mThisMonthCalendar.get(Calendar.MONTH) + 1),
                    String.valueOf(mThisMonthCalendar.get(Calendar.YEAR)),
                    true, cate, memo);  //DayInfo 클래스는 CalendarAdapter 에서 쓰이며 캘린더 그리드 뷰에 하나하나의 내용을 나타낸다.
            //memo 값에는 내용이 들어간다

            mDayList.add(day);
        }
        for (int i = 1; i < 42 - (thisMonthLastDay + dayOfMonth - 1) + 1; i++) { // 다음달의
            // 시작
            // 날짜들을
            // 구해서
            // mDayList
            // add
            // 시켜준다
            day = new DayInfo(Integer.toString(i),
                    String.valueOf(mThisMonthCalendar.get(Calendar.MONTH) + 1),
                    String.valueOf(mThisMonthCalendar.get(Calendar.YEAR)),
                    false, "0", "");
            mDayList.add(day);
        }

        mCalendarAdapter = new CalendarAdapter(getApplicationContext(),
                R.layout.grid_calendar, mDayList, "");
        mGvCalendar.setAdapter(mCalendarAdapter); // 캘린더 어댑터를 생성해서 mGvCalendar
        // 그리드뷰에 연결해준다

        int spec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                View.MeasureSpec.AT_MOST);
        mGvCalendar.measure(0, spec);
        mGvCalendar.getLayoutParams().height = mGvCalendar.getMeasuredHeight();

    }

    private int checkDate(String string) { // mScList. 에는 서버에서 가져온 스케줄값이 들어있다.
        // 저장 되어있다(

        for (int i = 0; i < mScList.size(); i++) {
            String reg_date = mScList.get(i).getSc_date().replace("00:00:00", "").trim();
            int lenth = reg_date.length();
            int aa = Integer.parseInt(reg_date.substring(lenth - 2, lenth));


            if (string.equals(aa + "")) { // 오늘날짜와 일치하면 메모 값
                // 리턴 없으면 "-1" 리턴
                return i;
            }
        }
        return -1;

    }

    private void setCal() { //달력클릭시 이벤트 처리


        mGvCalendar = (GridView) findViewById(R.id.gv_calendar_activity_gv_calendar);// 캘린더 그리드뷰

        mGvCalendar.setOnItemClickListener(new AdapterView.OnItemClickListener() { //달력 날짜 클릭시 처리
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                if (mDayList.get(position).isInMonth()) {
                    Intent gt = new Intent(GroupSchedeulActivity.this,
                            ScheduleListActivity.class); // ScheduleInfoActvity로
//
                    int mon = mThisMonthCalendar.get(Calendar.MONTH) + 1;
                    String month = "";
                    if (mon < 10) {
                        month = "0" + mon;
                    } else {
                        month = "" + mon;
                    }
//
                    String dayTxt = "";
                    int day = Integer.parseInt(mDayList.get(position).getDay());
                    if (day < 10) {
                        dayTxt = "0" + mDayList.get(position).getDay();
                    } else {
                        dayTxt = Integer.toString(day);
                    }
//

                    gt.putExtra("day", mDayList.get(position).getmYear() + "-"
                            + month + "-"
                            + dayTxt);

                    gt.putExtra("group_idx",gdata.getIdx());
                     startActivityForResult(gt, 11);    // 오늘 날짜를 가지고 엑티비티 이동한다


                }
            }
        });
    }

}
