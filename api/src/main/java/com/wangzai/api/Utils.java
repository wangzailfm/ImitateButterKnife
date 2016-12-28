package com.wangzai.api;

import android.support.annotation.IdRes;
import android.view.View;

/**
 * Created by pc on 2016/12/26.
 */
public class Utils {

    /**
     * 获取View
     * @param source
     * @param id
     * @param who
     * @return
     */
    public static View findRequiredView(View source, @IdRes int id, String who) {
        View view = source.findViewById(id);
        if (view != null) {
            return view;
        }
        String name = getResourceEntryName(source, id);
        throw new IllegalStateException("Required view '"
                + name
                + "' with ID "
                + id
                + " for "
                + who
                + " was not found. If this view is optional add '@Nullable' (fields) or '@Optional'"
                + " (methods) annotation.");
    }

    /**
     * 获取View
     * @param source
     * @param id
     * @param who
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> T findRequiredViewAsType(View source, @IdRes int id, String who,
                                               Class<T> cls) {
        View view = findRequiredView(source, id, who);
        return castView(view, id, who, cls);
    }

    /**
     * 根据id转换成目标View
     * @param view
     * @param id
     * @param who
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> T castView(View view, @IdRes int id, String who, Class<T> cls) {
        try {
            return cls.cast(view);
        } catch (ClassCastException e) {
            String name = getResourceEntryName(view, id);
            throw new IllegalStateException("View '"
                    + name
                    + "' with ID "
                    + id
                    + " for "
                    + who
                    + " was of the wrong type. See cause for more info.", e);
        }
    }

    /**
     * getResourceEntryName
     * @param view
     * @param id
     * @return
     */
    private static String getResourceEntryName(View view, @IdRes int id) {
        if (view.isInEditMode()) {
            return "<unavailable while editing>";
        }
        return view.getContext().getResources().getResourceEntryName(id);
    }

}
