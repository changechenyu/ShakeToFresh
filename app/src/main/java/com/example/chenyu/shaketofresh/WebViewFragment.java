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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Think on 2015/11/7.
 */
public class WebViewFragment  extends Fragment implements SensorEventListener {
    private WebView webView;
    private ProgressBar bar;
    private String url = "http://blog.csdn.net/u011068702";
    //定义sensor管理器
    private SensorManager mSensorManager;
    //震动
    private Vibrator vibrator;
    @Override
    public void onResume() {
        super.onResume();
        //加速度传感器
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                //还有SENSOR_DELAY_UI、SENSOR_DELAY_FASTEST、SENSOR_DELAY_GAME等，
                //根据不同应用，需要的反应速率不同，具体根据实际情况设定
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this);
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        int sensorType = event.sensor.getType();
        //values[0]:X轴，values[1]：Y轴，values[2]：Z轴
        float[] values = event.values;
        if(sensorType == Sensor.TYPE_ACCELEROMETER){
  /*因为一般正常情况下，任意轴数值最大就在9.8~10之间，只有在你突然摇动手机

  *的时候，瞬时加速度才会突然增大或减少。

  *所以，经过实际测试，只需监听任一轴的加速度大于14的时候，改变你需要的设置

  *就OK了~~~

  */
            if((Math.abs(values[0])>14||Math.abs(values[1])>14||Math.abs(values[2])>14)) {
                //摇动手机后，设置button上显示的字为空
                new GetDataTask().execute();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //当传感器精度改变时回调该方法，Do nothing.
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.webview_fragment, container, false);
        webView= (WebView) view.findViewById(R.id.webview_fragment);
        bar=(ProgressBar)view.findViewById(R.id.myProgressBar);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    bar.setVisibility(View.INVISIBLE);
                } else {
                    if (View.INVISIBLE == bar.getVisibility()) {
                        bar.setVisibility(View.VISIBLE);
                    }
                    bar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }

        });
        webView.loadUrl(url);
        //获取传感器管理服务
        mSensorManager = (SensorManager) getActivity().getSystemService(Service.SENSOR_SERVICE);
        //震动
        vibrator = (Vibrator) getActivity().getSystemService(Service.VIBRATOR_SERVICE);
        return view;
    }
    private class GetDataTask extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... params) {
            Map<String,Object> map=new HashMap<String,Object>();
            //如果这个地方不使用线程休息的话，刷新就不会显示在那个PullToRefreshListView的UpdatedLabel上面
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setSupportZoom(true);
            //加载需要显示的网页
                vibrator.vibrate(500);
                webView.reload();
        }
    }
}
