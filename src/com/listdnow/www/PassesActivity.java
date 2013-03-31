package com.listdnow.www;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.listdnow.www.util.JSONObjectClickedListener;
import com.savagelook.android.UrlJsonAsyncTask;

public class PassesActivity extends Activity {
	private static final String PASSES_URL = "http://10.0.2.2:3000/api/v1/passes.json";
	private SharedPreferences mPreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_passes);
		mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
		getPassesFromAPI(PASSES_URL);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_passes, menu);
		return true;
	}

	private void getPassesFromAPI(String url) {
		PassesResultsTask passesResultsTask = new PassesResultsTask(
				PassesActivity.this);
		passesResultsTask.setMessageLoading("Retrieving Passes...");
		passesResultsTask.execute(url + "?auth_token="
				+ mPreferences.getString("AuthToken", ""));

	}

	private class PassesResultsTask extends UrlJsonAsyncTask {
		public PassesResultsTask(Context context) {
			super(context);
		}

		@Override
		protected JSONObject doInBackground(String... urls) {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(urls[0]);
			JSONObject searchQuery = new JSONObject();
			String response = null;
			JSONObject json = new JSONObject();

			try {
				try {
					// setup the returned values in case
					// something goes wrong
					json.put("success", false);
					json.put("info", "Something went wrong. Retry!");

					// setup the request headers
					get.setHeader("Accept", "application/json");
					get.setHeader("Content-Type", "application/json");

					ResponseHandler<String> responseHandler = new BasicResponseHandler();
					response = client.execute(get, responseHandler);
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
					JSONArray jsonPasses = json.getJSONObject("data")
							.getJSONArray("passes");
					Bundle b = new Bundle();
					b.putString("passes", jsonPasses.toString());
					int length = jsonPasses.length();
					List<String> passesTitle = new ArrayList<String>(length);

					for (int i = 0; i < length; i++) {
						passesTitle.add(jsonPasses.getJSONObject(i).getString(
								"name")+ " - " + jsonPasses.getJSONObject(i).getJSONObject("pass_set").getString(
										"date") );
					}

					ListView passesListView = (ListView) findViewById(R.id.passesListView);
					if (passesListView != null) {
						passesListView
								.setAdapter(new ArrayAdapter<String>(
										PassesActivity.this,
										android.R.layout.simple_list_item_1,
										passesTitle));
						passesListView
								.setOnItemClickListener(new JSONObjectClickedListener(
										jsonPasses, PassesActivity.this,
										PassActivity.class));
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
