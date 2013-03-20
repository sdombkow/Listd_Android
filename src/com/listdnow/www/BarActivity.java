package com.listdnow.www;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.listdnow.www.util.JSONObjectClickedListener;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

			JSONArray jsonPassSets = myBar.getJSONArray("pass_sets");
			Bundle b = new Bundle();
			b.putString("pass_sets", jsonPassSets.toString());
			int length = jsonPassSets.length();
			List<String> barTitles = new ArrayList<String>(length);

			for (int i = 0; i < length; i++) {
				barTitles.add(jsonPassSets.getJSONObject(i).getString("date"));
			}

			ListView barListView = (ListView) findViewById(R.id.pass_set_listView);
			if (barListView != null) {
				barListView.setAdapter(new ArrayAdapter<String>(
						BarActivity.this, android.R.layout.simple_list_item_1,
						barTitles));
				barListView
						.setOnItemClickListener(new JSONObjectClickedListener(
								jsonPassSets, BarActivity.this,
								PassSetActivity.class, "JSONObject"));
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
