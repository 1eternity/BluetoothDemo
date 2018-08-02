package collect.pingshow.com.bluetoothdemo.thread;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by lgq on 2018/8/1.
 */

public class RWStream extends Thread {
   private  InputStream is;
   private  OutputStream os;
   private BluetoothSocket socket;
   private DataShow dataShow;
   public RWStream(BluetoothSocket socket){
       this.socket=socket;
   }
    @Override
    public void run() {
        super.run();
        try {
            is=socket.getInputStream();
            os=socket.getOutputStream();
            byte[] buf=new byte[1024];
            int len=0;
            while(socket.isConnected()) { //当socket是连接的，就一直读取数据
                while((len=is.read(buf))!=-1){
                    String str = new String(buf, 0, len);
                    Log.e("tag","获取流里面的数据"+str);
                    //如果在另一端设置的接口对象，那么就传递数据
                    if(dataShow!=null){
                        dataShow.getMessage(str);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    /**
     * 数据的写入
     */
    public void write(String str){
        if(os!=null){
            //socket 数据写入
            try {
                os.write(str.getBytes());
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setDataShow(DataShow dataShow) {
        this.dataShow = dataShow;
        Log.e("lgq","dataShow="+dataShow);
    }

    public  interface DataShow{
       void getMessage(String message);
    }
}
