package com.contentstack.graphql.product.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.contentstack.graphql.AllProductQuery;
import com.contentstack.graphql.R;
import com.contentstack.graphql.product.holder.ViewHolder;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;


import androidx.recyclerview.widget.RecyclerView;


public class ProductAdapter extends RecyclerView.Adapter<ViewHolder> {

    private List<AllProductQuery.Item> apolloResp;
    public ProductAdapter() {
        this.apolloResp = new ArrayList<>();
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_container, parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        holder.setData(apolloResp.get(position));
    }

    @Override
    public int getItemCount() {
        return apolloResp.size();
    }

    public void addAll(List<AllProductQuery.Item> newResp) {
        int initialSize = newResp.size();
        apolloResp.addAll(newResp);
        notifyItemRangeInserted(initialSize, newResp.size());
    }

}