/*
 * @(#)BaseEntity.java 2016年11月4日 上午9:11:58
 * Copyright 2016 施建波, Inc. All rights reserved. 积木科技
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.hogdeer.extend.common.bean;

import com.baomidou.mybatisplus.annotations.TableField;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * <p>File：BaseEntity.java</p>
 * <p>Title: 实体基类</p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2016年11月4日 上午9:11:58</p>
 * <p>Company: </p>
 * @author 施建波
 * @version 1.0
 */
public class BaseEntity implements Serializable { 
    
    @TableField(exist = false)
    private static final long serialVersionUID = -3306811047216912474L;

    //排序字符串
    @TableField(exist = false)
    private String orderByField;
    //分组字符串
    @TableField(exist = false)
    private String groupByField;
    //having字符串
    @TableField(exist = false)
    private String havingByField;
    //列名集合如id,name等
    @TableField(exist = false)
    private String entityColumns;
    //修改为空值的列名
    @TableField(exist = false)
    private String upNullColumns;
    // 用于存放统计数量
    @TableField(exist = false)
    private Long resultCount;
    // 左连接
    @TableField(exist = false)
    private String leftJoinTable;
    //修改为空值的列名集合
    @TableField(exist = false)
    private List<String> upNullColumnList;

    public String getOrderByField() {
        return orderByField;
    }

    public void setOrderByField(String orderByField) {
        this.orderByField = orderByField;
    }

    public String getGroupByField() {
        return groupByField;
    }

    public void setGroupByField(String groupByField) {
        this.groupByField = groupByField;
    }

    public String getEntityColumns() {
        return entityColumns;
    }

    public void setEntityColumns(String entityColumns) {
        this.entityColumns = entityColumns;
    }

    public String getHavingByField() {
        return havingByField;
    }

    public void setHavingByField(String havingByField) {
        this.havingByField = havingByField;
    }

	public String getUpNullColumns() {
		return upNullColumns;
	}

	public void setUpNullColumns(String upNullColumns) {
		this.upNullColumns = upNullColumns;
	}

    public Long getResultCount() {
        return resultCount;
    }

    public void setResultCount(Long resultCount) {
        this.resultCount = resultCount;
    }

    public String getLeftJoinTable() {
        return leftJoinTable;
    }

    public void setLeftJoinTable(String leftJoinTable) {
        this.leftJoinTable = leftJoinTable;
    }

	public List<String> getUpNullColumnList() {
		return upNullColumnList;
	}

	public void setUpNullColumnList(List<String> upNullColumnList) {
		this.upNullColumnList = upNullColumnList;
	}
	
	public void addUpNullColumnList(String columnName){
		if(StringUtils.isNotBlank(columnName)){
			if(null == this.upNullColumnList){
				this.upNullColumnList = Lists.newArrayList();
			}
			this.upNullColumnList.add(columnName);
		}
		
	}
}
