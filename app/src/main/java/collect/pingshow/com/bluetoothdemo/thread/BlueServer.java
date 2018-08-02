package collect.pingshow.com.bluetoothdemo.thread;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by lgq on 2018/8/1.
 */

public class BlueServer extends Thread {
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private RWStream rwStream;

    public RWStream getRwStream() {
        return rwStream;
    }
    private BluetoothAdapter mBluetoothAdapter;
    public BlueServer(BluetoothAdapter bluetoothAdapter){
        this.mBluetoothAdapter=bluetoothAdapter;
    }

    @Override
    public void run() {
        super.run();
        Log.e("lgq","启动了  BlueServer");
        try {
            //创建蓝牙服务端的Socket，这里第一个参数是服务器的名称，第二个参数是UUID的字符串的值
            BluetoothServerSocket socket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("server", MY_UUID);
            //获取蓝牙客户端对象，这是一个同步方法，用客户端接入才有后面的操作
            BluetoothSocket client = socket.accept();
            //获取可读可写对象
            rwStream=new RWStream(client);
            //开始可读可写线程的操作，这里是一直在读取数据的状态
            rwStream.start();
            Log.e("lgq","blueServer_rwStream="+rwStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
