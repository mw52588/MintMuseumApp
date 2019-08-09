package com.museum;

import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.app.SherlockListFragment;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//Adds listFrag to the parent activity.  Creates a listview and is populated from parseobjects from Parse.com.
public class ListFrag extends SherlockListFragment {

	private ListView lv;
	private List<ParseObject> artQuery;
	private ProgressDialog dialog;
	private LayoutInflater mInflater;
	private Dialog progressDialog;
	private boolean taskRun;
	private Context context;
	private CustomAdapter adapter;
	private List<ParseObject> objects;
	boolean mDualPane;
	public DetailFrag frag;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//Inflate the listview
		final View view = inflater.inflate(R.layout.list, container, false);
		Log.i("NavigationListFragment", "ListView Inflated!!");
		return view;
	}
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		//Retrieve the parseObjects from the current view.
		ParseObject parseObject = (ParseObject) getListView().getItemAtPosition(position);
		frag = (DetailFrag) getFragmentManager().findFragmentById(R.id.frag_art);
		
		Artist a = new Artist(parseObject);
		ArrayList<String> data = new ArrayList<String>();
		//Toast.makeText(getActivity(), "ArtName is" + parseObject.getString("artistName"), Toast.LENGTH_LONG).show();
		//Toast.makeText(getActivity(), "ArtName is" + a.artName, Toast.LENGTH_LONG).show();
		
		//Add the data to a list.
	    data.add(a.getArtName());
	    data.add(a.getCountry());
	    data.add(a.getYear());
	    data.add(a.getDeath());
	    data.add(a.getInfo());
	    data.add(a.getObjectId());
	    String artistPhoto = parseObject.getParseFile("photo").getUrl();
	    
	    //Check if it's a tablet or smartphone.
		if (!mDualPane) {
			//If its smartphone just start the activity with data and artistPhoto as extras.
			Intent intent = new Intent();
			intent.setClass(getActivity(), DetailActivity.class);
			intent.putExtra("artistPhoto", artistPhoto);
			intent.putStringArrayListExtra("data", data);
			startActivity(intent);
			
		} else {
			
			//If it's tablet start the detailFragment.
			frag.getData(data, artistPhoto);
			
		}
		
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//Get the layout file.
		View detailsFrame = getActivity().findViewById(R.id.frag_art);
		//Determine if it's smartphone or tablet.
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
        ConnectionInformation ci = new ConnectionInformation(getActivity());
		
        //Check for connection to the internet.
        if (ci.isConnectingToInternet()) {
        	//Start a parseQuery with class name "Artist" where it is ordered by descending.
			ParseQuery query = new ParseQuery("Artist");
			//query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
			query.orderByDescending("_created_at");
			//Async Callback method to retrieve data.
			query.findInBackground(new FindCallback() {

				@Override
				public void done(List<ParseObject> objects, ParseException e) {
					// TODO Auto-generated method stub
					//Make sure no exceptions are found.
					if (e == null) {
						artQuery = objects;
						if (getActivity() != null) {
							//populate the custom Adapter.
							adapter = new CustomAdapter(getActivity(), R.layout.data,
								artQuery);
							//Set the adapter to the custom adapter.
							getListView().setAdapter(adapter);
						
						}
					}
					 else {
				            Log.d("score", "Error: " + e.getMessage());
				     }
				}
			});
		}
		else {
				//Start WIFI settings dialog if can't connect to internet.
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("WIFI not enabled")
				.setMessage("Would like to enable the WIFI settings")
				.setCancelable(true)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
						startActivity(intent);
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						getActivity().finish();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();		
			
		}

	}

	public class CustomAdapter extends ArrayAdapter<ParseObject> {

		Context context;
		int resource;
		
		//Resoure = layout file, and object is the results of the query.
		public CustomAdapter(Context context, int resource,
				List<ParseObject> objects) {
			super(context, resource, objects);
			this.resource = resource;
		}
		
		//Get the position and items from the view.
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			LinearLayout listView;
			ParseObject parseObject = getItem(position);
			//Create the view only if hasn't already been created.
			if (convertView == null) {
				listView = new LinearLayout(getContext());
				String inflater = Context.LAYOUT_INFLATER_SERVICE;
				LayoutInflater vi;
				vi = (LayoutInflater) getContext().getSystemService(inflater);
				vi.inflate(resource, listView, true);
			} else {
				listView = (LinearLayout) convertView;
			}
			
			//set the layout file items.
			final ImageView photo = (ImageView) listView
					.findViewById(R.id.photo);
			TextView artName = (TextView) listView.findViewById(R.id.artName);
			TextView country = (TextView) listView.findViewById(R.id.country);
			TextView year = (TextView) listView.findViewById(R.id.year);

			//Download image in asynctask and set to imageview.
			ParseFile image = (ParseFile) parseObject.get("photo");
			image.getDataInBackground(new GetDataCallback() {

				@Override
				public void done(byte[] data, com.parse.ParseException e) {
					
					Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
					photo.setImageBitmap(bm);
				}
			});

			artName.setText(parseObject.getString("artistName"));
			country.setText(parseObject.getString("country"));
			year.setText(parseObject.getString("birthYear") + " - "
					+ parseObject.getString("death"));

			return listView;
		}
	}
}