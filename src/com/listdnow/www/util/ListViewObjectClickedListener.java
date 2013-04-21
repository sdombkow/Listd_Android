package com.listdnow.www.util;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.listdnow.www.PassesActivity;
import com.listdnow.www.SearchActivity;

public class ListViewObjectClickedListener implements OnItemClickListener {

	private Activity myActivity;
	private List<String> myList;
	private String myType;

	public ListViewObjectClickedListener(Activity activity,
			List<String> taskOptions) {
		myActivity = activity;
		myList = taskOptions;
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		myType = myList.get(position);
		if (myType.equals("Bars Nearby")) {
			Activity a = myActivity;
			Intent i = new Intent(a, SearchActivity.class);
			i.putExtra("searchNearby", true);
			a.startActivityForResult(i, 0);
		}
		if (myType.equals("My Passes")) {
			Activity a = myActivity;
			Intent i = new Intent(a, PassesActivity.class);
			a.startActivityForResult(i, 0);
		}
		if (myType.equals("Billing Information")) {

		}
		if (myType.equals("Account Information")) {

		}

	}

}
