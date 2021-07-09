package com.example.annaeyang.Managers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.annaeyang.R;
import java.util.ArrayList;

public class CustomListViewAdapter extends BaseAdapter {

    private ArrayList<ListViewItem> listViewItemList = new ArrayList<>();

    public CustomListViewAdapter(){

    }

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


            ListViewItem listViewItem = listViewItemList.get(position);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);

            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
            TextView textView = (TextView) convertView.findViewById(R.id.name);

            textView.setText(listViewItem.getChildName());
        }
        return convertView;
    }

    public void addItem(String name){
        ListViewItem item = new ListViewItem();

        item.setChildName(name);

        listViewItemList.add(item);
    }

}
