package com.example.demo;

//import java.util.Map;

public class ObjectWithKey {
	private String key;
	//private Map<String, Object> object;
	private Object object;
	
	public ObjectWithKey() {
	}

	public ObjectWithKey(String key, Object object) {//Map<String, Object> object) {
		super();
		this.key = key;
		this.object = object;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	//public Map<String, Object> getObject() {
	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {//Map<String, Object> object) {
		this.object = object;
	}
}
