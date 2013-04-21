package com.listdnow.www;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.listdnow.www.util.ListViewObjectClickedListener;

public class HomeActivity extends Activity {

	private static final String TASKS_URL = "http://10.0.2.2:3000/api/v1/tasks.json";
	private String mSearchQuery;
	private SharedPreferences mPreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);

		List<String> taskOptions = new ArrayList<String>();
		taskOptions.add("Bars Nearby");
		taskOptions.add("My Passes");
		taskOptions.add("Billing Information");
		taskOptions.add("Account Information");
		ListView taskListView = (ListView) findViewById(R.id.tasks_list_view);
		if (taskListView != null) {
			taskListView.setAdapter(new ArrayAdapter<String>(HomeActivity.this,
					android.R.layout.simple_list_item_1, taskOptions));
			taskListView
					.setOnItemClickListener(new ListViewObjectClickedListener(
							HomeActivity.this, taskOptions));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_home, menu);
		return true;
	}

	public void search(View button) {
		EditText searchField = (EditText) findViewById(R.id.search_edittext);
		mSearchQuery = searchField.getText().toString();
		Intent intent = new Intent(getApplicationContext(),
				SearchActivity.class);
		intent.putExtra("search_query", mSearchQuery);
		intent.putExtra("searchNearby", false);
		startActivity(intent);
	}
}
