/*
 * @(#)ValidateUtils.java 2017年10月8日 下午2:29:51
 * Copyright 2017 施建波, Inc. All rights reserved. cargogm.com
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.hogdeer.extend.common.utils;

import com.hogdeer.extend.common.bean.PropertiesLoader;
import com.hogdeer.extend.common.consts.ApplicationConst;
import com.hogdeer.extend.common.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.GenericTypeValidator;
import org.apache.commons.validator.routines.CalendarValidator;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>File：ValidateUtils.java</p>
 * <p>Title: 服务器端验证通用处理类</p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2017年10月8日 下午2:29:51</p>
 * <p>Company: cargogm.com</p>
 * @author 施建波
 * @version 1.0
 */
public class ValidateUtils {

private static final PropertiesLoader propertiesLoader = new PropertiesLoader("regular.properties"); 
    
    // 密码验证正则表达式
    public static final String REGULAR_USERNAME   = propertiesLoader.getProperty("regular.username");
    
    // 密码验证正则表达式
    public static final String REGULAR_PASSWORD   = propertiesLoader.getProperty("regular.password");

    // url地址验证正则表达式
    public static final String REGULAR_URL        = propertiesLoader.getProperty("regular.url");

    // IP地址验证正则表达式
    public static final String REGULAR_IP_ADDRESS = propertiesLoader.getProperty("regular.ip");

    // 身份证验证正则表达式
    public static final String REGULAR_ID_CARD    = propertiesLoader.getProperty("regular.idcard");

    // 邮政编码验证正则表达式
    public static final String REGULAR_ZIP_CODE   = propertiesLoader.getProperty("regular.zipcode");

    // 电话验证正则表达式
    public static final String REGULAR_PHONE      = propertiesLoader.getProperty("regular.phone");

    // 邮箱验证正则表达式
    public static final String REGULAR_EMIAL      = propertiesLoader.getProperty("regular.email");

    // 手机验证正则表达式
    public static final String REGULAR_MOBILE     = propertiesLoader.getProperty("regular.mobile");
    
    // 金额验证正则表达式
    public static final String REGULAR_MONEY = propertiesLoader.getProperty("regular.money");

    // email 地址最大长度
    public static final Integer    MAX_EMAIL_LENGTH   = propertiesLoader.getInteger("regular.email.maxLen");

    // 手机号码长度
    public static final Integer    MAX_MOBILE_LENGTH      = propertiesLoader.getInteger("regular.mobile.maxLen");

    /*// 密码最小长度
    public static final Integer    MIN_PWD_LENGTH     = propertiesLoader.getInteger("regular.password.minLen");

    // 密码最大长度
    public static final Integer   MAX_PWD_LENGTH     = propertiesLoader.getInteger("regular.password.maxLen");*/
    
    //模版替换的正则表达式 {abc}
    public static final String REGULAR_TEMPLATE = propertiesLoader.getProperty("regular.template");
    
    // 金额最小额度
    public static final BigDecimal MIN_MONEY_LENGTH = new BigDecimal(propertiesLoader.getInteger("regular.money.minLen"));

    // 金额最大额度
    public static final BigDecimal MAX_MONEY_LENGTH = new BigDecimal(propertiesLoader.getInteger("regular.money.maxLen"));

    /**
     * 私有构造器，防止类的实例化
     */
    private ValidateUtils()
    {
        super();
    }

    /**
     * 判断一个字符串是否为Null或""或"  "
     * @param checkString 要检查的字符串
     * @return boolean 是否为null或""或"  "
     */
    public static boolean isNull(String checkString)
    {
        return StringUtils.isBlank(checkString);
    }
    
    public static boolean isObjectNull(Object checkObj)
    {
    	if(null == checkObj){
    		return Boolean.TRUE;
    	}
        return StringUtils.isBlank(checkObj.toString());
    }

    public static void main(String[] args){
    	Object obj = null;
    	
        String checkString = String.valueOf(obj);
        Boolean bool = ValidateUtils.isObjectNull(obj);
        System.out.println(bool);
    }

    /**
     * 检查一个字符串的内容长度（一个中文等于一个字符）
     * @param checkString 要检查的字符串
     * @return int 字符串内容长度
     */
    public static int length(String checkString)
    {
        int length = 0;
        if (!isNull(checkString))
            length = checkString.length();
        return length;
    }

