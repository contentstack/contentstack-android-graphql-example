package com.contentstack.graphql.product;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.contentstack.graphql.R;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import androidx.recyclerview.widget.RecyclerView;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private ArrayList<ProductModel> apolloResp;
    private final Context context;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    public ProductAdapter(Context context, ArrayList<ProductModel> productModels) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.apolloResp = productModels;
    }


    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View rightIcon = LayoutInflater.from(context).inflate(R.layout.grid_container, null);
        return new ViewHolder(rightIcon);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.txtTitle.setText(apolloResp.get(position).getTitle());
        holder.txtPrice.setText("Price $"+String.valueOf(apolloResp.get(position).getPrice()));
        String thumbnails = apolloResp.get(position).getUrl();
        Log.e("thumbnails", thumbnails);
        Glide.with(context).load(apolloResp.get(position).getUrl()).into(holder.iconThumbnail);

    }

    @Override
    public int getItemCount() {
        return apolloResp.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView txtTitle;
        private TextView txtPrice;
        private ImageView iconThumbnail;

        ViewHolder(View itemView) {
            super(itemView);
            txtTitle               = itemView.findViewById(R.id.txtTitle);
            txtPrice              = itemView.findViewById(R.id.tvPrice);
            iconThumbnail = itemView.findViewById(R.id.iconThumbnail);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public ProductModel getItem(int id) {
        return apolloResp.get(id);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


}