package org.smart4j.framework.helper;


import org.smart4j.framework.annotation.Controller;
import org.smart4j.framework.annotation.Service;
import org.smart4j.framework.util.ClassUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by jing_xu on 2017/7/13.
 */
public final class ClassHelper {
    //定义类集合（用于存放所加载的类）
    private static final Set<Class<?>> CLASS_SET;

    static{
        String basePackage = ConfigHelper.getAppBasePackage();
        CLASS_SET = ClassUtil.getClassSet(basePackage);
    }

    public static Set<Class<?>> getClassSet(){
        return CLASS_SET;
    }

    public static Set<Class<?>> getServiceClassSet(){
        Set<Class<?>> classSet = new HashSet<Class<?>>();
        for(Class<?> c :CLASS_SET)
        {
            if(c.isAnnotationPresent(Service.class))
            {
                classSet.add(c);
            }
        }
        return  classSet;
    }

    public static Set<Class<?>> getControllerClass(){
        Set<Class<?>> classSet = new HashSet<Class<?>>();
        for(Class<?> c :CLASS_SET)
        {
            if(c.isAnnotationPresent(Controller.class))
            {
                classSet.add(c);
            }
        }
        return  classSet;
    }

    public static Set<Class<?>> getBeanClassSet(){
        Set<Class<?>> beanClassSet = new HashSet<Class<?>>();

        beanClassSet.addAll(getServiceClassSet());
        beanClassSet.addAll(getControllerClass());
        return  beanClassSet;
    }
 }
