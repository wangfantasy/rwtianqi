package com.fyp.renwenweather.datamanager;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import com.fyp.renwenweather.Constants;
import com.fyp.renwenweather.entity.KeyAndValue;

import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.Calendar;
import java.util.List;

/**
 * Created by fyp on 2016/1/19.
 */
public class Utils {
    public static void showToast(String content) {
        Toast.makeText(x.app(), content, Toast.LENGTH_SHORT).show();
    }

    public static synchronized void saveObject(String name, Serializable object) {
        try {
            FileOutputStream fos = x.app().openFileOutput(name, Activity.MODE_PRIVATE);
            Log.i("File", fos.getFD().toString());
            ObjectOutputStream outputStream = new ObjectOutputStream(fos);
            outputStream.writeObject(object);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized Object readObject(String name) {
        try {
            FileInputStream fis = x.app().openFileInput(name);
            ObjectInputStream objectInputStream = new ObjectInputStream(fis);
            Object o = objectInputStream.readObject();
            objectInputStream.close();
            return o;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static synchronized void deleteObject(String name) {
        x.app().deleteFile(name);
    }

    public static CharSequence getCurrentHourAndMinute() {
        int h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int m = Calendar.getInstance().get(Calendar.MINUTE);

        String hour = "";
        String minute = "";
        if (h < 10) {
            hour = "0" + h;
        } else {
            hour += h;
        }
        if (m < 10) {
            minute = "0" + m;
        } else {
            minute += m;
        }
        return hour + ":" + minute;
    }

    public static IntentFilter getIntentFilter(String... actions) {
        IntentFilter filter = new IntentFilter();
        for (String action : actions) {
            filter.addAction(action);
        }
        return filter;
    }

    public static synchronized void saveConfig(String key, String value) {
        try {
            //删除可能存在的旧数据
            WeatherDbManager.getDb().execNonQuery("delete from config where key='" + key + "'");
        } catch (DbException e) {
            e.printStackTrace();
        }
        try {
            WeatherDbManager.getDb().saveOrUpdate(new KeyAndValue(key, value));
        } catch (DbException e) {
            e.printStackTrace();
        }
        Intent i = new Intent();
        i.setAction(Constants.ACTION_CONFIG_CHANGED);
        i.putExtra(Constants.EXTRA_CONFIG_OPTION, key);
        x.app().sendBroadcast(i);
//        if("LATITUDE".equals(key)){
//            Utils.showToast("定位广播已发送");
//        }
        //Utils.showToast("发送广播 action:"+Constants.ACTION_CONFIG_CHANGED+"option:"+key);
    }

    public static String readConfig(String key, String ifNull) {
        try {
            List<KeyAndValue> result = WeatherDbManager.getDb().selector(KeyAndValue.class).where("key", "=", key).findAll();
            if (result != null && result.size() > 0) {
                return result.get(0).getValue();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return ifNull;
    }

    public static void copyFile(File fileFrom, File fileTo) throws IOException {
        //文件复制
        FileInputStream localFileInputStream = new FileInputStream(fileFrom);
        FileOutputStream localFileOutputStream = new FileOutputStream(fileTo);
        byte[] arrayOfByte = new byte[1024];
        while (true) {
            int i = localFileInputStream.read(arrayOfByte);
            if (i == -1)
                break;
            localFileOutputStream.write(arrayOfByte, 0, i);
        }
        localFileInputStream.close();
        localFileOutputStream.close();
    }
}
