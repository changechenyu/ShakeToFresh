package com.example.chenyu.shaketofresh;

import android.app.Fragment;
import android.app.Service;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Think on 2015/11/8.
 */
public class CodeFragment extends Fragment implements SensorEventListener {
    private ImageView imageView;
    private EditText etUrl;
    public static String url;
    //定义sensor管理器
    private SensorManager mSensorManager;
    //震动
    private Vibrator vibrator;
    private int QR_WIDTH = 500;
    private int QR_HEIGHT = 500;

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
        int sensorType = event.sensor.getType();
        //values[0]:X轴，values[1]：Y轴，values[2]：Z轴
        float[] values = event.values;
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
  /*因为一般正常情况下，任意轴数值最大就在9.8~10之间，只有在你突然摇动手机

  *的时候，瞬时加速度才会突然增大或减少。

  *所以，经过实际测试，只需监听任一轴的加速度大于14的时候，改变你需要的设置

  *就OK了~~~

  */
            if ((Math.abs(values[0]) > 14 || Math.abs(values[1]) > 14 || Math.abs(values[2]) > 14)) {
              //  生成二维码
                vibrator.vibrate(500);
                url=etUrl.getText().toString();
                WebViewFragment.url=url;
                createQRImage(url, imageView);
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
        View view=inflater.inflate(R.layout.codeview_fragment, container, false);
        etUrl= (EditText) view.findViewById(R.id.edit_url);
        imageView=(ImageView)view.findViewById(R.id.image_code);
        createQRImage(WebViewFragment.url, imageView);
        //获取传感器管理服务
        mSensorManager = (SensorManager) getActivity().getSystemService(Service.SENSOR_SERVICE);
        //震动
        vibrator = (Vibrator) getActivity().getSystemService(Service.VIBRATOR_SERVICE);
        return view;
    }
    //生成二维码
    public void createQRImage(String url,ImageView imageView) {
        try {
            //判断URL合法性
            if (url == null || "".equals(url) || url.length() < 1) {
                Toast.makeText(getActivity(),"url不能为空",Toast.LENGTH_LONG);
                return;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            //图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            //下面这里按照二维码的算法，逐个生成二维码的图片，
            //两个for循环是图片横列扫描的结果
            for (int y = 0; y < QR_HEIGHT; y++) {
                for (int x = 0; x < QR_WIDTH; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    } else {
                        pixels[y * QR_WIDTH + x] = 0xffffffff;
                    }
                }
            }
            //生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
            //显示到一个ImageView上面
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
