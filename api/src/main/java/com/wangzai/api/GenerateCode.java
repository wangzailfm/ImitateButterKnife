package com.wangzai.api;

import android.app.Activity;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.view.View;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by pc on 2016/12/26.
 */
public class GenerateCode {
    static final Map<Class<?>, Constructor<? extends UnBinder>> BINDINGS = new LinkedHashMap<>();

    private GenerateCode() {
        throw new AssertionError("No instances.");
    }

    /**
     * activity
     * @param target 目标
     * @return
     */
    @NonNull @UiThread
    public static UnBinder bind(@NonNull Activity target) {
        View sourceView = target.getWindow().getDecorView();
        return createBinding(target, sourceView);
    }

    /**
     * view
     * @param target 目标类
     * @return
     */
    @NonNull @UiThread
    public static UnBinder bind(@NonNull View target) {
        return bind(target, target);
    }

    /**
     * 将view作为rootView
     * @param target 绑定的目标类
     * @param source 将查找ID的view。
     * @return
     */
    @NonNull @UiThread
    public static UnBinder bind(@NonNull Object target, @NonNull View source) {
        return createBinding(target, source);
    }

    /**
     * 创建bind
     * @param target 目标
     * @param source 源
     * @return
     */
    private static UnBinder createBinding(@NonNull Object target, @NonNull View source) {
        Class<?> targetClass = target.getClass();
        Constructor<? extends UnBinder> constructor = findBindingConstructorForClass(targetClass);

        if (constructor == null) {
            return UnBinder.EMPTY;
        }

        //noinspection TryWithIdenticalCatches Resolves to API 19+ only type.
        try {
            return constructor.newInstance(target, source);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to invoke " + constructor, e);
        } catch (InstantiationException e) {
            throw new RuntimeException("Unable to invoke " + constructor, e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new RuntimeException("Unable to create binding instance.", cause);
        }
    }

    /**
     * 获取对应的Constructor，用于创建构造函数
     * @param cls
     * @return
     */
    @Nullable @CheckResult @UiThread
    private static Constructor<? extends UnBinder> findBindingConstructorForClass(Class<?> cls) {
        Constructor<? extends UnBinder> bindingCtor = BINDINGS.get(cls);
        if (bindingCtor != null) {
            return bindingCtor;
        }
        String clsName = cls.getName();
        if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
            return null;
        }
        try {
            Class<?> bindingClass = Class.forName(clsName + "_ViewBinder");
            //noinspection unchecked
            bindingCtor = (Constructor<? extends UnBinder>) bindingClass.getConstructor(cls, View.class);
        } catch (ClassNotFoundException e) {
            bindingCtor = findBindingConstructorForClass(cls.getSuperclass());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find binding constructor for " + clsName, e);
        }
        BINDINGS.put(cls, bindingCtor);
        return bindingCtor;
    }

}
