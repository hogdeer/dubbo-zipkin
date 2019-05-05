package com.hogdeer.extend.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.net.util.IPAddressUtil;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;


/**
 * <p>File：IPUtil.java</p>
 * <p>Title: 获取IP工具类</p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2015 2015年3月20日 下午4:15:54</p>
 * <p>Company: kinorsoft</p>
 * @author 施建波
 * @version 1.0
 */
@SuppressWarnings("restriction")
public class IPUtils
{    
    private static final Logger logger = LoggerFactory.getLogger(IPUtils.class);
    
    /**
     * 取得客户端IP地址并转化为整数
     * @param request HttpServletRequest
     * @return int 整数格式的客户端IP地址
     */
    public static int getIpAddr(HttpServletRequest request)
    {
        String ip = getOriginalIpAddr(request);
        return formatStrIpToInt(ip);
    }

    /**
     * 获取 字符串类型的ip
     * @param request
     * @return
     */
    public static String getOriginalIpAddr(HttpServletRequest request)
    {
        /*String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getRemoteAddr();
        }
        return ip;*/
    	String ipAddress = request.getHeader("x-forwarded-for");  
        if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {  
            ipAddress = request.getHeader("Proxy-Client-IP");  
        }  
        if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {  
            ipAddress = request.getHeader("WL-Proxy-Client-IP");  
        }  
        if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {  
            ipAddress = request.getRemoteAddr();  
            if(ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")){  
                //根据网卡取本机配置的IP  
                InetAddress inet=null;  
                try {  
                    inet = InetAddress.getLocalHost();  
                } catch (UnknownHostException e) {  
                    e.printStackTrace();  
                }  
                ipAddress= inet.getHostAddress();  
            }  
        }  
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割  
        if(ipAddress!=null && ipAddress.length()>15){ //"***.***.***.***".length() = 15  
            if(ipAddress.indexOf(",")>0){  
                ipAddress = ipAddress.substring(0,ipAddress.indexOf(","));  
            }  
        }  
        return ipAddress; 
    }

    public static int formatStrIpToInt(String strIp)
    {
        int ipInt = 0;
        if (StringUtils.isNotBlank(strIp))
        {
            if (IPAddressUtil.isIPv4LiteralAddress(strIp))
            {
                ipInt = ipToInt(strIp);
            }
            else
            {
                String[] string = strIp.split(",");
                int iLen = string.length;
                if (iLen > 0)
                    strIp = StringUtils.trimToEmpty(string[iLen - 1]);
                if (IPAddressUtil.isIPv4LiteralAddress(strIp))
                {
                    ipInt = ipToInt(strIp);
                }
            }
        }
        return ipInt;
    }
    
    /**
     * 将IP地址转换为整数类型
     * @param addr 字符串类型的IP地址
     * @return 整数
     */
    public static int ipToInt(final String addr)
    {
        int ip = 0;
        if (StringUtils.isNotBlank(addr))
        {
            final String[] addressBytes = addr.split("\\.");
            ip = 0;
            for (int i = 0; i < 4; i++)
            {
                ip <<= 8;
                ip |= Integer.parseInt(addressBytes[i]);
            }
        }
        return ip;
    }
    
    /**
     * 将整数类型的IP地址转换为字符串类型的IP地址
     * @param i 整数
     * @return IP地址
     */
    public static String intToIp(int i)
    {
        if (i == 0)
            return "";
        return ((i >> 24) & 0xFF) + "." + ((i >> 16) & 0xFF) + "."
                + ((i >> 8) & 0xFF) + "." + (i & 0xFF);
    }
    
    /**
     * 根据网卡取本机配置的IP
     * 如果是双网卡的，则取出外网IP
     * @return
     */
    public static String getNetIp()
    {
        String localip = null;// 本地IP，如果没有配置外网IP则返回它
        String netip = null;// 外网IP
        try
        {
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            InetAddress ip = null;
            boolean finded = false;// 是否找到外网IP
            while (netInterfaces.hasMoreElements() && !finded)
            {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> address = ni.getInetAddresses();
                while (address.hasMoreElements())
                {
                    ip = address.nextElement();
                    if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
                            && ip.getHostAddress().indexOf(":") == -1)
                    {// 外网IP
                        netip = ip.getHostAddress();
                        finded = true;
                        break;
                    }
                    else if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
                            && ip.getHostAddress().indexOf(":") == -1)
                    {// 内网IP
                        localip = ip.getHostAddress();
                    }
                }
            }
        }
        catch (SocketException e)
        {
            logger.error(e.getMessage(), e);
        }
        if (netip != null && !"".equals(netip))
        {
            return netip;
        }
        else
        {
            return localip;
        }
    }
    
    /**
    * 根据网卡取本机配置的内网IP
    * 如果是双网卡的，则取出内网IP
    * @return String 内网IP地址
    */
    public static String getLocalIp()
    {
        String localip = null;// 本地IP，如果没有配置外网IP则返回它
        try
        {
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            InetAddress ip = null;
            boolean finded = false;// 是否找到外网IP
            while (netInterfaces.hasMoreElements() && !finded)
            {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> address = ni.getInetAddresses();
                while (address.hasMoreElements())
                {
                    ip = address.nextElement();
                    if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
                            && ip.getHostAddress().indexOf(":") == -1)
                    {
                        localip = ip.getHostAddress();
                        finded = true;
                        break;
                    }
                }
            }
        }
        catch (SocketException e)
        {
            logger.error(e.getMessage(), e);
        }
        return localip;
    }
    
    /**获取机器名*/
    public static String getHostName() {
        String hostName = "";
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            logger.error(e.getMessage(), e);
        }
        return hostName;
    }
}
