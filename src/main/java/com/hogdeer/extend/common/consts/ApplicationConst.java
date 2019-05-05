package com.hogdeer.extend.common.consts;

import com.hogdeer.extend.common.utils.CalendarUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>File：ApplicationConst.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2017年10月8日 下午2:27:37</p>
 * <p>Company: cargogm.com</p>
 * @author 施建波
 * @version 1.0
 */
public class ApplicationConst {

	// 私有构造器，防止类的实例化
    private ApplicationConst(){}

    // 系统默认字符编码
    public static final String LANGUAGE_UTF8 = "UTF-8";
    
    //gbk编码
    public static final String LANGUAGE_GBK = "GBK";

    // 系统默认每页记录数
    public static final Integer DEFAULT_PAGE_SIZE = 20;

    // 系统默认每页最大记录数
    public static final Integer MAX_PAGE_SIZE = DEFAULT_PAGE_SIZE * 100;

    // 系统默认当前页
    public static final Integer DEFAULT_CURRENT_PAGE = 1;
    
    // 系统年月格式，如：2010-06
    public static final String   DATE_FORMAT_YM         = "yyyy-MM";

    // 系统年月日格式，如：2010-08-19
    public static final String   DATE_FORMAT_YMD        = "yyyy-MM-dd";

    // 系统年月日格式，如：2010-08-19 05:23:20
    public static final String   DATE_FORMAT_YMDHMS     = "yyyy-MM-dd HH:mm:ss";
    
    //成功CODE编码
    public static final Integer ERROR_CODE_SUCCESS = 100;
    //错误CODE编码
    public static final Integer ERROR_CODE_FAILURE = 101;
    //系统错误CODE编码
    public static final Integer ERROR_CODE_SYSTEM_FAILUE = 199;
    
    //一年的天数
    public static final Integer YEAR_DAY_NUMBER = 365;
    //一月的天数
    public static final Integer MONTH_DAY_NUMBER = 30;
    //一年的月数
    public static final Integer YEAR_MONTH_NUMBER = 12;
    //mybatis String NULL值替换
    public static final String MYBATIS_NULL_STRING_REPLACE = "@O(L#Y$M%T)E^CH@";
    //mybatis Date NULL值替换
    public static final Date MYBATIS_NULL_DATE_REPLACE = CalendarUtils.parseStringToDate("1900-01-01 14:24:34444", "yyyy-MM-dd HH:mm:ssSSS");
    //mybatis BigDecimal NULL值替换
    public static final BigDecimal MYBATIS_NULL_BIGDECIMAL_REPLACE = new BigDecimal("-0.0000000000000001");
    
    //Content-Type 
    public static final String APPLICATION_JSON = "application/json";
    //requestBody request key
    public static final String REQUEST_BODY_PARAM_JSON = "request_body_param_json";
    
    // select最大in数量
    public static final Integer SELECT_MAX_IN = 1000; 
}
