//그룹회원 클래스
package com.coolwhite.todaylist;

import java.io.Serializable;

public class GroupMemberData implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	String idx = "";// 인덱스
	String mem_id = "";// 회원아이디
	String tag = "";// 태그(그룹의 회원인지 아닌지 판단)

	public GroupMemberData(String idx, String mem_id, String tag) {
		this.idx = idx;
		this.mem_id = mem_id;
		this.tag = tag;
	}

	public String getIdx() {
		return idx;
	}

	public void setIdx(String idx) {
		this.idx = idx;
	}

	public String getMem_id() {
		return mem_id;
	}

	public void setMem_id(String mem_id) {
		this.mem_id = mem_id;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}













}
