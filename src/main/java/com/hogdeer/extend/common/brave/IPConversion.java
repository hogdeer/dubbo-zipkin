package com.hogdeer.extend.common.brave;

/**
 * 
 * <p>File：IPConversion.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2017年10月11日 下午3:31:44</p>
 * <p>Company: cargogm.com</p>
 * @author 施建波
 * @version 1.0
 */
public class IPConversion {

    public  static  int convertToInt(String ipAddr){  
        String[] p4 = ipAddr.split("\\.");
        int ipInt = 0;
        int part = Integer.valueOf(p4[0]);
        ipInt = ipInt | (part << 24);
        part = Integer.valueOf(p4[1]);
        ipInt = ipInt | (part << 16);
        part = Integer.valueOf(p4[2]);
        ipInt = ipInt | (part << 8);
        part = Integer.valueOf(p4[3]);
        ipInt = ipInt | (part);
        return ipInt;
    }
}
