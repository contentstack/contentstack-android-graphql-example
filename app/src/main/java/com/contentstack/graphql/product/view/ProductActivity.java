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

import com.apollographql.apollo3.ApolloCall;
import com.apollographql.apollo3.ApolloClient;
import com.apollographql.apollo3.api.ApolloResponse;
import com.apollographql.apollo3.exception.ApolloException;
import com.contentstack.graphql.ALLProductsQuery;
import com.contentstack.graphql.BuildConfig;
import com.contentstack.graphql.databinding.ProductsLayoutBinding;
import com.contentstack.graphql.R;
import com.contentstack.graphql.product.adapter.ProductAdapter;

import org.jetbrains.annotations.NotNull;


public class ProductActivity extends AppCompatActivity {

    private final String TAG = ProductActivity.class.getSimpleName();
    private final String APIkey = BuildConfig.APIKey;
    private final String deliveryToken = BuildConfig.deliveryToken;
    private final String environment = BuildConfig.environment;

    private final String BASE_URL = "https://graphql.contentstack.com/stacks/"+APIkey+"?access_token="+deliveryToken+"&environment="+environment;
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
        Log.e("Graphql URL:", BASE_URL);
        return new ApolloClient.Builder().serverUrl(BASE_URL).okHttpClient(okHttpClient).build();
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
        getApolloClient().query(new ALLProductsQuery(skipCount, limit))
                .enqueue(new ApolloCall.Callback<ALLProductsQuery.Data>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NotNull ApolloResponse<ALLProductsQuery.Data> response) {
                if (response.data() != null) {
                    response.data().all_product().items().forEach(item -> {
                        Log.i("Title", item.title());
                        Log.i("Price", item.price().toString());
                        Log.i("description", item.description());
                        Log.e("image", item.featured_imageConnection().edges().get(0).node().url());
                    });
                    ProductActivity.this.runOnUiThread(() -> {
                        binding.tvError.setVisibility(View.GONE);
                        binding.refreshContainer.setRefreshing(false);

                        if (response.data().all_product().items().size() > 0) {
                            Log.i(TAG, response.data().all_product().items().toString());
                            adapter.addAll(response.data().all_product().items());
                            binding.recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
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
