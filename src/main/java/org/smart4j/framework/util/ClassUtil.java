package org.smart4j.framework.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.print.DocFlavor;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by jing_xu on 2017/7/12.
 */
public final class ClassUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassUtil.class);

    /**
     * 获取类加载器
     *
     * @return
     */
    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 加载类
     *
     * @param className
     * @param isInitialized
     * @return
     */
    public static Class<?> loadClass(String className, boolean isInitialized) {
        Class<?> clz;
        try {
            clz = Class.forName(className, isInitialized, getClassLoader());
        } catch (ClassNotFoundException e) {
            LOGGER.error("load class failture", e);
            throw new RuntimeException(e);
        }
        return clz;
    }

    /**
     * 获取指定包下的所有类
     *
     * @param packageName
     * @return
     */
    public static Set<Class<?>> getClassSet(String packageName) {
        Set<Class<?>> classSet = new HashSet<Class<?>>();
        Enumeration<URL> urls = null;
        try {
            //通过类加载器获取包名下的所有资源路径URL
            urls = getClassLoader().getResources(packageName.replace(".", "/"));
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (url != null) {
                    String protocol = url.getProtocol();
                    if (protocol.equals("file")) {
                        //file类型
                        String packagePath = url.getPath().replaceAll("%20", " ");
                        //加载类
                        addClass(classSet,packagePath,packageName);
                    } else if (protocol.equals("jar")) {
                        //jar包
                        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                        if (jarURLConnection != null) {
                            JarFile jarFile = jarURLConnection.getJarFile();
                            if (jarFile != null) {
                                Enumeration<JarEntry> jarEntries = jarFile.entries();
                                while (jarEntries.hasMoreElements()) {
                                    JarEntry jarEntry = jarEntries.nextElement();
                                    String jarEntryName = jarEntry.getName();
                                    if (jarEntryName.endsWith(".class")) {
                                        String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/", ".");
                                        doAddClass(classSet,className);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
          LOGGER.error("get class set failure",e);
            throw  new RuntimeException(e);
        }
        return classSet;
    }

    public static  void  addClass(Set<Class<?>> classSet, String packagePath, String packageName){
        //列举包路径下对应的文件（仅接受.class字节码文件）
        File[] files = new File(packagePath).listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return (pathname.isFile()&&pathname.getName().endsWith(".class"))|| pathname.isDirectory();
            }
        });
        for(File file : files){
            String fileName = file.getName();
            if(file.isFile())
            {//文件格式
                String className = fileName.substring(0,fileName.lastIndexOf("."));
                //完整类名：全限定类名
                className = StringUtils.isNotEmpty(packageName)?packageName+"."+className:className;
                //加载类
                doAddClass(classSet,className);
            }
            else{//目录格式
                //子包路径
                String subPackagePath = fileName;
                subPackagePath = StringUtils.isNotEmpty(packagePath)?packagePath+"/"+subPackagePath:subPackagePath;
                //子包名称
                String subPackageName = fileName;
                subPackageName = StringUtils.isNotEmpty(packageName)?packageName+"."+subPackageName:subPackageName;
                addClass(classSet,subPackagePath,subPackageName);
            }
        }
    }

    public static void doAddClass(Set<Class<?>> classSet,String className)
    {
        Class<?> cls = loadClass(className,false);
        classSet.add(cls);
    }
}
