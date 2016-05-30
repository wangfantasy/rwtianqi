package com.fyp.renwenweather.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by fyp on 2015/12/29.
 */
public class JuheAirStatus implements Serializable {

    /**
     * resultcode : 200
     * reason : SUCCESSED!
     * result : [{"city":"衡阳","PM25":"124","AQI":"163","quality":"中度污染","PM10":"166","CO":"1.4","NO2":"49","O3":"4","SO2":"16","time":"2015-12-29 21:00:10"}]
     * error_code : 0
     */

    private String resultcode;
    private String reason;
    private int error_code;
    /**
     * city : 衡阳
     * PM25 : 124
     * AQI : 163
     * quality : 中度污染
     * PM10 : 166
     * CO : 1.4
     * NO2 : 49
     * O3 : 4
     * SO2 : 16
     * time : 2015-12-29 21:00:10
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
        private String city;
        private String PM25;
        private String AQI;
        private String quality;
        private String PM10;
        private String CO;
        private String NO2;
        private String O3;
        private String SO2;
        private String time;

        public void setCity(String city) {
            this.city = city;
        }

        public void setPM25(String PM25) {
            this.PM25 = PM25;
        }

        public void setAQI(String AQI) {
            this.AQI = AQI;
        }

        public void setQuality(String quality) {
            this.quality = quality;
        }

        public void setPM10(String PM10) {
            this.PM10 = PM10;
        }

        public void setCO(String CO) {
            this.CO = CO;
        }

        public void setNO2(String NO2) {
            this.NO2 = NO2;
        }

        public void setO3(String O3) {
            this.O3 = O3;
        }

        public void setSO2(String SO2) {
            this.SO2 = SO2;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getCity() {
            return city;
        }

        public String getPM25() {
            return PM25;
        }

        public String getAQI() {
            return AQI;
        }

        public String getQuality() {
            return quality;
        }

        public String getPM10() {
            return PM10;
        }

        public String getCO() {
            return CO;
        }

        public String getNO2() {
            return NO2;
        }

        public String getO3() {
            return O3;
        }

        public String getSO2() {
            return SO2;
        }

        public String getTime() {
            return time;
        }
    }
}
