package cn.edu.pku.ss.hzm.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SelectCityActivity extends Activity implements View.OnClickListener{

    public static String RETURN_MAIN_CITY_ID = "return_main_city_id";

    private String cityName;
    private String cityID;
    private TextView mainCity;
    private ImageView cityBack;
    private Button lanzhou;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        Intent intent = getIntent();
        cityName = intent.getStringExtra(MainActivity.MAIN_CITY_NAME);
        mainCity = (TextView) findViewById(R.id.select_city_name);
        mainCity.setText("当前城市：" + cityName);

        lanzhou = (Button) findViewById(R.id.city_lanzhou);
        lanzhou.setOnClickListener(this);

        cityBack = (ImageView) findViewById(R.id.select_city_back);
        cityBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.city_lanzhou) {
            cityName = lanzhou.getText().toString();
            mainCity.setText("当前城市：" + cityName);
        }else if(view.getId() == R.id.select_city_back) {
           onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(RETURN_MAIN_CITY_ID,"101160101");
        setResult(RESULT_OK,intent);
        finish();
    }
}
