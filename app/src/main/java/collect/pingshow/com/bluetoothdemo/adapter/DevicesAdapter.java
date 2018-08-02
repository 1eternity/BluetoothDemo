package collect.pingshow.com.bluetoothdemo.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import collect.pingshow.com.bluetoothdemo.R;

/**
 * Created by lgq on 2018/8/1.
 */

public class DevicesAdapter extends BaseAdapter {
    private Context mContext;
    private List<BluetoothDevice> mList;
    public DevicesAdapter(Context context, List<BluetoothDevice> list){
        this.mContext=context;
        this.mList=list;
    }
    
    @Override
    public int getCount() {
        return mList==null?0:mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder=null;
        if(view==null){
            view=View.inflate(mContext, R.layout.bluetooth_list,null);
            viewHolder=new ViewHolder(view);
            view.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) view.getTag();
        }
        viewHolder.name.setText(mList.get(i).getName());
        viewHolder.param.setText(mList.get(i).getAddress());
        return view;
    }

    public void regresh(List<BluetoothDevice> list) {
        this.mList=list;
        notifyDataSetChanged();
    }

    private class ViewHolder{

        private final TextView name;
        private final TextView param;

        public ViewHolder(View view){
            name = view.findViewById(R.id.name);
            param = view.findViewById(R.id.param);
        }
    }
}
