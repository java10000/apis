package com.varicom.aop;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.varicom.aop.resource.ResourceCache;

public class LoadWADLController extends AbstractController
{
	private final Logger log=LoggerFactory.getLogger(getClass());

    private ResourceCache resourceCache;

    @Required
    public void setResourceCache(ResourceCache resourceCache)
    {
        this.resourceCache = resourceCache;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
    	log.info("重新加载 Application.wadl");
    	
    	resourceCache.load();
    	
    	ESBController.renderText(response, "加载完成");
        return null;
    }


}
