package com.listdnow.www.authentication;

import java.io.IOException;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
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
import android.widget.EditText;
import android.widget.Toast;

import com.listdnow.www.AdminActivity;
import com.listdnow.www.HomeActivity;
import com.listdnow.www.PartnerActivity;
import com.listdnow.www.R;
import com.savagelook.android.UrlJsonAsyncTask;

public class LoginActivity extends Activity {

	private final static String LOGIN_API_ENDPOINT_URL = "http://10.0.2.2:3000/api/v1/sessions.json";
	private SharedPreferences mPreferences;
	private String mUserEmail;
	private String mUserPassword;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}

	public void login(View button) {
		EditText userEmailField = (EditText) findViewById(R.id.userEmail);
		mUserEmail = userEmailField.getText().toString();
		EditText userPasswordField = (EditText) findViewById(R.id.userPassword);
		mUserPassword = userPasswordField.getText().toString();

		if (mUserEmail.length() == 0 || mUserPassword.length() == 0) {
			// input fields are empty
			Toast.makeText(this, "Please complete all the fields",
					Toast.LENGTH_LONG).show();
			return;
		} else {
			LoginTask loginTask = new LoginTask(LoginActivity.this);
			loginTask.setMessageLoading("Logging in...");
			loginTask.execute(LOGIN_API_ENDPOINT_URL);
		}
	}

	private class LoginTask extends UrlJsonAsyncTask {
		public LoginTask(Context context) {
			super(context);
		}

		@Override
		protected JSONObject doInBackground(String... urls) {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(urls[0]);
			JSONObject holder = new JSONObject();
			JSONObject userObj = new JSONObject();
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
					userObj.put("email", mUserEmail);
					userObj.put("password", mUserPassword);
					holder.put("user", userObj);
					StringEntity se = new StringEntity(holder.toString());
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
					// everything is ok
					SharedPreferences.Editor editor = mPreferences.edit();
					// save the returned auth_token into
					// the SharedPreferences
					editor.putString("AuthToken", json.getJSONObject("data")
							.getString("auth_token"));
					editor.commit();

					Intent intent;
					if (json.getBoolean("admin")) {
						intent = new Intent(getApplicationContext(),
								AdminActivity.class);
					} else if (json.getBoolean("partner")) {
						intent = new Intent(getApplicationContext(),
								PartnerActivity.class);
					} else {
						intent = new Intent(getApplicationContext(),
								HomeActivity.class);
					}
					startActivityForResult(intent, 0);
					finish();
				}
				Toast.makeText(context, json.getString("info"),
						Toast.LENGTH_LONG).show();
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
