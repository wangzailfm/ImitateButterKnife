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
 * ��OnClickע���ǵķ�����ģ����
 */
public class OnClickMethod {
    private String onClickClassName = OnClick.class.getSimpleName();

    /**
     * ע��@OnClick�ķ�����
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
        // �ж��Ƿ��Ƿ���
        if (element.getKind() != ElementKind.METHOD) {
            throw new IllegalArgumentException(String.format("Only method can be annotated with @%s", onClickClassName));
        }
        ExecutableElement methodElement = (ExecutableElement) element;
        // ��ȡ������
        onClickMethodName = methodElement.getSimpleName();
        mParamsList = methodElement.getParameters();
        // ��ȡע���ֵ
        mResIds = new ArrayList<>();
        for (int mResId : methodElement.getAnnotation(OnClick.class).value()) {
            mResIds.add(mResId);
        }
    }

    /**
     * ��ȡ������
     * @return
     */
    public Name getOnClickMethodName() {
        return onClickMethodName;
    }

    /**
     * ��ȡע���ֵ
     * @return
     */
    public List<Integer> getResIds() {
        return mResIds;
    }

    /**
     * ��ȡparams
     * @return
     */
    public List<? extends VariableElement> getParamsList() {
        return mParamsList;
    }
}
