package com.varicom.aop;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.varicom.aop.utils.WebUtil;

public class LogFilter implements Filter{

	private final Logger log=Logger.getLogger(getClass());
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		StringBuffer mag=new StringBuffer();
		HttpServletRequest req=(HttpServletRequest)request;
		String method=req.getParameter("method");
		if(StringUtils.isNotBlank(method)){
			mag.append("IP:").append(WebUtil.getIpAddr(req)).append(" Method:").append(method);
			if (method.endsWith("get") || method.endsWith("search"))
			{
				mag.append(" Param:[");
				Set keySet=req.getParameterMap().keySet();
				for (Object obj:keySet) {
					mag.append(obj).append(":").append(req.getParameter(obj.toString())).append(",");
				}
				mag.append("]");
			}
			log.fatal(mag.toString());
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}

}
