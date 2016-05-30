package com.fyp.renwenweather.datamanager;

/**
 * 每次配置发生改变都会发送广播
 */
public class ConfigManager {

    public static final String NOTIFICATION_TEXT_COLOR = "NOTIFICATION_TEXT_COLOR";
    public static final String WEATHER_REMIND_ON = "WEATHER_REMIND_ON";
    public static final String AUTO_UPDATE_WEATHER = "AUTO_UPDATE_WEATHER";
    public static final String DEFAULT_DISPLAY = "DEFAULT_DISPLAY";
    public static final String CURRENT_CITY_NAME = "CURRENT_CITY_NAME";
    public static final String STAY_IN_NOTIFICATION = "STAY_IN_NOTIFICATION";
    public static final String WEATHER_REMIND_MORNING_ON = "WEATHER_REMIND_MORNING_ON";
    public static final String WEATHER_REMIND_EVENING_ON = "WEATHER_REMIND_EVENING_ON";
    public static final String UPDATE_FREQUENCY = "UPDATE_FREQUENCY";
    public static final String CONTENT_BACKGROUND_URI = "CONTENT_BACKGROUND_URI";

    /**
     * 配置当前所在城市
     */
    public static String getCurrentCityName() {
        return Utils.readConfig(CURRENT_CITY_NAME, "衡阳");
    }

    public static void setCurrentCityName(String cityName) {
        Utils.saveConfig(CURRENT_CITY_NAME, cityName);
    }

    /**
     * @return 是否常驻通知栏
     */
    public static boolean getStayInNotification() {
        return Boolean.valueOf(Utils.readConfig(STAY_IN_NOTIFICATION, String.valueOf(false)));
    }

    /**
     * @param checked 是否常驻通知栏
     */
    public static synchronized void setStayInNotification(boolean checked) {
        Utils.saveConfig(STAY_IN_NOTIFICATION, String.valueOf(checked));
    }

    /**
     * 读取是否启用天气提醒，默认false
     */
    public static boolean getWeatherRemindOn() {
        return Boolean.parseBoolean(Utils.readConfig(WEATHER_REMIND_ON, String.valueOf(false)));
    }

    /**
     * 是否启用天气提醒
     */
    public static void setWeatherRemindOn(boolean b) {
        Utils.saveConfig(WEATHER_REMIND_ON, String.valueOf(b));
    }

    public static boolean getWeatherRemindMorningOn() {
        return Boolean.parseBoolean(Utils.readConfig(WEATHER_REMIND_MORNING_ON, String.valueOf(false)));
    }

    public static void setWeatherRemindMorningOn(boolean checked) {
        Utils.saveConfig(WEATHER_REMIND_MORNING_ON, String.valueOf(checked));
    }

    public static boolean getWeatherRemindEveningOn() {
        return Boolean.parseBoolean(Utils.readConfig(WEATHER_REMIND_EVENING_ON, String.valueOf(false)));
    }

    public static void setWeatherRemindEveningOn(boolean checked) {
        Utils.saveConfig(WEATHER_REMIND_EVENING_ON, String.valueOf(checked));
    }

    /**
     * 每次更新的时间间隔(有效期)
     */
    public static int getUpdateFrequency() {
        return Integer.valueOf(Utils
                .readConfig(UPDATE_FREQUENCY, "3"));
    }

    /**
     * 更新频率
     */
    public static void setUpdateFrequency(int h) {
        Utils.saveConfig(UPDATE_FREQUENCY, String.valueOf(h));
    }

    /**
     * 是否开启自动更新天气
     */
    public static boolean getAutoUpdateWeatherIsEnabled() {
        return Boolean.parseBoolean(Utils.readConfig(AUTO_UPDATE_WEATHER, "false"));
    }

    public static void setAutoUpdateWeatherIsEnabled(boolean checked) {
        Utils.saveConfig(AUTO_UPDATE_WEATHER, String.valueOf(checked));
    }

    public static int getDefaultDisplay() {
        return Integer.valueOf(Utils.readConfig(DEFAULT_DISPLAY, "0"));
    }

    public static void setDefaultDisplay(int which) {
        Utils.saveConfig(DEFAULT_DISPLAY, String.valueOf(which));
    }

    public static String getContentBackgroundURI() {
        return Utils.readConfig(CONTENT_BACKGROUND_URI, "");
    }

    public static void setContentBackgroundURI(String uri) {
        Utils.saveConfig(CONTENT_BACKGROUND_URI, uri);
    }

    public static int getNotificationTextColor() {
        return Integer.valueOf(Utils.readConfig(NOTIFICATION_TEXT_COLOR, "-1"));
    }

    public static void setNotificationTextColor(int color) {
        Utils.saveConfig(NOTIFICATION_TEXT_COLOR, String.valueOf(color));
    }
}
