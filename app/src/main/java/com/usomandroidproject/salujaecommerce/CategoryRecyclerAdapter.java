package com.usomandroidproject.salujaecommerce;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.HashMap;
import java.util.List;

public class CategoryRecyclerAdapter extends RecyclerView.Adapter<CategoryRecyclerAdapter.CategoryViewHolder> {
    Context context;
    private HashMap<Integer, List<Product>> products;
    private List<Integer> categoryIds;
    LinearLayoutManager MyLayoutManager;


    public CategoryRecyclerAdapter(Context context,
                                   HashMap<Integer, List<Product>> products, List<Integer> categoryIds ) {
        this.context = context;
        this.products = products;
        this.categoryIds = categoryIds;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_vertical_list, parent, false);

        CategoryViewHolder holder = new CategoryViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final CategoryViewHolder holder, int position) {

        int categoryId = categoryIds.get(position);
        List<Product> productList = products.get(categoryId);

        holder.categoryName.setText(productList.get(0).getProductCategoryName());
        holder.viewAll.setTag(R.id.viewAllText, categoryId);
        holder.viewAll.setTag(R.id.categoryName, productList.get(0).getProductCategoryName());

        MyLayoutManager = new LinearLayoutManager(context);
        MyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        if (productList.size() > 0 & productList != null) {
            holder.recyclerView.setAdapter(new CategoryItemRecyclerAdapter(productList, context));
        }

        holder.recyclerView.setLayoutManager(MyLayoutManager);

    }

    @Override
    public int getItemCount() {
        return products.size();
    }
    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        public RecyclerView recyclerView;
        public TextView viewAll, categoryName;

        public CategoryViewHolder(View v) {
            super(v);
            viewAll = (TextView) v.findViewById(R.id.viewAllText);
            categoryName = (TextView) v.findViewById(R.id.categoryName);
            recyclerView = (RecyclerView) v.findViewById(R.id.recyclerElement);
            viewAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, GridViewActivity.class);
                    intent.putExtra("CategoryId", Integer.parseInt(viewAll.getTag(R.id.viewAllText).toString()));
                    intent.putExtra("HeaderTitle", viewAll.getTag(R.id.categoryName).toString());
                    context.startActivity(intent);
                }});
        }

    }
}
