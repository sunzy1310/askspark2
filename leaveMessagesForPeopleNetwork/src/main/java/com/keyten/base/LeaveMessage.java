package com.keyten.base;

public class LeaveMessage {
	private String subject;
	private String tid;
	
	

	public LeaveMessage() {
		super();
	}

	public LeaveMessage(String subject, String tid) {
		super();
		this.subject = subject;
		this.tid = tid;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	
}
