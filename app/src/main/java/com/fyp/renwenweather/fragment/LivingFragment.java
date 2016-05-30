package com.fyp.renwenweather.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fyp.renwenweather.R;
import com.fyp.renwenweather.datamanager.ConfigManager;
import com.fyp.renwenweather.datamanager.Utils;
import com.fyp.renwenweather.entity.Juhe7DaysWeather;
import com.fyp.renwenweather.entity.TotalInfo;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class LivingFragment extends SelfRefreshFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @ViewInject(R.id.list_livingIndex)
    private LinearLayout listLivingIndex;
    @ViewInject(R.id.text_noLivingIndexData)
    private View noData;



    //private OnFragmentInteractionListener mListener;

    public LivingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LivingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LivingFragment newInstance(String param1, String param2) {
        LivingFragment fragment = new LivingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //TODO
        View rootView = inflater.inflate(R.layout.fragment_living, container, false);
        x.view().inject(this, rootView);
        Log.i("ListView is", " " + listLivingIndex);
        refreshContent();
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    @Override
    public void refreshContent() {
        //TODO
        if (listLivingIndex == null) return;
        resetContent();
        TotalInfo totalInfo = (TotalInfo) Utils.readObject(ConfigManager.getCurrentCityName());
        if (totalInfo != null && totalInfo.isComplete()) {
            listLivingIndex.setVisibility(View.VISIBLE);
            noData.setVisibility(View.INVISIBLE);
            Log.i("LF", "刷新生活指数");
            Juhe7DaysWeather.ResultEntity.TodayEntity today = totalInfo.sevenDaysWeather.getResult().getToday();
            //TODO 记得写完生活指数部分
            ((TextView)listLivingIndex.findViewById(R.id.living_dressing).findViewById(R.id.text_details)).setText(today.getDressing_advice());
            ((TextView)listLivingIndex.findViewById(R.id.living_exercise).findViewById(R.id.text_details)).setText(today.getExercise_index());
            ((TextView)listLivingIndex.findViewById(R.id.living_washing).findViewById(R.id.text_details)).setText(today.getWash_index());
        } else {
            Log.i("LF", "刷新生活指数失败");
        }
    }

    public void resetContent() {
        listLivingIndex.setVisibility(View.INVISIBLE);
        noData.setVisibility(View.VISIBLE);
        View dv = listLivingIndex.findViewById(R.id.living_dressing);
        ((ImageView) dv.findViewById(R.id.iv_icon)).setImageResource(R.drawable.ic_chuanyizhishu_white);
        View ev = listLivingIndex.findViewById(R.id.living_exercise);
        ((ImageView) ev.findViewById(R.id.iv_icon)).setImageResource(R.drawable.ic_yundongzhishu_white);
        View wv = listLivingIndex.findViewById(R.id.living_washing);
        ((ImageView) wv.findViewById(R.id.iv_icon)).setImageResource(R.drawable.ic_xichezhishu_white);
        ((TextView)listLivingIndex.findViewById(R.id.living_dressing).findViewById(R.id.text_title)).setText("穿衣指数");
        ((TextView)listLivingIndex.findViewById(R.id.living_exercise).findViewById(R.id.text_title)).setText("运动指数");
        ((TextView)listLivingIndex.findViewById(R.id.living_washing).findViewById(R.id.text_title)).setText("洗车指数");
    }
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p/>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
