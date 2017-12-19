package cn.edu.pku.ss.hzm.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.edu.pku.ss.hzm.bean.TodayWeather;

/**
 * auto update weather
 */

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新天气信息
     */

    private void updateWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String cityCode = prefs.getString("main_city_code", null);
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        HttpURLConnection httpconnect = null;
        TodayWeather todayWeather = null;
        try {
            URL url = new URL(address);
            httpconnect = (HttpURLConnection) url.openConnection();
            httpconnect.setRequestMethod("GET");
            httpconnect.setConnectTimeout(8000);
            httpconnect.setReadTimeout(8000);
            InputStream in = httpconnect.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            todayWeather = parseXML(response.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpconnect != null) {
                httpconnect.disconnect();
            }
        }
    }

    private TodayWeather parseXML(final String response) {
        TodayWeather todayWeather = null;
        try {
            XmlPullParserFactory xmlparserfactory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlparser = xmlparserfactory.newPullParser();
            xmlparser.setInput(new StringReader(response));
            int eventType = xmlparser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (xmlparser.getName().equals("resp")) {
                            todayWeather = new TodayWeather();
                        }
                        if (todayWeather != null) {
                            if (xmlparser.getName().equals("city")) {
                                eventType = xmlparser.next();
                                todayWeather.setCity(xmlparser.getText());
                            } else if (xmlparser.getName().equals("updatetime")) {
                                eventType = xmlparser.next();
                                todayWeather.setUpdatetime(xmlparser.getText());
                            } else if (xmlparser.getName().equals("shidu")) {
                                eventType = xmlparser.next();
                                todayWeather.setShidu(xmlparser.getText());
                            } else if (xmlparser.getName().equals("wendu")) {
                                eventType = xmlparser.next();
                                todayWeather.setWendu(xmlparser.getText());
                            } else if (xmlparser.getName().equals("pm25")) {
                                eventType = xmlparser.next();
                                todayWeather.setPm25(xmlparser.getText());
                            } else if (xmlparser.getName().equals("quality")) {
                                eventType = xmlparser.next();
                                todayWeather.setQuality(xmlparser.getText());
                            } else if (xmlparser.getName().equals("fengxiang")) {
                                eventType = xmlparser.next();
                                todayWeather.getFengxiang().add(xmlparser.getText());
//                                fengxiangCount++;
                            } else if (xmlparser.getName().equals("fengli")) {
                                eventType = xmlparser.next();
                                todayWeather.getFengli().add(xmlparser.getText());
//                                fengliCount++;
                            } else if (xmlparser.getName().equals("date")) {
                                eventType = xmlparser.next();
                                todayWeather.getDate().add(xmlparser.getText());
//                                dateCount++;
                            } else if (xmlparser.getName().equals("high")) {
                                eventType = xmlparser.next();
                                todayWeather.getHigh().add(xmlparser.getText().substring(2).trim());
//                                highCount++;
                            } else if (xmlparser.getName().equals("low")) {
                                eventType = xmlparser.next();
                                todayWeather.getLow().add(xmlparser.getText().substring(2).trim());
//                                lowCount++;
                            } else if (xmlparser.getName().equals("type")) {
                                eventType = xmlparser.next();
                                todayWeather.getType().add(xmlparser.getText());
//                                typeCount++;
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    default:
                        break;
                }
                eventType = xmlparser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return todayWeather;
    }



}
