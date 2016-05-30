package com.fyp.renwenweather.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.fyp.renwenweather.R;
import com.fyp.renwenweather.datamanager.ConfigManager;
import com.fyp.renwenweather.datamanager.Utils;
import com.fyp.renwenweather.entity.Juhe7DaysWeather;
import com.fyp.renwenweather.entity.TotalInfo;
import com.fyp.renwenweather.service.WeatherLoaderService;

import org.xutils.x;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in WeatherWidgetConfigureActivity WeatherWidgetConfigureActivity}
 */
public class WeatherWidget extends AppWidgetProvider {
    /**
     * 更新小部件内容
     **/
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        Log.i("Widget", "updateAppWidget");
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
        views.setTextViewText(R.id.textCityName, ConfigManager.getCurrentCityName());
        views.setTextViewText(R.id.textTime, Utils.getCurrentHourAndMinute());
        TotalInfo totalInfo = (TotalInfo) Utils.readObject(ConfigManager.getCurrentCityName());
        if (totalInfo != null && totalInfo.isComplete()) {
            Juhe7DaysWeather.ResultEntity.TodayEntity today = totalInfo.sevenDaysWeather.getResult().getToday();
            views.setTextViewText(R.id.textWeather, today.getWeather());
            String fa = "p" + totalInfo.sevenDaysWeather.getResult().getToday().getWeather_id().getFa();
            views.setImageViewResource(R.id.image_weather, x.app().getResources().getIdentifier(fa, "drawable", "com.fyp.renwenweather"));
            views.setTextViewText(R.id.textTemperature, today.getTemperature());
            if(totalInfo.isFresh(ConfigManager.getUpdateFrequency()*3600)){
                views.setViewVisibility(R.id.textWarning, View.GONE);
            }else {
                views.setViewVisibility(R.id.textWarning, View.VISIBLE);
            }
        } else {
            views = new RemoteViews(context.getPackageName(), R.layout.weather_widget_if_null);
        }
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        //保证后台服务存活
        Intent updateWeather=new Intent(x.app(), WeatherLoaderService.class);
        updateWeather.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        x.app().startService(updateWeather);

        Log.i("WeatherWidget", "onReceive " + intent.getAction());
        AppWidgetManager awm = AppWidgetManager.getInstance(context);
        ComponentName componentName = new ComponentName(context, this.getClass());
        int[] ids = awm.getAppWidgetIds(componentName);
        onUpdate(context, awm, ids);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        Log.i("Widget", "onUpdate");
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.i("Widget", "onDeleted");
        // When the user deletes the widget, delete the preference associated with it.
//        for (int appWidgetId : appWidgetIds) {
//            WeatherWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
//        }
    }

    @Override
    public void onEnabled(Context context) {
        Log.i("Widget", "onEnabled");
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {

        Log.i("Widget", "onDisabled");
        // Enter relevant functionality for when the last widget is disabled
    }
}

