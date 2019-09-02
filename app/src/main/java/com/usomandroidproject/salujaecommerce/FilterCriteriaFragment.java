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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilterCriteriaFragment extends Fragment {
    List<Brand> categoryList = new ArrayList<>();
    String[] AndroidOS = new String[] { "Category","Brand","Price","Color"};
    List<Integer> selectedIds;
    int filterType;

    ListView lv;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.filter_layout, container, false);
        lv=(ListView)view.findViewById(R.id.filterListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, AndroidOS);
        lv.setAdapter(adapter);

        lv.setSelector(android.R.color.holo_blue_dark);
        lv.setSelection(0);
        lv.setItemChecked(0,true);
        //lv.getSelectedView().setSelected(true);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                selectedIds = new ArrayList<>();
                //HashMap<Integer, List<Integer>> hashMap = ((Cache)getActivity().getApplicationContext()).getSelectedFilterValues();
                switch (position)
                {
                    case 0 :  categoryList = ((Cache)getActivity().getApplicationContext()).getCategoryLit();
                        selectedIds = ((Cache)getActivity().getApplicationContext()).getFilterCategoryIds();//hashMap.get(0);
                        filterType = 0;
                        break;

                    case 1 : categoryList = ((Cache)getActivity().getApplicationContext()).getBrandList();
                        selectedIds = ((Cache)getActivity().getApplicationContext()).getFilterBrandIds();//hashMap.get(1);
                        filterType = 1;
                        break;

                    case 2 :categoryList = new ArrayList<>();
                        Map<Integer, String> priceList = ((Cache)getActivity().getApplicationContext()).priceList;
                        for(int i = 0 ; i<= priceList.size(); i++) {
                            Brand brand = new Brand(i+1, priceList.get(i+1), "");
                            categoryList.add(brand);
                        }
                        selectedIds = ((Cache)getActivity().getApplicationContext()).getFilterPriceIds();//hashMap.get(2);
                        filterType = 2;
                        break;
                    case 3: categoryList = ((Cache)getActivity().getApplicationContext()).getColorList();
                        selectedIds = ((Cache)getActivity().getApplicationContext()).getColourIds();//hashMap.get(3);
                        filterType = 3;
                        break;
                }

                FilterSubCategory listFragment=(FilterSubCategory) getFragmentManager().findFragmentById(R.id.fragment2);

                listFragment.change(categoryList, selectedIds, filterType);

                //TextFragment txtfrag = (TextFragment)getFragmentManager().findFragmentById(R.id.fragment2);
                //txtfrag.change("Android OS:-"+AndroidOS[i]);

            }
        });
        return view;
    }
}
