package com.fyp.renwenweather.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fyp.renwenweather.R;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by fyp on 2016/4/5.
 */
public class SimpleSelectItemView extends RelativeLayout {
    @ViewInject(R.id.text_title)
    private TextView textTitle;
    @ViewInject(R.id.text_desc)
    private TextView textDesc;
    private String title;
    private String desc;

    public SimpleSelectItemView(Context paramContext) {
        super(paramContext);
    }

    public SimpleSelectItemView(Context paramContext, AttributeSet paramAttributeSet) {
        this(paramContext, paramAttributeSet, 0);
    }

    public SimpleSelectItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.simple_select_item, this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.mSimpleSelectItem, defStyleAttr, 0);
        this.title = typedArray.getString(R.styleable.mSimpleSelectItem_simple_select_title);
        this.desc =typedArray.getString(R.styleable.mSimpleSelectItem_simple_select_desc);
        typedArray.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        x.view().inject(this);
        this.textTitle.setText(title);
        this.textDesc.setText(desc);
    }

    public void setDesc(String paramString) {
        this.textDesc.setText(paramString);
    }
}
