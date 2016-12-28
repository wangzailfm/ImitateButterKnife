package com.wangzai.model;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import com.wangzai.TypeClassName;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by pc on 2016/12/22.
 * ��BindViewIdע��������
 */
public class BindViewClass {

    /**
     * ����
     */
    private TypeElement mTypeElement;

    /**
     * ����@BindViewIdע��ı���list
     */
    private List<BindViewIdField> mBindViewIdFields;

    /**
     * ����@OnClickע��ı���list
     */
    private List<OnClickMethod> mOnClickMethods;

    /**
     * Ԫ����صĸ�����
     */
    private Elements mElements;

    public BindViewClass(Elements elements, TypeElement typeElement) {
        mElements = elements;
        mTypeElement = typeElement;
        mBindViewIdFields = new ArrayList<>();
        mOnClickMethods = new ArrayList<>();
    }

    /**
     * ����java����
     * @return
     */
    public JavaFile generateCode() {
        // ��ȡ����
        String packageName = getPackageName(mTypeElement);
        // ��ȡ����
        String className = getClassName(mTypeElement, packageName);
        // ��ȡClassName
        ClassName bindClassName = ClassName.get(packageName, className);
        // ����unBind����
        MethodSpec.Builder unBinderMethod = MethodSpec.methodBuilder("unBind")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC);
        unBinderMethod.addStatement("$T target = this.target", bindClassName);
        unBinderMethod.beginControlFlow("if(target == null)");
        unBinderMethod.addStatement("throw new $T(\"Bindings already cleared.\")", IllegalStateException.class);
        unBinderMethod.endControlFlow();
        unBinderMethod.beginControlFlow("else");
        // ���췽��
        MethodSpec.Builder buildViewMethod = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeClassName.T, "target", Modifier.FINAL)
                .addParameter(TypeClassName.VIEW, "source");
        buildViewMethod.addStatement("this.target = target");
        // ����findViewById
        if (mBindViewIdFields.size() > 0) {
            for (BindViewIdField bindViewIdField : mBindViewIdFields) {
                buildViewMethod.addStatement("target.$N = ($T) $T.findRequiredViewAsType(source, $L, $S, $N)",
                        bindViewIdField.getFieldName(),
                        bindViewIdField.getFieldType(),
                        TypeClassName.UTILS,
                        bindViewIdField.getFieldResId(),
                        bindViewIdField.getFieldName().toString(),
                        bindViewIdField.getFieldClass() + ".class");
                // ����onClick
                if (mOnClickMethods.size() > 0) {
                    StringBuilder onclick = new StringBuilder();
                    for (OnClickMethod onClickMethod : mOnClickMethods) {
                        if (onClickMethod.getResIds().contains(bindViewIdField.getFieldResId())) {
                            onclick.append("target.$N.setOnClickListener(new $T() {");
                            onclick.append("public void onClick(View v) {");
                            for (OnClickMethod onClickMethods : mOnClickMethods) {
                                if (onClickMethods.getResIds().contains(bindViewIdField.getFieldResId())) {
                                    onclick.append("target.");
                                    onclick.append(onClickMethods.getOnClickMethodName().toString());
                                    onclick.append(" (v);");
                                }
                            }
                            onclick.append("}})");
                            buildViewMethod.addStatement(onclick.toString(),
                                    bindViewIdField.getFieldName(),
                                    TypeClassName.ONCLICK);
                            unBinderMethod.addStatement("target.$N.setOnClickListener(($T) null)",
                                    bindViewIdField.getFieldName(),
                                    TypeClassName.ONCLICK);
                            break;
                        }
                    }
                }
                unBinderMethod.addStatement("target.$N = null", bindViewIdField.getFieldName());
            }
        }
        unBinderMethod.addStatement("target = null");
        unBinderMethod.endControlFlow();
        // ������
        TypeSpec.Builder viewBinder = TypeSpec.classBuilder(String.format("%s_ViewBinder", bindClassName.simpleName()))
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(TypeClassName.UNBINDER)
                .addField(FieldSpec.builder(TypeClassName.T, "target", Modifier.PROTECTED).build())
                .addTypeVariable(TypeVariableName.get("T", bindClassName)) // ��ӷ���<T extends MainActivity>
                .addMethod(buildViewMethod.build())
                .addMethod(unBinderMethod.build());
        return JavaFile.builder(packageName, viewBinder.build()).build();
    }

    /**
     * ��ȡ����
     *
     * @return String
     */
    private String getPackageName(TypeElement typeElement) {
        return mElements.getPackageOf(typeElement).getQualifiedName().toString();
    }

    /**
     * ��ӵ�list
     *
     * @param field ����ע��ı���
     */
    public void addBindViewIdField(BindViewIdField field) {
        mBindViewIdFields.add(field);
    }

    /**
     * ��ӵ�list
     *
     * @param onClickMethod ����ע��ķ���
     */
    public void addOnClickMethod(OnClickMethod onClickMethod) {
        mOnClickMethods.add(onClickMethod);
    }

    /**
     * ����
     */
    private static String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
    }
}