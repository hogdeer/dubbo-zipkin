
package com.hogdeer.extend.common.filter;

import com.hogdeer.extend.common.consts.ApplicationConst;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


public class RequestReplacedFilter implements Filter{

	private static final Logger logger = LoggerFactory.getLogger(RequestReplacedFilter.class);
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest)request;
        String contentType = servletRequest.getContentType();
        if(StringUtils.isNotBlank(contentType) && contentType.startsWith(ApplicationConst.APPLICATION_JSON)){
            WrappedHttpServletRequest requestWrapper = new WrappedHttpServletRequest(servletRequest);
            String param = requestWrapper.getRequestParams();
            if(StringUtils.isNotBlank(param)){
            	requestWrapper.setAttribute(ApplicationConst.REQUEST_BODY_PARAM_JSON, param);
            }
            chain.doFilter(requestWrapper, response);
        }else{
            chain.doFilter(request, response);
        }
        
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
	}

}
