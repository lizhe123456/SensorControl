package com.sensorcontrol.ui.fragment;

import android.app.ProgressDialog;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizWifiConfigureMode;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.enumration.GizWifiGAgentType;
import com.gizwits.gizwifisdk.listener.GizWifiDeviceListener;
import com.gizwits.gizwifisdk.listener.GizWifiSDKListener;
import com.sensorcontrol.R;
import com.sensorcontrol.app.Constants;
import com.sensorcontrol.base.BaseFragment;
import com.sensorcontrol.util.ErrorHandleUtil;
import com.sensorcontrol.view.ListDialog;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import butterknife.BindView;
import butterknife.OnClick;
import static android.content.Context.WIFI_SERVICE;
import static com.gizwits.gizwifisdk.enumration.GizWifiErrorCode.GIZ_SDK_DEVICE_CONFIG_IS_RUNNING;

/**
 * Created by lizhe on 2017/10/9 0009.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class WifiFragment extends BaseFragment {

    @BindView(R.id.ll_all)
    LinearLayout mALL;

    private static final int HARDWARE = 1;
    @BindView(R.id.ed_wifi_name)
    EditText edWifiName;
    @BindView(R.id.ed_wifi_pass)
    EditText edWifiPass;
    private String mUid;
    private String mTohen;

    private List<GizWifiGAgentType> types;
    private String wifiName;
    private String wifiKey;
    private GizWifiDevice mDevice;
    private List<GizWifiDevice> mDevices;
    private ProgressDialog dialog;
    private ListDialog mListDialog;

    private GizWifiSDKListener mListener = new GizWifiSDKListener() {

        //等待配置完成或超时，回调配置完成接口
        @Override
        public void didSetDeviceOnboarding(GizWifiErrorCode result, GizWifiDevice device) {
            Log.d("静茹","诗圣杜甫");

            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                // 配置成功
                if (mDevices == null) {
                    mDevices = new ArrayList<>();
                    mDevices.add(device);
                }
                mDevices.add(device);
                mDevice = device;
                boundDevices();
                subscribeDevices();
                Log.d("配置成功  ","");
                dialog.dismiss();
            } else if (result == GIZ_SDK_DEVICE_CONFIG_IS_RUNNING) {
                // 正在配置
                Toast.makeText(getContext(), "正在配置", Toast.LENGTH_SHORT).show();
                Log.d("正在配置  ","...");
            } else {
                // 配置失败
//                Toast.makeText(getContext(), "配置失败", Toast.LENGTH_SHORT).show();
                Log.d("配置失败  ","...");
                Toast.makeText(getContext(), ErrorHandleUtil.toastError(result,getContext()), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }

        @Override
        public void didBindDevice(GizWifiErrorCode result, String did) {
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                Toast.makeText(getContext(), "绑定成功", Toast.LENGTH_SHORT).show();
            } else {
                // 绑定失败
                Toast.makeText(getContext(), "绑定失败", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void didDiscovered(GizWifiErrorCode result, List<GizWifiDevice> deviceList) {
            // 提示错误原因
            if (result != GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                Log.d("", "result: " + result.name());
            }
            // 显示变化后的设备列表
            Log.d("", "discovered deviceList: " + deviceList);
            mDevices = deviceList;
        }


    };

    private GizWifiDeviceListener mDeviceListener = new GizWifiDeviceListener() {
        @Override
        public void didSetSubscribe(GizWifiErrorCode result, GizWifiDevice device, boolean isSubscribed) {
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                // 订阅或解除订阅成功
//                Intent intent = new Intent(getContext(), DeviceControlActivity.class);
//                intent.putExtra("GizWifiDevice",mDevice);
//                startActivity(intent);
                send(5);
            } else {
                // 失败
            }
        }

        //获取设配硬件信息
        @Override
        public void didGetHardwareInfo(GizWifiErrorCode result, GizWifiDevice device, ConcurrentHashMap<String, String> hardwareInfo) {
            StringBuilder sb = new StringBuilder();
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                sb.append("Wifi Hardware Version:" + hardwareInfo.get("wifiHardVersion")
                        + "\r\n");
                sb.append("Wifi Software Version:" + hardwareInfo.get("wifiSoftVersion")
                        + "\r\n");
                sb.append("MCU Hardware Version:" + hardwareInfo.get("mcuHardVersion")
                        + "\r\n");
                sb.append("MCU Software Version:" + hardwareInfo.get("mcuSoftVersion")
                        + "\r\n");
                sb.append("Firmware Id:" + hardwareInfo.get("wifiFirmwareId") + "\r\n");
                sb.append("Firmware Version:" + hardwareInfo.get("wifiFirmwareVer")
                        + "\r\n");
                sb.append("Product Key:" + hardwareInfo.get("productKey") + "\r\n");
                sb.append("Device ID:" + device.getDid() + "\r\n");
                sb.append("Device IP:" + device.getIPAddress() + "\r\n");
                sb.append("Device MAC:" + device.getMacAddress() + "\r\n");
            } else {
                sb.append("获取失败，错误号：" + result);
            }

            String str = sb.toString();
        }

        @Override
        public void didReceiveData(GizWifiErrorCode result, GizWifiDevice device, ConcurrentHashMap<String, Object> dataMap, int sn) {
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                if (sn == 5) {
                    // 命令序号相符，开灯指令执行成功
                    Toast.makeText(getContext(), "chenggong", Toast.LENGTH_SHORT).show();
                } else {
                    // 其他命令的ack或者数据上报
                }
            } else {
                // 操作失败
                Toast.makeText(getContext(), "失败", Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    protected int setLayout() {
        return R.layout.fragment_wifi;
    }


    private String wifiSSID;
    private List<ScanResult> mScanResults;
    @Override
    protected void init() {
        GizWifiSDK.sharedInstance().setListener(mListener);

        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        wifiSSID = wifiInfo.getSSID();
        Log.d("wifi mac: ", wifiSSID);
        if (wifiInfo.getSSID().equals("<unknown ssid>")){
            Toast.makeText(getContext(), "请先连接wifi", Toast.LENGTH_SHORT).show();
        }else {
            edWifiName.setText(wifiSSID.replaceAll("\\\"",""));
        }
        wifiManager.startScan();
        mScanResults = wifiManager.getScanResults();

        dialog = new ProgressDialog(getContext());

    }

    @Override
    protected void setData() {
        dialog.setIndeterminate(true);
        dialog.setMessage("正在配对.....");
        mListDialog = new ListDialog(getContext(),mScanResults);
        mListDialog.setOnItemListener(new ListDialog.onItemListener() {
            @Override
            public void onItemClick(String name) {
                edWifiName.setText(name);
                mListDialog.dismiss();
            }
        });
    }

    /**
     * AirLink配置
     * AirLink使用UDP广播方式，由手机端发出含有目标路由器名称和密码的广播，设备上的Wifi模块接收到广播包后自动连接目标路由器，连上路由器后发出配置成功广播，通知手机配置已完成。
     * 模块开启AirLink模式后，如果一分钟内未收到AirLink广播或无法正确连上路由器，将进入SoftAP模式。
     */
    public void setAirLink(String wifiName,String wifiKey) {
        dialog.show();
        if (types == null) {
            types = new ArrayList<GizWifiGAgentType>();
        }
        // 让手机连上目标Wifi
        // MCU发出开启AirLink串口指令，通知模组开启AirLink模式。
        //配置设备入网，发送要配置的wifi名称、密码
        types.add(GizWifiGAgentType.GizGAgentESP);
        GizWifiSDK.sharedInstance().setDeviceOnboarding(wifiName, wifiKey, GizWifiConfigureMode.GizWifiAirLink, null, 60, types);
    }

    /**
     * 设备进入SoftAP模式后，会产生一个Wifi热点。手机连上此热点后，将要配置的SSID和密码发给设备。
     * 设备上的Wi-Fi模块接收到SoftAP配置包后自动连接目标路由器，与airlink一样，连上路由器后发出配置成功广播，通知手机配置已完成。
     * 使用机智云提供的模组固件，设备产生的Wifi热点以“XPG-GAgent-”开头，密码为” 123456789”。
     * 其他厂商提供的模组，SoftAP热点名称由各自厂商指定。APP可以根据需要传入正确的热点前缀。
     *
     * @param your_ssid
     * @param your_key
     */

    private void setSoftAP(String your_ssid, String your_key) {
        // MCU发出进入SoftAP串口指令，通知模组开启SoftAP模式。
        //让手机连接模组的SoftAP热点
        //配置设备入网，发送要配置的wifi名称、密码
        GizWifiSDK.sharedInstance().setListener(mListener);
        GizWifiSDK.sharedInstance().setDeviceOnboarding(your_ssid, your_key, GizWifiConfigureMode.GizWifiSoftAP, "your_gagent_hotspot_prefix", 60, null);
        //模块收到配置信息，尝试连接路由器并自动关闭热点
        //让手机连接到配置的wifi上
    }

    //绑定设配
    private void boundDevices() {
        // 使用缓存的设备列表刷新UI
        GizWifiSDK.sharedInstance().getBoundDevices(mUid, mTohen);
    }

    //订阅设配
    private void subscribeDevices() {
        mDevice.setListener(mDeviceListener);
        mDevice.setSubscribe(true);
    }

    private void getHardwareInfo() {
        mDevice.getHardwareInfo();
    }

    //非局域网设配绑定
    private void bindRemoteDevice() {
        GizWifiSDK.sharedInstance().bindRemoteDevice(mUid, mTohen, mDevice.getMacAddress(), mDevice.getProductKey(), Constants.product_secret);
    }

    //解绑
    private void unbindDevice() {
        GizWifiSDK.sharedInstance().unbindDevice(mUid, mTohen, mDevice.getDid());
    }

    //向设配发送指令
    private void send(int sn) {
        // 订阅设备并变为可控状态后，执行开灯动作
        ConcurrentHashMap<String, Object> command = new ConcurrentHashMap<String, Object>();
        command.put("LED_OnOff", true);
        mDevice.write(command, sn);
    }


    @OnClick({R.id.iv_goto, R.id.goto_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_goto:
                mListDialog.show();
                break;
            case R.id.goto_btn:
                if (TextUtils.isEmpty(edWifiName.getText())){
                    Toast.makeText(getContext(), "wifi名不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(edWifiPass.getText())){
                    Toast.makeText(getContext(), "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                setAirLink(edWifiName.getText().toString(),edWifiPass.getText().toString());
                break;
        }
    }
}
