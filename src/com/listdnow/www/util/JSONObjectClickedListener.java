package com.listdnow.www.util;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class JSONObjectClickedListener implements OnItemClickListener {

	private JSONArray myObjects;
	private Activity myActivity;
	private Class<?> myNextActivity;
	private String myType;

	public JSONObjectClickedListener(JSONArray objects, Activity activity,
			Class<?> nextActivity, String Type) {
		myObjects = objects;
		myActivity = activity;
		myNextActivity = nextActivity;
		myType = Type;
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (myType.equals("JSONObject")) {
			ClickJSONObject(position);
		}
	}

	public void ClickJSONObject(int position) {
		try {
			Activity a = myActivity;
			Intent i = new Intent(a, myNextActivity);
			i.putExtra("jsonobject", myObjects.getJSONObject(position)
					.toString());
			Log.d("Activity Transition",
					"transitionioning from search activity to bar activity");
			a.startActivityForResult(i, 0);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
