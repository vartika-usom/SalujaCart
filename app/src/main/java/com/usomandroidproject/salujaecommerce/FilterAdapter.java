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

public class FilterAdapter extends BaseAdapter {
    Context c;

    List<Brand> quantity;
    List<Integer> quantitySelected;

    public FilterAdapter(List<Brand> quantity, Context c, List<Integer> quantitySelected) {
        this.quantity = quantity;
        this.c = c;
        this.quantitySelected = quantitySelected;
    }

    public void setSelectedPosition(List<Integer> selectedIds)
    {
        quantitySelected = selectedIds;
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

            Brand selectedBrand = quantity.get(position);
            textView.setText(selectedBrand.getTitle());
            if(quantitySelected != null && quantitySelected.size() > 0
                    && quantitySelected.contains(selectedBrand.getId()))
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
