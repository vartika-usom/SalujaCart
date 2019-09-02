package com.usomandroidproject.salujaecommerce;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class FilterSubCategory extends Fragment {
    ListView listView;
    int filter;
    List<Integer> filterId;
    FilterAdapter filterAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.filter_layout, container, false);
        listView = (ListView) view.findViewById(R.id.filterListView);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int filterIds;

                switch (filter)
                {

                    case 0 : filterIds = ((Cache)getActivity().getApplicationContext()).getCategoryLit().get(position).getId();
                        filterId = ((Cache)getActivity().getApplicationContext()).getFilterCategoryIds();
                        if(filterId == null)
                        {
                            filterId = new ArrayList<>();
                        }

                        if(filterId.contains(filterIds))
                        {
                            filterId.remove(Integer.valueOf(filterIds));
                        }
                        else {
                            filterId.add(filterIds);
                        }

                        ((Cache)getActivity().getApplicationContext()).setFilterCategoryIds(filterId);
                        break;

                    case 1:  filterIds = ((Cache)getActivity().getApplicationContext()).getBrandList().get(position).getId();
                        filterId = ((Cache)getActivity().getApplicationContext()).getFilterBrandIds();

                        if(filterId == null)
                        {
                            filterId = new ArrayList<>();
                        }
                        if(filterId.contains(filterIds))
                        {
                            filterId.remove(Integer.valueOf(filterIds));
                        }
                        else {
                            filterId.add(filterIds);
                        }
                        ((Cache)getActivity().getApplicationContext()).setFilterBrandIds(filterId);
                        break;

                    case 2: filterIds = position + 1;
                        filterId = ((Cache)getActivity().getApplicationContext()).getFilterPriceIds();
                        if(filterId == null)
                        {
                            filterId = new ArrayList<>();
                        }
                        if(filterId.contains(filterIds))
                        {
                            filterId.remove(Integer.valueOf(filterIds));
                        }
                        else {
                            filterId.add(filterIds);
                        }

                        ((Cache)getActivity().getApplicationContext()).setFilterPriceIds(filterId);
                        break;
                    case 3:filterIds = ((Cache)getActivity().getApplicationContext()).getColorList().get(position).getId();
                        filterId = ((Cache)getActivity().getApplicationContext()).getColourIds();
                        if(filterId == null)
                        {
                            filterId = new ArrayList<>();
                        }
                        if(filterId.contains(filterIds))
                        {
                            filterId.remove(Integer.valueOf(filterIds));
                        }
                        else {
                            filterId.add(filterIds);
                        }
                        ((Cache)getActivity().getApplicationContext()).setColourIds(filterId);
                        break;
                }

                filterAdapter.setSelectedPosition(filterId);
                filterAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }

    public void change(List<Brand> textos, List<Integer> selectedIds, int filterType) {

        filter = filterType;
        filterAdapter = new FilterAdapter(textos, getActivity(), selectedIds);
        listView.setAdapter(filterAdapter);
    }
}
