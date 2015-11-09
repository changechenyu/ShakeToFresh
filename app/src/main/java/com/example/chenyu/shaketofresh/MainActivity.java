package com.example.chenyu.shaketofresh;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private TextView tvListView;  //侧滑菜单listView
    private TextView tvGridView;  //侧滑菜单GridView
    private TextView tvWebview;   //侧滑菜单WebView
    private TextView tvCode;      //侧滑菜单我的二维码
    private ArrayList<TextView> textViews;
    private static SlidingMenu menu;
    private ListView slidingLv;   //侧滑菜单
    private SensorManager mSensorManager;//定义sensor管理器
    private Vibrator vibrator;    //震动

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化界面
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ListViewFragment listViewFragment = new ListViewFragment();
        ft.replace(R.id.content, listViewFragment);
        ft.commit();
        //初始左侧菜单
        initSlidingMenu();
    }

    private void initSlidingMenu() {
        //侧滑菜单
        menu = new SlidingMenu(MainActivity.this); // 实例化滑动菜单对象
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        //  menu.setShadowWidthRes(R.dimen.shadow_width); // 设置菜单边缘的渐变颜色宽度 （阴影效果宽度）
        //   menu.setShadowDrawable(R.drawable.slidingmenu_shadow); // 设置滑动阴影的图像资源
        //  menu.setBehindOffsetRes(R.dimen.slidingmenu_offset); // 设置滑动菜单视图的宽度
        menu.setFadeDegree(0.35f);// 边框的角度，这里指边界地方（设置渐入渐出效果的值 ）
        menu.attachToActivity(MainActivity.this, SlidingMenu.SLIDING_CONTENT); // 把侧滑栏关联到当前的Activity
        menu.setMenu(R.layout.slidingmenu);// 设置当前的视图
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        long screenWidth = metric.widthPixels;// 获取屏幕的宽度
        menu.setBehindWidth((int) (screenWidth * 0.4));// 设置左页的宽度
        View view = menu.getMenu();
        findMenuViews(view);//
//        menu.showMenu();  如果想一进入界面，想把左侧菜单显示出来，用这个函数
    }

    /**
     * 把侧滑菜单的控件初始化
     * @param view
     */
    private void findMenuViews(View view) {
        tvListView = (TextView) view.findViewById(R.id.menu_listview);
        tvGridView = (TextView) view.findViewById(R.id.menu_gridview);
        tvWebview = (TextView) view.findViewById(R.id.menu_webview);
        tvCode = (TextView) view.findViewById(R.id.menu_code);

        tvListView.setOnClickListener(new MyOnClickListener());
        tvGridView.setOnClickListener(new MyOnClickListener());
        tvWebview.setOnClickListener(new MyOnClickListener());
        tvCode.setOnClickListener(new MyOnClickListener());

    }

    /**
     * 改变每次点击左侧菜单的颜色
     * @param textView
     */
    public void changeTextColor(TextView textView) {
        textViews = new ArrayList<TextView>();
        textViews.add(tvGridView);
        textViews.add(tvWebview);
        textViews.add(tvListView);
        textViews.add(tvCode);
        for (int i = 0; i < textViews.size(); i++) {
            if (textViews.get(i).equals(textView)) {
                textView.setTextColor(Color.GREEN);
            } else {
                textViews.get(i).setTextColor(Color.WHITE);
            }
        }
    }

    class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            switch (v.getId()) {
                case R.id.menu_listview:
                    changeTextColor(tvListView);
                    ListViewFragment listViewFragment = new ListViewFragment();
                    ft.replace(R.id.content, listViewFragment);
                    break;
                case R.id.menu_gridview:
                    changeTextColor(tvGridView);
                    GridViewFragment gridViewFragment = new GridViewFragment();
                    ft.replace(R.id.content, gridViewFragment);
                    break;
                case R.id.menu_webview:
                    changeTextColor(tvWebview);
                    WebViewFragment webViewFragment = new WebViewFragment();
                    ft.replace(R.id.content, webViewFragment);
                    break;
                case R.id.menu_code:
                    changeTextColor(tvCode);
                    CodeFragment codeFragment = new CodeFragment();
                    ft.replace(R.id.content, codeFragment);
                    break;
                default:
                    break;
            }
            ft.commit();
        }
    }
}
