package com.museum;

import com.parse.ParseObject;

//Class of string data in order to store the data without querying data to the server everytime.
public class Artwork {
	private String objectId, artistName, artworkInfo, artworkName;

	Artwork(ParseObject object) {
		this.objectId = object.getString("objectId");
		this.artistName = object.getString("artistName");
		this.artworkInfo = object.getString("artworkInfo");
		this.artworkName = object.getString("artworkName");
		
	}
	
	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	public String getArtworkInfo() {
		return artworkInfo;
	}

	public void setArtworkInfo(String artworkInfo) {
		this.artworkInfo = artworkInfo;
	}

	public String getArtworkName() {
		return artworkName;
	}

	public void setArtworkName(String artworkName) {
		this.artworkName = artworkName;
	}
	
	
}
