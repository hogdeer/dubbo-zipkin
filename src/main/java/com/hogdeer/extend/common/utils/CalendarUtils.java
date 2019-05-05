package com.hogdeer.extend.common.utils;

import com.hogdeer.extend.common.consts.ApplicationConst;
//import com.olymtech.shopkeeper.common.utils.NumericUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTime.Property;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * <p>File：CalendarUtils.java</p>
 * <p>Title: 日期工具类</p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2015 2015年3月20日 下午2:35:10</p>
 * <p>Company: kinorsoft</p>
 * @author 施建波
 * @version 1.0
 */
public class CalendarUtils
{
    private static final Logger logger = LoggerFactory.getLogger(CalendarUtils.class);
    
    // 私有构造器，防止类的实例化
    public CalendarUtils(){}

    
    /**
     * 根据系统时间获取当前年份
     * @return int 当前年份
     */
    public static int getCurrentYear()
    {
        DateTime dt = new DateTime();
        return dt.getYear();
    }

    /**
     * 获得给定日期的年份
     * @param date 给定日期
     * @return int 年份
     */
    public static int getYear(Date date)
    {
        if (null == date)
        {
            throw new NullPointerException("日期参数为null");
        }
        else
        {
            DateTime dt = new DateTime(date);
            return dt.getYear();
        }
    }

    /**
     * 根据系统时间获取当前月份
     * @return int 当前月份
     */
    public static int getCurrentMonth()
    {
        DateTime dt = new DateTime();
        return dt.getMonthOfYear();
    }

    /**
     * 根据指定日期对象获取当前月份
     * @param date Date对象
     * @return int 当前月份
     */
    public static int getMonth(Date date)
    {
        if (null == date)
        {
            throw new NullPointerException("日期参数为null");
        }
        else
        {
            DateTime dt = new DateTime(date);
            return dt.getMonthOfYear();
        }
    }

    /**
     * 根据系统时间获取当前日期
     * @return int 当前日期
     */
    public static int getCurrentDate()
    {
        DateTime dt = new DateTime();
        return dt.getDayOfMonth();
    }

    /**
     * 根据系统时间取得当前小时数
     * @return int 当前小时
     */
    public static int getCurrentHour()
    {
        DateTime dt = new DateTime();
        return dt.getHourOfDay();
    }

    /**
     * 根据系统时间取得当前分钟数
     * @return int 当前分钟数
     */
    public static int getCurrentMinute()
    {
        DateTime dt = new DateTime();
        return dt.getMinuteOfHour();
    }

    /**
     * 根据系统时间取得当前秒数
     * @return int 当前秒数
     */
    public static int getCurrentSecond()
    {
        DateTime dt = new DateTime();
        return dt.getSecondOfMinute();
    }
    
    /**
     * 获取当天的总秒数
     * @param date
     * @return
     * @author 施建波  2015年10月9日 下午3:11:27
     */
    public static long getCurrentDaySecond(Date date){
    	return (CalendarUtils.getCurrentLong() - date.getTime()) / 1000;
    }
    
    public static long getCurrentDaySecond(){
    	return getCurrentDaySecond(CalendarUtils.getShortDate(new Date()));
    }

    /**
     * 根据给定日期对象取得日期
     * @param date Date对象
     * @return int date对象的日期
     */
    public static int getDate(Object date)
    {
        if (null == date)
        {
            throw new NullPointerException("日期参数为Null");
        }
        else
        {
            DateTime dt = new DateTime(date);
            return dt.getDayOfMonth();
        }
    }

    /**
     * 根据年、月、日、时、分来构造Date对象
     * @param year 年
     * @param monthOfYear 月
     * @param dayOfMonth 日
     * @param hourOfDay 时
     * @param minuteOfHour 分
     * @return Date Date
     */
    public static Date getDate(int year, int monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour)
    {
        DateTime dt = new DateTime(year, monthOfYear, dayOfMonth, hourOfDay,
                minuteOfHour);
        return dt.toDate();
    }

    /**
     * 根据年、月、日、时、分来构造指定日期格式的日期字符串
     * @param year 年,
     * @param monthOfYear 月
     * @param dayOfMonth 日
     * @param hourOfDay 时
     * @param minuteOfHour 分
     * @param format 日期格式
     * @return String 指定日期格式的日期字符串
     */
    public static String getDate(int year, int monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour, String format)
    {
        DateTime dt = new DateTime(year, monthOfYear, dayOfMonth, hourOfDay,
                minuteOfHour);
        return dt.toString(format);
    }