    /**
     * 获取一个字符串的长度（一个中文等于两个字符）
     * @param checkString 指定的字符串
     * @return int 字符串长度
     */
    public static int size(String checkString)
    {
        int valueLength = 0;
        if (StringUtils.isNotBlank(checkString))
        {
            String chinese = "[\u0391-\uFFE5]";
            /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
            for (int i = 0; i < checkString.length(); i++)
            {
                /* 获取一个字符 */
                String temp = checkString.substring(i, i + 1);
                /* 判断是否为中文字符 */
                if (temp.matches(chinese))
                {
                    /* 中文字符长度为2 */
                    valueLength += 2;
                }
                else
                {
                    /* 其他字符长度为1 */
                    valueLength += 1;
                }
            }
        }
        return valueLength;
    }

    /**
     * 检查一个字符串的内容是否包含中文字
     * @param checkString 要检查的字符串
     * @return boolean 是否包含中文字
     */
    public static boolean isChinese(String checkString)
    {
        if (isNull(checkString)) return false;
        for (int i = 0; i < checkString.length(); i++)
        {
            if ((int) checkString.charAt(i) > 256) return true;
        }
        return false;
    }

    /**
     * 检查一个字符串的内容是否包含系统不允许的特殊字符<主要是防止javascript处理出错>
     * @param checkString 要检查的字符串
     * @return boolean 是否包含系统不允许的特殊字符
     */
    public static boolean hasBadChar(String checkString)
    {
        String szExp = "'\\/?\"<>|";
        if (isNull(checkString) || isNull(szExp))
        {
            return false;
        }
        for (int counter = 0; counter < szExp.length(); counter++)
        {
            char curr_char = szExp.charAt(counter);
            if (checkString.indexOf(curr_char) >= 0)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查一个字符串是否为指定的帐号格式
     * @param checkString 要检查的字符串
     * @param isRequired 是否必须输入
     * @return boolean 是否为指定的帐号格式
     */
    public static boolean isAccountFormat(String checkString, boolean isRequired)
    {
        boolean bool = true;
        boolean boolNull = isNull(checkString);
        if (isRequired) bool = !boolNull;
        if (!boolNull)
        {
            //String regex = "^([0-9A-Za-z_.@-]{" + min + "," + max + "})?$";// 该表达式允许字符串为空
            bool = matchRegexp(checkString, REGULAR_USERNAME); 
        }
        return bool;
    }
    
    /**
     * 检查一个字符串是否为指定的密码格式
     * @param checkString   要检查的字符串
     * @param isRequired    是否必须输入
     * @return
     * @author 施建波  2015年9月15日 上午11:32:52
     */
    public static boolean isPassWordFormat(String checkString, boolean isRequired){
        boolean bool = true;
        boolean boolNull = isNull(checkString);
        if (isRequired) bool = !boolNull;
        if (!boolNull)
        {
            bool = matchRegexp(checkString, REGULAR_PASSWORD); 
        }
        return bool;
    }

    /**
     * 检查一个字符串是否为6-20位字符长度的帐号格式
     * @param checkString 要检查的字符串
     * @param isRequired 是否必须
     * @return boolean 是否为6-20位字符长度的帐号格式
     */
    /*public static boolean isAccountFormat(String checkString, boolean isRequired)
    {
        return isAccountFormat(checkString, isRequired, MIN_PWD_LENGTH,
                MAX_PWD_LENGTH);
    }*/

    /**
     * 检查一个字符串是否为正确的邮件格式(支持一个邮件地址或多个以;号隔开的邮件地址验证)
     * @param checkString 要查检的字符串
     * @param isRequired 是否必须
     * @param maxLength 最大长度
     * @return boolean 是否为正确的邮件格式
     */
//    public static boolean isMailFormat(String checkString, boolean isRequired,
//            int maxLength)
//    {
//        return validate(checkString, isRequired, maxLength, REGULAR_EMIAL);
//    }

    /**
     * 判断一个字符串是否匹配一个正则表达式
     * @param value 要判断的字符串
     * @param regexp 要匹配的正则表达式
     * @return boolean 是否匹配
     */
    public static boolean matchRegexp(String value, String regexp)
    {
        if (StringUtils.isBlank(value) || StringUtils.isBlank(regexp))
        {
            return false;
        }
        Pattern p = Pattern.compile("/" + regexp + "/"); 
        Matcher m = p.matcher(value); 
        return m.matches();
    }

    /**
     * 检查一个字符串是否为正确的电话格式(支持一个电话号或多个以;号隔开的电话号验证)
     * @param checkString 要检查的字符串
     * @param isRequired 是否必须
     * @param maxLength 最大长度
     * @return boolean 是否为正确的电话格式
     */
//    public static boolean isTelFormat(String checkString, boolean isRequired,
//            int maxLength)
//    {
//        return validate(checkString, isRequired, maxLength, REGULAR_PHONE);
//    }

    /**
     * 判断一个字符串是否正确的日期格式
     * @param value 要检查的字符串
     * @param locale java.util.Locale
     * @return boolean 是否正确的日期格式
     */
    public static boolean isDate(String value, Locale locale)
    {
        return CalendarValidator.getInstance().isValid(value, locale);
    }

    /**
     * 判断一个字符串是否为正确的日期及格式
     * @param value 要检查的字符串
     * @param datePattern 日期格式
     * @return boolean 是否为正确的日期及格式
     */
    public static boolean isDate(String value, String datePattern)
    {
        return CalendarValidator.getInstance().isValid(value, datePattern);
    }

    public static boolean isTimeFormat(String checkString, String timeFormat,
            boolean isRequired)
    {
        return false;
    }

    /**
     * 检查一个字符串是否为正确有效的日期格式字符串
     * @param checkString 要检查的字符串
     * @param dateFormat 指定的日期格式，如：yyyy-MM-dd 或 yyyy-MM
     * @param isRequired 是否必须
     * @return boolean 是否为正确有效的日期格式字符串
     */
    public static boolean isDateFormat(String checkString, String dateFormat,
            boolean isRequired)
    {
        boolean boolNull = isNull(checkString);
        if (isRequired)
        {
            if (boolNull) return false;
        }
        if (!boolNull)
        {
            if (!isDate(checkString, dateFormat))
            {
                return false;
            }
            if (dateFormat.equalsIgnoreCase(ApplicationConst.DATE_FORMAT_YM))
            {
                return matchRegexp(checkString, "^([0-9]{4}-[0-9]{1,2})?$");
            }
            if (dateFormat.equalsIgnoreCase(ApplicationConst.DATE_FORMAT_YMD))
            {
                return matchRegexp(checkString,
                        "^([0-9]{4}-[0-9]{1,2}-[0-9]{1,2})?$");
            }
        }
        return true;
    }

    /**
     * 判断一个字符串是否为Byte类型的数据
     * @param value 要检查的字符串
     * @return boolean 是否为Byte类型的数据
     */
    public static boolean isByte(String value)
    {
        return (GenericTypeValidator.formatByte(value) != null);
    }

    /**
     * 判断一个字符串是否为Short类型的数据
     * @param value 要检查的字符串
     * @return boolean 是否为Short类型的数据
     */
    public static boolean isShort(String value)
    {
        return (GenericTypeValidator.formatShort(value) != null);
    }

    /**
     * 判断一个字符串是否为Int类型的数据
     * @param value 要检查的字符串
     * @return boolean 是否为Int类型的数据
     */
    public static boolean isInt(String value)
    {
        return (GenericTypeValidator.formatInt(value) != null);
    }

    /**
     * 判断一个字符串是否为Long类型的数据
     * @param value 要检查的字符串
     * @return boolean 是否为Long类型的数据
     */
    public static boolean isLong(String value)
    {
        return (GenericTypeValidator.formatLong(value) != null);
    }

    /**
     * 判断一个字符串是否为Float类型的数据
     * @param value 要检查的字符串
     * @return boolean 是否为Float类型的数据
     */
    public static boolean isFloat(String value)
    {
        return (GenericTypeValidator.formatFloat(value) != null);
    }

    /**
     * 判断一个字符串是否为Double类型的数据
     * @param value 要检查的字符串
     * @return boolean 是否为Double类型的数据
     */
    public static boolean isDouble(String value)
    {
        return (GenericTypeValidator.formatDouble(value) != null);
    }

    /**
     * 验证整数范围
     * @param value 要验证的整数
     * @param min 最小值
     * @param max 最大值
     * @return boolean 是否在指定的整数范围之内
     *//*
    public static boolean isInRange(int value, int min, int max)
    {
        return ((value >= min) && (value <= max));
    }

    *//**
     * 验证整数范围
     * @param checkInt  要验证的整数
     * @param min       最小值
     * @param max       最大值
     * @param isRequiredd 是否必须
     * @return boolean 是否在指定的整数范围之内
     *//*
    public static boolean isIntRange(int checkInt, int min, int max,
            boolean isRequiredd)
    {
        if (!isRequiredd)
        {
            if (checkInt == 0) return true;
        }
        return isInRange(checkInt, min, max);
    }*/
    
    /**
     * 验证整数范围
     * @param value 要验证的整数
     * @param min   最小值
     * @param max   最大值
     * @return
     * @author 施建波  2015年9月16日 下午7:31:37
     */
    public static Boolean isIntRange(Integer checkInt, Integer min, Integer max){
        boolean bool = false;
        bool = (null != checkInt);
        if(bool && null != min)
        {
            bool = (checkInt.intValue() >= min.intValue());
        }
        if(bool && null != max)
        {
            bool = (checkInt.intValue() <= max.intValue());
        }
        return bool;
    }
    
    /**
     * 验证短整数范围
     * @param value 要验证的整数
     * @param min   最小值
     * @param max   最大值
     * @return
     * @author 施建波  2015年9月16日 下午7:31:37
     */
    public static Boolean isShortRange(Short checkInt, Short min, Short max){
        boolean bool = false;
        bool = (null != checkInt);
        if(bool && null != min)
        {
            bool = (checkInt.shortValue() >= min.shortValue());
        }
        if(bool && null != max)
        {
            bool = (checkInt.shortValue() <= max.shortValue());
        }
        return bool;
    }

    /**
     * 验证邮政编码 6位(支持验证多个的功能)
     * @param checkString 要检查的字符串
     * @param isRequired 是否必填
     * @param maxLength 最大长度
     * @return boolean 是否正确的邮编格式
     */
//    public static boolean isZipCode(String checkString, boolean isRequired,
//            int maxLength)
//    {
//        return validate(checkString, isRequired, maxLength, REGULAR_ZIP_CODE);
//    }

    /**
     * 验证身份证号码 15位或18位(支持验证多个的功能)
     * @param checkString 要检查的字符串
     * @param isRequired 是否必填
     * @param maxLength 最大长度
     * @return boolean 是否正确的身份证号码格式
     */
//    public static boolean isIDCard(String checkString, boolean isRequired,
//            int maxLength)
//    {
//        return validate(checkString, isRequired, maxLength, REGULAR_ID_CARD);
//    }

    /**
     * 手机号码格式验证(支持验证多个的功能)
     * 
     * @param checkString 要检查的字符串
     * @param isRequired 是否必填
     * @param maxLength 最大长度
     * @return boolean 是否正确的手机号码格式
     */
//    public static boolean isMobileFormat(String checkString,
//            boolean isRequired, int maxLength)
//    {
//        return validate(checkString, isRequired, maxLength, REGULAR_MOBILE);
//    }

    /**
     * 检查一个字符串是否为正确的IP地址格式(支持验证多个的功能)
     * @param checkString 要检查的字符串
     * @param isRequired 是否必填
     * @param maxLength 最大长度
     * @return boolean 是否正确的IP地址格式
     */
//    public static boolean isIpAddress(String checkString, boolean isRequired,
//            int maxLength)
//    {
//        return validate(checkString, isRequired, maxLength, REGULAR_IP_ADDRESS);
//    }

    /**
     * 检查一个字符串是否为正确的URL地址格式(支持验证多个的功能)
     * @param checkString 要检查的字符串
     * @param isRequired 是否必填
     * @param maxLength 最大长度
     * @return boolean 是否正确的URL地址格式
     */
//    public static boolean isUrl(String checkString, boolean isRequired,
//            int maxLength)
//    {
//        return validate(checkString, isRequired, maxLength, REGULAR_URL);
//    }

    /**
     * 抽象验证，正则匹配
     * @param checkString 要检查的字符串
     * @param isRequired 是否必填
     * @param maxLength 最大长度
     * @param regex 正则表达式
     * @param splitChar 支持多个字符串验证，该参数表示字符串之间的分隔符
     * @return boolean 是否通过验证
     */
//    public static boolean validate(String checkString, boolean isRequired,
//            int maxLength, String regex, String splitChar)
//    {
//        checkString = StringUtils.trimToEmpty(LanguageUtils
//                .quanToBan(checkString));
//        boolean bool = isRange(checkString, isRequired, maxLength);
//        boolean boolNull = isNull(checkString);
//        if (checkString.indexOf(splitChar) == -1)
//        {
//            if (bool && !boolNull) bool = matchRegexp(checkString, regex);
//            return bool;
//        }
//        else
//        {
//            if (bool && !boolNull)
//            {
//                String[] strs = checkString.split(splitChar);
//                for (int i = 0; i < strs.length; i++)
//                {
//                    String str = strs[i];
//                    if (!matchRegexp(str, regex))
//                    {
//                        bool = false;
//                        break;
//                    }
//                }
//            }
//            return bool;
//        }
//    }

    /**
     * 抽象验证，正则匹配
     * @param checkString 要检查的字符串
     * @param isRequired 是否必填
     * @param maxLength 最大长度
     * @param regex 正则表达式
     * @return boolean 是否通过验证
     */
//    public static boolean validate(String checkString, boolean isRequired,
//            int maxLength, String regex)
//    {
//        checkString = StringUtils.trimToEmpty(LanguageUtils
//                .quanToBan(checkString));
//        boolean bool = isRange(checkString, isRequired, maxLength);
//        boolean boolNull = isNull(checkString);
//        if (checkString.indexOf(";") == -1)
//        {
//            if (bool && !boolNull) bool = matchRegexp(checkString, regex);
//            return bool;
//        }
//        else
//        {
//            if (bool && !boolNull)
//            {
//                String[] strs = checkString.split(";");
//                for (int i = 0; i < strs.length; i++)
//                {
//                    String str = strs[i];
//                    if (!matchRegexp(str, regex))
//                    {
//                        bool = false;
//                        break;
//                    }
//                }
//            }
//            return bool;
//        }
//    }

    /**
     * 普通字符串验证，检查字符串内容长度是否在指定范围之内
     * @param checkString 要检查的字符串
     * @param min 最小长度
     * @param max 最大长度
     * @param isRequired 是否必须
     * @return boolean 是否通过验证
     */
    public static boolean isRange(String checkString, int min, int max,
            boolean isRequired)
    {
        boolean bool = true;
        boolean boolNull = isNull(checkString);
        if (isRequired) bool = !boolNull;
        if (!boolNull)
        {
            int iLen = length(checkString);
            bool = (iLen >= min) && (iLen <= max);
        }
        return bool;
    }
   /**
    * 用于密码校验
    * @param checkString
    * @param min
    * @param max
    * @param isRequired
    * @return
    */
    public static boolean isRegex(String checkString, int min, int max, boolean isRequired)
    {
    	 boolean bool = true;
         boolean boolNull = isNull(checkString);
         if (isRequired) bool = !boolNull;
         if (!boolNull)
         {
             int iLen = length(checkString);
             bool = (iLen >= min) && (iLen <= max) && matchRegexp(checkString,REGULAR_PASSWORD);
         }
         return bool;
    }
    /**
     * 普通字符串验证，检查字符串内容长度是否在指定范围之内
     * @param checkString 要检查的字符串
     * @param max 最大长度
     * @param isRequired 是否必须
     * @return boolean 是否通过验证
     */
    public static boolean isRange(String checkString, boolean isRequired,
            int max)
    {
        boolean bool = true;
        boolean boolNull = isNull(checkString);
        if (isRequired) bool = !boolNull;
        if (!boolNull) bool = length(checkString) <= max;
        return bool;
    }
    
    /**
     * 普通字符串验证，检查字符串内容长度是否在指定范围之内(中文两个字节计算)
     * @param checkString   要检查的字符串
     * @param isRequired    是否必须
     * @param max           最大长度
     * @return
     */
    public static boolean isChinaRange(String checkString, boolean isRequired,
            int max)
    {
        boolean bool = true;
        boolean boolNull = isNull(checkString);
        if (isRequired) bool = !boolNull;
        if (!boolNull) bool = size(checkString) <= max;
        return bool;
    }
    
    /**
     * 验证是否为金额，如果是是否在指定的范围内
     * @param price     金额
     * @param min       最小值　
     * @param max       最大值
     * @return
     */
    public static Boolean isMoney(BigDecimal price, BigDecimal min, BigDecimal max)
    {
        boolean bool = false;
        bool = (null != price);
        if(bool)
        {
            bool = matchRegexp(price.toString(), REGULAR_MONEY);
        }
        if(bool && null != min)
        {
            bool = (price.compareTo(min) > 0);
        }
        if(bool && null != max)
        {
            bool = (price.compareTo(max) <= 0);
        }
        return bool;
    }
    
    /**
     *  验证是否为金额，如果是是否在默认的范围内
     * @param price 金额
     * @return
     * @author 施建波  2015年9月16日 下午5:54:17
     */
    public static Boolean isMoney(BigDecimal price)
    {
        return isMoney(price, MIN_MONEY_LENGTH, MAX_MONEY_LENGTH);
    }
    
    /**
     * 验证是否为大于最大数的整数
     * @param value	要验证的数
     * @param max	最大数
     * @return
     * @author 施建波  2015年9月26日 上午10:05:04
     */
    public static Boolean isInt(Integer value, int max){
    	boolean bool = false;
        bool = (null != value);
        if(bool){
        	bool = value.intValue() > max;
        }
        return bool;
    }
    
    public static Boolean isInt(Integer value){
    	return isInt(value, 0);
    }
    
    /**
     * 验证是否为大于最大数的整数
     * @param value	要验证的数
     * @param max	最大数
     * @return
     * @author 施建波  2015年10月17日 上午10:07:54
     */
    public static Boolean isLong(Long value, int max){
    	boolean bool = false;
        bool = (null != value);
        if(bool){
        	bool = value.intValue() > max;
        }
        return bool;
    }
    
    /**
     * 检测字符串是否为空
     * @param value
     * @param errorMessage
     * @author zhangcm 2016-09-27 14:13:15
     */
    public static void checkNotEmpty(String value, String errorMessage){
        checkNotEmpty(value, null, errorMessage);
    }
    
    /**
     * 检测字符串是否为空
     * @param value
     * @param maxLength
     * @param errorMessage
     * @author zhangcm 2016-09-27 14:13:15
     */
    public static void checkNotEmpty(String value, Integer maxLength, String errorMessage){
        if(StringUtils.isBlank(value)){
            throw new BusinessException(errorMessage);
        }
        if(null != maxLength && value.length() > maxLength.intValue()){
            throw new BusinessException(errorMessage);
        }
    }
    
    /**
     * 检测字符串长度，字符串可以为空
     * @param value
     * @param maxLength
     * @param errorMessage
     * @author zhangcm 2016-09-27 14:23:47
     */
    public static void checkLength(String value, Integer maxLength, String errorMessage){
        if(StringUtils.isBlank(value)){
            return;
        }
        if(null != maxLength && value.length() > maxLength.intValue()){
            throw new BusinessException(errorMessage);
        }
    }
    
    /**
     * 检测数字是否符合规范
     * @param checkInt
     * @param min
     * @param max
     * @param errorMessage
     * @author zhangcm 2016-09-27 14:13:37
     */
    public static void checkIntRange(Integer checkInt, Integer min, Integer max, String errorMessage){
        if(!isIntRange(checkInt, min, max)){
            throw new BusinessException(errorMessage);
        }
    }
    
    /**
     * 检测对象是否为空，为空抛错
     * @param value
     * @param errorMessage
     * @author zhangcm 2016-09-29 09:38:36
     */
    public static void checkNotNull(Object value, String errorMessage){
        if(null == value){
            throw new BusinessException(errorMessage);
        }
    }
}
