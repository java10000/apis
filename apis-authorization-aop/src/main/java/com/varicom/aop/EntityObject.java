package com.varicom.aop;

import java.io.Serializable;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
@XmlRootElement
public class EntityObject extends JSONObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EntityObject() {
		super();
		// TODO Auto-generated constructor stub
	}

	public EntityObject(JSONObject jo, String[] names) throws JSONException {
		super(jo, names);
		// TODO Auto-generated constructor stub
	}

	public EntityObject(JSONTokener x) throws JSONException {
		super(x);
		// TODO Auto-generated constructor stub
	}

	public EntityObject(Map map, boolean includeSuperClass) {
		super(map, includeSuperClass);
		// TODO Auto-generated constructor stub
	}

	public EntityObject(Map map) {
		super(map);
		// TODO Auto-generated constructor stub
	}

	public EntityObject(Object bean, boolean includeSuperClass) {
		super(bean, includeSuperClass);
		// TODO Auto-generated constructor stub
	}

	public EntityObject(Object object, String[] names) {
		super(object, names);
		// TODO Auto-generated constructor stub
	}

	public EntityObject(Object bean) {
		super(bean);
		// TODO Auto-generated constructor stub
	}

	public EntityObject(String source) throws JSONException {
		super(source);
		// TODO Auto-generated constructor stub
	}

}
