package com.sensorcontrol.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sensorcontrol.R;
import com.sensorcontrol.base.BaseActivity;
import com.sensorcontrol.module.ClientManager;
import com.sensorcontrol.ui.fragment.ConfigFragment;
import com.sensorcontrol.ui.fragment.DeviceListFragment;
import com.sensorcontrol.ui.fragment.DisplayFragment;
import com.sensorcontrol.ui.fragment.SendImgFragment;
import com.sensorcontrol.ui.fragment.WifiFragment;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

import static android.support.design.widget.Snackbar.LENGTH_SHORT;

/**
 * Created by lizhe on 2017/9/25 0025.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class MainActivity extends BaseActivity {

    private static final int REQUEST_PERMISSION_BT = 11;
    @BindView(R.id.fragment_context)
    FrameLayout fragmentContext;
    @BindView(R.id.home_page)
    TextView homePage;
    @BindView(R.id.pair)
    TextView pair;
    @BindView(R.id.wifi)
    TextView wifi;
    @BindView(R.id.send_img)
    TextView send_img;
    @BindView(R.id.rl_all)
    RelativeLayout rl_all;


    private FragmentManager mFragmentManager;
    private Set<Fragment> set;
    private String showFragmentTag;
    public static final String HOMEPAGE = "homepage";
    public static final String CONFIG = "config";
    public static final String WIFI = "wifi";
    public static final String DEVICE = "device";
    public static final String SENDIMG = "send";
    @Override
    protected int setLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        homePage.setSelected(true);
        setShowFragment(HOMEPAGE);
    }

    @Override
    protected void setData() {

    }

    @OnClick({R.id.home_page, R.id.pair, R.id.wifi,R.id.send_img})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.home_page:
                clickAnimation(homePage);
                setBtn(HOMEPAGE);
                setShowFragment(HOMEPAGE);
                break;
            case R.id.pair:
                clickAnimation(pair);
                setBtn(CONFIG);
                setShowFragment(CONFIG);
                break;
            case R.id.wifi:
                clickAnimation(wifi);
                setBtn(WIFI);
                setShowFragment(DEVICE);
                break;
            case R.id.send_img:
                setBtn(SENDIMG);
                clickAnimation(send_img);
                setShowFragment(SENDIMG);
                break;
        }
    }

    public void setShowFragment(String tag){
        if (showFragmentTag !=null && showFragmentTag.equals(tag)){
            return;
        }
        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        //转场动画
//        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        Fragment hideFragment = mFragmentManager.findFragmentByTag(showFragmentTag);
        if (hideFragment != null){
            fragmentTransaction.hide(hideFragment);
        }

        Fragment showFragment = mFragmentManager.findFragmentByTag(tag);
        if (showFragment == null){
            switch (tag){
                case HOMEPAGE:
                    showFragment = new DisplayFragment();
                    break;
                case CONFIG:
                    showFragment = new ConfigFragment();
                    break;
                case WIFI:
                    showFragment = new WifiFragment();
                    break;
                case DEVICE:
                    showFragment = new DeviceListFragment();
                    break;
                case SENDIMG:
                    showFragment = new SendImgFragment();
                    break;
            }
        }

        if (showFragment == null){

        }else if (showFragment.isAdded()){
            fragmentTransaction.show(showFragment);
        }else {
            fragmentTransaction.add(R.id.fragment_context,showFragment,tag);
            if (set == null){
                set = new HashSet<>();
            }
            set.add(showFragment);
        }
        fragmentTransaction.commit();
        showFragmentTag = tag;

    }

    public void setBtn(String btn) {
        switch (btn){
            case HOMEPAGE:
                homePage.setSelected(true);
                pair.setSelected(false);
                wifi.setSelected(false);
                send_img.setSelected(false);
                break;
            case CONFIG:
                homePage.setSelected(false);
                pair.setSelected(true);
                wifi.setSelected(false);
                send_img.setSelected(false);
                break;
            case WIFI:
                homePage.setSelected(false);
                pair.setSelected(false);
                wifi.setSelected(true);
                send_img.setSelected(false);
                break;
            case SENDIMG:
                send_img.setSelected(true);
                homePage.setSelected(false);
                pair.setSelected(false);
                wifi.setSelected(false);
                break;
        }
    }

    private static Boolean isQuit = false;
    private Timer timer = new Timer();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (Fragment f : set) {
            mFragmentManager.beginTransaction().remove(f);
        }
        ClientManager.getClient().closeBluetooth();
    }

    @Override
    public void onBackPressed() {
        if (isQuit == false) {
            isQuit = true;
            Snackbar.make(fragmentContext,"再按一次注销",LENGTH_SHORT).show();
            TimerTask task = null;
            task = new TimerTask() {
                @Override
                public void run() {
                    isQuit = false;
                }
            };
            timer.schedule(task, 2000);
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 77){
            Uri uri = data.getData();
            EventBus.getDefault().post(uri);
        }
        if (requestCode == 1) {
            String path = saveCameraImage(data);
            Intent intent = new Intent(this,SendActivity.class);
            intent.putExtra("path",path);
            startActivity(intent);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();
    }

    // onResume 中进行调用
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Snackbar.make(rl_all,"权限已经被禁用",LENGTH_SHORT).show();
                return;
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_BT);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_BT:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(rl_all,"请求权限成功",LENGTH_SHORT).show();
                } else {
                    Snackbar.make(rl_all,"权限已经被禁用",LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    /** 保存相机的图片 **/
    private String saveCameraImage(Intent data) {
        // 检查sd card是否存在
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.i("saveCameraImage", "sd card is not avaiable/writeable right now.");
            return null;
        }
        // 为图片命名啊
        String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String name = date + ".jpg";
        Bitmap bmp = (Bitmap) data.getExtras().get("data");// 解析返回的图片成bitmap

        // 保存文件
        FileOutputStream fos = null;
        File file = new File("/mnt/sdcard/test/");
        file.mkdirs();// 创建文件夹
        String fileName = "/mnt/sdcard/test/" + name;// 保存路径

        try {// 写入SD card
            fos = new FileOutputStream(fileName);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.flush();
                fos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return fileName;
    }

}
