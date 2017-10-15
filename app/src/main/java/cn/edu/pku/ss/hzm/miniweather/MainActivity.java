package cn.edu.pku.ss.hzm.miniweather;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
 * Created by i on 2017/10/1.
 */

public class MainActivity extends Activity implements View.OnClickListener{

    private static final int UPDATE_TODAY_WEATHER = 1;

    private ImageView upDateBtn,weatherImg, pmImg;
    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv,
            pmQualityTv,temperatureTv, climateTv, windTv, windpowerTv,
            city_name_Tv, high_low_tempTv;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        upDateBtn = (ImageView) findViewById(R.id.title_update_btn);
        upDateBtn.setOnClickListener(this);

        if(NetUtil.getNetworkState(this) == NetUtil.NETWORN_NONE) {
            Toast.makeText(MainActivity.this,"网络断开，请检查网络状态!",Toast.LENGTH_SHORT).show();
        }
        initView();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.title_update_btn) {
            SharedPreferences sharedPre = getSharedPreferences("config",MODE_PRIVATE);
            String cityCode = sharedPre.getString("main_city_code","101010100");

            if(NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                queryWeatherCode(cityCode);
            }else {
                Toast.makeText(MainActivity.this,"网页请求失败，请检查网络状况!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void queryWeatherCode(final String cityCode) {
        new Thread(new Runnable() {
            final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
            @Override
            public void run() {
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
                    while((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    todayWeather = parseXML(response.toString());
                    Message msg =new Message();
                    msg.what = UPDATE_TODAY_WEATHER;
                    msg.obj = todayWeather;
                    mHandler.sendMessage(msg);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if (httpconnect != null) {
                        httpconnect.disconnect();
                    }
                }
            }
        }).start();
    }

    private TodayWeather parseXML(final String response) {
        TodayWeather todayWeather = null;
        int fengxiangCount=0;
        int fengliCount =0;
        int dateCount=0;
        int highCount =0;
        int lowCount=0;
        int typeCount =0;
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
                        if(xmlparser.getName().equals("resp")) {
                            todayWeather = new TodayWeather();
                        }
                        if(todayWeather != null) {
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
                            } else if (xmlparser.getName().equals("fengxiang") && fengxiangCount == 0) {
                                eventType = xmlparser.next();
                                todayWeather.setFengxiang(xmlparser.getText());
                                fengxiangCount++;
                            } else if (xmlparser.getName().equals("fengli") && fengliCount == 0) {
                                eventType = xmlparser.next();
                                todayWeather.setFengli(xmlparser.getText());
                                fengliCount++;
                            } else if (xmlparser.getName().equals("date") && dateCount == 0) {
                                eventType = xmlparser.next();
                                todayWeather.setDate(xmlparser.getText());
                                dateCount++;
                            } else if (xmlparser.getName().equals("high") && highCount == 0) {
                                eventType = xmlparser.next();
                                todayWeather.setHigh(xmlparser.getText().substring(2).trim());
                                highCount++;
                            } else if (xmlparser.getName().equals("low") && lowCount == 0) {
                                eventType = xmlparser.next();
                                todayWeather.setLow(xmlparser.getText().substring(2).trim());
                                lowCount++;
                            } else if (xmlparser.getName().equals("type") && typeCount == 0) {
                                eventType = xmlparser.next();
                                todayWeather.setType(xmlparser.getText());
                                typeCount++;
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

    void initView(){
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity_data);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pm2_5_data);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        high_low_tempTv = (TextView) findViewById(R.id.high_low_temp);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.fengxiang);
        windpowerTv = (TextView) findViewById(R.id.fengli);
        weatherImg = (ImageView) findViewById(R.id.weather_image);
        city_name_Tv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        weekTv.setText("N/A");
        windpowerTv.setText("N/A");
        temperatureTv.setText("N/A");
        high_low_tempTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");
    }

    void updateTodayWeather(TodayWeather todayWeather){
        city_name_Tv.setText(todayWeather.getCity()+"天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime()+ "发布");
        humidityTv.setText(todayWeather.getShidu());
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getWendu()+"°C");
        high_low_tempTv.setText(todayWeather.getHigh()+"~"+todayWeather.getLow());
        climateTv.setText(todayWeather.getType());
        windTv.setText(todayWeather.getFengxiang());
        windpowerTv.setText(todayWeather.getFengli());
        Toast.makeText(MainActivity.this,"更新成功！",Toast.LENGTH_SHORT).show();
    }

}