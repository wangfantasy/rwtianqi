package com.fyp.renwenweather.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.fyp.renwenweather.R;
import com.fyp.renwenweather.datamanager.ConfigManager;
import com.fyp.renwenweather.datamanager.Utils;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import net.sourceforge.pinyin4j.PinyinHelper;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fyp on 2015/12/30.
 */
public class CityChooseActivity extends AppCompatActivity {
    @ViewInject(R.id.searchView)
    SearchView searchView;
    @ViewInject(R.id.cityListView)
    ListView cityListView;
    CityListAdapter adapter = new CityListAdapter();
    private List<LinkedTreeMap> cityList;
    private LocationClient mLocationClient;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_choose);
        x.view().inject(this);
        this.cityList = CityListData.getAllCity();
        cityListView.setAdapter(adapter);
        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ConfigManager.setCurrentCityName(((TextView) view).getText().toString());
                finish();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i("onQueryTextSubmit", query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //忽略大小写
                newText=newText.toLowerCase();
                Log.i("onQueryTextChange", newText);
                List<String> citys = adapter.getList();
                citys.clear();
                String lastCityName = null;
                for (LinkedTreeMap map : cityList) {
                    //遍历城市名称
                    String cityName = map.get("city").toString();
                    //不重复
                    if (cityName.equals(lastCityName)) {
                        continue;
                    }
                    //城市名称不能比用户输入还短
                    if (cityName.length() < newText.length()) {
                        continue;
                    }
                    //如果匹配失败则记为false
                    boolean isMatched = true;
                    for (int i = 0; i < newText.length(); i++) {
                        //拿到汉字的所有读音
                        String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(cityName.charAt(i));
                        //多音字匹配标记
                        boolean multiMatch = false;
                        for (String pinyin : pinyins) {
                            //多个读音只要有一个匹配即认为匹配成功
                            if (pinyin.charAt(0) == newText.charAt(i)) {
                                multiMatch = true;
                                break;
                            }
                        }
                        if (!multiMatch) {
                            isMatched = false;
                        }
                        if (!isMatched) {
                            break;
                        }
                    }
                    if (isMatched) {
                        citys.add(cityName);
                    }
                    lastCityName = cityName;
                }
                adapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    /**
     * 定位之后由服务更新天气并通知所有监听器(包括MainActivity)
     */
    @Event(value = R.id.autoLocationButton,
            type = View.OnClickListener.class)
    private void autoLocationClick(View view) {
        mLocationClient = new LocationClient(x.app());
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("gcj02");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 0;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(false);//可选，默认false,设置是否使用gps
        option.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(false);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(false);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(true);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(final BDLocation location) {
                //通过网络或GPS定位成功
                if (location.getLocType() == BDLocation.TypeGpsLocation || location.getLocType() == BDLocation.TypeNetWorkLocation) {// GPS定位结果
                    //ConfigManager.setCurrentLocation(location.getLatitude(),location.getLongitude());
                    String cityName=location.getCity();
                    cityName=cityName.substring(0,cityName.length()-1);
                    ConfigManager.setCurrentCityName(cityName);
                    finish();
                    Utils.showToast("定位成功");
                } else {
                    Utils.showToast("定位出现问题,获取经纬度失败");
                }
            }
        });
        mLocationClient.start();
    }
    
    static class CityListData {
        static List<LinkedTreeMap> cityList;

        public static List getAllCity() {
            Gson gson = new Gson();
            if (cityList == null) {
                Reader jsonReader = null;
                try {
                    jsonReader = new InputStreamReader(x.app().getAssets().open("cityList.json"));
                    cityList = gson.fromJson(jsonReader, List.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return cityList;
        }

        public static class GsonCityData {

            /**
             * id : 1
             * province : 北京
             * city : 北京
             * district : 北京
             */

            private String id;
            private String province;
            private String city;
            private String district;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getProvince() {
                return province;
            }

            public void setProvince(String province) {
                this.province = province;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getDistrict() {
                return district;
            }

            public void setDistrict(String district) {
                this.district = district;
            }
        }
    }

    private class CityListAdapter extends BaseAdapter {

        List<String> strs = new ArrayList();

        @Override
        public int getCount() {
            return strs.size();
        }

        /**
         * Get the data item associated with the specified position in the data set.
         *
         * @param position Position of the item whose data we want within the adapter's
         *                 data set.
         * @return The data at the specified position.
         */
        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(x.app()).inflate(R.layout.search_tips, null);
            }
            ((TextView) convertView).setText(strs.get(position));
            return convertView;
        }

        public List<String> getList() {
            return strs;
        }
    }
}
