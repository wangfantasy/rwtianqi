package com.fyp.renwenweather.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fyp.renwenweather.R;
import com.fyp.renwenweather.datamanager.ConfigManager;
import com.fyp.renwenweather.datamanager.Utils;
import com.fyp.renwenweather.entity.Juhe3HourWeather;
import com.fyp.renwenweather.entity.TotalInfo;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Calendar;
import java.util.List;

public class ThreeHourWeatherActivity extends AppCompatActivity {
    @ViewInject(R.id.three_hour_listView)
    ListView mList;
    @ViewInject(R.id.text_noData)
    TextView noData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three_hour_weather);
        x.view().inject(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle(ConfigManager.getCurrentCityName() + "3小时天气");
        mList.setAdapter(new BaseAdapter() {
            public long delay;
            TotalInfo totalInfo;

            @Override
            public int getCount() {
                totalInfo = (TotalInfo) Utils.readObject(ConfigManager.getCurrentCityName());
                if (totalInfo == null || !totalInfo.isComplete()) {
                    noData.setVisibility(View.VISIBLE);
                    return 0;
                } else {
                    //TODO 在当前时间之前的天气不显示
                    noData.setVisibility(View.GONE);
                    //上次更新时间
                    //14:25
                    String lastUpdateTime = totalInfo.sevenDaysWeather.getResult().getSk().getTime();
                    //上次更新日期
                    //2014年03月21日
                    String lastUpdateDate = totalInfo.sevenDaysWeather.getResult().getToday().getDate_y();
                    //日期
                    // 20140530080000
                    String sfDate = totalInfo.threeHourWeather.getResult().get(0).getSfdate();

                    //不显示晚于最后一次更新的天气
                    //这里是第一项的时间
                    Calendar firstItem = Calendar.getInstance();
                    String hour = sfDate.substring(8, 10);
                    String date = sfDate.substring(6, 8);
                    String month = sfDate.substring(4, 6);
                    String year = sfDate.substring(0, 4);
                    firstItem.set(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(date), Integer.valueOf(hour), 0);
                    //这里是最后一次发布的时间
                    Calendar lastRelease = Calendar.getInstance();
                    String minute1 = lastUpdateTime.substring(3, 5);
                    String hour1 = lastUpdateTime.substring(0, 2);
                    String date1 = lastUpdateDate.substring(8, 10);
                    String month1 = lastUpdateDate.substring(5, 7);
                    String year1 = lastUpdateDate.substring(0, 4);
                    lastRelease.set(Integer.valueOf(year1), Integer.valueOf(month1), Integer.valueOf(date1), Integer.valueOf(hour1), Integer.valueOf(minute1));
                    //最新更新时间
                    long f = firstItem.getTimeInMillis();
                    //第一项三小时天气
                    long l = lastRelease.getTimeInMillis();
                    this.delay = (l - f) / 10800000;
                    Log.i(year + month + date + hour + "00", year1 + month1 + date1 + hour1 + minute1);
                    return (int)(totalInfo.threeHourWeather.getResult().size() - delay) ;
                }
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(ThreeHourWeatherActivity.this).inflate(R.layout.item_hour_forecast, null);
                }
                List<Juhe3HourWeather.ResultEntity> threeHour = totalInfo.threeHourWeather.getResult();
                StringBuffer sb = new StringBuffer(threeHour.get((int) (position + delay)).getDate());
                sb.replace(0, 4, "").insert(2, '月').append('日').append(threeHour.get((int) (position + delay)).getSh()).append(":00");
                ((TextView) convertView.findViewById(R.id.text_hour)).setText(sb);
                ((TextView) convertView.findViewById(R.id.text_type)).setText(threeHour.get((int) (position + delay)).getWeather());
                ((TextView) convertView.findViewById(R.id.text_temp)).setText(threeHour.get((int) (position + delay)).getTemp1() + "℃");
                return convertView;
            }
        });
    }
}
