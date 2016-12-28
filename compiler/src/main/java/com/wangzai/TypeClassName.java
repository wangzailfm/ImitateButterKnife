package com.wangzai;

import com.squareup.javapoet.ClassName;

/**
 * Created by pc on 2016/12/28.
 */
public class TypeClassName {
    public static final ClassName VIEW = ClassName.get("android.view", "View");
    public static final ClassName UNBINDER = ClassName.get("com.wangzai.api", "UnBinder");
    public static final ClassName UTILS = ClassName.get("com.wangzai.api", "Utils");
    public static final ClassName T = ClassName.get("", "T");
    public static final ClassName ONCLICK = ClassName.get("android.view", "View", "OnClickListener");
}
