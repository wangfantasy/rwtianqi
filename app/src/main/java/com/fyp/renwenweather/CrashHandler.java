package com.fyp.renwenweather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.fyp.renwenweather.activity.CrashActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fyp on 2016/4/12.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {


    public static final String TAG = CrashHandler.class.getCanonicalName();
    // CrashHandler实例
    private static CrashHandler INSTANCE = new CrashHandler();
    // 系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    // 程序的Context对象
    private Context mContext;
    // 用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<String, String>();

    // 用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
    private String stackTrace;

    /**
     * 保证只有一个实例
     */
    private CrashHandler() {
    }

    /**
     * 获取实例 ，单例模式
     */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 获取捕捉到的异常的字符串
     */
    public static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }

        Throwable t = tr;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "";
            }
            t = t.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            // 退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
//        // 使用Toast来显示异常信息
//        new Thread()
//        {
//            @Override
//            public void run()
//            {
//                Looper.prepare();
//                Toast.makeText(mContext, "很抱歉，程序出现异常。", Toast.LENGTH_LONG)
//                        .show();
//                Looper.loop();
//
//            }
//        }.start();

        // 收集设备参数信息
        collectDeviceInfo(mContext);
        String filePathAndName = saveLogToFile(ex);
        askForUploadLog(filePathAndName);
        return true;
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null"
                        : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }

        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }

    /**
     * 询问用户是否允许上传错误日志
     *
     * @param ex
     * @return 文件的全路径
     */
    private String saveLogToFile(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet())
        {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append("[" + key + ", " + value + "]\n");
        }

        sb.append("\n" + getStackTraceString(ex));

        try
        {
            String time = formatter.format(new Date());

            TelephonyManager mTelephonyMgr = (TelephonyManager) mContext
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String imei = mTelephonyMgr.getDeviceId();
            if (TextUtils.isEmpty(imei))
            {
                imei = "unknownimei";
            }

            String fileName = "CRS_" + time + "_" + imei + ".txt";

//            File sdDir = null;
//
//            if (Environment.getExternalStorageState().equals(
//                    android.os.Environment.MEDIA_MOUNTED))
//                sdDir = Environment.getExternalStorageDirectory();

            File cacheDir = mContext.getExternalCacheDir();

            if (!cacheDir.exists())
                cacheDir.mkdir();

            File filePath = new File(cacheDir + File.separator + fileName);

            FileOutputStream fos = new FileOutputStream(filePath);

            fos.write(sb.toString().getBytes());
            fos.close();

            return cacheDir + File.separator + fileName;
        }
        catch (Exception e)
        {
            Log.e(TAG, "an error occured while writing file...", e);
        }
        return null;
    }

    private void askForUploadLog(String filePathAndName) {
        Intent intent = new Intent(mContext, CrashActivity.class);
        intent.putExtra("filePathName", filePathAndName);
        PendingIntent restartIntent = PendingIntent.getActivity(mContext, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        //退出程序
        AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 500,
                restartIntent); // 半秒钟后请求发送错误报告
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
