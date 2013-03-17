package com.listdnow.www;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class AdminActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_admin, menu);
        return true;
    }
}
