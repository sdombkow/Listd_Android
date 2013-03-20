package com.listdnow.www;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.listdnow.www.authentication.WelcomeActivity;
import com.listdnow.www.util.JSONObjectClickedListener;
import com.savagelook.android.UrlJsonAsyncTask;

public class HomeActivity extends Activity {

	private static final String TASKS_URL = "http://10.0.2.2:3000/api/v1/tasks.json";
	private String mSearchQuery;
	private SharedPreferences mPreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
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
		intent.putExtra("search_query",mSearchQuery);
		startActivity(intent);
	}
}
