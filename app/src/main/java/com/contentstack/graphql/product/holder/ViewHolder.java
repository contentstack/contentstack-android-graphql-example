package com.contentstack.graphql.product.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.contentstack.graphql.AllProductQuery;
import com.contentstack.graphql.R;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {

    private TextView txtTitle;
    private TextView txtPrice;
    private ImageView iconThumbnail;

    public ViewHolder(View itemView) {
        super(itemView);
        findViews(itemView);
    }

    private void findViews(View itemView) {
        txtTitle = itemView.findViewById(R.id.txtTitle);
        txtPrice = itemView.findViewById(R.id.tvPrice);
        iconThumbnail = itemView.findViewById(R.id.iconThumbnail);
    }


    public void setData(AllProductQuery.Item data) {

        txtTitle.setText(data.title());
        txtPrice.setText("$"+data.price());

        Glide.with(iconThumbnail.getContext())
                .load(data.featured_image().get(0).url())
                .thumbnail(0.1f).into(iconThumbnail);
    }

}