    /**
     * 根据年、月、日、时、分、秒来构造Date对象
     * @param year 年
     * @param monthOfYear 月
     * @param dayOfMonth 日
     * @param hourOfDay 时
     * @param minuteOfHour 分
     * @param secondOfMinute 秒
     * @return Date Date
     */
    public static Date getDate(int year, int monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour, int secondOfMinute)
    {
        DateTime dt = new DateTime(year, monthOfYear, dayOfMonth, hourOfDay,
                minuteOfHour, secondOfMinute);
        return dt.toDate();
    }

    /**
     * 根据年、月、日、时、分、秒返回指定格式的日期字符串
     * @param year 年
     * @param monthOfYear 月
     * @param dayOfMonth 日
     * @param hourOfDay 时
     * @param minuteOfHour 分
     * @param secondOfMinute 秒
     * @param format 格式
     * @return String 指定格式的日期字符串
     */
    public static String getDate(int year, int monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour, int secondOfMinute, String format)
    {
        DateTime dt = new DateTime(year, monthOfYear, dayOfMonth, hourOfDay,
                minuteOfHour, secondOfMinute);
        return dt.toString(format);
    }

    /**
     * 将日期对象以指定的日期格式字符串返回
     * @param date Date
     * @param format 日期格式
     * @return String 指定的日期格式字符串
     */
    public static String getDate(Date date, String format)
    {
        if (null == date)
        {
            throw new NullPointerException("日期参数为Null");
        }
        else
        {
            DateTime dt = new DateTime(date);
            return dt.toString(format);
        }
    }

    /**
     * 将当前时间以指定的日期格式以字符串形式返回
     * @param format 日期格式
     * @return String 指定的日期格式
     */
    public static String getCurrentDate(String format)
    {
        if (StringUtils.isBlank(format))
        {
            throw new NullPointerException("日期参数为Null");
        }
        else
        {
            DateTime dt = new DateTime();
            return dt.toString(format);
        }
    }
    
    

    /**
     * 将指定时间加上指定秒数，返回长整数时间戳
     * @param time 指定的长整数时间或时间date
     * @param amouont 加上的秒数
     * @return Date 结果
     */
    public static Date addSecond(Object time,int amouont)
    {
        if (null == time)
        {
            throw new NullPointerException("日期参数为null");
        }
        else 
        {
            DateTime dt=new DateTime(time);
            return dt.plusSeconds(amouont).toDate();
        }
    }
    
    /**
     * 将指定时间加上指定分钟数，返回长整数时间戳
     * @param time 指定的长整数时间或时间date
     * @param amouont 加上的分钟数
     * @return Long 结果
     */
    public static Long addMinute(Object time,int amount)
    {
        if (null == time)
        {
            throw new NullPointerException("日期参数为null");
        }
        else 
        {
            DateTime dt=new DateTime(time);
            return dt.plusMinutes(amount).getMillis();
        }
    }
    
    /**
     * 将指定时间加上指定小时数，返回时间戳
     * @param time 指定的长整数时间或时间date
     * @param amouont 加上的小时数
     * @return Date 结果
     */
    public static Date addHour(Object time,int amount)
    {
        if (null == time)
        {
            throw new NullPointerException("日期参数为null");
        }
        else 
        {
            DateTime dt=new DateTime(time);
            return dt.plusHours(amount).toDate();
        }
    }
    
    /**
     * 将指定时间加上指定小时数和分钟数，返回时间戳
     * @param time
     * @param hour
     * @param minute
     * @return
     * @author 施建波  2016年10月4日 上午10:53:03
     */
    public static Date addHourAndMinute(Object time, int hour, int minute)
    {
    	if (null == time)
        {
            throw new NullPointerException("日期参数为null");
        }
        else 
        {
            DateTime dt=new DateTime(time);
            return dt.plusHours(hour).plusMinutes(minute).toDate();
        }
    }
    
