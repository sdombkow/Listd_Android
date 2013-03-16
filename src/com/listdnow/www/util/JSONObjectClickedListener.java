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
	public JSONObjectClickedListener(JSONArray objects,Activity activity, Class<?> nextActivity)
	{
		myObjects = objects;
		myActivity = activity;
		myNextActivity = nextActivity;
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		try {
			Toast.makeText(myActivity,
				      "Clicked item " + myObjects.getJSONObject(position).getString(
								"name"), Toast.LENGTH_LONG)
				      .show();
			Activity a = myActivity;
			Intent i = new Intent(a,myNextActivity);
			i.putExtra("jsonobject", myObjects.getJSONObject(position).toString());
			Log.d("Activity Transition","transitionioning from home to bar activity");
			a.startActivityForResult(i, 0);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			  }
	

}
