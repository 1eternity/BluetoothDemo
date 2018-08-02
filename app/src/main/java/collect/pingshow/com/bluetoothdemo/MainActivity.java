package collect.pingshow.com.bluetoothdemo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import collect.pingshow.com.bluetoothdemo.adapter.DevicesAdapter;
import collect.pingshow.com.bluetoothdemo.thread.BlueServer;
import collect.pingshow.com.bluetoothdemo.thread.RWStream;
import collect.pingshow.com.bluetoothdemo.util.Utils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * 打开蓝牙
     */
    private Button mOpenBluetooth;
    /**
     * 关闭蓝牙
     */
    private Button mCloseBluetooth;
    /**
     * 设置蓝牙为服务端
     */
    private Button mCreateServer;
    /**
     * 监听数据的接收
     */
    private Button mListen;
    /**
     * 请输入数据
     */
    private EditText mEtSend;
    /**
     * 发送
     */
    private Button mBtnSend;
    private BlueToothController mBlueToothController;
    private List<BluetoothDevice> mDevicesList = new ArrayList<>();
    private List<BluetoothDevice> mBondDeviceList = new ArrayList<>();
    private ListView mDeviceList;
    private DevicesAdapter mAdapter;
    private BlueServer blueServer;
    private boolean isServer = false;   //默认是普通客户端
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private RWStream client;
    private TextView mTvShowService;
    private TextView mTvShow;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                mTvShowService.setTextColor(Color.RED);
                mTvShowService.setTextSize(20);
                mTvShowService.append(msg.obj + "\n");
            } else {
                mTvShow.setTextColor(Color.BLUE);
                mTvShow.setTextSize(20);
                mTvShow.append(msg.obj + "\n");
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        register();
        initView();
        initBand();
    }

    private void init() {
        mBlueToothController = new BlueToothController();
    }

    private void register() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, filter);
    }

    private void initView() {
        mOpenBluetooth = (Button) findViewById(R.id.openBluetooth);
        mOpenBluetooth.setOnClickListener(this);
        mCloseBluetooth = (Button) findViewById(R.id.closeBluetooth);
        mCloseBluetooth.setOnClickListener(this);
        mDeviceList = (ListView) findViewById(R.id.deviceList);
        mCreateServer = (Button) findViewById(R.id.createServer);
        mCreateServer.setOnClickListener(this);
        mListen = (Button) findViewById(R.id.listen);
        mListen.setOnClickListener(this);
        mEtSend = (EditText) findViewById(R.id.et_send);
        mBtnSend = (Button) findViewById(R.id.btn_send);
        mBtnSend.setOnClickListener(this);
//        点击对应的条目就创建对应的客户端，并经行数据的读取和写入
        mDeviceList.setOnItemClickListener(connServerListener);
        mEtSend.setOnClickListener(this);
        mTvShowService = (TextView) findViewById(R.id.tv_show_service);
        mTvShow = (TextView) findViewById(R.id.tv_show);
    }

    AdapterView.OnItemClickListener connServerListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            //连接服务器
            connServer(mDevicesList.get(i));
            Toast.makeText(MainActivity.this, "连接服务器", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 客户端连接服务器
     *
     * @param device
     */
    private void connServer(BluetoothDevice device) {
        try {
            //创建蓝牙客户端的Socket对象，这里是类BluetoothSocket，服务端是BluetoothServerSocket
            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            socket.connect(); //连接socket
            //创建可读写的客户对象，传入Socket对象
            client = new RWStream(socket);
            //开始客户端的线程
            client.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开启蓝牙服务的服务端
     */
    private void createServer() {
        Log.e("lgq", "mBlueToothController.getmBluetoothAdapter()=" + mBlueToothController.getmBluetoothAdapter());
        blueServer = new BlueServer(mBlueToothController.getmBluetoothAdapter());
        blueServer.start();
        //设置为服务端
        isServer = true;
    }


    private void initBand() {
        mDevicesList = mBlueToothController.getBondDeveces();
        mAdapter = new DevicesAdapter(this, mDevicesList);
        mDeviceList.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.openBluetooth:
                mBlueToothController.openBlueTooth(this, BlueToothController.REQUEST_ENABLE_CODE);
                mBlueToothController.startDiscovery();
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.closeBluetooth:
                mBlueToothController.closeBlueTooth();
                break;
            case R.id.createServer:
//                启蓝牙服务的服务端
                createServer();
                break;
            case R.id.listen:
                //服务器的监听
                Toast.makeText(this, "监听中", Toast.LENGTH_SHORT).show();
                Log.e("lgq", "blueServer__=" + blueServer);
                Log.e("lgq", "client__=" + client);
                if (blueServer != null) {
//                    RWStream rwStream = blueServer.getRwStream();
//                    Log.e("lgq","rwStream+"+rwStream);
                    Log.e("lgq", "blueServer不为空");
                    client.setDataShow(new RWStream.DataShow() {
                        @Override
                        public void getMessage(String message) {
                            //要在主线程中改变UI
                            Log.e("lgq", "-------listen---Service" + message);
                            handlerSendMessager(2, message);
                        }
                    });
                } else if (client != null) {
                    Log.e("lgq", "client不为空");
                    client.setDataShow(new RWStream.DataShow() {
                        @Override
                        public void getMessage(String message) {
                            //要在主线程中改变UI
                            Log.e("lgq", "-------listen---client" + message);
                            handlerSendMessager(1, message);
                        }
                    });
                }
                break;
            case R.id.btn_send:
                String message = mEtSend.getText().toString().trim();
                //给另一端写入数据
                write(message);
                break;
        }
    }

    /**
     * 数据的传递
     */
    private void write(String message) {
        Log.e("tag", "---" + message);
        if (isServer) { //服务器写数据
            mBtnSend.setText("服务器");
            handlerSendMessager(1, message);
            if (blueServer != null && blueServer.getRwStream() != null) {
                blueServer.getRwStream().write(message);
            }
        } else {//客户端写数据
            mBtnSend.setText("客户端");
            handlerSendMessager(2, message);
            if (client != null) {
                client.write(message);
            }
        }
    }

    /**
     * Handler包装类
     */
    private void handlerSendMessager(int what, String message) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = message;
        mHandler.sendMessage(msg);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BlueToothController.REQUEST_ENABLE_CODE) {
            Toast.makeText(this, "蓝牙开启", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "蓝牙未开启", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        private BluetoothDevice device;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.e("tag", "扫描到设备:" + device.getName() + "----" + device.getAddress());
                if (device.getName() != null && !device.getName().equals("")) {
                    mDevicesList.add(device);
                }
                mAdapter.regresh(Utils.removeDuplicate(mDevicesList));
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Toast.makeText(context, "扫描结束", Toast.LENGTH_SHORT).show();
            }

        }
    };
}
