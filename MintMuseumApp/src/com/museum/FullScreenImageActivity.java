package com.museum;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.museum.BrowseArtwork.MyAdapter;
import com.parse.Parse;
import com.parse.ParseObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FullScreenImageActivity extends SherlockActivity {
	String image1;
	String artistPhoto;
	List<ParseObject> artworkQuery;
	List<ParseObject> artPhoto;
	ArrayList<String> artist = new ArrayList<String>();
	String artworkInfo, artistName, artworkName, artworkObjectId, artworkYear;
	ImageView artistImage, artworkImage;
	Bitmap bm;
	boolean isDetail;
	boolean isGallery;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fullscreenimage);
		final ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		Parse.initialize(this, "JoBoV30qUpocI35hV5zZImoPaAKi9M0Rpjsk4I1W",
				"kHQFymJ2daKdSrStae0MwzaU8MTCXWeEOVwQCLpC");

		// get intent data
		Intent i = getIntent();
		image1 = i.getStringExtra("image1");
		artist = i.getStringArrayListExtra("artist");
		artistName = i.getStringExtra("artistName");
		artworkInfo = i.getStringExtra("artworkInfo");
		artworkName = i.getStringExtra("artworkName");
		artworkObjectId = i.getStringExtra("artworkObjectId");
		artworkYear = i.getStringExtra("artworkYear");
		artistPhoto = i.getStringExtra("artistPhoto");
		isDetail = i.getBooleanExtra("isDetail", false);
		isGallery = i.getBooleanExtra("isGallery", false);
		Button getInfo = (Button) findViewById(R.id.getInfoButton);
		// Selected image id

		ConnectionInformation ci = new ConnectionInformation(
				getApplicationContext());
		if (ci.isConnectingToInternet()) {
			new DownloadImageTask().execute(image1);
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

		//Get info button that passes in extras to the tabview activity.
		getInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(FullScreenImageActivity.this, TabViewActivity.class);
				boolean isArtwork = true;
				if (artworkName != null && !artworkName.isEmpty()
						|| artworkInfo != null && !artworkInfo.isEmpty()
						|| artistName != null && !artistName.isEmpty()) {
					intent.putExtra("artworkName", artworkName);
					intent.putExtra("artworkInfo", artworkInfo);
					intent.putExtra("artistName", artistName);
					intent.putExtra("artworkObjectId", artworkObjectId);
					intent.putExtra("artistPhoto", artistPhoto);
					intent.putExtra("artworkYear", artworkYear);
					intent.putExtra("image1", image1);
					intent.putExtra("isGallery", isGallery);
					intent.putExtra("isDetail",isDetail);
					intent.putStringArrayListExtra("artist", artist);
					intent.putExtra("isArtwork", isArtwork);
					startActivity(intent);
				} else {
					Toast.makeText(
							getApplicationContext(),
							"Not enough information provided, please check back later!",
							Toast.LENGTH_LONG).show();
				}

			}

		});

	}

	//Set a home button to the actionbar
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {

		case android.R.id.home:
			startActivity(new Intent(FullScreenImageActivity.this,
					BrowseArtwork.class));
			break;
		default:
			return super.onOptionsItemSelected(menuItem);
		}
		return true;
	}

	public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// CREATE A MATRIX FOR THE MANIPULATION
		Matrix matrix = new Matrix();
		// RESIZE THE BIT MAP
		matrix.postScale(scaleWidth, scaleHeight);

		// "RECREATE" THE NEW BITMAP
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
				matrix, false);
		return resizedBitmap;
	}

	private class DownloadImageTask extends AsyncTask<String, Bitmap, Bitmap> {

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			result = getResizedBitmap(result, 866, 640);

			ImageView imageView = (ImageView) findViewById(R.id.full_image_view);

			imageView.setImageBitmap(result);
		}
	}

}
