package org.smart4j.framework.helper;

import org.smart4j.framework.util.ReflectionUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/** Bean容器
 * Created by jing_xu on 2017/7/13.
 */
public final class BeanHelper {
    /**
     * 定义bean映射Map<bean类，bean实例>
     */
   private static final Map<Class<?>,Object> BEAN_MAP = new HashMap<Class<?>,Object>();

    static{
        Set<Class<?>> beanClassSet = ClassHelper.getBeanClassSet();
        for(Class<?> beanClass : beanClassSet){
            Object obj = ReflectionUtil.newInstance(beanClass);
            BEAN_MAP.put(beanClass,obj);
        }
    }

    /**
     * 获取bean映射
     * @return
     */
    public static Map<Class<?>,Object> getBeanMap(){
        return BEAN_MAP;
    }

    /**
     * 获取指定类的实例
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> cls){
        if(!BEAN_MAP.containsKey(cls)) {
            throw new RuntimeException("can not get bean by class:"+cls.getName());
        }
        return  (T)BEAN_MAP.get(cls);
    }
}
