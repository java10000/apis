package org.surfnet.oaaas.consent;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class AddableHttpRequest extends HttpServletRequestWrapper {

	public AddableHttpRequest(HttpServletRequest request) {
		super(request);
	}

	private HashMap params = new HashMap();

	public String getParameter(String name) {
		// if we added one, return that one
		if (params.get(name) != null) {
			return (String) params.get(name);
		}
		// otherwise return what's in the original request
		HttpServletRequest req = (HttpServletRequest) super.getRequest();
		return req.getParameter(name);
	}

	public void addParameter(String name, String value) {
		params.put(name, value);
	}

}
