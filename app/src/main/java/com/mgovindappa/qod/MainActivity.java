package com.mgovindappa.qod;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mgovindappa.qod.fragment.FragEmailQuote;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            // swap to use fragment
            getSupportFragmentManager().beginTransaction().replace(R.id.activity_content_container, new FragEmailQuote()).commit();
        }
    }
}
