package com.listdnow.www;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class PartnerActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_partner, menu);
        return true;
    }
}
