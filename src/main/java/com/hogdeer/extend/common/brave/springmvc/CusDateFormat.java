package com.hogdeer.extend.common.brave.springmvc;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.util.StdDateFormat;

import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间转化
 * @author 张昌苗 2018年2月6日
 */
public class CusDateFormat extends StdDateFormat {

    private static final long serialVersionUID = 3441545018984744155L;

    private SimpleDateFormat datetimeSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");

    public Date parse(String dateStr, ParsePosition pos) {
        if (StringUtils.isNotBlank(dateStr) && dateStr.length() == 19) {
            return datetimeSdf.parse(dateStr, pos);
        } else {
            return dateSdf.parse(dateStr, pos);
        }
    }

    public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
        return datetimeSdf.format(date, toAppendTo, fieldPosition);
    }

    public CusDateFormat clone() {
        return new CusDateFormat();
    }
}
