package com.wangzai.model;

import com.wangzai.OnClick;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;

/**
 * Created by pc on 2016/12/22.
 * 被OnClick注解标记的方法的模型类
 */
public class OnClickMethod {
    private String onClickClassName = OnClick.class.getSimpleName();

    /**
     * 注解@OnClick的方法名
     */
    private Name onClickMethodName;
    /**
     * id
     */
    private List<Integer> mResIds;

    /**
     * params
     */
    private List<? extends VariableElement> mParamsList = new ArrayList<>();

    public OnClickMethod(Element element) throws IllegalArgumentException {
        // 判断是否是方法
        if (element.getKind() != ElementKind.METHOD) {
            throw new IllegalArgumentException(String.format("Only method can be annotated with @%s", onClickClassName));
        }
        ExecutableElement methodElement = (ExecutableElement) element;
        // 获取方法名
        onClickMethodName = methodElement.getSimpleName();
        mParamsList = methodElement.getParameters();
        // 获取注解的值
        mResIds = new ArrayList<>();
        for (int mResId : methodElement.getAnnotation(OnClick.class).value()) {
            mResIds.add(mResId);
        }
    }

    /**
     * 获取方法名
     * @return
     */
    public Name getOnClickMethodName() {
        return onClickMethodName;
    }

    /**
     * 获取注解的值
     * @return
     */
    public List<Integer> getResIds() {
        return mResIds;
    }

    /**
     * 获取params
     * @return
     */
    public List<? extends VariableElement> getParamsList() {
        return mParamsList;
    }
}
