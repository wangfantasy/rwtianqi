// IWeatherLoader.aidl
package com.fyp.renwenweather;

// Declare any non-default types here with import statements

interface IWeatherLoader {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
     /**读取配置文件更新天气*/
    void updateWeather();
    /**
    *根据经纬度更新天气
    */
    void updateWeatherWithLocation(double latitude,double lontitude);
}
