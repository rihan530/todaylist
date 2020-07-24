//스케쥴 데이터
package com.coolwhite.todaylist;

import java.io.Serializable;

public class ScheduleData implements Serializable {
	String idx = "";
	String group_idx = "";//그룹의 인덱스
	String reg_id= "";//등록자
	String reg_date = "";//등록일
	String contents = "";//내용
	String sc_date = "";

	public ScheduleData(String idx, String group_idx, String reg_id, String reg_date, String contents, String sc_date, String tag) {
		this.idx = idx;
		this.group_idx = group_idx;
		this.reg_id = reg_id;
		this.reg_date = reg_date;
		this.contents = contents;
		this.sc_date = sc_date;
		this.tag = tag;
	}

	public String getIdx() {
		return idx;
	}

	public void setIdx(String idx) {
		this.idx = idx;
	}

	public String getGroup_idx() {
		return group_idx;
	}

	public void setGroup_idx(String group_idx) {
		this.group_idx = group_idx;
	}

	public String getReg_id() {
		return reg_id;
	}

	public void setReg_id(String reg_id) {
		this.reg_id = reg_id;
	}

	public String getReg_date() {
		return reg_date;
	}

	public void setReg_date(String reg_date) {
		this.reg_date = reg_date;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public String getSc_date() {
		return sc_date;
	}

	public void setSc_date(String sc_date) {
		this.sc_date = sc_date;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	String tag = "";










}
