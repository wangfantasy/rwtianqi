package com.fyp.renwenweather.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

/**
 * Created by fyp on 2016/4/2.
 */
public class ThreeHourListView extends ListView {
    private boolean a;

    public ThreeHourListView(Context context) {
        super(context);
    }

    public ThreeHourListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ThreeHourListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void animate(View paramView, int location) {
        paramView.animate().cancel();
        //paramView.setTranslationY(300);
        paramView.setTranslationX(300);
        paramView.setAlpha(0.0F);
        paramView.animate().alpha(1).translationX(0).setDuration(500).setStartDelay(location * 200);
    }

    public boolean dispatchTouchEvent(MotionEvent paramMotionEvent) {
        return (!this.a) || (super.dispatchTouchEvent(paramMotionEvent));
    }

//    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
//        super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
//        if (paramBoolean)
//            for (int i = 0; i < getChildCount(); i++) {
//                animate(getChildAt(i), i);
//                if (i != -1 + getChildCount())
//                    continue;
//                getHandler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        ThreeHourListView.this
//                    }
//                }, i * 100);
//            }
//    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        for (int i = 0; i < getChildCount(); i++) {
            animate(getChildAt(i), i);
        }
    }
}
