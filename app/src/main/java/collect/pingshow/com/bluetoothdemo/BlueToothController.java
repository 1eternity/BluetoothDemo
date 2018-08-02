package collect.pingshow.com.bluetoothdemo;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by lgq on 2018/8/1.
 */

public class BlueToothController {

    public static final int REQUEST_ENABLE_CODE = 100;
    private final BluetoothAdapter mBluetoothAdapter;

    public BlueToothController(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public BluetoothAdapter getmBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    /**
     *打开蓝牙
     * @param activity
     * @param code
     */
    public void openBlueTooth(Activity activity,int code){
        Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(intent,code);
    }

    /**
     * 关闭蓝牙
     */
    public void closeBlueTooth(){
        mBluetoothAdapter.disable();
    }
    /**
     * 开始扫描设备
     */
    public void startDiscovery(){
        mBluetoothAdapter.startDiscovery();
    }

    /**
     * 获取已绑定设备
     */
    public List<BluetoothDevice> getBondDeveces(){
        Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();
        List<BluetoothDevice> list=new ArrayList<>();
        for (BluetoothDevice devices: bondedDevices) {
            list.add(devices);
        }
        return list;
    }

}
