package com.contentstack.graphql.product;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import com.contentstack.graphql.AllProductQuery;
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
import com.contentstack.graphql.about.AboutActivity;
import com.contentstack.graphql.CSGraphQLApp;
import com.contentstack.graphql.R;
import com.contentstack.graphql.databinding.ActivityProductLayoutBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class ProductActivity extends AppCompatActivity {

    private ActivityProductLayoutBinding binding;
    private ApolloClient apolloClient;
    private ProductAdapter adapter;
    private ArrayList<ProductModel> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_layout);
        apolloClient = CSGraphQLApp.getApolloClient();

        setToolbar();
    }


    private void setToolbar() {

        getSupportActionBar().setTitle("Contentstack");
        getSupportActionBar().setSubtitle("GraphQL");
        getSupportActionBar().setElevation(0);
        // call when screen loads
        getProducts();
        binding.refreshContainer.setOnRefreshListener(() -> {
            getProducts();
        });

    }




    private void getProducts()
    {
        binding.refreshContainer.setRefreshing(true);
        arrayList = new ArrayList<>();

        apolloClient.query(AllProductQuery.builder().build()).enqueue(new ApolloCall.Callback<AllProductQuery.Data>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NotNull Response<AllProductQuery.Data> response) {

                response.data().all_product().items().forEach(allProduct -> {
                    String title = allProduct.title();
                    double price = allProduct.price();
                    String url = allProduct.featured_image().get(0).url();
                    arrayList.add(new ProductModel(title, price, url));
                });

                // update your ui in UI Thread
                ProductActivity.this.runOnUiThread(() -> {
                    binding.refreshContainer.setRefreshing(false);
                    binding.errorLayout.setVisibility(View.GONE);

                    // Setup layout manager and ProductGridAdapter to recyclerview
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
                    binding.tvError.setText(String.format("Error Occured %s", e.getLocalizedMessage()));
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
