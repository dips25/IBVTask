package com.image.ibvtask;

import android.graphics.drawable.GradientDrawable;

import androidx.annotation.NonNull;

public class MyGradient extends GradientDrawable implements Cloneable {

    @NonNull
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
