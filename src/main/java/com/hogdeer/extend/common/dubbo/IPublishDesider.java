package com.hogdeer.extend.common.dubbo;

import com.alibaba.dubbo.config.ReferenceConfig;

public interface IPublishDesider {
	 String desideVersion(ReferenceConfig referenceConfig, String publishFilter);
}
