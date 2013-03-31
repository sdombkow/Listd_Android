package com.listdnow.www;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonObject;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class PassActivity extends Activity {
	JSONObject myPass;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass);
		TextView myTitleView = (TextView) findViewById(R.id.passNameTextView);

        try {
			myPass = new JSONObject(getIntent().getStringExtra("jsonobject"));
			myTitleView.setText(myPass.getString("name").toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_pass, menu);
        return true;
    }
}
