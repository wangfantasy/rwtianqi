package com.fyp.renwenweather.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by fyp on 2015/12/29.
 */
public class Juhe3HourWeather implements Serializable {
    /**
     * resultcode : 200
     * reason : successed
     * result : [{"weatherid":"00","weather":"晴","temp1":"27","temp2":"31","sh":"08","eh":"11","date":"20140530","sfdate":"20140530080000","efdate":"20140530110000"},{"weatherid":"01","weather":"多云","temp1":"31","temp2":"33","sh":"11","eh":"02","date":"20140530","sfdate":"20140530110000","efdate":"20140530140000"},{"weatherid":"01","weather":"多云","temp1":"33","temp2":"30","sh":"02","eh":"05","date":"20140530","sfdate":"20140530140000","efdate":"20140530170000"},{"weatherid":"01","weather":"多云","temp1":"30","temp2":"25","sh":"05","eh":"08","date":"20140530","sfdate":"20140530170000","efdate":"20140530200000"},{"weatherid":"01","weather":"多云","temp1":"25","temp2":"24","sh":"08","eh":"11","date":"20140530","sfdate":"20140530200000","efdate":"20140530230000"},{"weatherid":"01","weather":"多云","temp1":"24","temp2":"23","sh":"11","eh":"02","date":"20140530","sfdate":"20140530230000","efdate":"20140531020000"},{"weatherid":"02","weather":"阴","temp1":"23","temp2":"22","sh":"02","eh":"05","date":"20140531","sfdate":"20140531020000","efdate":"20140531050000"},{"weatherid":"02","weather":"阴","temp1":"22","temp2":"24","sh":"05","eh":"08","date":"20140531","sfdate":"20140531050000","efdate":"20140531080000"},{"weatherid":"02","weather":"阴","temp1":"24","temp2":"28","sh":"08","eh":"11","date":"20140531","sfdate":"20140531080000","efdate":"20140531110000"},{"weatherid":"01","weather":"多云","temp1":"28","temp2":"29","sh":"11","eh":"02","date":"20140531","sfdate":"20140531110000","efdate":"20140531140000"},{"weatherid":"02","weather":"阴","temp1":"29","temp2":"27","sh":"02","eh":"05","date":"20140531","sfdate":"20140531140000","efdate":"20140531170000"},{"weatherid":"02","weather":"阴","temp1":"27","temp2":"25","sh":"05","eh":"08","date":"20140531","sfdate":"20140531170000","efdate":"20140531200000"},{"weatherid":"02","weather":"阴","temp1":"25","temp2":"24","sh":"08","eh":"11","date":"20140531","sfdate":"20140531200000","efdate":"20140531230000"},{"weatherid":"07","weather":"小雨","temp1":"24","temp2":"24","sh":"11","eh":"02","date":"20140531","sfdate":"20140531230000","efdate":"20140601020000"},{"weatherid":"07","weather":"小雨","temp1":"24","temp2":"23","sh":"02","eh":"05","date":"20140601","sfdate":"20140601020000","efdate":"20140601050000"},{"weatherid":"08","weather":"中雨","temp1":"23","temp2":"24","sh":"05","eh":"08","date":"20140601","sfdate":"20140601050000","efdate":"20140601080000"},{"weatherid":"08","weather":"中雨","temp1":"24","temp2":"26","sh":"08","eh":"11","date":"20140601","sfdate":"20140601080000","efdate":"20140601110000"},{"weatherid":"09","weather":"大雨","temp1":"26","temp2":"26","sh":"11","eh":"02","date":"20140601","sfdate":"20140601110000","efdate":"20140601140000"},{"weatherid":"09","weather":"大雨","temp1":"26","temp2":"26","sh":"02","eh":"05","date":"20140601","sfdate":"20140601140000","efdate":"20140601170000"},{"weatherid":"10","weather":"暴雨","temp1":"26","temp2":"25","sh":"05","eh":"08","date":"20140601","sfdate":"20140601170000","efdate":"20140601200000"},{"weatherid":"10","weather":"暴雨","temp1":"25","temp2":"26","sh":"08","eh":"11","date":"20140601","sfdate":"20140601200000","efdate":"20140601230000"},{"weatherid":"10","weather":"暴雨","temp1":"26","temp2":"26","sh":"11","eh":"02","date":"20140601","sfdate":"20140601230000","efdate":"20140602020000"},{"weatherid":"10","weather":"暴雨","temp1":"26","temp2":"26","sh":"02","eh":"05","date":"20140602","sfdate":"20140602020000","efdate":"20140602050000"},{"weatherid":"08","weather":"中雨","temp1":"26","temp2":"26","sh":"05","eh":"08","date":"20140602","sfdate":"20140602050000","efdate":"20140602080000"},{"weatherid":"08","weather":"中雨","temp1":"26","temp2":"28","sh":"08","eh":"02","date":"20140602","sfdate":"20140602080000","efdate":"20140602140000"},{"weatherid":"01","weather":"多云","temp1":"28","temp2":"26","sh":"02","eh":"08","date":"20140602","sfdate":"20140602140000","efdate":"20140602200000"},{"weatherid":"01","weather":"多云","temp1":"26","temp2":"23","sh":"08","eh":"02","date":"20140602","sfdate":"20140602200000","efdate":"20140603020000"},{"weatherid":"07","weather":"小雨","temp1":"23","temp2":"23","sh":"02","eh":"08","date":"20140603","sfdate":"20140603020000","efdate":"20140603080000"},{"weatherid":"07","weather":"小雨","temp1":"23","temp2":"26","sh":"08","eh":"02","date":"20140603","sfdate":"20140603080000","efdate":"20140603140000"},{"weatherid":"07","weather":"小雨","temp1":"26","temp2":"24","sh":"02","eh":"08","date":"20140603","sfdate":"20140603140000","efdate":"20140603200000"},{"weatherid":"07","weather":"小雨","temp1":"24","temp2":"22","sh":"08","eh":"02","date":"20140603","sfdate":"20140603200000","efdate":"20140604020000"},{"weatherid":"07","weather":"小雨","temp1":"22","temp2":"23","sh":"02","eh":"08","date":"20140604","sfdate":"20140604020000","efdate":"20140604080000"},{"weatherid":"07","weather":"小雨","temp1":"23","temp2":"26","sh":"08","eh":"02","date":"20140604","sfdate":"20140604080000","efdate":"20140604140000"},{"weatherid":"07","weather":"小雨","temp1":"26","temp2":"23","sh":"02","eh":"08","date":"20140604","sfdate":"20140604140000","efdate":"20140604200000"},{"weatherid":"07","weather":"小雨","temp1":"23","temp2":"22","sh":"08","eh":"02","date":"20140604","sfdate":"20140604200000","efdate":"20140605020000"},{"weatherid":"07","weather":"小雨","temp1":"22","temp2":"24","sh":"02","eh":"08","date":"20140605","sfdate":"20140605020000","efdate":"20140605080000"},{"weatherid":"07","weather":"小雨","temp1":"24","temp2":"27","sh":"08","eh":"02","date":"20140605","sfdate":"20140605080000","efdate":"20140605140000"},{"weatherid":"02","weather":"阴","temp1":"27","temp2":"24","sh":"02","eh":"08","date":"20140605","sfdate":"20140605140000","efdate":"20140605200000"},{"weatherid":"02","weather":"阴","temp1":"24","temp2":"22","sh":"08","eh":"02","date":"20140605","sfdate":"20140605200000","efdate":"20140606020000"},{"weatherid":"01","weather":"多云","temp1":"22","temp2":"25","sh":"02","eh":"08","date":"20140606","sfdate":"20140606020000","efdate":"20140606080000"}]
     * error_code : 0
     */

