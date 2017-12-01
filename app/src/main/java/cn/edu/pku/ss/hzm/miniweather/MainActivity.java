package cn.edu.pku.ss.hzm.miniweather;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.ss.hzm.adapter.CityAdapter;
import cn.edu.pku.ss.hzm.adapter.WeatherAdapter;
import cn.edu.pku.ss.hzm.app.MyApplication;
import cn.edu.pku.ss.hzm.bean.City;
import cn.edu.pku.ss.hzm.bean.OtherWeather;
import cn.edu.pku.ss.hzm.bean.TodayWeather;
import cn.edu.pku.ss.hzm.util.NetUtil;

/**
 * Created by i on 2017/10/1.
 */

public class MainActivity extends Activity implements View.OnClickListener {

    private static final int UPDATE_TODAY_WEATHER = 1;

    private ImageView upDateBtn, selectCity, weatherImg, pmImg;
    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv,
            pmQualityTv, temperatureTv, climateTv, windTv, windpowerTv,
            city_name_Tv, high_low_tempTv;

    private String mainCityID = "";
    private String mainCityName = "";
    private WeatherAdapter mWeatherAdapter;
    private RecyclerView recyclerView;
    private ArrayList<OtherWeather> mOtherWeather;
    private LinearLayoutManager layoutManager;

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

        initView();

        if (NetUtil.getNetworkState(this) == NetUtil.NETWORN_NONE) {
            Toast.makeText(MainActivity.this, "网络断开，请检查网络状态!", Toast.LENGTH_SHORT).show();
            getOldInfo();
        } else {
            SharedPreferences sharedPre = getSharedPreferences("cityinfo", MODE_PRIVATE);
            mainCityID = sharedPre.getString("main_city_code", "101010100");
            mainCityName = sharedPre.getString("main_city_name", "北京");
            queryWeatherCode(mainCityID);

        }

        upDateBtn = (ImageView) findViewById(R.id.title_update_btn);
        upDateBtn.setOnClickListener(this);

