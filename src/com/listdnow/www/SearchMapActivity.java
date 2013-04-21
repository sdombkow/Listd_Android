package com.listdnow.www;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.LatLngBounds.Builder;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class SearchMapActivity extends Activity implements
		OnInfoWindowClickListener {

	static final LatLng HAMBURG = new LatLng(53.558, 9.927);
	static final LatLng KIEL = new LatLng(53.551, 9.993);
	private GoogleMap map;
	private List<LatLng> mAddresses;
	private JSONObject myBars;
	private JSONArray jsonBars;
	private double[] barLatLng;
	private LatLngBounds latlngBound;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_map);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		map.setOnInfoWindowClickListener(this);
		try {
			myBars = new JSONObject(getIntent().getStringExtra("jsonobject"));
			jsonBars = myBars.getJSONObject("data").getJSONArray("bars");
			barLatLng = getIntent().getDoubleArrayExtra("barLatLng");
			mAddresses = new ArrayList<LatLng>(barLatLng.length / 2);
			for (int i = 0; i < barLatLng.length; i = i + 2) {
				LatLng currentBarLatLng = new LatLng(barLatLng[i],
						barLatLng[i + 1]);
				Marker currentBarMarker = map
						.addMarker(new MarkerOptions().position(
								currentBarLatLng)
								.title(jsonBars.getJSONObject(i / 2).getString(
										"name")));
				mAddresses.add(currentBarMarker.getPosition());

			}

			Builder boundsBuilder = new LatLngBounds.Builder();
			for (int i = 0; i < mAddresses.size(); i++) {
				boundsBuilder.include(mAddresses.get(i));
			}
			LatLngBounds bounds = boundsBuilder.build();
			map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 5, 5, 1));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onInfoWindowClick(Marker marker) {
		// TODO Auto-generated method stub

		LatLng currPosition = marker.getPosition();
		try {
			JSONObject jsonOBJ = jsonBars.getJSONObject(mAddresses
					.indexOf(currPosition));
			Intent i = new Intent(this, BarActivity.class);
			i.putExtra("jsonobject", jsonOBJ.toString());
			startActivityForResult(i, 0);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}