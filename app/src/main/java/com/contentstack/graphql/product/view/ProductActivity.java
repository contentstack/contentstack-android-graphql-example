package com.contentstack.graphql.product.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.contentstack.graphql.AllProductQuery;
import com.contentstack.graphql.BuildConfig;
import com.contentstack.graphql.databinding.ProductsLayoutBinding;
import com.contentstack.graphql.utils.NetworkInterceptor;
import com.contentstack.graphql.R;
import com.contentstack.graphql.product.adapter.ProductAdapter;
import org.jetbrains.annotations.NotNull;


public class ProductActivity extends AppCompatActivity {

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


    private ApolloClient getApolloClient(){

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new NetworkInterceptor()).build();
        ApolloClient apolloClient = ApolloClient.builder()
                .serverUrl(HttpUrl.get(BuildConfig.BASE_ENDPOINT))
                .okHttpClient(httpClient).build();
        return apolloClient;
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
        getApolloClient().query(AllProductQuery.builder()
                .skip(skipCount)
                .limit(limit)
                .build()).enqueue(new ApolloCall.Callback<AllProductQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<AllProductQuery.Data> response) {
                assert response.data() != null;
                Log.e("Response", response.data().all_product().items().size()+" Loaded");

                ProductActivity.this.runOnUiThread(() -> {
                    binding.tvError.setVisibility(View.GONE);
                    binding.refreshContainer.setRefreshing(false);

                    if (response.data().all_product().items().size()>0){
                        adapter.addAll(response.data().all_product().items());
                        binding.recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }else {
                        Toast.makeText(ProductActivity.this, "No more items", Toast.LENGTH_SHORT).show();
                    }


                });
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                Log.e("onFailure", e.getLocalizedMessage());

                ProductActivity.this.runOnUiThread(()->{
                    binding.refreshContainer.setRefreshing(false);
                    binding.tvError.setVisibility(View.VISIBLE);
                    binding.tvError.setText(String.format("Error Occurred %s",
                            e.getLocalizedMessage()));
                });
            }
        });
    }


}
