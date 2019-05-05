package com.hogdeer.extend.common.utils;

import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.util.*;

/**
 * 
 * @author 张昌苗 2017年11月27日
 */
public class ArrayHandleUtils {

    @SuppressWarnings("unchecked")
    public static <T> T[][] listToArray(List<T> list, Integer seat) {
        if (!CollectionUtils.isEmpty(list)) {
            Integer len = (int) Math.ceil((double) list.size() / seat);
            T[][] array = (T[][]) Array.newInstance(list.get(0).getClass(), len, seat);
            for (T t : list) {
                for (int i = 0; i < len; i++) {
                    for (int j = 0; j < seat; j++) {
                        array[i][j] = t;
                    }
                }
            }
            return array;
        }
        return null;
    }
    
    /**
     * 获取属性值List
     * @param sourceList
     * @param attrName
     * @return
     * @author zhangcm 2016-10-31 16:28:47
     */
    public static <E> List<E> readAttrList(Collection<? extends Object> sourceList, String attrName) {
        return readAttrList(sourceList, attrName, true);
    }

    /**
     * 获取属性值List
     * @param sourceList
     * @param attrName
     * @param isUnique
     * @return
     * @author zhangcm 2016-10-31 18:09:14
     */
    public static <E> List<E> readAttrList(Collection<? extends Object> sourceList, String attrName, Boolean isUnique) {
        return readAttrList(sourceList, attrName, true, false);
    }

    /**
     * 获取属性值List
     * @param sourceList
     * @param attrName
     * @param isUnique
     * @param keepNull
     * @return
     * @author zhangcm 2016-10-31 18:09:14
     */
    public static <E> List<E> readAttrList(Collection<? extends Object> sourceList, String attrName, Boolean isUnique, Boolean keepNull) {
        Preconditions.checkNotNull(attrName);
        Preconditions.checkNotNull(isUnique);
        List<E> result = Lists.newArrayList();
        if (CollectionUtils.isEmpty(sourceList)) {
            return result;
        }
        for (Object source : sourceList) {
            E attrValue = readAttr(source, attrName);
            result.add(attrValue);
        }
        if (isUnique && CollectionUtils.isNotEmpty(result)) {
            Set<E> set = Sets.newLinkedHashSet(result);
            result = Lists.newArrayList(set);
        }
        if(!keepNull) {
            result.remove(null);
        }
        return result;
    }

    /**
     * 把List转Map， 属性值为key，V对象为value
     * @param sourceList
     * @param attrName 
     * @return
     * @author zhangcm 2016-10-31 17:07:05
     */
    public static <K, V> Map<K, V> parseMap(List<V> sourceList, String attrName) {
        Map<K, V> result = Maps.newHashMap();
        if (CollectionUtils.isEmpty(sourceList)) {
            return result;
        }
        for (V source : sourceList) {
            K attrValue = readAttr(source, attrName);
            result.put(attrValue, source);
        }
        return result;
    }

    /**
     * 对List分组
     * 根据attrName对sourceList进行分组，Map的key为attrName的值
     * @param sourceList
     * @param attrName
     * @return
     * @author zhangcm 2016-11-04 16:51:41
     */
    public static <K, V> Map<K, List<V>> parseMapGroup(List<V> sourceList, String attrName) {
        Map<K, List<V>> result = Maps.newHashMap();
        if (CollectionUtils.isEmpty(sourceList)) {
            return result;
        }
        for (V source : sourceList) {
            K attrValue = readAttr(source, attrName);
            List<V> list = result.get(attrValue);
            if (CollectionUtils.isEmpty(list)) {
                list = Lists.newArrayList();
                result.put(attrValue, list);
            }
            list.add(source);
        }
        return result;
    }
    
