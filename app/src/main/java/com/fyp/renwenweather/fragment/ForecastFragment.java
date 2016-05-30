package com.fyp.renwenweather.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fyp.renwenweather.R;
import com.fyp.renwenweather.datamanager.ConfigManager;
import com.fyp.renwenweather.datamanager.Utils;
import com.fyp.renwenweather.entity.Juhe7DaysWeather;
import com.fyp.renwenweather.entity.TotalInfo;

import org.xutils.x;

import java.util.List;

/**
 * Created by fyp on 2015/11/19.
 */
public class ForecastFragment extends SelfRefreshFragment {

    FrameLayout rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("ForecastFragment", "onCreateView");
        if (rootView == null) {
            rootView = (FrameLayout) inflater.inflate(R.layout.fragment_forcast, container, false);
        }
        x.view().inject(this, rootView);
        resetContent();
        return rootView;
    }

    public void updateWeather(TotalInfo totalInfo) {
        Log.i("ForecastFragment", "updateWeather");
        if (totalInfo == null || !totalInfo.isComplete()) {
            resetContent();
            return;
        }
        rootView.findViewById(R.id.text_noData).setVisibility(View.GONE);
        List<Juhe7DaysWeather.ResultEntity.FutureEntity> fe = totalInfo.sevenDaysWeather.getResult().getFuture();
        LinearLayout layout = (LinearLayout) (rootView.findViewById(R.id.forecast_list));
        for (int i = 0; i < 3; i++) {
            View view = layout.getChildAt(i);
            Juhe7DaysWeather.ResultEntity.FutureEntity e = fe.get(i + 1);
            ((TextView) view.findViewById(R.id.textDate)).setText(e.getWeek());
            ((TextView) view.findViewById(R.id.textTemperature)).setText(e.getTemperature());
            ((TextView) view.findViewById(R.id.textWind)).setText(e.getWind());
            ((TextView) view.findViewById(R.id.textWeather)).setText(e.getWeather());
            String fa = "p" + e.getWeather_id().getFa();
            int imageId = getResources().getIdentifier(fa, "drawable", "com.fyp.renwenweather");
            ((ImageView) view.findViewById(R.id.image_weather)).setImageResource(imageId);
        }
        rootView.findViewById(R.id.forecast_list).setVisibility(View.VISIBLE);
    }

    public void resetContent() {
        rootView.findViewById(R.id.text_noData).setVisibility(View.VISIBLE);
        LinearLayout layout = (LinearLayout) (rootView.findViewById(R.id.forecast_list));
        layout.setVisibility(View.GONE);
        for (int i = 0; i < 3; i++) {
            View view = layout.getChildAt(i);
            String date = "明天";
            switch (i) {
                case 1:
                    date = "后天";
                    break;
                case 2:
                    date = "大后天";
                    break;
            }
            ((TextView) view.findViewById(R.id.textDate)).setText(date);
            ((TextView) view.findViewById(R.id.textTemperature)).setText("温度:未知");
            ((TextView) view.findViewById(R.id.textWind)).setText("风力:未知");
            ((TextView) view.findViewById(R.id.textWeather)).setText("天气:未知");
            ((ImageView) view.findViewById(R.id.image_weather)).setImageResource(R.drawable.unknown);
        }
    }

    /**
     * 自行通过dataManager读取并更新数据
     */
    @Override
    public void refreshContent() {
        if (rootView == null) return;
        TotalInfo totalInfo = (TotalInfo) Utils.readObject(ConfigManager.getCurrentCityName());
        if (totalInfo != null && totalInfo.isComplete()) {
            updateWeather(totalInfo);
        } else {
            resetContent();
        }
    }
}
