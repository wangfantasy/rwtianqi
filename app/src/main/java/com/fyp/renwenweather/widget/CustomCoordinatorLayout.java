package com.fyp.renwenweather.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by fyp on 2016/4/9.
 */
public class CustomCoordinatorLayout extends CoordinatorLayout {


    public CustomCoordinatorLayout(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        setStatusBarBackgroundColor(Color.argb(0,0,0,0));
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }
}
