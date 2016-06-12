package com.sunty.droidparticle;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by sunty on 16-6-4.
 */
public class ColorMatrixFilterHelper {
    static private Method sMethod;

    static {
        try {
            sMethod = ColorMatrixColorFilter.class.getMethod("setColorMatrix", ColorMatrix.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    static public void setFilterMatrix(ColorMatrixColorFilter filter, ColorMatrix colorMatrix) {
        try {
            sMethod.invoke(filter, colorMatrix);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