    /**
     * 将指定时间加上指定天数，返回长整数时间戳
     * @param time 指定的长整数时间
     * @param amouont 加上的天数
     * @return Long 结果
     */
    public static Date addDay(Object time, int amount) {
        if (null == time) {
            return null;
        }
        DateTime dt=new DateTime(time);
        return dt.plusDays(amount).toDate();
    }
    
    /**
     * 将指定的Date对象加上指定周数，并返回指定格式的日期字符串
     * @param date Date对象
     * @param amount 指定周数
     * @param format 日期格式
     * @return String 返回指定格式的日期字符串
     */
    public static String addWeek(Object date, int amount, String format)
    {
        if (null == date)
        {
            throw new NullPointerException("日期参数为null");
        }
        else
        {
            DateTime dt = new DateTime(date);
            return dt.plusWeeks(amount).toString(format);
        }
    }
    
    /**
     * 将指定的Date对象加上指定月数，并返 回指定格式的日期字符串
     * @param date		date Date对象
     * @param amount	amount 添加的月数
     * @return
     * @author 施建波  2015年9月28日 下午4:36:28
     */
    public static Date addMonth(Object date, int amount)
    {
        if (null == date)
        {
            throw new NullPointerException("日期参数为null");
        }
        else
        {
            DateTime dt = new DateTime(date);
            return dt.plusMonths(amount).toDate();
        }
    }
    
    /**
     * 将指定的Date对象加上指定的年数，并返回指定的日期格式字符串
     * @param date		Date对象
     * @param amount	添加的年数
     * @return
     * @author 施建波  2015年9月28日 下午4:40:55
     */
    public static Date addYear(Object date, int amount)
    {
        if (null == date)
        {
            throw new NullPointerException("日期参数为null");
        }
        else
        {
            DateTime dt = new DateTime(date);
            return dt.plusYears(amount).toDate(); 
        }
    }

    /**
     * 根据DateTime对象判断该月是否为润月
     * @param dt DateTime
     * @return boolean true：是，false：否
     */
    public static boolean monthIsLeap(DateTime dt)
    {
        Property property = dt.monthOfYear();
        return property.isLeap();
    }

    /**
     * 判断当前月是否为润月
     * @return boolean true：是，false：否
     */
    public static boolean monthIsLeap()
    {
        DateTime dt = new DateTime();
        return monthIsLeap(dt);
    }

    /**
     * 根据指定的日期对象判断是否为润月
     * @param date Date
     * @return boolean true：是，false：否
     */
    public static boolean monthIsLeap(Date date)
    {
        if (null == date)
        {
            throw new NullPointerException("日期参数为Null");
        }
        else
        {
            DateTime dt = new DateTime(date);
            return monthIsLeap(dt);
        }
    }

    /**
     * 根据指定的字符串格式的日期判断该月是否为润月
     * @param date 字符串格式的日期
     * @return boolean true：是，false：否
     */
    public static boolean monthIsLeap(String date)
    {
        if (StringUtils.isBlank(date))
        {
            throw new NullPointerException("日期参数为Null");
        }
        else
        {
            DateTime dt = new DateTime(date);
            return monthIsLeap(dt);
        }
    }

    /**
     * 
     * @param date1
     * @param date2
     * @return
     */
    public static int secondDiff(Object date1, Object date2)
    {
        if (null == date1 || null == date2)
        {
            throw new NullPointerException("日期参数为Null");
        }
        else
        {
            DateTime dt1 = new DateTime(date1);
            DateTime dt2 = new DateTime(date2);
            Period period = new Period(dt1, dt2, PeriodType.seconds());
            return period.getSeconds();
        }
    }

    /**
     * 计算两个日期对象间隔的天数(date2-date1)
     * @param date1 Date或字符串格式的日期
     * @param date2 Date或字符串格式的日期
     * @return int 间隔天数
     */
    public static int dateDiff(Object date1, Object date2)
    {
        if (null == date1 || null == date2)
        {
            throw new NullPointerException("日期参数为Null");
        }
        else
        {
            DateTime dt1 = new DateTime(date1);
            DateTime dt2 = new DateTime(date2);
            Period period = new Period(dt1, dt2, PeriodType.days());
            return period.getDays();
        }
    }

