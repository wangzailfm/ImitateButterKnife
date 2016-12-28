package com.wangzai;

import com.google.auto.service.AutoService;
import com.wangzai.model.BindViewClass;
import com.wangzai.model.BindViewIdField;
import com.wangzai.model.OnClickMethod;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

@AutoService(Processor.class)
public class MyProcessor extends AbstractProcessor {
    /**
     * �ļ���صĸ�����
     */
    private Filer mFiler;
    /**
     * Ԫ����صĸ�����
     */
    private Elements mElementUtils;
    /**
     * ��־��صĸ�����
     */
    private Messager mMessager;
    /**
     * ������Ŀ��ע�⼯��
     */
    private Map<String, BindViewClass> mBindViewClassMap = new HashMap<>();

    /**
     * ע�⴦����Ҫ�����ע������,ֵΪ��ȫ�޶��������Ǵ����ڰ�����·������ȫ����
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        // ���ظ�ע�⴦����֧�ֵ�ע�⼯��
        types.add(BindViewId.class.getCanonicalName());
        return types;
    }

    /**
     * ָ��֧�ֵ� java �汾��ͨ������ SourceVersion.latestSupported()
     * @return java �汾
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * init��������Processor����ʱ��javac���ò�ִ�г�ʼ��������
     * @param processingEnvironment �ṩһϵ�е�ע�⴦���ߡ�
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        mFiler = processingEnvironment.getFiler();
        mElementUtils = processingEnvironment.getElementUtils();
        mMessager = processingEnvironment.getMessager();
    }

    /**
     * ע�⴦����Ҫִ��һ�λ��߶�Ρ�ÿ�ν���ǰ��Ҫ��գ����������������ã����Ҵ����˵�ǰҪ�����ע�����͡�
     * ���������������ɨ��ʹ���ע�⣬������Java���롣
     * @param annotations ��ǰҪ�����ע������
     * @param roundEnvironment ��������ṩ��ǰ������һ��ע�⴦���б�ע���ע��Դ�ļ�Ԫ�ء���������б���ע��Ԫ   �أ�
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        mBindViewClassMap.clear();
        try {
            getBindViewIdForRoundEnv(roundEnvironment);
            getOnClickForRoundEnv(roundEnvironment);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return true;
        }

        try {
            for (BindViewClass bindViewClass : mBindViewClassMap.values()) {
                bindViewClass.generateCode().writeTo(mFiler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * ��ȡRoundEnvironment�����@BindViewIdע��
     * @param roundEnvironment RoundEnvironment
     */
    private void getBindViewIdForRoundEnv(RoundEnvironment roundEnvironment) {
        for (Element element : roundEnvironment.getElementsAnnotatedWith(BindViewId.class)) {
            BindViewClass bindViewClass = getBindViewClass(element);
            // ��BindViewClass�������field
            bindViewClass.addBindViewIdField(new BindViewIdField(element));
        }
    }


    /**
     * ��ȡRoundEnvironment�����@OnClickע��
     * @param roundEnvironment RoundEnvironment
     */
    private void getOnClickForRoundEnv(RoundEnvironment roundEnvironment) {
        for (Element element : roundEnvironment.getElementsAnnotatedWith(OnClick.class)) {
            BindViewClass bindViewClass = getBindViewClass(element);
            // ��BindViewClass������ӷ���
            bindViewClass.addOnClickMethod(new OnClickMethod(element));
        }
    }

    /**
     * ��ȡBindViewClass
     * @param element
     * @return
     */
    private BindViewClass getBindViewClass(Element element) {
        // ��ȡ��ǰElement���ڵ�TypeElement
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        // ��ȡTypeElement����ȫ��
        String fullClassName = enclosingElement.getQualifiedName().toString();
        // �����map�д��ھ�ֱ���ã������ھ�new��������map��
        BindViewClass bindViewClass = mBindViewClassMap.get(fullClassName);
        if (bindViewClass == null) {
            bindViewClass = new BindViewClass(mElementUtils, enclosingElement);
            mBindViewClassMap.put(fullClassName, bindViewClass);
        }
        return bindViewClass;
    }
}
