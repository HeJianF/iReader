package io.github.marktony.reader.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import io.github.marktony.reader.R;
import io.github.marktony.reader.util.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.R.attr.bitmap;
import static android.R.attr.path;
import static com.bumptech.glide.Glide.with;

public class MainActivity extends AppCompatActivity {

    private ImageView bingPicImg;
    private ImageView navBingPicImg;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private String bingPic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏化,未达到要求
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_main);

        //初始化控件
        init();

        //对抽屉中item的管理
        navItem();

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        //加载图片
        loadBIngPicture();

        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new JokePageFragment()).commit();
    }

    private void navItem() {
        navigationView.setCheckedItem(R.id.nav_home);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nac_save_BingPic:
                        loadBitMap();
                        drawerLayout.closeDrawers();
                        Toast.makeText(MainActivity.this, "图片保存成功！", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nac_newBic:
                        loadBingPic();
                        Toast.makeText(MainActivity.this, "背景图片每天只能换一次奥！", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void init() {
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        navBingPicImg = (ImageView) findViewById(R.id.nav_bing_pic_img);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.activity_main);
    }

    //保存图片
    public static void saveImageToGallery(Context context, Bitmap bmp) {
        Log.d("main", "saveImageToGallery: " + bitmap);
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "DuanZi");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
    }

    private void loadBIngPicture() {
        //获取默认的存储
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //加载图片背景
        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null) {
            with(this).load(bingPic).into(bingPicImg);
            with(this).load(bingPic).into(navBingPicImg);
        } else {
            loadBingPic();
        }

    }

    public void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.
                        getDefaultSharedPreferences(MainActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        with(MainActivity.this).load(bingPic).into(bingPicImg);
                        with(MainActivity.this).load(bingPic).into(navBingPicImg);
                    }
                });
            }
        });
    }

    public void loadBitMap() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bit;
                try {
                    bit = Glide.with(MainActivity.this).load(bingPic).asBitmap().into(500, 500).get();
                    saveImageToGallery(MainActivity.this, bit);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}