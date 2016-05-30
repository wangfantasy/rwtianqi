package com.fyp.renwenweather.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by fyp on 2016/1/19.
 */
public class TotalInfo implements Serializable {
    public JuheAirStatus airStatus;
    public Juhe3HourWeather threeHourWeather;
    public Juhe7DaysWeather sevenDaysWeather;
    public Date lastUpdate;

    public boolean isComplete() {
        boolean isComplete = (airStatus != null && airStatus.getError_code() == 0
                && "200".equals(airStatus.getResultcode()) &&
                threeHourWeather != null && threeHourWeather.getError_code() == 0 &&
                "200".equals(threeHourWeather.getResultcode()) &&
                sevenDaysWeather != null && "200".equals(sevenDaysWeather.getResultcode()) &&
                lastUpdate != null);
//        if (!isComplete) {
//            Utils.showToast("数据不完整");
//        }
        return isComplete;
    }

    /**
     * @param second 保质期 单位为秒
     * @return 是否已经过期(根据当前系统时间计算)
     */
    public boolean isFresh(long second) {
        if (lastUpdate == null) {
            return false;
        }
        long now=System.currentTimeMillis();
        long last=lastUpdate.getTime();
        if ((now - last)/1000 > second) {
            return false;
        }
        return true;
    }
}
