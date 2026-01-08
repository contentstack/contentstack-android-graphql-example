package com.contentstack.graphql.product.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.apollographql.apollo3.ApolloClient;
import com.apollographql.apollo3.api.ApolloResponse;
import com.apollographql.apollo3.exception.ApolloException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlinx.coroutines.BuildersKt;
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
    private ExecutorService executorService;

    private int mTotalItemCount = 0;
    private int mLastVisibleItemPosition;
    private int mPostsPerPage = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.products_layout);

        executorService = Executors.newSingleThreadExecutor();
        getApolloClient();
        setToolbar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }


    private ApolloClient getApolloClient() {
        Log.e("Graphql URL:", BASE_URL);
        // Apollo v4 - serverUrl is sufficient, it uses OkHttp by default
        return new ApolloClient.Builder()
                .serverUrl(BASE_URL)
                .build();
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
        
        // Apollo v4 requires coroutines - use runBlocking from Java
        // Execute in background thread
        executorService.execute(() -> {
            try {
                // Use Kotlin's runBlocking to call the suspend function from Java
                ApolloResponse<ALLProductsQuery.Data> response = BuildersKt.runBlocking(
                        EmptyCoroutineContext.INSTANCE,
                        (scope, continuation) -> getApolloClient().query(
                                new ALLProductsQuery(
                                        com.apollographql.apollo3.api.Optional.present(skipCount),
                                        com.apollographql.apollo3.api.Optional.present(limit)
                                )
                        ).execute(continuation)
                );

                // Check for errors in response
                if (response.hasErrors()) {
                    String errorMessage = response.errors != null && !response.errors.isEmpty() 
                            ? response.errors.get(0).getMessage() 
                            : "Unknown error occurred";
                    Log.e("GraphQL Error", errorMessage);
                    
                    ProductActivity.this.runOnUiThread(() -> {
                        binding.refreshContainer.setRefreshing(false);
                        binding.tvError.setVisibility(View.VISIBLE);
                        binding.tvError.setText(String.format("Error Occurred: %s", errorMessage));
                    });
                    return;
                }

                // Process successful response
                if (response.data != null) {
                    ALLProductsQuery.Data data = response.data;
                    
                    // Log data (Java 7 compatible loop)
                    for (ALLProductsQuery.Item item : data.all_product.items) {
                        Log.i("Title", item.title);
                        Log.i("Price", item.price.toString());
                        Log.i("description", item.description);
                        if (!item.featured_imageConnection.edges.isEmpty()) {
                            Log.e("image", item.featured_imageConnection.edges.get(0).node.url);
                        }
                    }
                    
                    ProductActivity.this.runOnUiThread(() -> {
                        binding.tvError.setVisibility(View.GONE);
                        binding.refreshContainer.setRefreshing(false);

                        if (data.all_product.items.size() > 0) {
                            Log.i(TAG, data.all_product.items.toString());
                            adapter.addAll(data.all_product.items);
                            binding.recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
                
            } catch (Exception e) {
                Log.e("onFailure", e.getMessage() != null ? e.getMessage() : "Unknown error");
                ProductActivity.this.runOnUiThread(() -> {
                    binding.refreshContainer.setRefreshing(false);
                    binding.tvError.setVisibility(View.VISIBLE);
                    binding.tvError.setText(String.format("Error Occurred: %s",
                            e.getLocalizedMessage() != null ? e.getLocalizedMessage() : "Network error"));
                });
            }
        });
    }


}
