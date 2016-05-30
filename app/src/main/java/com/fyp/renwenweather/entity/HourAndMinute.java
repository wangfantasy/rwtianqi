package com.fyp.renwenweather.entity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by fyp on 2016/3/10.
 */
@Table(name = "updateTime")
public class HourAndMinute {
    @Column(
            name = "id",
            isId = true,
            autoGen = false
    )
    int id;
    @Column(name = "hourOfDay",property = "NOT NULL")
    int hourOfDay;
    @Column(name = "minute",property = "NOT NULL")
    int minute;

    public HourAndMinute() {
    }

    public HourAndMinute(int hourOfDay, int minute) {
        this.hourOfDay = hourOfDay;
        this.minute = minute;
        this.id = hourOfDay*60+minute;
    }

    public HourAndMinute(int id, int hourOfDay, int minute) {
        this.id = id;
        this.hourOfDay = hourOfDay;
        this.minute = minute;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public void setHourOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}
