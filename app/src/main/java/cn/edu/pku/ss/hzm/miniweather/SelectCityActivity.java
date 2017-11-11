package cn.edu.pku.ss.hzm.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.ss.hzm.adapter.CityAdapter;
import cn.edu.pku.ss.hzm.app.MyApplication;
import cn.edu.pku.ss.hzm.bean.City;

/**
 *选择城市页面
 */

public class SelectCityActivity extends Activity implements View.OnClickListener{

    private TextView mainCity;
    private ImageView cityBack;
    private ClearEditText mClearEditText;

    private List<City> citys = MyApplication.getInstance().getCityList();
    private ArrayList<City> filterDataList;
    private CityAdapter mAdapter;
    RecyclerView recyclerView;

    private String currentCityID = "";
    private String currentCityName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        mainCity = (TextView) findViewById(R.id.select_city_name);
        Intent intent = getIntent();
        currentCityName = intent.getStringExtra("cityname");
        mainCity.setText("当前城市："+ currentCityName);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new CityAdapter(citys);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new CityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {//设置每个view的监听器
                City city = citys.get(position);
                mainCity.setText("当前城市:"+city.getCity());
                currentCityID = city.getNumber();
            }
        });

        cityBack = (ImageView) findViewById(R.id.select_city_back);
        cityBack.setOnClickListener(this);

        mClearEditText = (ClearEditText) findViewById(R.id.select_search);
        //搜索监听
        mClearEditText.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterData(charSequence.toString());
                mAdapter.setOnItemClickListener(new CityAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        City city = filterDataList.get(position);
                        mainCity.setText("当前城市:"+city.getCity());
                        currentCityID = city.getNumber();
                    }
                });
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    /**
     *过滤数据并更新
     * @param s
     */
    private void filterData(String s) {
        filterDataList = new ArrayList<City>();
        if(TextUtils.isEmpty(s)) {
            for(City city:citys) {
                filterDataList.add(city);
            }
        }else {
            filterDataList.clear();
            for(City city:citys) {
                if(city.getCity().indexOf(s.toString()) != -1) {
                    filterDataList.add(city);
                }
            }
        }
        mAdapter = new CityAdapter(filterDataList);
    }

    @Override
    public void onClick(View view) {
       if(view.getId() == R.id.select_city_back) {
           onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        if(currentCityID == "") {
            setResult(RESULT_CANCELED);
        }
        else {
            intent.putExtra("cityID",currentCityID);
            setResult(RESULT_OK,intent);
        }
        finish();
    }
}
