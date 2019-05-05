package com.hogdeer.extend.common.brave.dubbo;

import com.github.kristofa.brave.Brave;

/**
 * 
 * <p>File：BraveDubboManagementBean.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * @version 1.0
 */
public class BraveDubboManagementBean { 

    public Brave brave;

    public BraveDubboManagementBean(Brave brave) {
        this.brave = brave;
        BraveConsumerFilter.setBrave(brave);
        BraveProviderFilter.setBrave(brave);
    }

    public void setBrave(Brave brave) {
        this.brave = brave;
        BraveConsumerFilter.setBrave(brave);
        BraveProviderFilter.setBrave(brave);
    }
}
