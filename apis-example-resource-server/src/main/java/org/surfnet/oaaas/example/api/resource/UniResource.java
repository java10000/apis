/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.surfnet.oaaas.example.api.resource;

import java.security.Principal;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.varicom.api.core.DefaultVaricomClient;
import com.varicom.api.domain.account.User;
import com.varicom.api.request.account.UserQueryRequest;
import com.varicom.api.response.account.QueryUserResponse;
import com.yammer.dropwizard.auth.Auth;
import com.yammer.metrics.annotation.Timed;

/**
 * Account resource
 * 
 */

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class UniResource {

	public static final String url = "";

	public static final String appkey = "test";

	public static final String appSecret = "test";

	public static final String sessionkey = "test";
	
	@POST
	@Timed
	@Path("/query")
	public Response getUserById(@Auth Principal principal) {
		User user = new User();
		QueryUserResponse response;
		try {
			String url = "http://172.16.1.198:9000/api";
			DefaultVaricomClient client = new DefaultVaricomClient(url, appkey, appSecret);// 实例化TopClient类
			client.setLocal(true);
			UserQueryRequest request = new UserQueryRequest();
			request.setUid(3l);
			request.setFields("id, user_type");
			response = client.execute(request, sessionkey); // 执行API请求并打印结果
			System.out.println("result:" + response.getBody());
			System.out.println(response);
			user = response.getUser();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Response.ok(user).build();
	}
}
