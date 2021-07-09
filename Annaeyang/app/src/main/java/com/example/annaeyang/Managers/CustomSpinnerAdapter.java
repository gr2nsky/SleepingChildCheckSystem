package com.example.annaeyang.Managers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.annaeyang.R;

import java.util.ArrayList;

public class CustomSpinnerAdapter extends BaseAdapter {

    ArrayList<SpinnerItem> items = new ArrayList<>();

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Context context = parent.getContext();

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.spinner_item, null);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.driverNameTV);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(items.get(position).name);
        return convertView;
    }

    public void addItem(String name, String xid){
        SpinnerItem item = new SpinnerItem();
        item.setName(name);
        item.setXid(xid);

        items.add(item);
    }

    public String getXid(int position){
        return items.get(position).xid;
    }

    public String getName(int position){
        return items.get(position).name;
    }

    class ViewHolder{
        TextView textView;
    }
}