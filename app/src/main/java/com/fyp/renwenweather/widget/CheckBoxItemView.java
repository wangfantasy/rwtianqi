package com.fyp.renwenweather.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fyp.renwenweather.R;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by fyp on 2016/4/5.
 */
public class CheckBoxItemView extends RelativeLayout {
    @ViewInject(R.id.text_title)
    TextView textTitle;
    @ViewInject(R.id.text_desc)
    TextView textDesc;
    @ViewInject(R.id.check_box)
    CheckBox checkBox;
    private String textOff = "";
    private String textOn = "";
    private String title = "";
    private CheckBoxClickedListener listener;

    public CheckBoxItemView(Context context) {
        super(context);
    }

    public CheckBoxItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckBoxItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.mCheckBoxItem, defStyleAttr, 0);
        this.title = a.getString(R.styleable.mCheckBoxItem_check_box_item_title);
        //Log.i("title",title);
        this.textOn = a.getString(R.styleable.mCheckBoxItem_check_box_on);
        //Log.i("textOn",textOn);
        this.textOff = a.getString(R.styleable.mCheckBoxItem_check_box_off);
        //Log.i("textOff", textOff);

        View.inflate(context, R.layout.check_box_item, this);
        a.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        x.view().inject(this);
        textTitle.setText(title);
        textDesc.setText(textOff);
    }


    @Event(type = CompoundButton.OnCheckedChangeListener.class, value = R.id.check_box)
    private void onCheckBoxClick(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            textDesc.setText(textOn);
        } else {
            textDesc.setText(textOff);
        }
        if (listener != null) {
            listener.onCheckBoxClicked(CheckBoxItemView.this);
        }
    }

    public boolean isChecked() {
        return checkBox.isChecked();
    }

    //设置这个控件checkBox的状态并通知监听器
    public void setCheckBox(boolean checked) {
        checkBox.setChecked(checked);
        if (checkBox.isChecked()) {
            textDesc.setText(textOn);
        } else {
            textDesc.setText(textOff);
        }
        if (listener != null) {
            listener.onCheckBoxClicked(CheckBoxItemView.this);
        }
    }

    public void setOnCheckboxClickedListener(CheckBoxClickedListener listener) {
        this.listener = listener;
    }

    public interface CheckBoxClickedListener {
        void onCheckBoxClicked(View view);
    }
}
