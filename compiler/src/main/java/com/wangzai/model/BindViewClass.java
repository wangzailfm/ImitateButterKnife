package com.wangzai.model;

import com.wangzai.TypeClassName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by pc on 2016/12/22.
 * 被BindViewId注解的类对象
 */
public class BindViewClass {

    /**
     * 类名
     */
    private TypeElement mTypeElement;

    /**
     * 具有@BindViewId注解的变量list
     */
    private List<BindViewIdField> mBindViewIdFields;

    /**
     * 具有@OnClick注解的变量list
     */
    private List<OnClickMethod> mOnClickMethods;

    /**
     * 元素相关的辅助类
     */
    private Elements mElements;

    public BindViewClass(Elements elements, TypeElement typeElement) {
        mElements = elements;
        mTypeElement = typeElement;
        mBindViewIdFields = new ArrayList<>();
        mOnClickMethods = new ArrayList<>();
    }

    /**
     * 创建java代码
     * @return
     */
    public JavaFile generateCode() {
        // 获取包名
        String packageName = getPackageName(mTypeElement);
        // 获取类名
        String className = getClassName(mTypeElement, packageName);
        // 获取ClassName
        ClassName bindClassName = ClassName.get(packageName, className);
        // 创建unBind方法
        MethodSpec.Builder unBinderMethod = MethodSpec.methodBuilder("unBind")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC);
        unBinderMethod.addStatement("$T target = this.target", bindClassName);
        unBinderMethod.beginControlFlow("if(target == null)");
        unBinderMethod.addStatement("throw new $T(\"Bindings already cleared.\")", IllegalStateException.class);
        unBinderMethod.endControlFlow();
        unBinderMethod.beginControlFlow("else");
        // 构造方法
        MethodSpec.Builder buildViewMethod = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeClassName.T, "target", Modifier.FINAL)
                .addParameter(TypeClassName.VIEW, "source");
        buildViewMethod.addStatement("this.target = target");
        // 创建findViewById
        if (mBindViewIdFields.size() > 0) {
            for (BindViewIdField bindViewIdField : mBindViewIdFields) {
                buildViewMethod.addStatement("target.$N = ($T) $T.findRequiredViewAsType(source, $L, $S, $N)",
                        bindViewIdField.getFieldName(),
                        bindViewIdField.getFieldType(),
                        TypeClassName.UTILS,
                        bindViewIdField.getFieldResId(),
                        bindViewIdField.getFieldName().toString(),
                        bindViewIdField.getFieldClass() + ".class");
                // 创建onClick
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
        // 构造类
        TypeSpec.Builder viewBinder = TypeSpec.classBuilder(String.format("%s_ViewBinder", bindClassName.simpleName()))
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(TypeClassName.UNBINDER)
                .addField(FieldSpec.builder(TypeClassName.T, "target", Modifier.PROTECTED).build())
                .addTypeVariable(TypeVariableName.get("T", bindClassName)) // 添加泛型<T extends MainActivity>
                .addMethod(buildViewMethod.build())
                .addMethod(unBinderMethod.build());
        return JavaFile.builder(packageName, viewBinder.build()).build();
    }

    /**
     * 获取包名
     *
     * @return String
     */
    private String getPackageName(TypeElement typeElement) {
        return mElements.getPackageOf(typeElement).getQualifiedName().toString();
    }

    /**
     * 添加到list
     *
     * @param field 包含注解的变量
     */
    public void addBindViewIdField(BindViewIdField field) {
        mBindViewIdFields.add(field);
    }

    /**
     * 添加到list
     *
     * @param onClickMethod 包含注解的方法
     */
    public void addOnClickMethod(OnClickMethod onClickMethod) {
        mOnClickMethods.add(onClickMethod);
    }

    /**
     * 类名
     */
    private static String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
    }
}