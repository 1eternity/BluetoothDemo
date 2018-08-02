package collect.pingshow.com.bluetoothdemo.util;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by lgq on 2018/8/1.
 */

public class Utils {
    /**
     * list去重
      * @param list
     * @return
     */
    public static List<BluetoothDevice> removeDuplicate(List<BluetoothDevice> list) {
        HashSet<BluetoothDevice> set = new HashSet<BluetoothDevice>(list.size());
        List<BluetoothDevice> result = new ArrayList<BluetoothDevice>(list.size());
        for (BluetoothDevice str : list) {
            if (set.add(str)) {
                result.add(str);
            }
        }
        list.clear();
        list.addAll(result);
        return list;
    }
}
