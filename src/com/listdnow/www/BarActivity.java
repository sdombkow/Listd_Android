package com.listdnow.www;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class BarActivity extends Activity {
	
	private JSONObject myBar;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bar);
		TextView myTitleView = (TextView) findViewById(R.id.barTitleTextView);
		try {
			myBar = new JSONObject(getIntent().getStringExtra("jsonobject"));
			myTitleView.setText(myBar.getString("name").toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
}
