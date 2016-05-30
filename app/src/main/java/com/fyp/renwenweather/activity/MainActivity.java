package com.fyp.renwenweather.activity;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fyp.renwenweather.Constants;
import com.fyp.renwenweather.R;
import com.fyp.renwenweather.datamanager.ConfigManager;
import com.fyp.renwenweather.datamanager.Utils;
import com.fyp.renwenweather.entity.Juhe7DaysWeather;
import com.fyp.renwenweather.entity.TotalInfo;
import com.fyp.renwenweather.fragment.CurrentFragment;
import com.fyp.renwenweather.fragment.ForecastFragment;
import com.fyp.renwenweather.fragment.LivingFragment;
import com.fyp.renwenweather.fragment.SelfRefreshFragment;
import com.fyp.renwenweather.service.WeatherLoaderService;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    @ViewInject(R.id.text_current_temperature)
    TextView textCurrentTemperature;
    @ViewInject(R.id.text_weather_type_and_tips)
    TextView textWeatherType;
    @ViewInject(R.id.text_humidity)
    TextView textHumidity;
    @ViewInject(R.id.text_update_time)
    TextView textUpdateTime;
    @ViewInject(R.id.text_pm25)
    TextView textPm25;
    @ViewInject(R.id.text_uv)
    TextView textUv;
    @ViewInject(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @ViewInject(R.id.dynamic_background)
    GifImageView gifImageView;
    @ViewInject(R.id.appbar)
    AppBarLayout appBarLayout;
    @ViewInject(R.id.content_head)
    LinearLayout contentHead;
    @ViewInject(R.id.text_homeWeatherTip)
    TextView homeWeatherTip;
    @ViewInject(R.id.homeSlideArea)
    LinearLayout homeSlideArea;
    @ViewInject(R.id.toolbar)
    private Toolbar toolbar;
    @ViewInject(R.id.tab_layout)
    private TabLayout mTabLayout;
    @ViewInject(R.id.view_pager)
    private ViewPager mViewPager;
    private MyFragmentPagerAdapter mPagerAdapter;
    //监听天气更新并刷新界面

    private BroadcastReceiver mWeatherUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final TotalInfo totalInfo = (TotalInfo) intent.getSerializableExtra(Constants.EXTRA_TOTALINFO);
            collapsingToolbarLayout.setTitle(ConfigManager.getCurrentCityName());
            boolean customBackgroundLoadSuccess = loadSpecifiedImageAsBackground();
            if (totalInfo != null && totalInfo.isComplete()) {
                //天气读取成功
                textCurrentTemperature.setText(totalInfo.sevenDaysWeather.getResult().getSk().getTemp() + "℃");
                textWeatherType.setText(totalInfo.sevenDaysWeather.getResult().getToday().getWeather());
                textHumidity.setText(totalInfo.sevenDaysWeather.getResult().getSk().getHumidity());
                textUpdateTime.setText(totalInfo.sevenDaysWeather.getResult().getSk().getTime() + "发布");
                textPm25.setText(totalInfo.airStatus.getResult().get(0).getPM25());
                textUv.setText(totalInfo.sevenDaysWeather.getResult().getToday().getUv_index());
                mPagerAdapter.reloadAllPages();
                String weather = totalInfo.sevenDaysWeather.getResult().getToday().getWeather();
                if (weather.contains("雪")) {
                    homeWeatherTip.setText("下雪啦，注意防寒保暖哦");
                } else if (weather.contains("雨")) {
                    homeWeatherTip.setText("下雨天出门记得带伞哦");
                } else if (weather.contains("阴")) {
                    homeWeatherTip.setText("多云天气，适合出行");
                } else {
                    homeWeatherTip.setText("天气晴好，可以安心出行");
                }
                if (customBackgroundLoadSuccess) {
                    //使用的是用户的自定义背景
                } else {
                    //没有设置自定义背景
                    if (weather.contains("雪")) {
                        gifImageView.setImageResource(R.drawable.dynamic_snow);
                    } else if (weather.contains("雨")) {
                        gifImageView.setImageResource(R.drawable.dynamic_rain);
                    } else if (weather.contains("阴")) {
                        gifImageView.setImageResource(R.drawable.dynamic_cloud);
                    } else {
                        gifImageView.setImageResource(R.drawable.dynamic_clear);
                    }
                }
                homeSlideArea.setVisibility(View.VISIBLE);
                if (!totalInfo.isFresh(ConfigManager.getUpdateFrequency() * 3600)) {
                    //信息已过期
                    Utils.showToast("天气信息已过期，请联网更新以保证准确性");
                }
            } else {
                homeSlideArea.setVisibility(View.INVISIBLE);
                homeWeatherTip.setText("天气加载失败");
                mPagerAdapter.showNothing();
                //最好在加载失败时不要黑屏
                gifImageView.setImageResource(R.drawable.load_fail);
            }
        }
    };
    //用来监听上拉调节内容透明度
    private AppBarLayout.OnOffsetChangedListener mOffsetChangedListener = new AppBarLayout.OnOffsetChangedListener() {
        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            float range = appBarLayout.getTotalScrollRange();
            //Log.i("verticalOffset", verticalOffset + "");
            contentHead.setAlpha((range + verticalOffset) / range);
            if (verticalOffset == 0) {
                contentHead.setEnabled(true);
                appBarLayout.setEnabled(true);
            } else {
                contentHead.setEnabled(false);
                appBarLayout.setEnabled(false);
            }
        }
    };
    private long lastBackPressTime;

    /**
     * 加载用户指定的图片作为背景
     *
     * @return 设置成功true/失败false
     * TODO 还是换个实现方法吧，这个有BUG
     */
    private boolean loadSpecifiedImageAsBackground() {
//        File file = new File(Environment.getExternalStorageDirectory(), ".renwenweather/homebg.png");
//        if (!file.exists()){
//            return false;
//        }
        String u = ConfigManager.getContentBackgroundURI();
        if (TextUtils.isEmpty(u)) {
            return false;
        }
        Uri uri = Uri.parse(u);
        if (uri != null) {
            try {
                gifImageView.setImageURI(uri);
            } catch (Exception e) {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            Log.i("MainActivity", "启用透明通知栏");
        }
        setContentView(R.layout.activity_main);
        x.view().inject(this);
        gifImageView.setBackgroundColor(Color.BLACK);
        Intent intent = new Intent(this, WeatherLoaderService.class);
        startService(intent);
        toolbar.setTitle(ConfigManager.getCurrentCityName());
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(0);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(mPagerAdapter);
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mViewPager.setCurrentItem(ConfigManager.getDefaultDisplay());
        registerReceiver(mWeatherUpdateReceiver, Utils.getIntentFilter(Constants.ACTION_WEATHER_UPDATED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        appBarLayout.removeOnOffsetChangedListener(mOffsetChangedListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ConfigManager.setCurrentCityName(ConfigManager.getCurrentCityName());
        appBarLayout.addOnOffsetChangedListener(mOffsetChangedListener);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long now = System.currentTimeMillis();
            if (now - lastBackPressTime > 2000) {
                Utils.showToast("再按一次退出");
                lastBackPressTime = now;
                return true;
            } else {
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mWeatherUpdateReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_3hour_forecast) {
            startActivity(new Intent(this, ThreeHourWeatherActivity.class));
        } else if (id == R.id.nav_setting) {
            startActivity(new Intent(this, SettingActivity.class));
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(this, AboutActivity.class));
        } else if (id == R.id.nav_contact_us) {
            startActivity(new Intent(this, ContactActivity.class));
        } else if (id == R.id.nav_share) {
            //TODO
            TotalInfo totalInfo = (TotalInfo) Utils.readObject(ConfigManager.getCurrentCityName());
            if (totalInfo != null && totalInfo.isComplete()) {
                Juhe7DaysWeather.ResultEntity.FutureEntity tomorrow = totalInfo.sevenDaysWeather.getResult().getFuture().get(1);
                StringBuffer toBeSend = new StringBuffer();
                toBeSend.append("人文天气提醒您:").append(ConfigManager.getCurrentCityName()).append("明天的天气为").append(tomorrow.getWeather()).append(",温度为").append(tomorrow.getTemperature());
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, toBeSend.toString());
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Utils.showToast("没有可以分享的方式");
                }
            } else {
                Utils.showToast("天气更新失败，无法分享哦");
            }
        } else if (id == R.id.nav_choose_city) {
            startActivity(new Intent(this, CityChooseActivity.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        SelfRefreshFragment[] fragmentList = new SelfRefreshFragment[]{new CurrentFragment(), new ForecastFragment(), new LivingFragment()};

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "实时天气";
            }
            if (position == 1) {
                return "未来天气";
            }
            return "生活指数";
        }

        @Override
        public SelfRefreshFragment getItem(int position) {
            return fragmentList[position];
        }

        @Override
        public int getCount() {
            return 3;
        }

        public void reloadAllPages() {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            for (Fragment fragment : fragments) {
                ((SelfRefreshFragment) fragment).refreshContent();
            }
        }

        public void showNothing() {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            for (Fragment fragment : fragments) {
                ((SelfRefreshFragment) fragment).resetContent();
            }
        }
    }
}