    private String resultcode;
    private String reason;
    private int error_code;
    /**
     * weatherid : 00
     * weather : 晴
     * temp1 : 27
     * temp2 : 31
     * sh : 08
     * eh : 11
     * date : 20140530
     * sfdate : 20140530080000
     * efdate : 20140530110000
     */

    private List<ResultEntity> result;

    public void setResultcode(String resultcode) {
        this.resultcode = resultcode;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public void setResult(List<ResultEntity> result) {
        this.result = result;
    }

    public String getResultcode() {
        return resultcode;
    }

    public String getReason() {
        return reason;
    }

    public int getError_code() {
        return error_code;
    }

    public List<ResultEntity> getResult() {
        return result;
    }

    public static class ResultEntity implements Serializable {
        private String weatherid;
        private String weather;
        private String temp1;
        private String temp2;
        private String sh;
        private String eh;
        private String date;
        private String sfdate;
        private String efdate;

        public void setWeatherid(String weatherid) {
            this.weatherid = weatherid;
        }

        public void setWeather(String weather) {
            this.weather = weather;
        }

        public void setTemp1(String temp1) {
            this.temp1 = temp1;
        }

        public void setTemp2(String temp2) {
            this.temp2 = temp2;
        }

        public void setSh(String sh) {
            this.sh = sh;
        }

        public void setEh(String eh) {
            this.eh = eh;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public void setSfdate(String sfdate) {
            this.sfdate = sfdate;
        }

        public void setEfdate(String efdate) {
            this.efdate = efdate;
        }

        public String getWeatherid() {
            return weatherid;
        }

        public String getWeather() {
            return weather;
        }

        public String getTemp1() {
            return temp1;
        }

        public String getTemp2() {
            return temp2;
        }

        public String getSh() {
            return sh;
        }

        public String getEh() {
            return eh;
        }

        public String getDate() {
            return date;
        }

        public String getSfdate() {
            return sfdate;
        }

        public String getEfdate() {
            return efdate;
        }
    }
}
