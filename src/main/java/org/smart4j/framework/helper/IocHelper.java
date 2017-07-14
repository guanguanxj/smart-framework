package org.smart4j.framework.helper;


import org.smart4j.framework.annotation.Inject;
import org.smart4j.framework.util.ColletionUtils;
import org.smart4j.framework.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.Map;

/** 控制反转：将某个类中某个字段带有@Inject注解的实例化，也就是依赖注入DI
 * Created by jing_xu on 2017/7/13.
 */
public final class IocHelper {
    static {
        //获取所有Bean与bean实例的映射关系
        Map<Class<?>, Object> beanMap = BeanHelper.getBeanMap();
        if (!ColletionUtils.isEmpty(beanMap)) {
            for (Map.Entry<Class<?>, Object> bean : beanMap.entrySet()) {
                Class<?> beanClass = bean.getKey();
                Object beanObject = bean.getValue();
                //获取类中所有声明的字段
                Field[] beanfields = beanClass.getDeclaredFields();
                for(Field field : beanfields)
                {
                    //字段带有@Inject注解的
                    if(field.isAnnotationPresent(Inject.class))
                    {
                        //注解字段对应的类
                        Class<?> fieldClass = field.getType();
                        //注解字段对应的实例
                        Object fieldObject = beanMap.get(fieldClass);
                        if(fieldObject!=null)
                        {//通过反射，将bean实例下某个字段的值进行实例化
                            ReflectionUtil.setField(beanObject,field,fieldObject);
                        }
                    }
                }
            }
        }
    }
}
