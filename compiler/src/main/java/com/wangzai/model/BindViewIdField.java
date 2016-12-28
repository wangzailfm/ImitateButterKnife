package com.wangzai.model;

import com.wangzai.BindViewId;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

/**
 * Created by pc on 2016/12/22.
 * ��BindViewIdע���ǵ��ֶε�ģ����
 */
public class BindViewIdField {
    private String bindViewIdFieldClassName = BindViewId.class.getSimpleName();
    /**
     * PackageElement        ��
     * ExecutableElement    ���������췽��
     * VariableElement       ��Ա������enum�������������췽���������ֲ��������쳣������
     * TypeElement           �ࡢ�ӿ�
     * TypeParameterElement  �ڷ������췽�����ࡢ�ӿڴ�����ķ��Ͳ�����
     */
    private VariableElement mVariableElement;
    /**
     * id
     */
    private int mResId;

    public BindViewIdField(Element element) throws IllegalArgumentException {
        // �ж��Ƿ��ǳ�Ա����
        if (element.getKind() != ElementKind.FIELD) {
            throw new IllegalArgumentException(String.format("Only field can be annotated with @%s", bindViewIdFieldClassName));
        }
        mVariableElement = (VariableElement) element;
        // ��ȡע���idֵ
        BindViewId bindViewId = mVariableElement.getAnnotation(BindViewId.class);
        mResId = bindViewId.value();
        if (mResId < 0) {
            throw new IllegalArgumentException(String.format("value() in %s for field %s is not valid",
                    bindViewIdFieldClassName, mVariableElement.getSimpleName()));
        }
    }

    /**
     * ��ȡ������
     *
     * @return Name
     */
    public Name getFieldName() {
        return mVariableElement.getSimpleName();
    }

    /**
     * ��ȡid
     *
     * @return int
     */
    public int getFieldResId() {
        return mResId;
    }

    /**
     * ��ȡ��������
     *
     * @return TypeMirror
     */
    public TypeMirror getFieldType() {
        return mVariableElement.asType();
    }

    /**
     * ��ȡ�������ͣ���TextView
     * @return
     */
    public String getFieldClass() {
        String className = getFieldType().toString();
        return className.substring(className.lastIndexOf(".") + 1);
    }
}