    /**
     * 计算两个日期对象间隔的周数(date2-date1)
     * @param date1 Date或字符串格式的日期
     * @param date2 Date或字符串格式的日期
     * @return int 间隔周数
     */
    public static int weekDiff(Object date1, Object date2)
    {
        if (null == date1 || null == date2)
        {
            throw new NullPointerException("日期参数为Null");
        }
        else
        {
            DateTime dt1 = new DateTime(date1);
            DateTime dt2 = new DateTime(date2);
            Period period = new Period(dt1, dt2, PeriodType.weeks());
            return period.getWeeks();
        }
    }

    /**
     * 计算两个日期对象间隔的月数(date2-date1)
     * @param date1 Date或字符串格式的日期
     * @param date2 Date或字符串格式的日期
     * @return int 间隔月数
     */
    public static int monthDiff(Object date1, Object date2)
    {
        if (null == date1 || null == date2)
        {
            throw new NullPointerException("日期参数为Null");
        }
        else
        {
            DateTime dt1 = new DateTime(date1);
            DateTime dt2 = new DateTime(date2);
            Period period = new Period(dt1, dt2, PeriodType.months());
            return period.getMonths();
        }
    }

    /**
     * 计算两个日期对象间隔的年数(date2-date1)
     * @param date1 Date或字符串格式的日期
     * @param date2 Date或字符串格式的日期
     * @return int 间隔年数
     */
    public static int yearDiff(Object date1, Object date2)
    {
        if (null == date1 || null == date2)
        {
            throw new NullPointerException("日期参数为Null");
        }
        else
        {
            DateTime dt1 = new DateTime(date1);
            DateTime dt2 = new DateTime(date2);
            Period period = new Period(dt1, dt2, PeriodType.years());
            return period.getYears();
        }
    }

    /**
     * 取得当前月的最后一天并以指定格式的字符串返回
     * @param format 指定的日期格式
     * @return String 指定格式的日期字符串
     */
    public static String getLastDayOfMonth(String format)
    {
        /*
         * Calendar calendar = Calendar.getInstance(); int max =
         * calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
         * calendar.set(Calendar.DAY_OF_MONTH, max); DateTime dt = new
         * DateTime(calendar); return dt.toString(format);
         */
        DateTime dt = new DateTime();
        return dt.dayOfMonth().withMaximumValue().toString(format);
    }

    /**
     * 根据给定的日期参数，计算年龄
     * @param date Unix时间戳(精确到秒)
     * @return int 年龄
     */
    public static int getAge(long date)
    {
        Date now = new Date();
        Date birthDate = getTimeFromLong(date);
        int age = yearDiff(birthDate, now);
        return age;
    }

    /**
     * Date now = new Date();
     * @param date 日期对象
     * @return int 年龄
     */
    public static int getAge(Date date)
    {
        Date now = new Date();
        int age = yearDiff(date, now);
        return age;
    }

    /**
     * 将当前时间精确到秒并转化为long格式
     * @return iTime 整数格式的当前时间，精确到毫秒
     */
    public static long getCurrentLong()
    {
        long iTime = System.currentTimeMillis() ;
        return iTime;
    }

    /**
     * 根据长整型时间戳返回指定格式的日期字符串
     * @param lTime 长整型时间戳
     * @param format 日期格式
     * @return String 指定格式的日期字符串
     */
    public static String getTimeFromLong(long lTime, String format)
    {
            DateTime dt = new DateTime(lTime);
            return dt.toString(format);
    }
    /**
     * 根据长整型时间戳返回指定格式的日期字符串 排除lTime 为0的情况
     * @param lTime 长整型时间戳
     * @param format 日期格式
     * @return String 指定格式的日期字符串
     */
    public static String getStringTime(Long lTime, String format)
    {
        if(!lTime.equals(0l)){
            DateTime dt = new DateTime(lTime);
            return dt.toString(format);
        }
        return null;
    }

