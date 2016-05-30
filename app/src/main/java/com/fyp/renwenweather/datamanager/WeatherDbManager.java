package com.fyp.renwenweather.datamanager;

import org.xutils.DbManager;
import org.xutils.x;

/**
 * Created by fyp on 2016/3/11.
 */
public class WeatherDbManager {
    private static DbManager db;
    static {
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig();
        daoConfig.setDbName("WeatherDB")
                .setAllowTransaction(true)
                .setDbDir(x.app().getFilesDir())
                .setDbVersion(1);
        db = x.getDb(daoConfig);
    }
    public static DbManager getDb(){
        return db;
    }
}
