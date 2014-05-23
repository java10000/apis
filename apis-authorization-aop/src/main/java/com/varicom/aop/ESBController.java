package com.varicom.aop;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.varicom.aop.resource.ResourceCache;
import com.varicom.aop.utils.web.ServletUtils;
import com.sun.jersey.api.client.WebResource;

public class ESBController extends AbstractController
{
    // -- header 常量定义 --//
    private static final String HEADER_ENCODING = "encoding";

    private static final String HEADER_NOCACHE = "no-cache";

    private static final String DEFAULT_ENCODING = "UTF-8";

    private static final boolean DEFAULT_NOCACHE = true;

    private static ObjectMapper mapper = new ObjectMapper();

    private ResourceCache resourceCache;

    @Required
    public void setResourceCache(ResourceCache resourceCache)
    {
        this.resourceCache = resourceCache;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Secured("ROLE_ACEONER")
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        // long l1 = System.currentTimeMillis();
        String method = request.getParameter("method");
        String format = request.getParameter("format");

        Map<String, String[]> parameters = request.getParameterMap();
        WebResource wr = resourceCache.getWebResource(method).path(method);

        for (Map.Entry<String, String[]> entry : parameters.entrySet())
        {
            if (!entry.getKey().equals("object_parameter"))
                wr = wr.queryParam(entry.getKey(), parameters.get(entry.getKey())[0]);
        }
        String result = "";
        if (method.endsWith("get"))
        {
            result = wr.accept(
                    format.toLowerCase().equals("json") ? MediaType.APPLICATION_JSON : MediaType.APPLICATION_XML).get(
                    String.class);
        }
        else if (method.endsWith("add") || method.endsWith("addChild"))
        {
            result = wr.accept(
                    format.toLowerCase().equals("json") ? MediaType.APPLICATION_JSON : MediaType.APPLICATION_XML)
                    .entity(parameters.get("object_parameter")[0], MediaType.APPLICATION_JSON).post(String.class);
        }
        else if (method.endsWith("update") || method.endsWith("updateChild"))
        {
            String entity = parameters.containsKey("object_parameter")?parameters.get("object_parameter")[0]:null;
            result = wr.accept(
                    format.toLowerCase().equals("json") ? MediaType.APPLICATION_JSON : MediaType.APPLICATION_XML)
                    .entity(entity, MediaType.APPLICATION_JSON).post(String.class);
        }
        else if (method.endsWith("delete"))
        {
            result = wr.accept(
                    format.toLowerCase().equals("json") ? MediaType.APPLICATION_JSON : MediaType.APPLICATION_XML)
                    .delete(String.class);
        }
        else if (method.endsWith("search"))
        {
        	Object param=parameters.containsKey("object_parameter")?parameters.get("object_parameter")[0]:null;
            result = wr.accept(
                    format.toLowerCase().equals("json") ? MediaType.APPLICATION_JSON : MediaType.APPLICATION_XML)
                    .entity(param, MediaType.APPLICATION_JSON).get(String.class);
        }
        else if (method.endsWith("updateIndexer"))
        {
            result = wr.accept(
                    format.toLowerCase().equals("json") ? MediaType.APPLICATION_JSON : MediaType.APPLICATION_XML).post(
                    String.class);
        }
        // 不对外开放索引管理接口
        // else{
        // result =
        // wr.accept(format.toLowerCase().equals("json")?MediaType.APPLICATION_JSON:MediaType.APPLICATION_XML).entity(parameters.get("object_parameter")[0],MediaType.APPLICATION_JSON).post(String.class);
        // }
        // long l2 = System.currentTimeMillis();
        // System.out.println(method+"|"+(l2-l1));
        // System.out.println(result);
        if (format.toLowerCase().toLowerCase().equals("json"))
            renderText(response, result);
        else
            renderXml(response, result);

        return null;
    }

    public static void render(final HttpServletResponse res, final String contentType, final String content,
            final String... headers)
    {
        HttpServletResponse response = initResponseHeader(res, contentType, headers);
        try
        {
            response.getWriter().write(content);
            response.getWriter().flush();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 直接输出文本.
     * @see #render(String, String, String...)
     */
    public static void renderText(final HttpServletResponse response, final String text, final String... headers)
    {
        render(response, ServletUtils.TEXT_TYPE, text, headers);
    }

    /**
     * 直接输出HTML.
     * @see #render(String, String, String...)
     */
    public static void renderHtml(final HttpServletResponse response, final String html, final String... headers)
    {
        render(response, ServletUtils.HTML_TYPE, html, headers);
    }

    /**
     * 直接输出XML.
     * @see #render(String, String, String...)
     */
    public static void renderXml(final HttpServletResponse response, final String xml, final String... headers)
    {
        render(response, ServletUtils.XML_TYPE, xml, headers);
    }

    /**
     * 直接输出JSON.
     * 
     * @param jsonString json字符串.
     * @see #render(String, String, String...)
     */
    public static void renderJson(final HttpServletResponse response, final String jsonString, final String... headers)
    {
        render(response, ServletUtils.JSON_TYPE, jsonString, headers);
    }

    /**
     * 直接输出JSON,使用Jackson转换Java对象.
     * 
     * @param data 可以是List<POJO>, POJO[], POJO, 也可以Map名值对.
     * @see #render(String, String, String...)
     */
    public static void renderJson(final HttpServletResponse res, final Object data, final String... headers)
    {
        HttpServletResponse response = initResponseHeader(res, ServletUtils.JSON_TYPE, headers);
        try
        {
            mapper.writeValue(response.getWriter(), data);
        }
        catch (IOException e)
        {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 分析并设置contentType与headers.
     */
    private static HttpServletResponse initResponseHeader(final HttpServletResponse response, final String contentType,
            final String... headers)
    {
        // 分析headers参数
        String encoding = DEFAULT_ENCODING;
        boolean noCache = DEFAULT_NOCACHE;
        for (String header : headers)
        {
            String headerName = StringUtils.substringBefore(header, ":");
            String headerValue = StringUtils.substringAfter(header, ":");

            if (StringUtils.equalsIgnoreCase(headerName, HEADER_ENCODING))
            {
                encoding = headerValue;
            }
            else if (StringUtils.equalsIgnoreCase(headerName, HEADER_NOCACHE))
            {
                noCache = Boolean.parseBoolean(headerValue);
            }
            else
            {
                throw new IllegalArgumentException(headerName + "不是一个合法的header类型");
            }
        }

        // 设置headers参数
        String fullContentType = contentType + ";charset=" + encoding;
        response.setContentType(fullContentType);
        if (noCache)
        {
            ServletUtils.setDisableCacheHeader(response);
        }

        return response;
    }

}
