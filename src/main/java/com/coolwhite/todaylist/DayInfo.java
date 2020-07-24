//달력 데이터 클래스
package com.coolwhite.todaylist;

public class DayInfo {
	private String day; //날짜
	private String mMonth;//월
	private String mYear;//년도
	private boolean inMonth;//이번달의 날자인지
	private String sc_type; //일정이 있는지 판단

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	private String title;

	public DayInfo(String day, String mMonth, String mYear, boolean inMonth,
                   String sc_type, String title) {
		super();
		this.day = day;
		this.mMonth = mMonth;
		this.mYear = mYear;
		this.inMonth = inMonth;
		this.sc_type = sc_type;
		this.title = title;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getmMonth() {
		return mMonth;
	}

	public void setmMonth(String mMonth) {
		this.mMonth = mMonth;
	}

	public String getmYear() {
		return mYear;
	}

	public void setmYear(String mYear) {
		this.mYear = mYear;
	}

	public boolean isInMonth() {
		return inMonth;
	}

	public void setInMonth(boolean inMonth) {
		this.inMonth = inMonth;
	}

	public String getSc_type() {
		return sc_type;
	}

	public void setSc_type(String sc_type) {
		this.sc_type = sc_type;
	}

}
