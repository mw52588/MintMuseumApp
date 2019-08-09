package com.museum;

public class ShapeData {
	private String name = "";
	private String shape = "";
	private String coords = "";
	private String id = "";
	private String info = "";
	private final String _idPrefix = "@+id/";
	
	public ShapeData(String n, String s, String c, String i, String inf) {
		name = n;
		shape = s;
		coords = c;
		id = i;
		info = inf;
	}
	
	public String getName(){
		return name;
	}
	
	public String getShape(){
		return shape;
	}
	
	public String getCoords(){
		return coords;
	}
	
	public String getId(){
		return _idPrefix + id;
	}
	
	public String getInfo(){
		return info;
	}
}
