package com.museum;

import java.io.InputStream;
import java.util.ArrayList;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.actionbarsherlock.app.SherlockFragment;
//Fragment that populates information about the artwork from previous query.
public class ArtworkFrag extends SherlockFragment {

	ArrayList<String> artist = new ArrayList<String>();
	Bitmap immutableBitmap;
	Bitmap bm;
	View myFragmentView;
	String image1, artworkYear, artistName, artworkName, artworkInfo;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		myFragmentView = inflater.inflate(R.layout.artwork_frag, container,
				false); //inflate the layout to the fragment.

		Bundle bundle = getArguments();
		//Get the extras data.
		image1 = bundle.getString("image1");
		artworkYear = bundle.getString("artworkYear");
		artistName = bundle.getString("artistName");
		artworkInfo = bundle.getString("artworkInfo");
		artworkName = bundle.getString("artworkName");
		ConnectionInformation ci = new ConnectionInformation(getActivity());
		if (ci.isConnectingToInternet()) {
			new DownloadImageTask().execute(image1);
		} else {

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
									getActivity().finish();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();

		}
		
		
		return myFragmentView;
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
			//Set all the needed information to the layout.
			ImageView iv;
			TextView artworkTitle;

			TextView aInfo;

			immutableBitmap = result;
			if (immutableBitmap != null) {
				Log.d("BLAH", "works");
			}
			immutableBitmap = getResizedBitmap(immutableBitmap, 433, 320);
			bm = immutableBitmap.copy(Bitmap.Config.ARGB_8888, true);
			artworkTitle = (TextView) myFragmentView
					.findViewById(R.id.tvArtworkTitle);
			aInfo = (TextView) myFragmentView
					.findViewById(R.id.tvArtworkIn);
			artworkTitle.setText(artworkName + " created by " + artistName
					+ " in " + artworkYear);
			aInfo.setText(artworkInfo);
			iv = (ImageView) myFragmentView.findViewById(R.id.artworkIV);
			iv.setImageBitmap(bm);
		}
	}
}
