package com.listdnow.www;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.savagelook.android.UrlJsonAsyncTask;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;

public class PurchaseActivity extends Activity {

	private static final String PURCHASE_URL = "http://10.0.2.2:3000/api/v1/search.json";
	private SharedPreferences mPreferences;
	private JSONObject myPassSet;
	private String purchaseNameOfTickets;
	private int purchaseNumberOfTickets;
	private String purchaseCreditCardNumber;
	private String purchaseCVV;
	private int purchaseMonth;
	private int purchaseYear;
	private int purchaseCost;
	static Map<String, Object> CardParams = new HashMap<String, Object>();
	static Map<String, Object> ChargeParams = new HashMap<String, Object>();
	String errorMessage = null;
	private ProgressDialog myDialog;

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
		Stripe.apiKey = "tGN0bIwXnHdwOa85VABjPdSn8nWY7G7I";
		ProcessStripe processStripe = new ProcessStripe(PurchaseActivity.this);
		processStripe.setMessageLoading("Processing Order");
		processStripe.execute(PURCHASE_URL + "?auth_token="
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
			// TODO Auto-generated method stub
			Charge charge;
			try {
				charge = Charge.create(ChargeParams);
				System.out.println(charge);
			} catch (StripeException e) {
				errorMessage = e.getMessage();
			}
			if (!errorMessage.equals(null)) {
				int a = 0;
				return null;
			} else {
				int b = 0;
				return null;
			}
		}

		protected void onPostExecute(JSONObject json) {
			if (!errorMessage.equals(null)) {
				Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
			}
			super.onPostExecute(json);
		}
	}

}
