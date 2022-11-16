package org.homework.dependency.injection;

import org.homework.annotation.Service;
import org.homework.annotation.StartPoint;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public final class DependencyInjection {

    private DependencyInjection() {
    }

    private static final Reflections reflections = new Reflections("org.homework");
    private static Map<String, Object> beanDictionary = new HashMap<>();


    private static void beanGrow(Set<Class<?>> services) {
        for (Class<?> classs : services) {
            if (!beanDictionary.containsKey(classs.getName())) {
                createBean(classs);
            }
        }
    }

    private static boolean isInterface(Class<?> classs) {
        Constructor<?>[] constructors = classs.getDeclaredConstructors();
        if (constructors.length == 0) {
            return true;
        }
        return false;
    }

    private static Constructor<?>[] getSubTypeConstructors(Class<?> classs) {
        Set<Class<?>> subtypes = reflections.getSubTypesOf((Class<Object>) classs);
        for (Class<?> subtype : subtypes) {
            if (subtype.isAnnotationPresent(Service.class)) {
                return subtype.getConstructors();
            }
        }
        return null;
    }

    private static Class<?> getSubTypeClass(Class<?> classs) {
        Set<Class<?>> subtypes = reflections.getSubTypesOf((Class<Object>) classs);
        for (Class<?> subtype : subtypes) {
            if (subtype.isAnnotationPresent(Service.class)) {
                return subtype;
            }
        }
        return null;
    }

    private static Object createBean(Class<?> classs) {

        Constructor<?>[] constructors = classs.getDeclaredConstructors();
        Object bean = null;

        if (isInterface(classs)) {
            getSubTypeClass(classs);
            constructors = getSubTypeConstructors(classs);
        }

        if (constructors[0].getParameterCount() == 0) {
            try {
                bean = constructors[0].newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }

        } else {
            Parameter[] constrParam = constructors[0].getParameters();
            Object[] constrParamBeans = new Object[constrParam.length];
            for (int i = 0; i < constrParam.length; i++) {
                Class<?> tempClass = constrParam[i].getType();
                if (isInterface(tempClass)) {
                    tempClass = getSubTypeClass(tempClass);
                }
                if (beanDictionary.containsKey(tempClass.getName())) {
                    bean = beanDictionary.get(tempClass.getName());
                } else {
                    bean = createBean(tempClass);
                }
                constrParamBeans[i] = bean;
            }
            try {
                bean = constructors[0].newInstance(constrParamBeans);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }

        }
        beanDictionary.put(classs.getName(), bean);
        return bean;
    }

    public static void start() {

        Set<Class<?>> services = reflections.getTypesAnnotatedWith(Service.class);

        beanGrow(services);

        for (Class classs : services) {
            Method[] meth = classs.getMethods();
            Object temp = beanDictionary.get(classs.getName());
            for (Method methh : meth) {
                if (methh.getAnnotation(StartPoint.class) != null) {
                    try {
                        methh.invoke(temp);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        }

    }

}
