package com.fyp.renwenweather.service;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import com.fyp.renwenweather.Constants;
import com.fyp.renwenweather.R;
import com.fyp.renwenweather.activity.MainActivity;
import com.fyp.renwenweather.datamanager.ConfigManager;
import com.fyp.renwenweather.datamanager.Utils;
import com.fyp.renwenweather.datamanager.WeatherDbManager;
import com.fyp.renwenweather.entity.HourAndMinute;
import com.fyp.renwenweather.entity.Juhe3HourWeather;
import com.fyp.renwenweather.entity.Juhe7DaysWeather;
import com.fyp.renwenweather.entity.JuheAirStatus;
import com.fyp.renwenweather.entity.TotalInfo;
import com.google.gson.Gson;
import com.thinkland.sdk.android.DataCallBack;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.Parameters;

import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 后台加载，缓存天气数据，定时执行
 */
public class WeatherLoaderService extends Service {

    private NotificationManager notificationManager;
    private TotalInfo totalInfo;
    private Notification stayNotification = new Notification(R.drawable.ic_launcher, "人文天气", System.currentTimeMillis());
    BroadcastReceiver mTickReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("TICK_RECEIVER", intent.getAction());
            //检查是否开启了自动更新，开启了才去刷新
            if (ConfigManager.getAutoUpdateWeatherIsEnabled()) {
                updateWeatherIfNecessary();
            }
            updateWidget();
            showAutoCancelNotificationIfNecessary();
        }
    };
    BroadcastReceiver mConfigReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String option = intent.getStringExtra(Constants.EXTRA_CONFIG_OPTION);
            //Utils.showToast("收到配置发生变化的广播 option=" + option);
            /**城市名称发生变化*/
            if (ConfigManager.CURRENT_CITY_NAME.equals(option)) {
                updateWeatherIfNecessary();
            }
            /**常驻通知栏发生变化*/
            if (ConfigManager.STAY_IN_NOTIFICATION.equals(option)) {
                refreshStayInNotification();
            }
            /**通知颜色*/
            if (ConfigManager.NOTIFICATION_TEXT_COLOR.equals(option)) {
                refreshStayInNotification();
            }
        }
    };
    BroadcastReceiver mMobileStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //监听手机状态 比如网络连上了
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                updateWeatherIfNecessary();
            }
        }
    };

    /**
     * 修改通知栏字体颜色。配置发生改变时被广播接收器调用
     **/


    /**
     * 检查配置决定立即发起网络请求或者仅仅读取缓存
     */
    private void updateWeatherIfNecessary() {
        totalInfo = (TotalInfo) Utils.readObject(ConfigManager.getCurrentCityName());
        //如果没有缓存，直接发起网络请求
        if (totalInfo == null) {
            updateWeatherNow();
        } else {
            //有缓存
            //计算上次更新应该出现的时间点，如果比缓存晚，则发起网络请求
            //例如，间隔是3小时，缓存是8点的，当前时间是8点半，那么上次更新应该出现在6点
            //缓存比上次实际更新要晚，因此不发起网络请求
            //超过一天就直接更新
            int frequency = ConfigManager.getUpdateFrequency();
            if (new Date().getTime() - totalInfo.lastUpdate.getTime() > frequency * 60 * 60 * 1000) {
                updateWeatherNow();
            } else {
                onWeatherUpdated(totalInfo, new UpdateHelper(true, true, true));
            }
        }
    }

    //注册监听器，城市名称或者常驻通知栏发生改变时有动作
    @Override
    public void onCreate() {
        super.onCreate();
        //监听配置发生变化
        registerReceiver(mConfigReceiver, Utils.getIntentFilter(Constants.ACTION_CONFIG_CHANGED));
        //监听时间变化
        registerReceiver(mTickReceiver, Utils.getIntentFilter(Intent.ACTION_TIME_TICK, Intent.ACTION_SCREEN_ON));
        //监听手机状态变化
        registerReceiver(mMobileStateReceiver, Utils.getIntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        //初始化notification
        notificationManager = (NotificationManager) x.app().getSystemService(Application.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        //点击启动MainActivity
        stayNotification.contentIntent = pIntent;
        stayNotification.flags = Notification.FLAG_NO_CLEAR;
        Log.i("WLS", "ServiceCreated");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("WLS", "ServiceDestroyed");
        unregisterReceiver(mConfigReceiver);
        unregisterReceiver(mTickReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 立即更新某城市天气并通知注册过的监听器
     * 直接发起网络请求
     * 请求之前会把三个flag置为false
     * 由三个子方法的回调修改flag并onWeatherUpdated
     * 不管请求是否成功都会把三个flag置为true,但是如果失败了则失败项会为null
     */
    //TODO 由于网络请求需要时间，而且可能失败，因此最好使用监听器模式在天气加载完后通知监听器
    private void updateWeatherNow() {
        //一次只处理一个请求
        totalInfo = new TotalInfo();
        UpdateHelper helper=new UpdateHelper(false,false,false);
        Log.i("WLS", "通过网络请求天气");
        Parameters params = new Parameters();
        params.add("cityname", ConfigManager.getCurrentCityName());
        params.add("city", ConfigManager.getCurrentCityName());
        params.add("key", "c150c184b8b46eb3a17657ca05ad15dd");
        update7DaysWeather(params,totalInfo,helper);
        update3HourWeather(params,totalInfo,helper);
        updateAirStatus(params,totalInfo,helper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    private synchronized void updateAirStatus(Parameters params, final TotalInfo totalInfo, final UpdateHelper helper) {
        JuheData.executeWithAPI(x.app(), 33, "http://web.juhe.cn:8080/environment/air/pm", JuheData.GET, params, new DataCallBack() {
            @Override
            public void onSuccess(int i, String s) {
                s = s.replace("PM2.5", "PM25");
                Log.i("空气质量", s);
                Gson gson = new Gson();
                totalInfo.airStatus = gson.fromJson(s, JuheAirStatus.class);
            }

            @Override
            public void onFinish() {
                helper.airStatusUpdated=true;
                onWeatherUpdated(totalInfo,helper);
            }

            @Override
            public void onFailure(int i, String s, Throwable throwable) {
            }
        });
    }

    private void update7DaysWeather(Parameters params, final TotalInfo totalInfo, final UpdateHelper helper) {
        JuheData.executeWithAPI(x.app(), 39, "http://v.juhe.cn/weather/index", JuheData.GET, params, new DataCallBack() {
            @Override
            public void onSuccess(int i, String str) {
                Log.i("hh", str);
                Gson gson = new Gson();
                str = str.replaceAll("\\\"day_[0-9]{8}\\\":", "");
                str = str.replace("\"future\":{", "\"future\":[");
                str = str.replace("}}}", "}]}");
                Log.i("hh", str);
                totalInfo.sevenDaysWeather = gson.fromJson(str, Juhe7DaysWeather.class);
            }

            @Override
            public void onFinish() {
                helper.sevenDaysUpdated=true;
                onWeatherUpdated(totalInfo,helper);
            }

            @Override
            public void onFailure(int i, String s, Throwable throwable) {
            }
        });
    }

    private void update3HourWeather(Parameters params, final TotalInfo totalInfo, final UpdateHelper helper) {
        JuheData.executeWithAPI(x.app(), 39, "http://v.juhe.cn/weather/forecast3h", JuheData.GET, params, new DataCallBack() {
            @Override
            public void onSuccess(int i, String str) {
                //Log.i("", str);
                Gson gson = new Gson();
                totalInfo.threeHourWeather = gson.fromJson(str, Juhe3HourWeather.class);
            }

            @Override
            public void onFinish() {
                helper.threeHourUpdated=true;
                onWeatherUpdated(totalInfo,helper);
            }

            @Override
            public void onFailure(int i, String s, Throwable throwable) {

            }
        });
    }

    class UpdateHelper {
        boolean threeHourUpdated;
        boolean sevenDaysUpdated;
        boolean airStatusUpdated;

        public UpdateHelper(boolean threeHourUpdated, boolean sevenDaysUpdated, boolean airStatusUpdated) {
            this.threeHourUpdated = threeHourUpdated;
            this.sevenDaysUpdated = sevenDaysUpdated;
            this.airStatusUpdated = airStatusUpdated;
        }

        public boolean isFinished() {
            return threeHourUpdated && sevenDaysUpdated && airStatusUpdated;
        }
    }

    /**
     * 通知所有监听器并重置flag
     */
    private synchronized void onWeatherUpdated(TotalInfo totalInfo, UpdateHelper helper) {
        //必须在三次请求都完成之后才往下走，否则返回
        if (!helper.isFinished()) {
            return;
        }
        //检查信息来自网络还是缓存（来自网络则为true）
        //用来记录有没有刷新天气缓存
        boolean contentChanged;
        //可能为空
        if (totalInfo == null) {
            totalInfo = new TotalInfo();
        }
        if (totalInfo.lastUpdate == null) {
            totalInfo.lastUpdate = new Date();
        }
        /* 天气信息不完整，从缓存加载 */
        if (!totalInfo.isComplete()) {
            contentChanged = false;
            Utils.showToast("天气更新失败,请检查网络连接");
            totalInfo = (TotalInfo) Utils.readObject(ConfigManager.getCurrentCityName());
        } else {
            contentChanged = true;
            Utils.saveObject(ConfigManager.getCurrentCityName(), totalInfo);
        }
        Intent i = new Intent();
        i.setAction(Constants.ACTION_WEATHER_UPDATED);
        i.putExtra(Constants.EXTRA_TOTALINFO, totalInfo);
        //并不是每次都需要刷新内容
        i.putExtra(Constants.EXTRA_CACHE_REFRESHED, contentChanged);
        sendBroadcast(i);
        if (contentChanged) {
            //缓存刷新了，重新设置通知内容
            WeatherLoaderService.this.refreshStayInNotification();
        }
    }

    /**
     * 刷新常驻通知栏的消息,会在配置发生改变以及天气更新时被调用
     * 例如常驻通知栏选项被开启/关闭时
     */
    private void refreshStayInNotification() {
        Log.i("WLS", "getStayInNotification:" + ConfigManager.getStayInNotification());
        if (ConfigManager.getStayInNotification()) {
            //字体颜色
            int notificationTextColor = ConfigManager.getNotificationTextColor();

            totalInfo = (TotalInfo) Utils.readObject(ConfigManager.getCurrentCityName());

            if (totalInfo != null && totalInfo.isComplete()) {
                //TODO 设置通知栏消息内容
                Juhe7DaysWeather.ResultEntity.TodayEntity today = totalInfo.sevenDaysWeather.getResult().getToday();
                RemoteViews remoteViewsStay = new RemoteViews(x.app().getPackageName(), R.layout.weather_notification);
                //字体颜色设置
                remoteViewsStay.setInt(R.id.text_pm25, "setTextColor", notificationTextColor);
                remoteViewsStay.setInt(R.id.text_weather_and_temperature, "setTextColor", notificationTextColor);
                remoteViewsStay.setInt(R.id.text_city_name, "setTextColor", notificationTextColor);
                remoteViewsStay.setInt(R.id.text_release_time, "setTextColor", notificationTextColor);

                //内容设置
                remoteViewsStay.setTextViewText(R.id.text_weather_and_temperature, today.getWeather() + " " + today.getTemperature());
                remoteViewsStay.setTextViewText(R.id.text_city_name, ConfigManager.getCurrentCityName());
                remoteViewsStay.setTextViewText(R.id.text_pm25, "PM2.5:" + totalInfo.airStatus.getResult().get(0).getPM25());
                remoteViewsStay.setTextViewText(R.id.text_release_time, totalInfo.sevenDaysWeather.getResult().getSk().getTime() + "发布");
                Log.i("WLS", "stayNotification.contentView = remoteViewsStay");
                stayNotification.contentView = remoteViewsStay;
            } else {
                Log.i("WLS", "stayNotification.contentView = remoteViewsFailed");
                RemoteViews remoteViewsFailed = new RemoteViews(x.app().getPackageName(), R.layout.weather_notification_failed);

                remoteViewsFailed.setInt(R.id.text_notification_load_fail, "setTextColor", notificationTextColor);
                stayNotification.contentView = remoteViewsFailed;
            }
            //Utils.showToast("推送常驻通知");
            notificationManager.notify(Constants.STAY_NOTIFICATION_ID, stayNotification);
        } else {
            //Utils.showToast("取消常驻通知");
            notificationManager.cancel(Constants.STAY_NOTIFICATION_ID);
        }
    }

    private void showAutoCancelNotificationIfNecessary() {
        //TODO 将天气信息显示到通知栏，如果没有网络就显示更新失败
        int textColor = ConfigManager.getNotificationTextColor();
        if (!ConfigManager.getWeatherRemindOn()) {
            return;
        }
        if (ConfigManager.getWeatherRemindMorningOn()) {
            List<HourAndMinute> times = null;
            try {
                times = WeatherDbManager.getDb().selector(HourAndMinute.class).where("id", "=", 1).findAll();
            } catch (DbException e) {
                e.printStackTrace();
            }
            if (times == null || times.size() == 0) {
                //没有设置早晨的提醒
            } else {
                Calendar c = Calendar.getInstance();
                HourAndMinute t = times.get(0);
                if (t.getHourOfDay() == c.get(Calendar.HOUR_OF_DAY) && t.getMinute() == c.get(Calendar.MINUTE)) {
                    notificationManager.cancel(1);
                    Notification notification = new Notification(R.drawable.ic_launcher, "今日天气提醒", System.currentTimeMillis());
                    notification.flags = Notification.FLAG_AUTO_CANCEL;
                    totalInfo = (TotalInfo) Utils.readObject(ConfigManager.getCurrentCityName());
                    if (totalInfo != null && totalInfo.isComplete()) {
                        RemoteViews remoteViews = new RemoteViews(x.app().getPackageName(), R.layout.weather_remind);
                        //设置通知栏消息内容
                        Juhe7DaysWeather.ResultEntity.TodayEntity today = totalInfo.sevenDaysWeather.getResult().getToday();
                        remoteViews.setTextViewText(R.id.text_city_name, ConfigManager.getCurrentCityName() + " 今日天气");
                        remoteViews.setTextColor(R.id.text_city_name, textColor);

                        //TODO 通知栏可能要改
                        String todayWeather = today.getWeather();
                        String tips = "";
                        if (todayWeather.contains("雪")) {
                            tips = "有雪小心地滑哦~";
                        }
                        if (todayWeather.contains("霾")) {
                            tips = "雾霾天气少出门，尽量待在室内";
                        }
                        if ((todayWeather.contains("多云")) || (todayWeather.contains("晴"))) {
                            tips = "晴天多出去走走，别当蜗牛哟~";
                        }
                        if (!todayWeather.contains("阴")) {
                            tips = "阴沉沉的天气，记得保持好心情哟~";
                        }
                        remoteViews.setTextViewText(R.id.text_weather_type_and_tips, todayWeather + today.getTemperature() + tips);
                        remoteViews.setTextViewText(R.id.text_update_time, totalInfo.sevenDaysWeather.getResult().getSk().getTime());

                        remoteViews.setTextColor(R.id.text_weather_type_and_tips, textColor);
                        remoteViews.setTextColor(R.id.text_update_time, textColor);

                        notification.contentView = remoteViews;
                        notificationManager.notify(1, notification);
                    } else {
                        //提示天气更新失败
                        Utils.showToast("天气更新失败，请检查网络是否连接成功");
                        RemoteViews remoteViews = new RemoteViews(x.app().getPackageName(), R.layout.weather_notification_failed);
                        remoteViews.setTextViewText(R.id.text_update_time, Utils.getCurrentHourAndMinute());
                        remoteViews.setTextColor(R.id.text_update_time, textColor);

                        notification.contentView = remoteViews;
                        notificationManager.notify(1, notification);
                    }
                }
            }
        }
        if (ConfigManager.getWeatherRemindEveningOn()) {
            List<HourAndMinute> times = null;
            try {
                times = WeatherDbManager.getDb().selector(HourAndMinute.class).where("id", "=", 2).findAll();
            } catch (DbException e) {
                e.printStackTrace();
            }
            if (times == null || times.size() == 0) {
                //没有设置晚间的提醒
            } else {
                Calendar c = Calendar.getInstance();
                HourAndMinute t = times.get(0);
                if (t.getHourOfDay() == c.get(Calendar.HOUR_OF_DAY) && t.getMinute() == c.get(Calendar.MINUTE)) {
                    //发送晚间天气提醒
                    notificationManager.cancel(1);
                    Notification notification = new Notification(R.drawable.ic_launcher, "明日天气提醒", System.currentTimeMillis());
                    notification.flags = Notification.FLAG_AUTO_CANCEL;
                    totalInfo = (TotalInfo) Utils.readObject(ConfigManager.getCurrentCityName());
                    if (totalInfo != null && totalInfo.isComplete()) {
                        RemoteViews remoteViews = new RemoteViews(x.app().getPackageName(), R.layout.weather_remind);
                        //设置通知栏消息内容
                        Juhe7DaysWeather.ResultEntity.FutureEntity tomorrow = totalInfo.sevenDaysWeather.getResult().getFuture().get(1);
                        remoteViews.setTextViewText(R.id.text_city_name, ConfigManager.getCurrentCityName() + " 明日天气");
                        remoteViews.setTextColor(R.id.text_city_name, textColor);
                        //TODO 通知栏可能要改
                        String tomorrowWeather = tomorrow.getWeather();
                        String tips = "";
                        if (tomorrowWeather.contains("雪")) {
                            tips = "有雪小心地滑哦~";
                        }
                        if (tomorrowWeather.contains("霾")) {
                            tips = "雾霾天气少出门，尽量待在室内";
                        }
                        if ((tomorrowWeather.contains("多云")) || (tomorrowWeather.contains("晴"))) {
                            tips = "晴天多出去走走，别当蜗牛哟~";
                        }
                        if (!tomorrowWeather.contains("阴")) {
                            tips = "阴沉沉的天气，记得保持好心情哟~";
                        }
                        remoteViews.setTextViewText(R.id.text_weather_type_and_tips, tomorrowWeather + tomorrow.getTemperature() + tips);
                        remoteViews.setTextColor(R.id.text_weather_type_and_tips, textColor);

                        remoteViews.setTextViewText(R.id.text_update_time, Utils.getCurrentHourAndMinute());
                        remoteViews.setTextColor(R.id.text_update_time, textColor);
                        notification.contentView = remoteViews;
                        notificationManager.notify(1, notification);
                    } else {
                        //提示天气更新失败
                        Utils.showToast("天气更新失败，请检查网络是否连接成功");
                        RemoteViews remoteViews = new RemoteViews(x.app().getPackageName(), R.layout.weather_notification_failed);
                        notification.contentView = remoteViews;
                        notificationManager.notify(1, notification);
                    }
                }
            }
        }
    }

    /**
     * TODO 刷新桌面小部件内容
     */
    private void updateWidget() {
        Intent intent = new Intent();
        intent.setAction("com.fyp.renwenweather.WIDGET_UPDATE");
        sendBroadcast(intent);
    }
}
