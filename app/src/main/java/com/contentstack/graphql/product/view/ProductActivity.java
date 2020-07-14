package com.contentstack.graphql.product.view;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import okhttp3.OkHttpClient;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.contentstack.graphql.ALLProductsQuery;
import com.contentstack.graphql.databinding.ProductsLayoutBinding;
import com.contentstack.graphql.R;
import com.contentstack.graphql.product.adapter.ProductAdapter;

import org.jetbrains.annotations.NotNull;
//import static com.contentstack.graphql.BuildConfig.BASE_URL;
import static com.contentstack.graphql.BuildConfig.DEV8_URL;


public class ProductActivity extends AppCompatActivity {

    private String TAG = ProductActivity.class.getSimpleName();
    private ProductsLayoutBinding binding;
    private ProductAdapter adapter;
    private GridLayoutManager gridLayoutManager;

    private int mTotalItemCount = 0;
    private int mLastVisibleItemPosition;
    private int mPostsPerPage = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.products_layout);

        getApolloClient();
        setToolbar();
    }


    private ApolloClient getApolloClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        return ApolloClient.builder().serverUrl(DEV8_URL).okHttpClient(okHttpClient).build();
    }


    private void setToolbar() {

        getSupportActionBar().setTitle("Contentstack");
        getSupportActionBar().setSubtitle("GraphQL");

        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        binding.recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new ProductAdapter();
        binding.recyclerView.setAdapter(adapter);

        getProducts(mTotalItemCount, mPostsPerPage);
        binding.refreshContainer.setOnRefreshListener(() -> getProducts(mTotalItemCount, mPostsPerPage));

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                mTotalItemCount = gridLayoutManager.getItemCount();
                mLastVisibleItemPosition = gridLayoutManager.findLastVisibleItemPosition();
                if (!binding.refreshContainer.isRefreshing() &&
                        mTotalItemCount <= (mLastVisibleItemPosition + mPostsPerPage)) {

                    binding.refreshContainer.setRefreshing(true);
                    Log.e("LastVisibleItemPosition", String.valueOf(mLastVisibleItemPosition));
                    Log.e("mTotalItemCount", String.valueOf(mTotalItemCount));
                    getProducts(mTotalItemCount, mPostsPerPage);
                }
            }
        });


    }


    private void getProducts(int skipCount, int limit) {

        binding.refreshContainer.setRefreshing(true);
        getApolloClient().query(ALLProductsQuery.builder()
                .skip(skipCount)
                .limit(limit)
                .build()).enqueue(new ApolloCall.Callback<ALLProductsQuery.Data>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NotNull Response<ALLProductsQuery.Data> response) {
                assert response.data() != null;
                response.data().all_product().items().stream().forEach(item -> {
                    Log.i("Title", item.title());
                    Log.i("Price", item.price().toString());
                    Log.i("description", item.description());
                    Log.e("image", item.featured_imageConnection().edges().get(0).node().url());
                });
                ProductActivity.this.runOnUiThread(() -> {
                    binding.tvError.setVisibility(View.GONE);
                    binding.refreshContainer.setRefreshing(false);

                    if (response.data().all_product().items().size() > 0) {
                        //Log.i(TAG, response.data().all_product().items().toString());
                        adapter.addAll(response.data().all_product().items());
                        binding.recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                Log.e("onFailure", e.getMessage());

                ProductActivity.this.runOnUiThread(() -> {
                    binding.refreshContainer.setRefreshing(false);
                    binding.tvError.setVisibility(View.VISIBLE);
                    binding.tvError.setText(String.format("Error Occurred %s",
                            e.getLocalizedMessage()));
                });
            }
        });
    }


}
