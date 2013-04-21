package com.listdnow.www;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.app.ProgressDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.listdnow.www.util.JSONObjectClickedListener;
import com.savagelook.android.UrlJsonAsyncTask;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;

public class PurchaseActivity extends Activity {

	private static final String PASS_SET_URL = "http://10.0.2.2:3000/api/v1/passset.json";
	private static final String PURCHASE_URL = "http://10.0.2.2:3000/api/v1/purchase.json";
	private SharedPreferences mPreferences;
	private JSONObject myPassSet;
	private String purchaseNameOfTickets;
	private int purchaseNumberOfTickets;
	private String purchaseCreditCardNumber;
	private String purchaseCVV;
	private int purchaseMonth;
	private int purchaseYear;
	private int purchaseCost;
	private boolean limitedTickets = false;
	static Map<String, Object> CardParams = new HashMap<String, Object>();
	static Map<String, Object> ChargeParams = new HashMap<String, Object>();
	String stripeErrorMessage = null;
	private ProgressDialog myDialog;
	private String chargeToken = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_purchase);

		mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);

		try {
			myPassSet = new JSONObject(getIntent().getStringExtra("jsonobject"));
			purchaseNumberOfTickets = getIntent().getIntExtra(
					"purchaseNumberOfTickets", 0);
			purchaseNameOfTickets = getIntent().getStringExtra(
					"purchaseNameOfTickets");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			purchaseCost = myPassSet.getInt("price") * purchaseNumberOfTickets;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TextView purchaseCost_textView = (TextView) findViewById(R.id.purchaseCost_textView);
		purchaseCost_textView.setText("Total Cost: $" + purchaseCost);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_purchase, menu);
		return true;
	}

	public void purchaseButtonClicked(View button) {
		EditText purchaseCreditCardNumber_editText = (EditText) findViewById(R.id.purchaseCreditCardNumber_editText);
		EditText purchaseCVV_editText = (EditText) findViewById(R.id.purchaseCVV_editText);
		EditText purchaseMonth_editText = (EditText) findViewById(R.id.purchaseMonth_editText);
		EditText purchaseYear_editText = (EditText) findViewById(R.id.purchaseYear_editText);
		purchaseCreditCardNumber = purchaseCreditCardNumber_editText.getText()
				.toString();
		purchaseCVV = purchaseCVV_editText.getText().toString();
		purchaseMonth = Integer.parseInt(purchaseMonth_editText.getText()
				.toString());
		purchaseYear = Integer.parseInt(purchaseYear_editText.getText()
				.toString());
		initializeCardParams();
		Stripe.apiKey = "sk_test_6HFfiDn24fHy0wzjETZZ1uIc";
		ProcessStripe processStripe = new ProcessStripe(PurchaseActivity.this);
		processStripe.setMessageLoading("Processing Order");
		processStripe.execute(PASS_SET_URL + "?auth_token="
				+ mPreferences.getString("AuthToken", ""));
	}

	private void initializeCardParams() {
		CardParams.put("number", purchaseCreditCardNumber);
		CardParams.put("exp_month", purchaseMonth);
		CardParams.put("exp_year", purchaseYear);
		CardParams.put("cvc", purchaseCVV);
		ChargeParams.put("amount", purchaseCost * 100);
		ChargeParams.put("currency", "usd");
		ChargeParams.put("card", CardParams);
	}

	private class ProcessStripe extends UrlJsonAsyncTask {
		public ProcessStripe(Context context) {
			super(context);
		}

		@Override
		protected JSONObject doInBackground(String... urls) {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(urls[0]);
			JSONObject purchaseParams = new JSONObject();
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
					purchaseParams.put("id", myPassSet.get("id"));
					StringEntity se = new StringEntity(
							purchaseParams.toString());
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

			try {
				myPassSet = json.getJSONObject("data").getJSONObject("PassSet");
				if (purchaseNumberOfTickets > myPassSet.getInt("unsold_passes")) {
					limitedTickets = true;
				} else {
					limitedTickets = false;
					stripeErrorMessage = null;
					Charge charge;
					try {
						charge = Charge.create(ChargeParams);
						chargeToken = charge.getId();
						System.out.println(charge);
					} catch (StripeException e) {
						stripeErrorMessage = e.getMessage();
					}
					if (stripeErrorMessage != null) {
						json = null;
					} else {
						client = new DefaultHttpClient();
						post = new HttpPost(PURCHASE_URL + "?auth_token="
								+ mPreferences.getString("AuthToken", ""));
						purchaseParams = new JSONObject();
						response = null;
						json = new JSONObject();
						try {
							try {
								// setup the returned values in case
								// something goes wrong
								json.put("success", false);
								json.put("info", "Something went wrong. Retry!");
								// add the user email and password to
								// the params
								purchaseParams.put("id", myPassSet.get("id"));
								purchaseParams.put("bar_id",
										myPassSet.get("bar_id"));
								purchaseParams.put("num_passes",
										purchaseNumberOfTickets);
								purchaseParams.put("purchaseCost",
										purchaseCost);
								purchaseParams.put("purchaseName",
										purchaseNameOfTickets);
								purchaseParams.put("chargeToken", chargeToken);

								StringEntity se = new StringEntity(
										purchaseParams.toString());
								post.setEntity(se);

								// setup the request headers
								post.setHeader("Accept", "application/json");
								post.setHeader("Content-Type",
										"application/json");

								ResponseHandler<String> responseHandler = new BasicResponseHandler();
								response = client
										.execute(post, responseHandler);
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
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;
		}

		protected void onPostExecute(JSONObject json) {
			if (limitedTickets) {
				Toast.makeText(context, "Not Enough Tickets Available",
						Toast.LENGTH_LONG).show();
				finish();
				super.onPostExecute(json);
			} else if (stripeErrorMessage != null) {
				Toast.makeText(context, stripeErrorMessage, Toast.LENGTH_LONG)
						.show();
				super.onPostExecute(json);
			} else {
				try {
					Intent intent = new Intent(getApplicationContext(),
							PassActivity.class);
					intent.putExtra("jsonobject", json.toString());
					startActivity(intent);
					finish();
				} catch (Exception e) {
					Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG)
							.show();
				} finally {
					super.onPostExecute(json);
				}
			}

		}
	}

}
