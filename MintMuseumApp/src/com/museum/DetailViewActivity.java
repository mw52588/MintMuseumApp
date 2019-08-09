package com.museum;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;


import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.Parse;

public class DetailViewActivity extends SherlockActivity {
	
	String artworkName, artistName, image1;
	final String by = "By: ";
	
	private String name = "", id = "" ,shape ="", info = "";
	private String coords = "";
	private ArrayList<ParseObject> retreievedCoordinates= null;
	private ArrayList<ShapeData> dataPoints = null;
	
	private ShapeData currCoord = null;
	private ImageMap map;
	private ImageMap mapLayoutRef = null;
	
	RelativeLayout mapParentLayout = null;
	
	Bitmap bm;
	Bitmap immutableBitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detailview_activity_layout);
		final ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		artworkName = getIntent().getExtras().getString("artworkName");
		image1 = getIntent().getExtras().getString("image1");
		artistName = getIntent().getExtras().getString("artistName");
		
		parseCoords();
	}
		
		private void parseCoords(){
			
			ParseQuery query = new ParseQuery("Coordinates");
			query.whereStartsWith("for", artworkName);
			query.findInBackground(new FindCallback() {
			 
				@Override
				public void done(List<ParseObject> returnedList, ParseException e) {
					if (e == null) {
			            //retreievedCoordinates.addAll(returnedList);
						dataPoints = new ArrayList<ShapeData>();
						retreievedCoordinates = new ArrayList<ParseObject>();
						for (int i = 0; i < returnedList.size(); i++) {
							retreievedCoordinates.add(returnedList.get(i));
						}
			            
			            for(ParseObject o : retreievedCoordinates){
			            	currCoord=null;
			            	name = o.getString("name");
			            	shape = o.getString("shape");
			            	coords = o.getString("coords");
			            	info = o.getString("info");
			            	id = o.getString("identifier");
			            	
			            	currCoord = new ShapeData(name, shape, coords, id, info);
			            	dataPoints.add(currCoord);	
			            }
			            
			        } else {
			        	Toast.makeText(DetailViewActivity.this, "Error retreiving mappings: " , Toast.LENGTH_LONG).show();
			        }
			        
		       		 
		               map = new ImageMap(DetailViewActivity.this, dataPoints);
		             
		               map.addOnImageMapClickedHandler(new ImageMap.OnImageMapClickedHandler() {
		       			@Override
		       			public void onImageMapClicked(int id) {
		       				
		       				map.showAllBubbles();
		       				mapParentLayout.invalidate();
		       			}

						@Override
						public void onImageMapClicked() {
							// TODO Auto-generated method stub
							map.showAllBubbles();
							mapParentLayout.invalidate();
						}

						@Override
						public void onBubbleClicked(String info) {
							AlertDialog.Builder builder = new AlertDialog.Builder(DetailViewActivity.this);
			       			builder.setTitle("This point is interesting because... ")
			       					.setMessage(info)
			       					.setCancelable(true)
			       					.setNegativeButton("Close",
			       							new DialogInterface.OnClickListener() {
			       								public void onClick(DialogInterface dialog,
			       										int which) {
			       									dialog.cancel();
			       								}
			       							});
			       			AlertDialog alert = builder.create();
			       			alert.show();	
						}
		       		});
		               
		               RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		               params.addRule(RelativeLayout.BELOW,findViewById(com.museum.R.id.artistNameLabel).getId());
		               
		               mapParentLayout = (RelativeLayout)findViewById(R.id.mapParentLayout);
		               
		               map.setLayoutParams(params);
		               mapParentLayout.addView(map);
		               map.setId(12345);
		               mapLayoutRef = (ImageMap)findViewById(12345);
		               
		               TextView artistLabel = (TextView)findViewById(com.museum.R.id.artistNameLabel);
		               TextView artworkLabel = (TextView)findViewById(com.museum.R.id.artNameLabel);
		               
		               artistLabel.setText(by + artistName);
		               artworkLabel.setText(artworkName);
		               
		               ConnectionInformation ci = new ConnectionInformation(DetailViewActivity.this);
		       		if (ci.isConnectingToInternet()) {
		       			new DownloadImageTask().execute(image1);
		       		} else {

		       			AlertDialog.Builder builder = new AlertDialog.Builder(DetailViewActivity.this);
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
		       								
		       								}
		       							});
		       			AlertDialog alert = builder.create();
		       			alert.show();

		       		}
					
				}
			});
		}

		private class DownloadImageTask extends AsyncTask<String, Bitmap, Bitmap> {

			protected Bitmap doInBackground(String... urls) {
				String urldisplay = urls[0];
				Bitmap image = null;
				try {
					InputStream in = new java.net.URL(urldisplay).openStream();
					image = BitmapFactory.decodeStream(in);
				} catch (Exception e) {
					Log.e("Error", e.getMessage());
					e.printStackTrace();
				}
				return image;
			}
			
			protected void onPostExecute(Bitmap result) {
		
				mapLayoutRef.setImageBitmap(result);
		
				mapParentLayout.invalidate();
			}
		}
		
		@Override
		public boolean onOptionsItemSelected(MenuItem menuItem) {
			switch (menuItem.getItemId()) {

			case android.R.id.home:
				finish();
				break;

			default:
				return super.onOptionsItemSelected(menuItem);
			}
			return true;
		}


}
