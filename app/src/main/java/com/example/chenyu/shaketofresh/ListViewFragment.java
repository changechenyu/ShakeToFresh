package com.example.chenyu.shaketofresh;

import android.app.Fragment;
import android.app.Service;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Think on 2015/11/7.
 */
public class ListViewFragment extends Fragment implements SensorEventListener {
    List<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
    private String[] mListTitle = {"功能: ", "附带：", "姓名: ", "我的QQ:", "QQ学习群:", "邮箱:"};
    private String[] mListStr = {"手机摇一摇震动刷新", "摇出我的二维码", "陈喻", "2657607916", "319010802", "2657607916@qq.com"};
//    private String[] mListTitle = {"姓名: ", "昵称：", "年龄:", "胸围:","爱好: ", "性格:"};
//    private String[] mListStr = {"陈彩凤", "小恐龙",  "23", "快到D了", "喜欢脱老公的短裤","火爆生猛"};
    private ListView mlistView = null;
    private ListView lv;
    private SimpleAdapter adapter;
    private int i = 0;
    private SensorManager mSensorManager;//定义sensor管理器
    private Vibrator vibrator;           //震动
    private int m=2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listview_fragment, container, false);
        mData = getmData();
        lv = (ListView) view.findViewById(R.id.listview_fragment);
        adapter = new SimpleAdapter(getActivity(), mData, R.layout.simple_list_item, new String[]{"title", "text"}, new int[]{R.id.text1, R.id.text2});
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(),"生日快乐",Toast.LENGTH_SHORT).show();
            }
        });

        //获取传感器管理服务
        mSensorManager = (SensorManager) getActivity().getSystemService(Service.SENSOR_SERVICE);
        //震动
        vibrator = (Vibrator) getActivity().getSystemService(Service.VIBRATOR_SERVICE);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //加速度传感器 注册监听
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                //还有SENSOR_DELAY_UI、SENSOR_DELAY_FASTEST、SENSOR_DELAY_GAME等，
                //根据不同应用，需要的反应速率不同，具体根据实际情况设定
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public List<Map<String, Object>> getmData() {
        for (int i = 0; i < mListTitle.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("title", mListTitle[i]);
            map.put("text", mListStr[i]);
            mData.add(map);
        }
        return mData;
    }
    //可以得到传感器实时测量出来的变化值
    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        int sensorType = event.sensor.getType();
        //values[0]:X轴，values[1]：Y轴，values[2]：Z轴
        float[] values = event.values;
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
               /*因为一般正常情况下，任意轴数值最大就在9.8~10之间，只有在你突然摇动手机
              *的时候，瞬时加速度才会突然增大或减少，所以，经过实际测试，只需监听任一轴的
              * 加速度大于14的时候，改变你需要的设置就OK了
              */
            if ((Math.abs(values[0]) > 14 || Math.abs(values[1]) > 14 || Math.abs(values[2]) > 14)) {
                //摇动手机后，设置button上显示的字为空
                new GetDataTask().execute();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //当传感器精度改变时回调该方法，Do nothing.
    }
    private class GetDataTask extends AsyncTask<Void, Void, Map<String, Object>> {

        @Override
        protected Map<String, Object> doInBackground(Void... params) {

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("title", "我是第--" + (++i) + "--个被摇出来的");
            map.put("text", "");
//            if(m%2==0){
//            map.put("title", "老婆，生日快乐");
//            map.put("text", "");
//                m++;
//            //摇动手机后，再伴随震动提示~~
//            vibrator.vibrate(500);
//            }else{
//                map.put("title", "老婆，我爱你");
//                map.put("text", "");
//                //摇动手机后，再伴随震动提示~~
//                vibrator.vibrate(500);
//                m++;
//            }
            return map;
        }

        @Override
        protected void onPostExecute(Map<String, Object> stringObjectMap) {
            //            super.onPostExecute(stringObjectMap);
            mData.add(stringObjectMap);
            adapter.notifyDataSetChanged();
            // Call onRefreshComplete when the list has been refreshed. 如果没有下面的函数那么刷新将不会停
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //取消注册
        mSensorManager.unregisterListener(this);
    }
}
