package com.nicks.sumit.eventcreator;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


import com.nicks.sumit.eventcreator.DbHelper.DatabaseHelper;
import com.nicks.sumit.eventcreator.adapter.FeedListAdapter;
import com.nicks.sumit.eventcreator.data.FeedItem;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class NewsFeed extends Fragment {
	private static final String TAG = NewsFeed.class.getSimpleName();
	private ListView listView;
	private FeedListAdapter listAdapter;
	private List<FeedItem> feedItems;
	private String URL_FEED = "http://api.androidhive.info/feed/feed.json";
	View rootView;
	private DatabaseHelper dbHelper;

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.news_feed, null);
		Log.i("Inter",TAG);
		listView = (ListView)rootView.findViewById(R.id.list);
		dbHelper = new DatabaseHelper(getActivity());
		feedItems = new ArrayList<FeedItem>();

		listAdapter = new FeedListAdapter(getActivity(), feedItems);
		listView.setAdapter(listAdapter);
		loadFromLocalDb();
		// These two lines not needed,
		// just to get the look of facebook (changing background color & hiding the icon)
//		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3b5998")));
//		getActionBar().setIcon(
//				new ColorDrawable(getResources().getColor(android.R.color.transparent)));

		// We first check for cached request
//		Cache cache = AppController.getInstance().getRequestQueue().getCache();
//		Cache.Entry entry = cache.get(URL_FEED);
//		if (entry != null) {
//			// fetch the data from cache
//			try {
//				String data = new String(entry.data, "UTF-8");
//				try {
//					parseJsonFeed(new JSONObject(data));
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
//
//		} else {
//			// making fresh volley request and getting json
//			JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
//					URL_FEED, null, new Response.Listener<JSONObject>() {
//
//				@Override
//				public void onResponse(JSONObject response) {
//					VolleyLog.d(TAG, "Response: " + response.toString());
//					parseJsonFeed(response);
//				}
//			}, new Response.ErrorListener() {
//
//				@Override
//				public void onErrorResponse(VolleyError error) {
//					VolleyLog.d(TAG, "Error: " + error.getMessage());
//				}
//			});
//
//			// Adding request to volley request queue
//			AppController.getInstance().addToRequestQueue(jsonReq);
//		}
		return rootView;
	}

	/**
	 * Parsing json reponse and passing the data to feed view list adapter
	 * */
	private void parseJsonFeed(JSONObject response) {
		try {
			JSONArray feedArray = response.getJSONArray("feed");

			for (int i = 0; i < feedArray.length(); i++) {
				JSONObject feedObj = (JSONObject) feedArray.get(i);

				FeedItem item = new FeedItem();
				item.setId(feedObj.getInt("id"));
				item.setEventName(feedObj.getString("name"));

				// Image might be null sometimes
				String image = feedObj.isNull("image") ? null : feedObj
						.getString("image");
				item.setImge(image);
				item.setEventDescription(feedObj.getString("status"));
				item.setProfilePic(feedObj.getString("profilePic"));
				item.setTimeStamp(feedObj.getString("timeStamp"));

				// url might be null sometimes
				String feedUrl = feedObj.isNull("url") ? null : feedObj
						.getString("url");
				item.setUrl(feedUrl);

				feedItems.add(item);
			}

			// notify data changes to list adapater
			listAdapter.notifyDataSetChanged();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void loadFromLocalDb(){
		ArrayList<FeedItem> contacts = dbHelper.getAllRecordsAlternate();
		if (contacts.size() > 0) {
			FeedItem contactModel;
			for (int i = 0; i < contacts.size(); i++) {
				contactModel = contacts.get(i);
				com.orhanobut.logger.Logger.i("Image Path->" + contactModel.getImagePath());
//				preview.setImageBitmap(imagePath);
				FeedItem item = new FeedItem();
				item.setId(i);
				item.setEventName(contactModel.getEventName());

				// Image might be null sometimes
//				String image = feedObj.isNull("image") ? null : feedObj
//						.getString("image");
				com.orhanobut.logger.Logger.i("ImagePath->>"+ contactModel.getImagePath());
				item.setImge(contactModel.getImagePath());
				item.setTimeStamp(contactModel.getTimeStamp());
				Logger.i("Category "+contactModel.getCategory());
				item.setCategory(contactModel.getCategory());
				item.setEventDescription(contactModel.getEventDescription());
				item.setProfilePic(contactModel.getImagePath());

				// url might be null sometimes
//				String feedUrl = feedObj.isNull("url") ? null : feedObj
//						.getString("url");
//				item.setUrl(feedUrl);

				feedItems.add(item);
			}
			listAdapter.notifyDataSetChanged();
		}
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}

}
