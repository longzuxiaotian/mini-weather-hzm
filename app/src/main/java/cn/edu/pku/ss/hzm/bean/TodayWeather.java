package cn.edu.pku.ss.hzm.bean;

import java.util.ArrayList;

/**
 * Created by i on 2017/10/15.
 */

public class TodayWeather {
    private String city;
    private String updatetime;
    private String wendu;
    private String shidu;
    private String pm25;
    private String quality;
    private ArrayList<String> fengxiang = new ArrayList<String>();
    private ArrayList<String> fengli = new ArrayList<String>();
    private ArrayList<String> date = new ArrayList<String>();
    private ArrayList<String> high = new ArrayList<String>();
    private ArrayList<String> low = new ArrayList<String>();
    private ArrayList<String> type = new ArrayList<String>();
    ;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public String getWendu() {
        return wendu;
    }

    public void setWendu(String wendu) {
        this.wendu = wendu;
    }

    public String getShidu() {
        return shidu;
    }

    public void setShidu(String shidu) {
        this.shidu = shidu;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public ArrayList<String> getDate() {
        return date;
    }

    public ArrayList<String> getFengli() {
        return fengli;
    }

    public ArrayList<String> getFengxiang() {
        return fengxiang;
    }

    public ArrayList<String> getHigh() {
        return high;
    }

    public ArrayList<String> getLow() {
        return low;
    }

    public ArrayList<String> getType() {
        return type;
    }
}
