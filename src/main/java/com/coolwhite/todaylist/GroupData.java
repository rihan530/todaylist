//그룹정보 클래스
package com.coolwhite.todaylist;

import java.io.Serializable;

public class GroupData implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	String idx = "";// 인덱스
	String make_id = "";// 등록자 아이디
	String title = "";// 그룹명
	String reg_date = "";// 등록일

	public GroupData(String idx, String make_id, String title, String reg_date, String contents) {
		this.idx = idx;
		this.make_id = make_id;
		this.title = title;
		this.reg_date = reg_date;
		this.contents = contents;
	}

	public String getIdx() {
		return idx;
	}

	public void setIdx(String idx) {
		this.idx = idx;
	}

	public String getMake_id() {
		return make_id;
	}

	public void setMake_id(String make_id) {
		this.make_id = make_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	String contents = "";// 그룹소개








}
