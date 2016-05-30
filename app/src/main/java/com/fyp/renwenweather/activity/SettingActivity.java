package com.fyp.renwenweather.activity;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

import com.fyp.renwenweather.Constants;
import com.fyp.renwenweather.R;
import com.fyp.renwenweather.datamanager.ConfigManager;
import com.fyp.renwenweather.datamanager.Utils;
import com.fyp.renwenweather.datamanager.WeatherDbManager;
import com.fyp.renwenweather.entity.HourAndMinute;
import com.fyp.renwenweather.widget.CheckBoxItemView;
import com.fyp.renwenweather.widget.SimpleSelectItemView;
import com.github.danielnilsson9.colorpickerview.view.ColorPickerView;

import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class SettingActivity extends AppCompatActivity implements CheckBoxItemView.CheckBoxClickedListener {
    @ViewInject(R.id.item_stay_in_notification)
    CheckBoxItemView checkStayInNotification;
    @ViewInject(R.id.item_weather_remind)
    CheckBoxItemView weatherRemind;
    @ViewInject(R.id.item_weather_remind_morning)
    CheckBoxItemView weatherRemindMorning;
    @ViewInject(R.id.item_weather_remind_evening)
    CheckBoxItemView weatherRemindEvening;
    @ViewInject(R.id.item_weather_remind_morning_time)
    SimpleSelectItemView weatherRemindMorningTime;
    @ViewInject(R.id.item_weather_remind_evening_time)
    SimpleSelectItemView weatherRemindEveningTime;
    @ViewInject(R.id.item_update_frequency)
    SimpleSelectItemView updateFrequency;
    @ViewInject(R.id.item_auto_update_weather)
    CheckBoxItemView autoUpdateWeather;
    @ViewInject(R.id.item_default_display)
    SimpleSelectItemView defaultDisplay;
    private int REQUEST_BACKGROUND_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        x.view().inject(this);
        checkStayInNotification.setOnCheckboxClickedListener(this);
        autoUpdateWeather.setOnCheckboxClickedListener(this);
        weatherRemind.setOnCheckboxClickedListener(this);
        weatherRemindMorning.setOnCheckboxClickedListener(this);
        weatherRemindEvening.setOnCheckboxClickedListener(this);
        autoUpdateWeather.setOnCheckboxClickedListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //TODO 读取配置
        checkStayInNotification.setCheckBox(ConfigManager.getStayInNotification());
        weatherRemind.setCheckBox(ConfigManager.getWeatherRemindOn());
        weatherRemindMorning.setCheckBox(ConfigManager.getWeatherRemindMorningOn());
        weatherRemindEvening.setCheckBox(ConfigManager.getWeatherRemindEveningOn());
        autoUpdateWeather.setCheckBox(ConfigManager.getAutoUpdateWeatherIsEnabled());
        int morningHour = 8, morningMinute = 0, eveningHour = 18, eveningMinute = 0;
        try {
            HourAndMinute hourAndMinute = WeatherDbManager.
                    getDb().
                    selector(HourAndMinute.class).
                    where("id", "=", 1).findFirst();
            if (hourAndMinute == null) {
                WeatherDbManager.getDb().saveOrUpdate(new HourAndMinute(1, 8, 0));
            } else {
                morningHour = hourAndMinute.getHourOfDay();
                morningMinute = hourAndMinute.getMinute();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        try {
            HourAndMinute hourAndMinute = WeatherDbManager.
                    getDb().
                    selector(HourAndMinute.class).
                    where("id", "=", 2).findFirst();
            if (hourAndMinute == null) {
                WeatherDbManager.getDb().saveOrUpdate(new HourAndMinute(2, 18, 0));
            } else {
                eveningHour = hourAndMinute.getHourOfDay();
                eveningMinute = hourAndMinute.getMinute();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        defaultDisplay.setDesc(Constants.DEFAULT_DISPLAY_OPTIONS[ConfigManager.getDefaultDisplay()]);
        weatherRemindMorningTime.setDesc((morningHour < 9 ? "0" + morningHour : "" + morningHour) + ":" + (morningMinute < 9 ? "0" + morningMinute : "" + morningMinute));
        weatherRemindEveningTime.setDesc((eveningHour < 9 ? "0" + eveningHour : "" + eveningHour) + ":" + (eveningMinute < 9 ? "0" + eveningMinute : "" + eveningMinute));
        updateFrequency.setDesc("每" + ConfigManager.getUpdateFrequency() + "小时更新一次");
    }

    @Override
    public void onCheckBoxClicked(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.item_stay_in_notification:
                ConfigManager.setStayInNotification(checkStayInNotification.isChecked());
                break;
            case R.id.item_weather_remind:
                if (weatherRemind.isChecked()) {
                    weatherRemindMorning.setVisibility(View.VISIBLE);
                    weatherRemindMorningTime.setVisibility(View.VISIBLE);
                    weatherRemindEvening.setVisibility(View.VISIBLE);
                    weatherRemindEveningTime.setVisibility(View.VISIBLE);
                    ConfigManager.setWeatherRemindOn(true);
                } else {
                    weatherRemindMorning.setVisibility(View.GONE);
                    weatherRemindMorningTime.setVisibility(View.GONE);
                    weatherRemindEvening.setVisibility(View.GONE);
                    weatherRemindEveningTime.setVisibility(View.GONE);
                    ConfigManager.setWeatherRemindOn(false);
                }
                break;
            case R.id.item_weather_remind_morning:
                //启用早晨天气提醒
                ConfigManager.setWeatherRemindMorningOn(weatherRemindMorning.isChecked());
                break;
            case R.id.item_weather_remind_evening:
                //启用晚间天气提醒
                ConfigManager.setWeatherRemindEveningOn(weatherRemindEvening.isChecked());
                break;
            case R.id.item_auto_update_weather:
                ConfigManager.setAutoUpdateWeatherIsEnabled(autoUpdateWeather.isChecked());
        }
    }

    @Event(R.id.item_weather_remind_morning_time)
    private void setMorningRemindTimeEvent(View view) {
        new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Log.i("morning time set", hourOfDay + ":" + minute);
                try {
                    //早晨的id是1
                    WeatherDbManager.getDb().saveOrUpdate(new HourAndMinute(1, hourOfDay, minute));
                } catch (DbException e) {
                    e.printStackTrace();
                }
                weatherRemindMorningTime.setDesc((hourOfDay > 9 ? "" + hourOfDay : "0" + hourOfDay) + ":" + (minute > 9 ? "" + minute : "0" + minute));
            }
        }, 7, 0, true).show();
    }

    @Event(R.id.item_weather_remind_evening_time)
    private void setEveningRemindTimeEvent(View view) {
        new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Log.i("evening time set", hourOfDay + ":" + minute);
                try {
                    //晚上的id是2
                    WeatherDbManager.getDb().saveOrUpdate(new HourAndMinute(2, hourOfDay, minute));
                } catch (DbException e) {
                    e.printStackTrace();
                }
                weatherRemindEveningTime.setDesc((hourOfDay > 9 ? "" + hourOfDay : "0" + hourOfDay) + ":" + (minute > 9 ? "" + minute : "0" + minute));
            }
        }, 18, 0, true).show();
    }

    @Event(R.id.item_update_frequency)
    private void setUpdateFrequenceEvent(View view) {
        int f = ConfigManager.getUpdateFrequency();
        int init = 0;
        switch (f) {
            case 6:
                init = 4;
                break;
            case 12:
                init = 5;
                break;
            case 24:
                init = 6;
                break;
            default:
                init = f - 1;
                break;
        }
        new AlertDialog.Builder(this).setTitle("选择一个更新间隔").setSingleChoiceItems(
                new String[]{"1小时", "2小时", "3小时", "4小时", "6小时", "12小时", "24小时"}, init, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("间隔", which + "");
                        dialog.dismiss();
                        int h;
                        switch (which) {
                            case 0:
                                h = 1;
                                break;
                            case 1:
                                h = 2;
                                break;
                            case 2:
                                h = 3;
                                break;
                            case 3:
                                h = 4;
                                break;
                            case 4:
                                h = 6;
                                break;
                            case 5:
                                h = 12;
                                break;
                            default:
                                h = 24;
                        }
                        ConfigManager.setUpdateFrequency(h);
                        updateFrequency.setDesc("每" + h + "小时更新一次");
                    }
                })
                .show();
    }

    @Event(R.id.item_default_display)
    private void setDefaultDisplayClick(View view) {
        new AlertDialog.Builder(this).setTitle("选择默认显示项").setSingleChoiceItems(
                Constants.DEFAULT_DISPLAY_OPTIONS, ConfigManager.getDefaultDisplay(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ConfigManager.setDefaultDisplay(which);
                        defaultDisplay.setDesc(Constants.DEFAULT_DISPLAY_OPTIONS[which]);
                    }
                })
                .show();
    }

    @Event(R.id.item_content_background)
    private void setHomepageBackgroundClick(View view) {

        new AlertDialog.Builder(this).setTitle("选择主界面背景").setPositiveButton("选择图片", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent ;
                if(Build.VERSION.SDK_INT<19){
                    intent= new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                }else {
                    intent=new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                }
                intent.setType("image/*");
                intent.putExtra("crop", true);
                intent.putExtra("return-data", true);
                SettingActivity.this.startActivityForResult(intent, REQUEST_BACKGROUND_IMAGE);
            }
        }).setNegativeButton("清除设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ConfigManager.setContentBackgroundURI("");
                Utils.showToast("清除成功");
            }
        }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_BACKGROUND_IMAGE) {
            //Utils.showToast(data.toUri(Intent.URI_ANDROID_APP_SCHEME));
            Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
            if(cursor==null){
                Utils.showToast("未选择任何图片");
                return;
            }
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                System.out.println(i+"-----------------"+cursor.getString(i));
            };
            ConfigManager.setContentBackgroundURI(data.getData().toString());
            Utils.showToast("设置成功");
            cursor.close();
        } else {
            Utils.showToast("未选择任何图片");
        }
    }

    @Event(R.id.item_notification_text_color)
    private void setNotificationTextColorClick(View view) {
        final ColorPickerView colorPickerView = new ColorPickerView(getApplicationContext());
        colorPickerView.setAlphaSliderVisible(true);
        colorPickerView.setAlphaSliderText("透明度");
        colorPickerView.setVerticalScrollBarEnabled(true);
        new AlertDialog.Builder(this).setTitle("选择通知栏字体颜色").
                setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ConfigManager.setNotificationTextColor(colorPickerView.getColor());
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setNeutralButton("恢复默认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ConfigManager.setNotificationTextColor(-1);
            }
        }).setView(colorPickerView).show();
    }
}

