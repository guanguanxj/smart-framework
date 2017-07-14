package org.smart4j.framework.helper;

import org.smart4j.framework.annotation.Action;
import org.smart4j.framework.bean.Handler;
import org.smart4j.framework.bean.Request;
import org.smart4j.framework.util.ClassUtil;
import org.smart4j.framework.util.ColletionUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by jing_xu on 2017/7/13.
 */
public final class ControllerHelper {
    //@Action("RequestMethod:RequestPath")与Handler的映射
    private static Map<Request,Handler> ACTION_MAP = new HashMap<Request, Handler>();

    //@Controller注解类下的@Action注解方法
    static {
        Set<Class<?>> controllerClassSet = ClassHelper.getControllerClass();
        if(!ColletionUtils.isEmpty(controllerClassSet)) {
            for (Class<?> controllerClass : controllerClassSet) {
                   Method[] methods = controllerClass.getDeclaredMethods();
                if(methods!=null && methods.length>0){
                    for(Method method : methods){
                        if(method.isAnnotationPresent(Action.class)){
                           Action action = method.getAnnotation(Action.class);
                           String actionValue = action.value();
                            //@Action("RequestMethod:RequestPath")
                            if(actionValue.matches("\\w+:/\\w*")){
                                String[] arrays = actionValue.split(":");
                                if(arrays!=null && arrays.length==2)
                                {
                                    Request request = new Request(arrays[0],arrays[1]);
                                    Handler handler = new Handler(controllerClass,method);
                                    ACTION_MAP.put(request,handler);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取某个request对应的Handler
     * @param requestMethod
     * @param requestPath
     * @return
     */
    public static Handler getHandler(String requestMethod,String requestPath){
        Request request = new Request(requestMethod,requestPath);
        return ACTION_MAP.get(request);
    }
}