        selectCity = (ImageView) findViewById(R.id.title_city_manager);
        selectCity.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        outputInfo();
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.title_city_manager) {
            Intent intent = new Intent(MainActivity.this, SelectCityActivity.class);
            intent.putExtra("cityname", mainCityName);
            startActivityForResult(intent, 2);
        }

        if (view.getId() == R.id.title_update_btn) {

            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                queryWeatherCode(mainCityID);
                Toast.makeText(MainActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "更新失败，请检查网络状况!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 通过http协议获取数据
     *
     * @param cityCode
     */
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
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    todayWeather = parseXML(response.toString());
                    Message msg = new Message();
                    msg.what = UPDATE_TODAY_WEATHER;
                    msg.obj = todayWeather;
                    mHandler.sendMessage(msg);
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
        }).start();
    }

    /**
     * 解析XML数据
     *
     * @param response
     * @return
     */
    private TodayWeather parseXML(final String response) {
        TodayWeather todayWeather = null;
//        int fengxiangCount = 0;
//        int fengliCount = 0;
//        int dateCount = 0;
//        int highCount = 0;
//        int lowCount = 0;
//        int typeCount = 0;
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

    void initView() {
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
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);

        recyclerView = (RecyclerView) findViewById(R.id.other_weathers);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        mOtherWeather = new ArrayList<OtherWeather>();
        for (int i = 0; i < 5; i++) {
            mOtherWeather.add(new OtherWeather());
        }
        mWeatherAdapter = new WeatherAdapter(mOtherWeather);
        recyclerView.setAdapter(mWeatherAdapter);
    }

    /**
     * 启动应用时从sharedpreferences中读取数据
     */
    void getOldInfo() {
        SharedPreferences sharedPre = getSharedPreferences("cityinfo", MODE_PRIVATE);
        city_name_Tv.setText(sharedPre.getString("main_city_name", "N/A") + "天气");
        cityTv.setText(sharedPre.getString("main_city_name", "N/A"));
        timeTv.setText(sharedPre.getString("time", "N/A"));
        humidityTv.setText(sharedPre.getString("humidity", "N/A"));
        pmDataTv.setText(sharedPre.getString("pmData", "N/A"));
        pmQualityTv.setText(sharedPre.getString("pmQuality", "N/A"));
        weekTv.setText(sharedPre.getString("week", "N/A"));
        windpowerTv.setText(sharedPre.getString("windpower", "N/A"));
        temperatureTv.setText(sharedPre.getString("temperature", "N/A"));
        high_low_tempTv.setText(sharedPre.getString("high_low_temp", "N/A"));
        climateTv.setText(sharedPre.getString("climate", "N/A"));
        windTv.setText(sharedPre.getString("wind", "N/A"));
        weatherImg.setImageResource(R.drawable.biz_plugin_weather_wu);
        pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
    }

    /**
     * 退出时将当前数据存入sharedpreferences中
     */
    void outputInfo() {
        SharedPreferences.Editor editor = getSharedPreferences("cityinfo", MODE_PRIVATE).edit();
        editor.putString("main_city_name", cityTv.getText().toString());
        editor.putString("main_city_code", mainCityID);
        editor.putString("time", timeTv.getText().toString());
        editor.putString("humidity", humidityTv.getText().toString());
        editor.putString("pmData", pmDataTv.getText().toString());
        editor.putString("pmQuality", pmQualityTv.getText().toString());
        editor.putString("week", weekTv.getText().toString());
        editor.putString("windpower", windpowerTv.getText().toString());
        editor.putString("temperature", temperatureTv.getText().toString());
        editor.putString("high_low_temp", high_low_tempTv.getText().toString());
        editor.putString("climate", climateTv.getText().toString());
        editor.putString("wind", windTv.getText().toString());
        editor.apply();
//        weatherImg.setImageResource(R.drawable.biz_plugin_weather_wu);
//        pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
    }

    /**
     * 更新数据
     *
     * @param todayWeather
     */
    void updateTodayWeather(TodayWeather todayWeather) {
        city_name_Tv.setText(todayWeather.getCity() + "天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime() + "发布");
        humidityTv.setText(todayWeather.getShidu());
        if (todayWeather.getPm25() == null) pmDataTv.setText("0");
        else pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate().get(0));
        temperatureTv.setText(todayWeather.getWendu() + "°C");
        high_low_tempTv.setText(todayWeather.getLow().get(0) + "~" + todayWeather.getHigh().get(0));
        climateTv.setText(todayWeather.getType().get(0));
        windTv.setText(todayWeather.getFengxiang().get(0));
        windpowerTv.setText(todayWeather.getFengli().get(0));
        mainCityName = todayWeather.getCity();
        updateImage(todayWeather);
        updateOtherWeather(todayWeather);
    }

    void updateImage(TodayWeather todayWeather) {
        int pm_25;
        if (todayWeather.getPm25() == null) pm_25 = 0;
        else pm_25 = Integer.parseInt(todayWeather.getPm25());
        String climate = todayWeather.getType().get(0);
        if (0 <= pm_25 && pm_25 <= 50) {
            pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
        } else if (51 <= pm_25 && pm_25 <= 100) {
            pmImg.setImageResource(R.drawable.biz_plugin_weather_51_100);
        } else if (101 <= pm_25 && pm_25 <= 150) {
            pmImg.setImageResource(R.drawable.biz_plugin_weather_101_150);
        } else if (151 <= pm_25 && pm_25 <= 200) {
            pmImg.setImageResource(R.drawable.biz_plugin_weather_151_200);
        } else if (201 <= pm_25 && pm_25 <= 300) {
            pmImg.setImageResource(R.drawable.biz_plugin_weather_201_300);
        } else if (301 <= pm_25) {
            pmImg.setImageResource(R.drawable.biz_plugin_weather_greater_300);
        }

        if (climate.equals("暴雪")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);
        } else if (climate.equals("暴雨")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
        } else if (climate.equals("大暴雨")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
        } else if (climate.equals("大雪")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
        } else if (climate.equals("大雨")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_dayu);
        } else if (climate.equals("多云")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
        } else if (climate.equals("雷阵雨")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
        } else if (climate.equals("雷阵雨冰雹")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
        } else if (climate.equals("晴")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);
        } else if (climate.equals("沙尘暴")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
        } else if (climate.equals("特大暴雨")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
        } else if (climate.equals("无")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_wu);
        } else if (climate.equals("小雪")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
        } else if (climate.equals("小雨")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
        } else if (climate.equals("阴")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_yin);
        } else if (climate.equals("雨夹雪")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
        } else if (climate.equals("阵雪")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
        } else if (climate.equals("阵雨")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
        } else if (climate.equals("中雪")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
        } else if (climate.equals("中雨")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
        }
    }

    void updateOtherWeather(TodayWeather todayWeather) {
        mOtherWeather = new ArrayList<OtherWeather>();
        for(int i=0; i<5; i++) {
            OtherWeather temp = new OtherWeather();
            temp.setDate(todayWeather.getDate().get(i));
            temp.setQuality(todayWeather.getQuality());
            temp.setType(todayWeather.getType().get(i));
            temp.setHigh(todayWeather.getHigh().get(i));
            temp.setLow(todayWeather.getLow().get(i));
            mOtherWeather.add(temp);
        }
        mWeatherAdapter = new WeatherAdapter(mOtherWeather);
        recyclerView.setAdapter(mWeatherAdapter);
    }

    /**
     * 退出选择城市页面时的回调函数，得到用户选择的城市的信息
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 2:
                if (resultCode == RESULT_OK) {
                    if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                        mainCityID = data.getStringExtra("cityID");
                        queryWeatherCode(mainCityID);
                        Toast.makeText(MainActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "更新失败，请检查网络状况!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }
    }
}