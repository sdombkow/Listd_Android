package com.listdnow.www;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class PassSetActivity extends Activity {

	private JSONObject myPassSet;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pass_set);

		TextView myTitleView = (TextView) findViewById(R.id.passSetTitleTextView);

		try {
			myPassSet = new JSONObject(getIntent().getStringExtra("jsonobject"));
			myTitleView.setText(myPassSet.getString("date").toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_pass_set, menu);
		return true;
	}

	public void continueButtonClicked(View button) {
		EditText purchaseNumberOfTickets = (EditText) findViewById(R.id.purchaseNumberOfTickets_EditText);
		EditText purchaseNameOfTickets = (EditText) findViewById(R.id.purchaseNameOfTickets_EditText);
		Intent intent = new Intent(getApplicationContext(),
				PurchaseActivity.class);
		intent.putExtra("jsonobject", myPassSet.toString());
		intent.putExtra("purchaseNumberOfTickets",
				Integer.parseInt(purchaseNumberOfTickets.getText().toString()));
		intent.putExtra("purchaseNameOfTickets", purchaseNameOfTickets
				.getText().toString());
		startActivity(intent);
	}
}
