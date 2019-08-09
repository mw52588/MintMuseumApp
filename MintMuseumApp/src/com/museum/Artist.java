package com.museum;

import java.util.Date;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseFile;
import com.parse.ParseObject;

public class Artist {

    private String artName = "";
	private String country = "";
	private String objectId = "";
	private String death = "";
	private String info = "";
	private String year = "";
	
	public Artist (ParseObject object) {
		setArtName(object.getString("artistName"));
		country = object.getString("country");
		objectId = object.getObjectId();
		setYear(object.getString("birthYear"));
		death = object.getString("death");
		info = object.getString("info");
	}

	public String getArtName() {
		return artName;
	}

	public void setArtName(String artName) {
		this.artName = artName;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getDeath() {
		return death;
	}

	public void setDeath(String death) {
		this.death = death;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}
}
