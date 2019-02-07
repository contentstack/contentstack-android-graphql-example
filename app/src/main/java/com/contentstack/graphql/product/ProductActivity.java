package com.contentstack.graphql.product;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.contentstack.graphql.AllProductQuery;
import com.contentstack.graphql.BuildConfig;
import com.contentstack.graphql.NetworkInterceptor;
import com.contentstack.graphql.about.AboutActivity;
import com.contentstack.graphql.R;
import com.contentstack.graphql.databinding.ProductLayoutBinding;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Objects;


public class ProductActivity extends AppCompatActivity {

    private ProductLayoutBinding binding;
    private ProductAdapter adapter;
    private ArrayList<ProductModel> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.product_layout);

        createApolloClient();
        setToolbar();
    }




    private ApolloClient createApolloClient(){

         OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(new NetworkInterceptor()).build();

         ApolloClient apolloClient = ApolloClient.builder()
                .serverUrl(HttpUrl.get(BuildConfig.ENDPOINT))
                .okHttpClient(httpClient)
                .build();

        return apolloClient;
    }



    private void setToolbar()
    {
        Objects.requireNonNull(getSupportActionBar()).setTitle("Contentstack");
        getSupportActionBar().setSubtitle("GraphQL");
        getSupportActionBar().setElevation(0);

        getProducts();
        binding.refreshContainer.setOnRefreshListener(this::getProducts);
    }

    private void getProducts() {

        binding.refreshContainer.setRefreshing(true);
        arrayList = new ArrayList<>();

        createApolloClient().query(AllProductQuery.builder().build()).enqueue(
                new ApolloCall.Callback<AllProductQuery.Data>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NotNull Response<AllProductQuery.Data> response) {

                Log.e("response", response.toString());

                assert response.data() != null;
                response.data().all_product().items().forEach(allProduct->{
                    String title = allProduct.title();
                    double price = allProduct.price();
                    String url = allProduct.featured_image().get(0).url();
                    arrayList.add(new ProductModel(title, price, url));
                });

                ProductActivity.this.runOnUiThread(() -> {
                    binding.refreshContainer.setRefreshing(false);
                    binding.errorLayout.setVisibility(View.GONE);
                    binding.recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2 ));
                    adapter = new ProductAdapter(getApplicationContext(), arrayList);
                    binding.recyclerView.setAdapter(adapter);
                });
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                Log.e("onFailure", e.getLocalizedMessage());

                ProductActivity.this.runOnUiThread(()->{
                    binding.refreshContainer.setRefreshing(false);
                    binding.errorLayout.setVisibility(View.VISIBLE);
                    binding.tvError.setText(String.format("Error Occurred %s", e.getLocalizedMessage()));
                });
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
