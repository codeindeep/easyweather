package com.easyweather.app.model;

public class County {
	private int id;
	private String coutyName;
	private String coutyCode;
	private int cityId;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCoutyName() {
		return coutyName;
	}
	public void setCoutyName(String coutyName) {
		this.coutyName = coutyName;
	}
	public String getCoutyCode() {
		return coutyCode;
	}
	public void setCoutyCode(String coutyCode) {
		this.coutyCode = coutyCode;
	}
	public int getCityId() {
		return cityId;
	}
	public void setCityId(int cityId) {
		this.cityId = cityId;
	}
}
