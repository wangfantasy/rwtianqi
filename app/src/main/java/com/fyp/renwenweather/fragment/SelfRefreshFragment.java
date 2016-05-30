package com.fyp.renwenweather.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by fyp on 2016/3/27.
 */
public abstract class SelfRefreshFragment extends Fragment {
    /**
     * 自行通过dataManager读取并更新数据
     */
    public abstract void refreshContent();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(this.getClass().getName() + "", "onAttach");
    }

    /**
     * 显示为没有数据
     */
    public abstract void resetContent();
}
