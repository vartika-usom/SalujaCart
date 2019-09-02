package com.usomandroidproject.salujaecommerce;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class SearchAdapter extends BaseAdapter {
    Context c;

    List<SearchCriteria> cityList;

    public SearchAdapter(List<SearchCriteria> cityList, Context c) {
        this.cityList = cityList;
        this.c = c;
    }

    @Override
    public int getCount() {
        return cityList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            convertView = LayoutInflater.from(c).inflate(R.layout.search_adapter, parent, false);
            TextView textView = (TextView) convertView.findViewById(R.id.cityHindiName);
            if(cityList.size() > 0) {
                String cityValue = cityList.get(position).getTitle();
                textView.setText(cityValue);

            }

        } catch (Exception ex) {
            Toast.makeText(c, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return convertView;
    }
}
