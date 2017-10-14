package cn.edu.pku.ss.hzm.miniweather;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by i on 2017/10/1.
 */

public class MainActivity extends Activity implements View.OnClickListener{

    private ImageView upDateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        upDateBtn = (ImageView) findViewById(R.id.title_update_btn);
        upDateBtn.setOnClickListener(this);

        if(NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
            Log.d("myWeather","网络ok");
        }else {
            Log.d("myWeather","网络连接失败");
            Toast.makeText(MainActivity.this,"网络断开，请检查网络状态!",Toast.LENGTH_SHORT).show();
        }
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
                    showResponse(response.toString());
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

    private void showResponse(final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,response,Toast.LENGTH_SHORT).show();
            }
        });
    }


}