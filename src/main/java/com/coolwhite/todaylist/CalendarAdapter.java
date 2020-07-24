//캘린더 어댑터
package com.coolwhite.todaylist;

import android.content.Context;
import android.graphics.Color;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CalendarAdapter extends BaseAdapter {
	private ArrayList<DayInfo> mDayList;
	private Context mContext;
	private int mResource;
	private LayoutInflater mLiInflater;
	Display display;




	public CalendarAdapter(Context context, int textResource,
                           ArrayList<DayInfo> dayList, String commusergroup) { //
		this.mContext = context;
		this.mDayList = dayList;
		this.mResource = textResource;
		this.mLiInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		display = ((WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDayList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mDayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final DayInfo day = mDayList.get(position);

		DayViewHolde dayViewHolder;

		if (convertView == null) {
			convertView = mLiInflater.inflate(mResource, null);

			if (position % 7 == 6) { //달력크기 정해줌
				convertView.setLayoutParams(new GridView.LayoutParams(
						getCellWidthDP() + getRestCellWidthDP(),
						getCellHeightDP()));
			} else {
				convertView.setLayoutParams(new GridView.LayoutParams(
						getCellWidthDP(), getCellHeightDP()));
			}

			dayViewHolder = new DayViewHolde();

			dayViewHolder.tvDay = (TextView) convertView
					.findViewById(R.id.day_cell_tv_day);
			dayViewHolder.img = (ImageView) convertView
					.findViewById(R.id.img);
			

			convertView.setTag(dayViewHolder);
		} else {
			dayViewHolder = (DayViewHolde) convertView.getTag();
		}

		if (day != null) {

			dayViewHolder.tvDay.setText(day.getDay());
			if(day.getSc_type().equals("0")){ //일정이 없으면 이미지 안보여줌
				dayViewHolder.img.setVisibility(View.GONE);
				
			}else{ //일정 있으면 이미지 보여줌
				dayViewHolder.img.setVisibility(View.VISIBLE);

			}
			

			if (day.isInMonth()) { //날짜 색상 처리
				if (position % 7 == 0) {
					dayViewHolder.tvDay.setTextColor(Color.RED);
				} else if (position % 7 == 6) {
					dayViewHolder.tvDay.setTextColor(Color.BLUE);
				} else {
					dayViewHolder.tvDay.setTextColor(Color.BLACK);
				}


			} else {
				dayViewHolder.tvDay.setTextColor(Color.GRAY);
			}

		}

		return convertView;
	}


	public class DayViewHolde {
		public TextView tvDay;
		public TextView day_txt;
		public ImageView img;
		
	}

	private int getCellWidthDP() {
		int cellWidth = (display.getWidth() - 12) / 7;

		return cellWidth;
	}

	private int getRestCellWidthDP() {
		int cellWidth = (display.getWidth() - 12) % 7;

		return cellWidth;
	}

	private int getCellHeightDP() {
		int cellHeight = (display.getWidth() - 12) / 7;

		return cellHeight;
	}

}
