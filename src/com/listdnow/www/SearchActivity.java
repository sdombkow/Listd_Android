package com.listdnow.www;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.listdnow.www.authentication.WelcomeActivity;
import com.listdnow.www.util.JSONObjectClickedListener;
import com.savagelook.android.UrlJsonAsyncTask;

public class SearchActivity extends Activity {

	private static final String SEARCH_URL = "http://10.0.2.2:3000/api/v1/search.json";
	private SharedPreferences mPreferences;
	private String mSearchQuery;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		Intent intent = this.getIntent();
		mSearchQuery = intent.getStringExtra("search_query");
		if (mSearchQuery == null) {
			mSearchQuery = ""; // search for nearest in the future
		}

		mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);

	}

	@Override
	public void onResume() {
		super.onResume();

		if (mPreferences.contains("AuthToken")) {
			searchBarsFromAPI(SEARCH_URL);
		} else {
			Intent intent = new Intent(SearchActivity.this,
					WelcomeActivity.class);
			startActivityForResult(intent, 0);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_search, menu);
		return true;
	}

	public void search(View button) {
		EditText searchField = (EditText) findViewById(R.id.search_edittext);
		mSearchQuery = searchField.getText().toString();
		searchBarsFromAPI(SEARCH_URL);
	}

	private void searchBarsFromAPI(String url) {
		SearchResultsTask searchResultsTask = new SearchResultsTask(
				SearchActivity.this);
		searchResultsTask.setMessageLoading("Seaching...");
		searchResultsTask.execute(url + "?auth_token="
				+ mPreferences.getString("AuthToken", ""));
	}

	private class SearchResultsTask extends UrlJsonAsyncTask {
		public SearchResultsTask(Context context) {
			super(context);
		}

		@Override
		protected JSONObject doInBackground(String... urls) {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(urls[0]);
			JSONObject searchQuery = new JSONObject();
			String response = null;
			JSONObject json = new JSONObject();

			try {
				try {
					// setup the returned values in case
					// something goes wrong
					json.put("success", false);
					json.put("info", "Something went wrong. Retry!");
					// add the user email and password to
					// the params
					searchQuery.put("search", mSearchQuery);
					StringEntity se = new StringEntity(searchQuery.toString());
					post.setEntity(se);

					// setup the request headers
					post.setHeader("Accept", "application/json");
					post.setHeader("Content-Type", "application/json");

					ResponseHandler<String> responseHandler = new BasicResponseHandler();
					response = client.execute(post, responseHandler);
					json = new JSONObject(response);

				} catch (HttpResponseException e) {
					e.printStackTrace();
					Log.e("ClientProtocol", "" + e);
					json.put("info",
							"Email and/or password are invalid. Retry!");
				} catch (IOException e) {
					e.printStackTrace();
					Log.e("IO", "" + e);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Log.e("JSON", "" + e);
			}

			return json;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				if (json.getBoolean("success")) {
					JSONArray jsonBars = json.getJSONObject("data")
							.getJSONArray("bars");
					Bundle b = new Bundle();
					b.putString("bars", jsonBars.toString());
					int length = jsonBars.length();
					List<String> barTitles = new ArrayList<String>(length);

					for (int i = 0; i < length; i++) {
						barTitles.add(jsonBars.getJSONObject(i).getString(
								"name"));
					}

					ListView barListView = (ListView) findViewById(R.id.search_results_list_view);
					if (barListView != null) {
						barListView
								.setAdapter(new ArrayAdapter<String>(
										SearchActivity.this,
										android.R.layout.simple_list_item_1,
										barTitles));
						barListView
								.setOnItemClickListener(new JSONObjectClickedListener(
										jsonBars, SearchActivity.this,
										BarActivity.class,"JSONObject"));
					}
				}
			} catch (Exception e) {
				// something went wrong: show a Toast
				// with the exception message
				Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG)
						.show();
			} finally {
				super.onPostExecute(json);
			}
		}
	}
}