    /**
     * 合并LIST
     * @param sourceList
     * @param attrName
     * @return
     * @author 施建波  2017年4月6日 下午3:41:07
     */
    public static <V, K> List<List<V>> parseListGroup(List<V> sourceList, String attrName) {
        List<List<V>> resultList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(sourceList)) {
            return resultList;
        }
        K temp = null;
        List<V> list = Lists.newArrayList();
        for (V source : sourceList) {
            K attrValue = readAttr(source, attrName);
            if(!attrValue.equals(temp)){
                list = Lists.newArrayList();
                temp = attrValue;
                resultList.add(list);
            }
            list.add(source);
        }
        return resultList;
    }
    
    
    public static <E, T> E readObject(List<E> sourceList, String attrName, T attrValue) {
        if (CollectionUtils.isEmpty(sourceList) || null == attrValue) {
            return null;
        }
        for (E source : sourceList) {
            T tempValue = readAttr(source, attrName);
            if(attrValue.equals(tempValue)){
                return source;
            }
        }
        return null;
    }

    /**
     * 获取属性值
     * @param source
     * @param attrName
     * @return
     * @author zhangcm 2016-10-31 16:28:47
     */
    public static <T> T readAttr(Object source, String attrName) {
        try {
            @SuppressWarnings("unchecked")
            T result = null != source ? (T) PropertyUtils.getProperty(source, attrName) : null;
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 获取子集
     * @param list
     * @param attrName
     * @param attrValue
     * @return
     * @author zhangcm 2017-07-05 12:07:37
     */
    @SuppressWarnings("deprecation")
    public static <T> List<T> readList(List<T> list, String attrName, Object attrValue) {
        List<T> result = Lists.newArrayList();
        if(CollectionUtils.isEmpty(list)){
            return result;
        }
        for (T t : list) {
            Object val = readAttr(t, attrName);
            if(!ObjectUtils.equals(attrValue, val)){
                continue;
            }
            result.add(t);
        }
        return result;
    }
    
    /**
     * 写入属性值
     * @param source
     * @param attrName
     * @param value
     * @author zhangcm 2017-03-10 10:24:10
     */
    public static <T> void writeAttr(Object source, String attrName, T value) {
        try {
            PropertyUtils.setProperty(source, attrName, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 去掉数组中null的值
     * @param arr
     * @return
     * @author zhangcm 2016-11-08 11:42:03
     */
    public static <T> List<T> filterNull(T[] arr){
        List<T> result = Lists.newArrayList();
        if(ArrayUtils.isEmpty(arr)){
            return result;
        }
        for (T obj : arr) {
            if(null != obj){
                result.add(obj);
            }
        }
        return result;
    }
    
    /**
     * 字符串转为list
     * @param str
     * @param separator
     * @param classOfT
     * @return
     * @author 施建波  2016年11月8日 下午2:04:38
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> strToList(String str, String separator, Class<?> classOfT){
        if(StringUtils.isBlank(separator)) separator = ",";
        T[] arr = (T[])ConvertUtils.convert(str.split(separator), classOfT);
        return Arrays.asList(arr);
    }
    
    /**
     * 去重
     * @param list
     * @return
     * @author zhangcm 2016-12-28 14:26:27
     */
    public static <T> List<T> unique(List<T> list){
        if(CollectionUtils.isEmpty(list)){
            return list;
        }
        Set<T> set = Sets.newLinkedHashSet(list);
        return Lists.newArrayList(set);
    }

    /**
     * 去重
     * @param list
     * @author zhangcm 2016-12-28 14:26:27
     */
    public static <T> void uniqueInList(List<T> list){
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        Set<T> set = Sets.newLinkedHashSet(list);
        list.clear();
        list.addAll(set);
    }
    
    /**
     * 获取map中对象的值
     * @param objectMap
     * @param key
     * @param attrName
     * @param defaultValue
     * @return
     * @author zhangcm 2017-03-09 09:46:59
     */
    public static <K, O, V> V readObjectMapValue(Map<K, O> objectMap, K key, String attrName, V defaultValue) {
        O object = objectMap.get(key);
        if(null == object){
            return defaultValue;
        }
        V value = readAttr(object, attrName);
        if(null == value){
            return defaultValue;
        }
        return value;
    }
    
    /**
     * 创建对象并加入容器
     * @param object
     * @param container
     * @return
     * @author zhangcm 2017-03-09 10:11:25
     */
    public static <O> O create(O object, Collection<O> container){
        container.add(object);
        return object;
    }
    
    /**
     * 连接数组
     * @param array 数组
     * @return
     * @author 施建波  2017年3月10日 下午4:43:47
     */
    public static String arrayToStr(Object[] array){
        StringBuilder sb = new StringBuilder();
        if(ArrayUtils.isNotEmpty(array)){
            for(Object obj:array){
                sb.append(obj).append(",");
            }
        }
        return sb.toString();
    }
    
    /**
     * 排序
     * @param list
     * @param sortAttr
     * @author zhangcm 2017-07-05 11:39:37
     */
    public static <E> void sort(List<E> list, String sortAttr){
        sort(list, sortAttr, true);
    }
    
    /**
     * 排序
     * @param list
     * @param sortAttr
     * @param isAsc
     * @author zhangcm 2017-07-05 11:39:41
     */
    public static <E> void sort(List<E> list, final String sortAttr, final Boolean isAsc){
        if(CollectionUtils.isEmpty(list) || StringUtils.isEmpty(sortAttr)){
            return;
        }
        Collections.sort(list, new Comparator<E>() {
            @Override
            public int compare(E o1, E o2) {
                Comparable<Object> sort1 = ArrayHandleUtils.readAttr(o1, sortAttr);
                Comparable<Object> sort2 = ArrayHandleUtils.readAttr(o2, sortAttr);
                if(null == sort1 && null == sort2) {
                    return 0;
                } else if(null == sort1 || null == sort2) {
                    return null == sort2 ? -1 : 1;
                }
                if(null == isAsc || isAsc){
                    return sort1.compareTo(sort2);
                }else{
                    return sort2.compareTo(sort1);
                }
            }
        });
    }
    
    /**
     * 追加字符串，并去重
     * @param fromStr
     * @param toStr
     * @param separator
     * @return
     * @author 张昌苗 2018年2月22日
     */
    public static String addStr(String fromStr, String toStr, String separator) {
        if(StringUtils.isEmpty(fromStr) && StringUtils.isEmpty(toStr)) {
            return null;
        }
        Set<String> fromSet = Sets.newLinkedHashSet(Lists.newArrayList(StringUtils.split(StringUtils.trimToEmpty(fromStr), separator)));
        Set<String> toSet = Sets.newLinkedHashSet(Lists.newArrayList(StringUtils.split(StringUtils.trimToEmpty(toStr), separator)));
        fromSet.addAll(toSet);
        return StringUtils.join(Lists.newArrayList(fromSet), separator);
    }
    
    public static List<Object> copyList(Object obj, Integer copayNum){
    	if(null == copayNum){
    		copayNum = 2;
    	}
    	List<Object> copyList = Lists.newArrayList();
    	if(null != obj){
        	if(obj instanceof List){
				List<Object> objList = (List)obj;
				copyList(objList, copyList, copayNum);
			}else if(obj instanceof PageInfo){
				PageInfo<Object> pageInfo = (PageInfo)obj;
				List<Object> objList = pageInfo.getList();
				copyList(objList, copyList, copayNum);
			}else if(obj instanceof Map){
				Map<Object, Object> objMap = (Map<Object, Object>)obj;
				copyMap(objMap, copyList, copayNum);
			}
    	}
    	return copyList;
    }
    
    public static void copyList(List<Object> objList, List<Object> copyList, Integer copayNum){
    	if(null == copayNum){
    		copayNum = 2;
    	}
    	if(CollectionUtils.isNotEmpty(objList)){
			int toIndex = objList.size();
			toIndex = (toIndex > copayNum) ? copayNum : toIndex;
			copyList.add(objList.subList(0, toIndex)); 
		}
    }
    
    public static void copyMap(Map<Object, Object> objMap, List<Object> copyList, Integer copayNum){
    	if(null == copayNum){
    		copayNum = 2;
    	}
    	if(MapUtils.isNotEmpty(objMap)){
    		int count = 1;
    		for (Map.Entry<Object, Object> entry : objMap.entrySet()) { 
    			if(count <= copayNum){
    				copyList.add(entry.getValue());
    			}else{
    				break;
    			}
    			count++;
    		}
    	}
    }
}
