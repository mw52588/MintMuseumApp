package com.museum;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

//Activity that populates a grid view based on all the artworks presented.  User can then click on items and view it in full screen.
public class BrowseArtwork extends SherlockActivity {
	

	List<ParseObject> artworkQuery;
	private MyAdapter adapter;
	List<ParseObject> artistQuery;
	GridView grid;
	ArrayList<String> data = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ConnectionInformation ci = new ConnectionInformation(
				getApplicationContext());
		final ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		if (ci.isConnectingToInternet()) {
			Parse.initialize(this, "JoBoV30qUpocI35hV5zZImoPaAKi9M0Rpjsk4I1W",
					"kHQFymJ2daKdSrStae0MwzaU8MTCXWeEOVwQCLpC");

			setContentView(R.layout.browse_artwork);
			new GetArtworkData().execute();

		} else {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					getApplicationContext());
			builder.setTitle("WIFI not enabled")
					.setMessage("Would like to enable the WIFI settings")
					.setCancelable(true)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent(
											Settings.ACTION_WIFI_SETTINGS);
									startActivity(intent);
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
									finish();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();

		}

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {

		case android.R.id.home:
			startActivity(new Intent(BrowseArtwork.this, MainActivity.class));
			break;
		default:
			return super.onOptionsItemSelected(menuItem);
		}
		return true;
	}

	private class GetArtworkData extends
			AsyncTask<String, Integer, List<ParseObject>> {
		ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(BrowseArtwork.this, "",
					"Loading...", true);
			super.onPreExecute();

		}

		@Override
		protected List<ParseObject> doInBackground(String... params) {
			ParseQuery query = new ParseQuery("Artwork");
			//query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
			query.orderByDescending("_created_at");

			try {
				artworkQuery = query.find();

			} catch (com.parse.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return artworkQuery;
		}

		@Override
		protected void onPostExecute(final List<ParseObject> artQuery) {

			if (getApplicationContext() != null) {
				grid = (GridView) findViewById(R.id.gridView);
				adapter = new MyAdapter(getApplicationContext(), R.layout.data,
						artQuery); // populates the custom adapter to the query results
				grid.setAdapter(adapter);
				progressDialog.dismiss();

				//Set the grid onitem click.
				grid.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View v,
							int position, long id) {
						Artwork artwork = new Artwork(artQuery.get(position));
						ParseObject parseObject = artQuery.get(position);
						String image1, artworkName, artistName, artworkInfo, artworkObjectId, artworkYear;
						artworkName = parseObject.getString("artworkName");
						artistName = parseObject.getString("artistName");
						if (artworkName != null && !artworkName.isEmpty()) {
							getArtistInfo(artistName, parseObject);
						}
					}

					private void getArtistInfo(String artistName,
							ParseObject parseObject) {
						new GetArtistData(artistName, parseObject).execute();

					}

				});
			}
		}

	}

	private class GetArtistData extends
			AsyncTask<String, Integer, List<ParseObject>> {
		String artistName;
		ParseObject parseObject;

		public GetArtistData(String artistName, ParseObject parseObject) {
			this.artistName = artistName;
			this.parseObject = parseObject;
		}

		@Override
		protected List<ParseObject> doInBackground(String... params) {
			ParseQuery query = new ParseQuery("Artist");
			query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
			query.whereStartsWith("artistName", artistName);
			try {
				artistQuery = query.find();

			} catch (com.parse.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return artistQuery;
		}

		@Override
		protected void onPostExecute(List<ParseObject> artistQuery) {
			// TODO Auto-generated method stub
			super.onPostExecute(artistQuery);
			Intent i = new Intent(getApplicationContext(),
					FullScreenImageActivity.class);
			ParseObject object = artistQuery.get(0);
			String image1, artworkName, artistName, artworkInfo, artworkObjectId, artworkYear;
			boolean isDetail, isGallery;
			image1 = parseObject.getParseFile("image1").getUrl();
			artworkName = parseObject.getString("artworkName");
			artistName = parseObject.getString("artistName");
			artworkInfo = parseObject.getString("artworkInfo");
			artworkObjectId = parseObject.getString("objectId");
			artworkYear = parseObject.getString("year");
			String artistPhoto = object.getParseFile("photo").getUrl();
			Artist a = new Artist(object);
			isDetail = parseObject.getBoolean("isDetail");
			isGallery = parseObject.getBoolean("isGallery");
			
			data.add(a.getArtName());
			data.add(a.getCountry());
			data.add(a.getYear());
			data.add(a.getDeath());
			data.add(a.getInfo());
			data.add(a.getObjectId());

			if (artworkName != null && !artworkName.isEmpty() 
			|| artworkInfo != null && !artworkInfo.isEmpty()
			|| artistName != null && !artistName.isEmpty()) {
				
				i.putExtra("artworkName", artworkName);
				i.putExtra("artworkInfo", artworkInfo);
				i.putExtra("artistName", artistName);
				i.putExtra("artworkObjectId", artworkObjectId);
				i.putExtra("artistPhoto", artistPhoto);
				i.putExtra("artworkYear", artworkYear);
				i.putExtra("image1", image1);
				i.putExtra("isDetail", isDetail);
				i.putStringArrayListExtra("artist", data);
				startActivity(i);

			} else {
				Toast.makeText(getApplicationContext(),"Not enough information provided, please check back later!",Toast.LENGTH_LONG).show();
			}
		}
	}

	public class MyAdapter extends ArrayAdapter<ParseObject> {
		private Context context;
		private final List<ParseObject> objects;
		int resource;

		public MyAdapter(Context context, int resource,
				List<ParseObject> objects) {
			super(context, resource, objects);
			this.context = context;
			this.resource = resource;
			this.objects = objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ParseObject parseObject = getItem(position);
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View gridView;

			if (convertView == null) {

				gridView = new View(context);

				gridView = inflater.inflate(R.layout.artwork_griditem, null);

			} else {
				gridView = (View) convertView;
			}
			TextView textView = (TextView) gridView.findViewById(R.id.label);
			final ImageView photo = (ImageView) gridView
					.findViewById(R.id.artwork_item);

			ParseFile image = (ParseFile) parseObject.get("image1");
			image.getDataInBackground(new GetDataCallback() {

				@Override
				public void done(byte[] data, com.parse.ParseException e) {

					Bitmap bm = BitmapFactory.decodeByteArray(data, 0,
							data.length);
					photo.setImageBitmap(bm);
				}
			});
			
			textView.setText(parseObject.getString("artworkName"));
			

			return gridView;
		}

	}
}