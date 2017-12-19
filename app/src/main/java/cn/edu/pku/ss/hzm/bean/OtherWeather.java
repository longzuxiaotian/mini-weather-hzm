package cn.edu.pku.ss.hzm.bean;

import java.util.ArrayList;

/**
 * Created by i on 2017/11/26.
 */

public class OtherWeather {
    private String date = "N/A";
    private String high = "N/A";
    private String low = "N/A";
    private String type = "N/A";
    private String quality = "N/A";

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
