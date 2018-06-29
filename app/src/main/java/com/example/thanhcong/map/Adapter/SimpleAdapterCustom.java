package com.example.thanhcong.map.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.example.thanhcong.map.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleAdapterCustom extends SimpleAdapter implements Filterable {

    Context context;
    List<HashMap<String,String>>data;
    int resouce;
    String[]from;
    int[]to;
    public SimpleAdapterCustom(Context context, List<HashMap<String,String>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.context=context;
        this.data=data;
        this.from=from;
        this.to=to;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            viewHolder=new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(resouce,null);
            viewHolder.txt=convertView.findViewById(R.id.txt_name);
            convertView.setTag(viewHolder);
        }
        viewHolder= (ViewHolder) convertView.getTag();
        HashMap<String,String>value = (HashMap<String, String>) getItem(position);
        viewHolder.txt.setText(value.get(from[0]));
        return convertView;
    }

    class ViewHolder{
        TextView txt;
    }

    @Override
    public Filter getFilter() {
        return super.getFilter();
    }
}
