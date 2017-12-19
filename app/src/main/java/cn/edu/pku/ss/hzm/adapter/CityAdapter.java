package cn.edu.pku.ss.hzm.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.edu.pku.ss.hzm.bean.City;
import cn.edu.pku.ss.hzm.miniweather.R;

/**
 * Created by i on 2017/10/25.
 * 城市适配器，用来展示在RecyclerView中，继承自RecyclerView.Adapter
 */

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder>
                        implements View.OnClickListener{

    private List<City> allCity;//从数据库读取的城市；
    private OnItemClickListener mOnItemClickListener = null;//向外提供每个TextView的监听接口

    public CityAdapter(List<City> cities) {
        allCity = cities;
    }

    /**
     * 重写RecyclerView.Adapter中的方法
     * 创建ViewHolder实例的方法
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }


    /**
     * 重写RecyclerView.Adapter中的方法
     * 对RecyclerView子项赋值
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        City city = allCity.get(position);
        holder.cityText.setText(city.getCity());
        holder.itemView.setTag(position);
    }

    /**
     * 重写RecyclerView.Adapter的方法
     * 返回的是子项的个数
     * @return
     */
    @Override
    public int getItemCount() {
        return allCity.size();
    }

    public static interface OnItemClickListener {
        void onItemClick(View view , int position);
    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(view,(int)view.getTag());
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    /**
     *内部类ViewHolder用来封装适配器中每个View子项
     */
    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView cityText;

        public ViewHolder(View view) {
            super(view);
            cityText = (TextView) view.findViewById(R.id.city_button);
        }
    }
}
