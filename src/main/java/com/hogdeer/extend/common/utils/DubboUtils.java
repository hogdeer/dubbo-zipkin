
package com.hogdeer.extend.common.utils;


public class DubboUtils {

	/**
	 * 获取DUBBO缓存KEY
	 * @Title: getReferenceCacheKey   
	 * @param reggroup			节点名
	 * @param group				组名
	 * @param interfaceName		接口名
	 * @param version			版本号
	 * @return 
	 * String
	 * @author
	 */
	public static String getReferenceCacheKey(String reggroup, String group, String interfaceName, String version){
		return StringHandleUtils.connectMulString("-", reggroup, group, interfaceName, version);
	}
}
