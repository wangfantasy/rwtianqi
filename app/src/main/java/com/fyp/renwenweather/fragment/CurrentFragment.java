package com.fyp.renwenweather.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fyp.renwenweather.R;
import com.fyp.renwenweather.datamanager.ConfigManager;
import com.fyp.renwenweather.datamanager.Utils;
import com.fyp.renwenweather.entity.TotalInfo;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * A placeholder fragment containing a simple view.
 */
public class CurrentFragment extends SelfRefreshFragment {
    @ViewInject(R.id.current_air)
    View airStatus;
    @ViewInject(R.id.current_temperature)
    View temperature;
    @ViewInject(R.id.current_humidity)
    View humidity;
    @ViewInject(R.id.current_wind)
    View wind;
    @ViewInject(R.id.current_all)
    View currentAll;
    @ViewInject(R.id.text_noRealTimeData)
    View textNoData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("CurrentFragment", "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_current, container, false);
        //View注入
        x.view().inject(this, rootView);
        refreshContent();
        return rootView;
    }


    /**
     * 自行通过dataManager读取并更新数据
     */
    @Override
    public void refreshContent() {
        if (currentAll == null) {
            return;
        }
        resetContent();
        TotalInfo totalInfo = (TotalInfo) Utils.readObject(ConfigManager.getCurrentCityName());
        if (totalInfo != null && totalInfo.isComplete()) {
            currentAll.setVisibility(View.VISIBLE);
            textNoData.setVisibility(View.INVISIBLE);
            ((TextView) airStatus.findViewById(R.id.tv_value)).setText(totalInfo.airStatus.getResult().get(0).getPM25());
            ((TextView) temperature.findViewById(R.id.tv_value)).setText(totalInfo.sevenDaysWeather.getResult().getSk().getTemp() + "℃");
            ((TextView) humidity.findViewById(R.id.tv_value)).setText(totalInfo.sevenDaysWeather.getResult().getSk().getHumidity());
            ((TextView) wind.findViewById(R.id.tv_leftValue)).setText(totalInfo.sevenDaysWeather.getResult().getSk().getWind_direction());
            ((TextView) wind.findViewById(R.id.tv_rightValue)).setText(totalInfo.sevenDaysWeather.getResult().getSk().getWind_strength());
        }
    }

    /**
     * 设置成没有内容
     */
    public void resetContent() {
        ((ImageView) airStatus.findViewById(R.id.image_subject)).setImageResource(R.drawable.ic_pm25);
        ((ImageView) wind.findViewById(R.id.image_subject)).setImageResource(R.drawable.ic_wind);
        ((ImageView) temperature.findViewById(R.id.image_subject)).setImageResource(R.drawable.ic_temperature);
        ((ImageView) humidity.findViewById(R.id.image_subject)).setImageResource(R.drawable.ic_humidity);
        currentAll.setVisibility(View.INVISIBLE);
        textNoData.setVisibility(View.VISIBLE);
        ((TextView) airStatus.findViewById(R.id.tv_title)).setText("PM2.5");
        ((TextView) temperature.findViewById(R.id.tv_title)).setText("温度");
        ((TextView) humidity.findViewById(R.id.tv_title)).setText("湿度");
        ((TextView) wind.findViewById(R.id.tv_leftTitle)).setText("风向");
        ((TextView) wind.findViewById(R.id.tv_rightTitle)).setText("风力");
    }
}