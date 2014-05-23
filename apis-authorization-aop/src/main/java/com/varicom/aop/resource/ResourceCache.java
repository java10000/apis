package com.varicom.aop.resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.varicom.aop.JerseyClientUtils;
import com.varicom.aop.utils.FileUtil;
import com.sun.jersey.api.client.WebResource;

/*
 * 文 件 名:  ResouceCache.java
 * 版    权:  深圳埃思欧纳信息咨询有限公司版权所有. YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  jeray.wu
 * 修改时间:  2011-7-28
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
public class ResourceCache
{
	private final Logger log=LoggerFactory.getLogger(getClass());
	
    private String[] wadlUrls;

    private static Map<String, WebResource> clients;

    private static Map<String, Resource> cache;

    /**
     * 缓存文件存放路径 
     */
    private String applicationLocalFilePath;

    @Required
	public void setWadlUrls(String[] wadlUrls) {
		this.wadlUrls = wadlUrls;
	}

	@Required
	public void setApplicationLocalFilePath(String applicationLocalFilePath) {
		this.applicationLocalFilePath = applicationLocalFilePath;
	}

    /**
     * 初始化方法
     * @author james.deng
     * @time 2012-3-22
     */
    public void init(){
    	log.info("初始化本地XML缓存目录");
    	if(StringUtils.isNotBlank(applicationLocalFilePath)){
    		File file=new File(applicationLocalFilePath);
    		if(!file.exists()){
    			try {
					FileUtil.newFolder(file);
				} catch (Exception e) {
					log.error("create "+applicationLocalFilePath+" error",e);
				}
    		}
    	}
    	load();
    }
	
	// 创建WebResource
    private WebResource createWebResource(String url)
    {
        return JerseyClientUtils.createClient(url);
    }

    // 获取当前方法的client
    public WebResource getWebResource(String method)
    {
        if (cache == null || clients == null || clients.size() ==0 || cache.size()==0)
        {
            this.load();
        }
        return clients.get(cache.get(method).getBasePath());
    }

    @SuppressWarnings("unchecked")
    public void load()
    {
        // resource list 各应用的根路径list
        cache = new HashMap<String, Resource>();
        // clients
        clients = new HashMap<String, WebResource>();
        
        for (String wadlUrl : wadlUrls) {
			if(StringUtils.isNotBlank(wadlUrl)){
				boolean result=remoteLoad(wadlUrl);
				//如果远程请求失败加载本地xml
				if(!result){
					log.info(wadlUrl+" 远程加载失败，进行本地文件加载");
					if(!localLoad(wadlUrl)){
						log.info(wadlUrl+"本地文件加载失败，请保证远程服务可用");
					}
				}else{
					log.info(wadlUrl+" 远程加载成功");
				}
			}
		}
    }

    /**
     * 远程加载 application.wadl
     * @author james.deng
     * @time 2012-3-22
     * @param wadlUrl 地址
     * @return 是否加载成功
     */
   private boolean remoteLoad(String wadlUrl){
	   InputStreamReader isr = null;
        BufferedReader in = null;
        InputStream inStream = null;
        try
        {
            URL url = new URL(wadlUrl);

            URLConnection urlConnection = url.openConnection();

            inStream = urlConnection.getInputStream();
            isr = new InputStreamReader(inStream, "UTF-8");
            in = new BufferedReader(isr);

            SAXReader reader = new SAXReader();
            Element root = reader.read(in).getRootElement();
            
            //读取xml
            readXML(root);
            
            //创建本地xml
            createLocalFile(root,wadlUrl);
            return true;
        }
        catch (Exception e)
        {
        	log.error("remote load application.wadl error",e);
        	return false;
        }
        finally
        {
            try
            {
                if (in != null)
                {
                    in.close();
                }
                if (isr != null)
                {
                    isr.close();
                }
                if (inStream != null)
                {
                    inStream.close();
                }
            }
            catch (IOException e)
            {
            	log.error("close error",e);
            }
        }
   }
   
   /**
    * 本地加载  application.wadl
    * @author james.deng
    * @time 2012-3-22
    * @param wadlUrl
    * @return
    */
   private boolean localLoad(String wadlUrl){
       File file=new File(applicationLocalFilePath+"/"+getLocalFileName(wadlUrl));
		if (file.exists()) {
			SAXReader reader = new SAXReader();
			try {
				Element root = reader.read(file).getRootElement();
				// 读取xml
				readXML(root);
				log.info(wadlUrl+" 本地加载成功");
				return true;
			} catch (DocumentException e) {
				log.error("read local xml error",e);
			}
		}
       return false;
   }
   
   private void readXML( Element root){
	   Element resources=root.element(ResourceConstant.ELEMENT_RESOURCES);
	   
	   //得到资源根
	   String baseUrl=resources.attributeValue(ResourceConstant.ATTRIBUTE_BASE);
	   baseUrl=StringUtils.removeEnd(baseUrl, "/");
	   System.out.println(baseUrl);
	   List<Element> list = resources.elements(ResourceConstant.ELEMENT_RESOURCE);

       for (Element baseResource : list)
       {
           // resource list 各应用的方法list
           List<Element> tmplist = baseResource.elements(ResourceConstant.ELEMENT_RESOURCE);

           // 初始化WebResource
           clients.put(baseResource.attributeValue(ResourceConstant.ATTRIBUTE_PATH), createWebResource(baseUrl
                   + baseResource.attributeValue(ResourceConstant.ATTRIBUTE_PATH)));

           for (Element secResource : tmplist)
           {
               Resource rc = new Resource();
               rc.setBasePath(baseResource.attributeValue(ResourceConstant.ATTRIBUTE_PATH));
               rc.setMethod(secResource.attributeValue(ResourceConstant.ATTRIBUTE_PATH));
               // 取出method node
               Element methodElement = secResource.element(ResourceConstant.ELEMENT_METHOD);
               if (methodElement == null)
               {
                   continue;
               }
               rc.setMethodId(methodElement.attributeValue(ResourceConstant.ATTRIBUTE_ID));
               rc.setMethodName(methodElement.attributeValue(ResourceConstant.ATTRIBUTE_NAME));
               // 取出request node
               Element requestElement = methodElement.element(ResourceConstant.ELEMENT_REQUEST);
               if (requestElement != null)
               {
                   List<Element> paramElements = requestElement.elements(ResourceConstant.ELEMENT_PARAM);
                   List<ResourceRequest> resourceRequests = new ArrayList<ResourceRequest>();
                   for (Element paramElement : paramElements)
                   {
                       ResourceRequest resourceRequest = new ResourceRequest();
                       resourceRequest.setName(paramElement.attributeValue(ResourceConstant.ATTRIBUTE_NAME));
                       resourceRequest.setStyle(paramElement.attributeValue(ResourceConstant.ATTRIBUTE_STYLE));
                       resourceRequest.setType(paramElement.attributeValue(ResourceConstant.ATTRIBUTE_TYPE));
                       resourceRequests.add(resourceRequest);
                   }
                   rc.setResourceRequest(resourceRequests);
               }
               // 取出 response node
               Element responseElement = methodElement.element(ResourceConstant.ELEMENT_RESPONSE);
               if (responseElement != null)
               {
                   List<Element> representationElements = responseElement
                           .elements(ResourceConstant.ELEMENT_REPRESENTATION);
                   List<ResourceResponse> resourceResponses = new ArrayList<ResourceResponse>();
                   for (Element mediaTypeElement : representationElements)
                   {
                       ResourceResponse resourceResponse = new ResourceResponse();
                       resourceResponse.setMediaType(mediaTypeElement
                               .attributeValue(ResourceConstant.ATTRIBUTE_MEDIATYPE));
                       resourceResponses.add(resourceResponse);
                   }
                   rc.setResourceResponse(resourceResponses);
               }
               String cacheKey=rc.getMethod().replace("/", "");
               if(cache.containsKey(cacheKey)){
            	   log.info(cacheKey+" 存在重复，分别属于:\n"+rc.getBasePath()
            			   +"\n"+cache.get(cacheKey).getBasePath()
            			   +"\n"+"本次以"+rc.getBasePath()+"为准");
               }
               cache.put(cacheKey, rc);
           }

       }
   }
   
   /**
    * 创建本地XML文件
    * @author james.deng
    * @time 2012-3-22
    * @param root
    * @param wadlURL
    */
   public void createLocalFile(Element root,String wadlURL){
	   String filePath=applicationLocalFilePath+"/"+getLocalFileName(wadlURL);
	   XMLWriter writer =null;
		try {
			 writer = new XMLWriter(new FileWriter(new File(filePath)));
			writer.write(root);
		} catch (IOException e) {
			log.error("create Local File error",e);
		}finally{
			if(writer != null){
				try {
					writer.flush();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
   }
   /**
    * 得到本地文件名
    * @author james.deng
    * @time 2012-3-22
    * @param wadlURL
    * @return
    */
   public  String getLocalFileName(String wadlURL){
       byte[] storedBytes = wadlURL.getBytes();
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(storedBytes);
			final byte[] digest = messageDigest.digest();
			return hex(digest)+".xml";
		} catch (NoSuchAlgorithmException e) {
			log.error("wadlURL MD5 error",e);
			return null;
		}
   }
   private static String hex(byte[] arr) {  
       StringBuffer sb = new StringBuffer();  
       for (int i = 0; i < arr.length; ++i) {  
           sb.append(Integer.toHexString((arr[i] & 0xFF) | 0x100).substring(1,  
                   3));  
       }  
       return sb.toString();  
   }  
    public static void main(String[] args)
    {
 //   	System.out.println(getLocalFileName("dsdfds"));
//        ResourceCache rc = new ResourceCache();
//        rc.load();
//        System.out.println("!!!!!!2!!311!!!!!!");

    }
}
