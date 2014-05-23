package com.varicom.aop.resource;

import java.util.List;

/*
 * 文 件 名:  Resouce.java
 * 版    权:  深圳埃思欧纳信息咨询有限公司版权所有. YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  jeray.wu
 * 修改时间:  2011-7-28
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
public class Resource
{
    String basePath;

    String method;

    String methodId;

    String methodName;

    List<ResourceRequest> resourceRequest;

    List<ResourceResponse> resourceResponse;

    public String getMethodId()
    {
        return methodId;
    }

    public void setMethodId(String methodId)
    {
        this.methodId = methodId;
    }

    public String getMethodName()
    {
        return methodName;
    }

    public void setMethodName(String methodName)
    {
        this.methodName = methodName;
    }

    public List<ResourceRequest> getResourceRequest()
    {
        return resourceRequest;
    }

    public void setResourceRequest(List<ResourceRequest> resourceRequest)
    {
        this.resourceRequest = resourceRequest;
    }

    public List<ResourceResponse> getResourceResponse()
    {
        return resourceResponse;
    }

    public void setResourceResponse(List<ResourceResponse> resourceResponse)
    {
        this.resourceResponse = resourceResponse;
    }

    public String getBasePath()
    {
        return basePath;
    }

    public void setBasePath(String basePath)
    {
        this.basePath = basePath;
    }

    public String getMethod()
    {
        return method;
    }

    public void setMethod(String method)
    {
        this.method = method;
    }

}
