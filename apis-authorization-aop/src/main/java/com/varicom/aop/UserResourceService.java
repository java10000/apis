package com.varicom.aop;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Path("/v1")
public class UserResourceService {
	private static Logger logger = LoggerFactory.getLogger(UserResourceService.class);
		
	@GET
	//@Secured("ROLE_ACEONER")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML + WsConstants.CHARSET })
	public String excute(@QueryParam("method") String method,@QueryParam("format") @DefaultValue("json") String format) {
		System.out.println(format);
		return "{user:\"\"}";
	}
}
