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
	public void onResume() {
		super.onResume();

		if (mPreferences.contains("AuthToken")) {
			loadTasksFromAPI(TASKS_URL);
		} else {
			Intent intent = new Intent(HomeActivity.this, WelcomeActivity.class);
			startActivityForResult(intent, 0);
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
		intent.putExtra("search_query",mSearchQuery);
		startActivity(intent);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_refresh:
			loadTasksFromAPI(TASKS_URL);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void loadTasksFromAPI(String url) {
		GetTasksTask getTasksTask = new GetTasksTask(HomeActivity.this);
		getTasksTask.setMessageLoading("Loading tasks...");
		getTasksTask.execute(url + "?auth_token="
				+ mPreferences.getString("AuthToken", ""));
	}

	private class GetTasksTask extends UrlJsonAsyncTask {
		public GetTasksTask(Context context) {
			super(context);
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				JSONArray jsonBars = json.getJSONObject("data").getJSONArray(
						"bars");
				Bundle b = new Bundle();
				b.putString("bars", jsonBars.toString());
				int length = jsonBars.length();
				List<String> barTitles = new ArrayList<String>(length);

				for (int i = 0; i < length; i++) {
					barTitles.add(jsonBars.getJSONObject(i).getString(
							"name"));
				}

				ListView barListView = (ListView) findViewById(R.id.tasks_list_view);
				if (barListView != null) {
					barListView.setAdapter(new ArrayAdapter<String>(
							HomeActivity.this,
							android.R.layout.simple_list_item_1, barTitles));
					barListView.setOnItemClickListener(new JSONObjectClickedListener(jsonBars,HomeActivity.this,BarActivity.class));
				}
			} catch (Exception e) {
				Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG)
						.show();
			} finally {
				super.onPostExecute(json);
			}
		}
	}

}
