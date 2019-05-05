package com.hogdeer.extend.common.brave.springmvc;

import com.github.kristofa.brave.KeyValueAnnotation;
import com.github.kristofa.brave.ServerRequestAdapter;
import com.github.kristofa.brave.SpanId;
import com.github.kristofa.brave.TraceData;
import com.github.kristofa.brave.http.BraveHttpHeaders;
import com.github.kristofa.brave.http.HttpServerRequest;
import com.github.kristofa.brave.http.SpanNameProvider;
import com.github.kristofa.brave.servlet.ServletHttpServerRequest;
import com.google.common.collect.Lists;
import com.hogdeer.extend.common.brave.entity.BraveUserInfo;
import com.hogdeer.extend.common.brave.utils.BraveUtils;
import com.hogdeer.extend.common.consts.BraveConst;
import com.hogdeer.extend.common.utils.IPUtils;
import com.hogdeer.extend.common.utils.StringHandleUtils;
import org.apache.commons.lang3.StringUtils;
import zipkin.TraceKeys;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

import static com.github.kristofa.brave.IdConversion.convertToLong;

/**
 * 
 * <p>File：CusHttpServerRequestAdapter.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2017年10月8日 下午2:16:38</p>
 * <p>Company: cargogm.com</p>
 * @author 施建波
 * @version 1.0
 */
@SuppressWarnings("deprecation")
public class CusHttpServerRequestAdapter  implements ServerRequestAdapter {
    private final HttpServletRequest request;
    private final SpanNameProvider spanNameProvider;
    private final String data;
    private final BraveUserInfo braveUserInfo;
    
    private final HttpServerRequest serviceRequest;
    private final String url;
    
    private static String localIp;
    private static Integer localPort;

    public CusHttpServerRequestAdapter(HttpServletRequest request,String data, BraveUserInfo braveUserInfo, SpanNameProvider spanNameProvider) {
        this.request = request;
        this.spanNameProvider = spanNameProvider;
        this.data = data;
        this.braveUserInfo = braveUserInfo;
        this.serviceRequest = new ServletHttpServerRequest(request);
        this.url = request.getRequestURI();
    }

    public TraceData getTraceData() {
        String sampled = serviceRequest.getHttpHeaderValue(BraveHttpHeaders.Sampled.getName());
        String parentSpanId = serviceRequest.getHttpHeaderValue(BraveHttpHeaders.ParentSpanId.getName());
        String traceId = serviceRequest.getHttpHeaderValue(BraveHttpHeaders.TraceId.getName());
        String spanId = serviceRequest.getHttpHeaderValue(BraveHttpHeaders.SpanId.getName());

        // Official sampled value is 1, though some old instrumentation send true
        Boolean parsedSampled = sampled != null
            ? sampled.equals("1") || sampled.equalsIgnoreCase("true")
            : null;
            
        TraceData traceData = null;
        if (traceId != null && spanId != null) {
        	traceData = TraceData.create(getSpanId(traceId, spanId, parentSpanId, parsedSampled));
        } else if (parsedSampled == null) {
        	traceData = TraceData.EMPTY;
        } else if (parsedSampled.booleanValue()) {
            // Invalid: The caller requests the trace to be sampled, but didn't pass IDs
        	traceData = TraceData.EMPTY;
        } else {
        	traceData = TraceData.NOT_SAMPLED;
        }
        return traceData;
    }

    public String getSpanName() {
    	return StringHandleUtils.connectMulString("", this.url, "(", spanNameProvider.spanName(serviceRequest), ")");
    }

    public Collection<KeyValueAnnotation> requestAnnotations() {
    	if(StringUtils.isBlank(localIp)){
    		localIp = IPUtils.getLocalIp();
    	}
    	if(null == localPort){
    		localPort = this.request.getLocalPort();
    	}
    	KeyValueAnnotation uriAnnotation = KeyValueAnnotation.create(TraceKeys.HTTP_URL, serviceRequest.getUri().toString());
    	KeyValueAnnotation addrAnnotation = KeyValueAnnotation.create("address", StringHandleUtils.connectMulString(":", localIp, localPort)); 
        KeyValueAnnotation dataAnnotation = KeyValueAnnotation.create("data", data);
        KeyValueAnnotation projectAnnotation = KeyValueAnnotation.create(BraveConst.BRAVE_PROJECT_TYPE, "web");
        List<KeyValueAnnotation> kas = Lists.newArrayList();
        kas.add(uriAnnotation);
        kas.add(addrAnnotation);
        kas.add(dataAnnotation);
        kas.add(projectAnnotation);

        if(null != braveUserInfo){
        	BraveUtils.setProjectTypeAnnotation(kas, BraveUtils.PROJECT_TYPE_NAME);
        	if(StringUtils.isNotBlank(braveUserInfo.getUserId())){
        		KeyValueAnnotation userIdAnnotation = KeyValueAnnotation.create("userInfo.userId", braveUserInfo.getUserId());
        		kas.add(userIdAnnotation);
        	}
        	if(StringUtils.isNotBlank(braveUserInfo.getUserName())){
        		KeyValueAnnotation userNameAnnotation = KeyValueAnnotation.create("userInfo.userName", braveUserInfo.getUserName());
        		kas.add(userNameAnnotation);
        	}
        	if(StringUtils.isNotBlank(braveUserInfo.getOrgId())){
        		KeyValueAnnotation orgIdAnnotation = KeyValueAnnotation.create("userInfo.orgId", braveUserInfo.getOrgId());
        		kas.add(orgIdAnnotation);
        	}
        	if(StringUtils.isNotBlank(braveUserInfo.getOrgName())){
        		KeyValueAnnotation orgNameAnnotation = KeyValueAnnotation.create("userInfo.orgName", braveUserInfo.getOrgName());
        		kas.add(orgNameAnnotation);
        	}
        	String ip = IPUtils.getOriginalIpAddr(this.request);
        	if(StringUtils.isNotBlank(ip)){
        		KeyValueAnnotation ipAnnotation = KeyValueAnnotation.create("userInfo.ip", ip);
        		kas.add(ipAnnotation); 
        	}
        }
        
        return kas;
        //return Collections..singleton(sera);
    }

    static SpanId getSpanId(String traceId, String spanId, String parentSpanId, Boolean sampled) {
        return SpanId.builder()
            .traceIdHigh(traceId.length() == 32 ? convertToLong(traceId, 0) : 0)
            .traceId(convertToLong(traceId))
            .spanId(convertToLong(spanId))
            .sampled(sampled)
            .parentId(parentSpanId == null ? null : convertToLong(parentSpanId)).build();
   }
}

