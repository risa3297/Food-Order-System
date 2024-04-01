package com.example.foodorder;

import android.widget.Filter;

import java.util.ArrayList;

public class FilterOrderSeller extends Filter {

    private AdapterOrderSeller adapter;
    private ArrayList<ModelOrderSeller> filterList;

    public FilterOrderSeller(AdapterOrderSeller adapter, ArrayList<ModelOrderSeller> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();

        if (constraint != null && constraint.length() > 0){

            constraint = constraint.toString().toUpperCase();

            ArrayList<ModelOrderSeller> filteredModels = new ArrayList<>();
            for (int i=0; i<filterList.size(); i++){

                if (filterList.get(i).getOrderStatus().toUpperCase().contains(constraint)){
                    filteredModels.add(filterList.get(i));
                }
            }

            results.count = filteredModels.size();
            results.values= filteredModels;
        }

        else {
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;

    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

        adapter.orderSellerArrayList = (ArrayList<ModelOrderSeller>) results.values;
        adapter.notifyDataSetChanged();

    }
}
