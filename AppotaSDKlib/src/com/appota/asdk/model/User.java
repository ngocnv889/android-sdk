package com.appota.asdk.model;

public class User {

	private String picture;
	private String userName;
	private String name;
	private String birthday;
	private String gender;
	private int greenTym;
	private int purpleTym;
	private String email;
	private String area;
	private String city;
	private String job;
	private int errorCode;
	
	public User(){}

	public int getPurpleTym() {
		return purpleTym;
	}

	public void setPurpleTym(int purpleTym) {
		this.purpleTym = purpleTym;
	}

	public String getAvatarUr() {
		return picture;
	}

	public void setAvatarUr(String avatarUr) {
		this.picture = avatarUr;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public User(String name){
		this.userName = name;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFullName() {
		return name;
	}

	public void setFullName(String fullName) {
		this.name = fullName;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public int getGreenTym() {
		return greenTym;
	}

	public void setGreenTym(int greenTym) {
		this.greenTym = greenTym;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

}
