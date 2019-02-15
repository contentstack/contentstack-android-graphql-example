package com.contentstack.graphql.splash;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.contentstack.graphql.R;
import com.contentstack.graphql.product.view.ProductActivity;
import java.util.Objects;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Objects.requireNonNull(getSupportActionBar()).hide();

        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, ProductActivity.class));
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            finish();
        }, 1000);
    }
}
