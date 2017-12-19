package cn.edu.pku.ss.hzm.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.edu.pku.ss.hzm.bean.OtherWeather;
import cn.edu.pku.ss.hzm.bean.TodayWeather;
import cn.edu.pku.ss.hzm.miniweather.R;

/**
 * Created by i on 2017/11/26.
 * 天气适配器，用来展示在RecyclerView中，继承自RecyclerView.Adapter
 */
public class WeatherAdapter extends RecyclerView.Adapter<cn.edu.pku.ss.hzm.adapter.WeatherAdapter.ViewHolder> {

    private List<OtherWeather> allWeather;

    public WeatherAdapter(List<OtherWeather> allWeather) {
        this.allWeather = allWeather;
    }

    @Override
    public cn.edu.pku.ss.hzm.adapter.WeatherAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_item, parent, false);
        final cn.edu.pku.ss.hzm.adapter.WeatherAdapter.ViewHolder holder = new cn.edu.pku.ss.hzm.adapter.WeatherAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(cn.edu.pku.ss.hzm.adapter.WeatherAdapter.ViewHolder holder, int position) {
        OtherWeather weather = allWeather.get(position);
        if(position==0) holder.feature_date.setText("今天");
        else if(position==1) holder.feature_date.setText("明天");
        else holder.feature_date.setText(weather.getDate().substring(weather.getDate().length()-3));
        holder.feature_quality.setText(weather.getQuality()==null?"0污染":weather.getQuality());
        holder.feature_type.setText(weather.getType());
        holder.feature_temperature.setText(weather.getLow()+"/"+weather.getHigh());
        setImage(holder);
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return allWeather.size();
    }

    /**
     * 内部类ViewHolder用来封装适配器中每个View子项
     */
    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView feature_date, feature_quality, feature_temperature,feature_type;
        ImageView feature_img;

        public ViewHolder(View view) {
            super(view);
            feature_date = (TextView) view.findViewById(R.id.feature_date);
            feature_quality = (TextView) view.findViewById(R.id.feature_quality);
            feature_temperature = (TextView) view.findViewById(R.id.feature_temperature);
            feature_type = (TextView) view.findViewById(R.id.feature_type);
            feature_img = (ImageView) view.findViewById(R.id.feature_img);
        }
    }

    void setImage(ViewHolder holder) {
        String climate = holder.feature_type.getText().toString();

        if (climate.equals("暴雪")) {
            holder.feature_img.setImageResource(R.drawable.biz_plugin_weather_baoxue);
        } else if (climate.equals("暴雨")) {
            holder.feature_img.setImageResource(R.drawable.biz_plugin_weather_baoyu);
        } else if (climate.equals("大暴雨")) {
            holder.feature_img.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
        } else if (climate.equals("大雪")) {
            holder.feature_img.setImageResource(R.drawable.biz_plugin_weather_daxue);
        } else if (climate.equals("大雨")) {
            holder.feature_img.setImageResource(R.drawable.biz_plugin_weather_dayu);
        } else if (climate.equals("多云")) {
            holder.feature_img.setImageResource(R.drawable.biz_plugin_weather_duoyun);
        } else if (climate.equals("雷阵雨")) {
            holder.feature_img.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
        } else if (climate.equals("雷阵雨冰雹")) {
            holder.feature_img.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
        } else if (climate.equals("晴")) {
            holder.feature_img.setImageResource(R.drawable.biz_plugin_weather_qing);
        } else if (climate.equals("沙尘暴")) {
            holder.feature_img.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
        } else if (climate.equals("特大暴雨")) {
            holder.feature_img.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
        } else if (climate.equals("无")) {
            holder.feature_img.setImageResource(R.drawable.biz_plugin_weather_wu);
        } else if (climate.equals("小雪")) {
            holder.feature_img.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
        } else if (climate.equals("小雨")) {
            holder.feature_img.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
        } else if (climate.equals("阴")) {
            holder.feature_img.setImageResource(R.drawable.biz_plugin_weather_yin);
        } else if (climate.equals("雨夹雪")) {
            holder.feature_img.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
        } else if (climate.equals("阵雪")) {
            holder.feature_img.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
        } else if (climate.equals("阵雨")) {
            holder.feature_img.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
        } else if (climate.equals("中雪")) {
            holder.feature_img.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
        } else if (climate.equals("中雨")) {
            holder.feature_img.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
        }
    }

}
