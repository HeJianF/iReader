package io.github.marktony.reader.withnav;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import io.github.marktony.reader.R;

public class AboutThisApp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //APP全屏化
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_about_this_app);
        ImageView imageView = (ImageView) findViewById(R.id.this_App_Pic);
        TextView textViewHelp = (TextView) findViewById(R.id.app_content_text);
        TextView textViewFrame = (TextView) findViewById(R.id.app_content_frame);

        String textContentHelp = " 数据都是来源与网络，糗事百科，煎蛋，内涵段子\n"+
                " 在GitHub上找到了这款app（https://github.com/TonnyL/Reader），感谢作者的开源。" +
                "在这款app的基础上进行了加工改造，增加了一些功能，" +
                "也让界面变得更好看了！\n" +
                " app的背景图片是来自必应搜索的背景图，可以在'背景图片相关'中查看相关知识！\n" +
                " 项目还未完成，有些知识点还为未理解透彻，写的有点乱，还会继续完善！";
        String textContentFrame = " compile files('libs/volley.jar')\n" +
                " compile 'com.android.support:appcompat-v7:24.2.1'\n" +
                " compile 'com.android.support:recyclerview-v7:24.2.1'\n" +
                " compile 'com.android.support:design:24.2.1'\n" +
                " compile 'com.android.support:cardview-v7:24.2.1'\n" +
                " compile 'com.google.code.gson:gson:2.7'\n" +
                " compile 'de.hdodenhof:circleimageview:2.1.0'\n" +
                " compile 'com.github.bumptech.glide:glide:3.7.0'\n" +
                " compile 'com.squareup.okhttp3:okhttp:3.4.1'\n" +
                " compile 'com.google.android.gms:play-services-appindexing:9.4.0'";
        textViewHelp.setText(textContentHelp);
        textViewFrame.setText(textContentFrame);

        Toolbar toolbar = (Toolbar) findViewById(R.id.this_app_toolBar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Glide.with(this).load(R.drawable.this_app).into(imageView);
    }

    //处理返回按钮点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
