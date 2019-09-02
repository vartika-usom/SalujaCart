package com.usomandroidproject.salujaecommerce;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class QuantityAdapter extends BaseAdapter {
    Context c;

    List<Integer> quantity;
    int quantitySelected;

    public QuantityAdapter(List<Integer> quantity, Context c, int quantitySelected) {
        this.quantity = quantity;
        this.c = c;
        this.quantitySelected = quantitySelected;
    }

    public void setSelectedPosition(int pos)
    {
        quantitySelected = pos + 1;
    }

    @Override
    public int getCount() {
        return quantity.size();
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
            convertView = LayoutInflater.from(c).inflate(R.layout.list_quantity_adapter, parent, false);
            TextView textView = (TextView) convertView.findViewById(R.id.quantityText);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.checkQuantity);


            textView.setText(String.valueOf(quantity.get(position)));
            if(quantitySelected != 0 && (position+1) == quantitySelected)
            {
                imageView.setVisibility(View.VISIBLE);
            }
            else
            {
                imageView.setVisibility(View.INVISIBLE);
            }

        } catch (Exception ex) {
            Toast.makeText(c, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return convertView;
    }
}