    /**
     * 根据长整型时间戳返回日期对象
     * @param lTime 长整型时间戳
     * @return Date 日期对象
     */
    public static Date getTimeFromLong(long longTimestamp)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(longTimestamp);
        return cal.getTime();
    }
 
    /**
     * 根据日期格式的字符串取得长整数时间戳
     * @param date 日期格式的字符串
     * @return long 长整数时间戳
     */
    public static long getLongFromTime(String date)
    {
        long lTime = 0L;
        if (StringUtils.isNotBlank(date))
        {
            DateTime dt = new DateTime(date);
            lTime = dt.toDate().getTime();
        }
        return lTime;
    }

    /**
     * 根据日期格式的字符串取得长整数时间戳
     * @param date
     * @param format 指定格式 如：yyyy-MM-dd HH:mm:ss
     * @return long 长整数时间戳
     */
    public static long getLongFromTime(String date,String format){
        long lTime = 0L;
        if (StringUtils.isNotBlank(date))
        {
            DateTime time = DateTimeFormat.forPattern(format).parseDateTime(date);
            lTime = time.toDate().getTime();
        }
        return lTime;
    }
  

    /**
     * 根据日期对象取得长整数时间戳
     * @param date Date
     * @return long 长整数时间戳
     */
    public static long getLongFromTime(Date date)
    {
        long lTime = 0L;
        if (null != date)
        {
            DateTime dt = new DateTime(date);
            lTime = dt.toDate().getTime();
        }
        return lTime;
    }

   
    /**
	 * 将yyyy-MM-dd HH:mm:ss格式的字符串时间转化为日期格式
	 * @param time yyyy-MM-dd HH:mm:ss格式的字符串
	 * @return Date Date
	 */
	public static Date parseStringToDate(String time, String format)
	{
	    DateTime dt = DateTimeFormat.forPattern(format).parseDateTime(time);;
	    return dt.toDate();
	}
	
	/**
	 * 将当前日期精确到秒并转化为long格式
	 * @return
	 * @author 施建波
	 */
    public static Long getCurrentDayLong()
    {
        DateTime dateTime = new DateTime();
        return dateTime.withTimeAtStartOfDay().getMillis();
    }
    
    

    
    /**
     * 将当前日期加上秒或分、时
     * @param cal       当前时间对象
     * @param field     时间类型秒、分、时
     * @param time      添加时间
     * @return
     * @author 施建波     2015年3月23日 下午5:41:09
     */
    public static Date addTime(Calendar cal, Integer field, Integer time){
        if(null == cal){
            cal = Calendar.getInstance();
        }
        cal.add(field, time);
        return cal.getTime();
    }
    
    /**
     * 将当然日期转成短型日期（2015-10-10）
     * @param date
     * @return
     * @author 施建波  2015年9月25日 下午6:36:37
     */
    public static Date getShortDate(Object date){
    	if (null == date)
        {
            throw new NullPointerException("日期参数为Null");
        }else{
	    	DateTime dt = new DateTime(date);
	    	return dt.withTimeAtStartOfDay().toDate();
        }
    }
    
    
    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     * @param str1 时间参数 1 格式：1990-01-01 12:00:00
     * @param str2 时间参数 2 格式：2009-01-01 12:00:00
     * @return long[] 返回值为：{天, 时, 分, 秒}
     */
    public static Long[] getDistanceTimes(String str1, String str2) {
        DateFormat df = new SimpleDateFormat(ApplicationConst.DATE_FORMAT_YMDHMS);
        Date one;
        Date two;
        Long day = 0L;
        Long hour = 0L;
        Long min = 0L;
        Long sec = 0L;
        try {
        	one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff ;
            if(time1<time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff/1000-day*24*60*60-hour*60*60-min*60);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Long[] times = {day, hour, min, sec};
        return times;
    }
    
    /**
     * 两个时间相差距离多少秒
     * @param str1 时间参数 1 格式：1990-01-01 12:00:00
     * @param str2 时间参数 2 格式：2009-01-01 12:00:00
     * @return
     */
//    public static long getTotalSeconds(String str1, String str2) {
//    	long TotalSeconds = 0;
//    	Long[] result = getDistanceTimes(str1, str2);
//
//    	TotalSeconds += NumericUtils.intMul(result[0].intValue(),24*60*60).longValue();
//    	TotalSeconds += NumericUtils.intMul(result[1].intValue(),60*60).longValue();
//    	TotalSeconds += NumericUtils.intMul(result[2].intValue(),60).longValue();
//    	TotalSeconds += result[3];
//
//		return TotalSeconds;
//    }
    
    /**
     * 获取系统当前时间
     * @return
     * @author zhangcm 2016-09-27 13:18:30
     */
    public static Date getNowDate(){
    	return new Date();
    }
}
