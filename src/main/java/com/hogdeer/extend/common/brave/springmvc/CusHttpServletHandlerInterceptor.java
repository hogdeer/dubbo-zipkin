package com.hogdeer.extend.common.brave.springmvc;

import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.toolkit.MapUtils;
import com.github.kristofa.brave.*;
import com.github.kristofa.brave.http.DefaultSpanNameProvider;
import com.github.kristofa.brave.http.HttpResponse;
import com.github.kristofa.brave.http.HttpServerResponseAdapter;
import com.github.kristofa.brave.http.SpanNameProvider;
import com.google.common.collect.Maps;
import com.hogdeer.extend.common.bean.DiamondConfig;
import com.hogdeer.extend.common.bean.DubboPublishBean;
import com.hogdeer.extend.common.brave.Interface.UserInfoInterface;
import com.hogdeer.extend.common.brave.entity.BraveUserInfo;
import com.hogdeer.extend.common.consts.ApplicationConst;
import com.hogdeer.extend.common.dubbo.PublishDesider;
import com.hogdeer.extend.common.utils.ControllerUtils;
import com.hogdeer.extend.common.utils.IPUtils;
import com.hogdeer.extend.common.utils.ValidateUtils;
//import com.olymtech.logservice.dsapi.util.LogUtils;
//import com.olymtech.logservice.dsapi.vo.BusinessLogVo;
import com.hogdeer.extend.common.utils.SpringContextUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@SuppressWarnings("deprecation")
public class CusHttpServletHandlerInterceptor  extends HandlerInterceptorAdapter {

	private final static Logger logger = LoggerFactory.getLogger(CusHttpServletHandlerInterceptor.class);

    static final String HTTP_SERVER_SPAN_ATTRIBUTE = CusHttpServletHandlerInterceptor.class.getName() + ".server-span";

    private final Brave brave;
    private final ServerRequestInterceptor requestInterceptor;
    private final ServerResponseInterceptor responseInterceptor;
    private final ServerSpanThreadBinder serverThreadBinder;
    private final SpanNameProvider spanNameProvider;
    private UserInfoInterface userInfoInterface;

	CusHttpServletHandlerInterceptor(Brave brave, UserInfoInterface userInfoInterface) {
    	this.brave = brave;
        this.requestInterceptor = brave.serverRequestInterceptor();
        this.responseInterceptor = brave.serverResponseInterceptor();
        this.serverThreadBinder = brave.serverSpanThreadBinder();
        this.spanNameProvider = new DefaultSpanNameProvider();
        this.userInfoInterface = userInfoInterface;
    }
    
	@Override
    public boolean preHandle(final HttpServletRequest request, HttpServletResponse response, final Object handler) throws Exception {
		try{
    		DubboPublishBean dubboPublishBean = new DubboPublishBean();
	    	dubboPublishBean.setIp(IPUtils.getOriginalIpAddr(request));
	    	
	    	String data = this.getData(request);
	    	BraveUserInfo braveUserInfo = null;
	    	if(null != this.userInfoInterface){
	    		braveUserInfo = this.userInfoInterface.getUserInfo();
	    		if(null != braveUserInfo){
	    			dubboPublishBean.setUsername(braveUserInfo.getUserName());
	    	    	dubboPublishBean.setOrgId(braveUserInfo.getOrgId());
	    	    	dubboPublishBean.setCookieValue(ControllerUtils.getCookieValue(request, PublishDesider.GRAY_COOKIE_RULE_KEY));
	    		}
	    	}
	    	RpcContext.getContext().setAttachment(PublishDesider.PUBLISH_FILTER_KEY, JSON.toJSONString(dubboPublishBean));

	    	CusHttpServerRequestAdapter csRequest = new CusHttpServerRequestAdapter(request,data, braveUserInfo, spanNameProvider);
	    	requestInterceptor.handle(csRequest);
	    	Long traceId = brave.serverSpanThreadBinder().getCurrentServerSpan().getSpan().getTrace_id();
	    	response.setHeader("traceId", IdConversion.convertToString(traceId));
	    	
	    	DiamondConfig collectWebConfig = null;
	    	if(SpringContextUtils.containsBean("collectWebConfig")){
	    		collectWebConfig = (DiamondConfig) SpringContextUtils.getBean("collectWebConfig");
	    	}
	    	Map<String, Object> urlMap = Maps.newHashMap();
	    	if(null != collectWebConfig){
	    		urlMap = collectWebConfig.getConfigMap();
	    	}
	    	if(MapUtils.isNotEmpty(urlMap)){
	    		String url = request.getRequestURI();
//	    		if(urlMap.containsKey(url)){
//	    			String[] valueAry = urlMap.get(url).toString().split(",");
//	    	    	BusinessLogVo businessLogVo = new BusinessLogVo();
//	    	    	if(null != braveUserInfo){
//	    	    		businessLogVo.setUserId(braveUserInfo.getUserId());
//		    	    	businessLogVo.setUserName(braveUserInfo.getUserName());
//		    	    	businessLogVo.setOrgId(braveUserInfo.getOrgId());
//	    	    	}
//	    	    	if(StringUtils.isNotBlank(data)){
//	    	    		businessLogVo.setJsonData(data);
//	    	    	}
//	    	    	businessLogVo.setTrackId(IdConversion.convertToString(traceId));
//
//	    	    	businessLogVo.setModule(valueAry[0]);
//	    	    	if(valueAry.length > 1){
//	    	    		businessLogVo.setMethod(valueAry[1]);
//	    	    	}
//	    	    	//logger.info("rest==============="+new Gson().toJson(businessLogVo));
//	    	    	//LogUtils.addLogToQueue(LogUtils.BUSINESSLOG_QUEUE, businessLogVo);
//	    		}
	    	}
    	}catch(Exception e){
    		logger.error(e.getMessage(), e); 
    	}
        return super.preHandle(request, response, handler);  
    }

	@Override
    public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final Exception ex) {

        /*final ServerSpan span = (ServerSpan) request.getAttribute(HTTP_SERVER_SPAN_ATTRIBUTE);

        if (span != null) {
            serverThreadBinder.setCurrentSpan(span);
        }*/
       try{
	       responseInterceptor.handle(new HttpServerResponseAdapter(new HttpResponse() { 
	           public int getHttpStatusCode() {
	               return response.getStatus();
	           }
	       }));
       }catch(Exception e){
    	   logger.error(e.getMessage(), e); 
       }
    }
    
    private String getData(final HttpServletRequest request){
    	String data = StringUtils.EMPTY;
    	JSONObject json = new JSONObject();
    	Object obj = request.getAttribute(ApplicationConst.REQUEST_BODY_PARAM_JSON);
    	if(!ValidateUtils.isObjectNull(obj)){
    		json.put("requestBody", obj);
    	}
    	data = JSON.toJSONString(request.getParameterMap());
    	if(StringUtils.isNotBlank(data)){
    		json.put("form", data);
    	}
    	data = request.getQueryString();
    	if(StringUtils.isNotBlank(data)){
    		json.put("query", data);
    	}
    	/*if(ValidateUtils.isObjectNull(obj)){
    		data = JSON.toJSONString(request.getParameterMap());
    		if(StringUtils.isBlank(data)){
    			data = request.getQueryString();
    		}
    	}else{
    		data = obj.toString();
    	}*/
    	return json.toJSONString();
    }
}
