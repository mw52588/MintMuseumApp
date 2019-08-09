package com.museum;

import java.io.InputStream;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

//Fragment that populates information about the artist from previous query.
public class ArtistFrag extends SherlockFragment {
	
	ArrayList<String> artist = new ArrayList<String>();
	String artistPhoto;
	Bitmap immutableBitmap;
	Bitmap bm;
	View myFragmentView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		myFragmentView = inflater.inflate(R.layout.artist_frag, container, false);
		
		Bundle bundle=getArguments();
		artist = bundle.getStringArrayList("artist");
		artistPhoto = bundle.getString("artistPhoto");
		
		 ConnectionInformation ci = new ConnectionInformation(getActivity());
			if (ci.isConnectingToInternet()) {
				new DownloadImageTask().execute(artistPhoto);
			}
			else {
			
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
			//set data to the layout.
			ImageView iv;
			TextView artistInfo;
			TextView artistYear;
			TextView artistName;
			TextView artistCountry;
			immutableBitmap = result;
			if (immutableBitmap != null) {
				Log.d("BLAH", "works");
			}
			immutableBitmap = getResizedBitmap(immutableBitmap, 433, 320);
			bm = immutableBitmap.copy(Bitmap.Config.ARGB_8888, true);
			artistName = (TextView) myFragmentView.findViewById(R.id.artistTitle);
			artistCountry = (TextView) myFragmentView.findViewById(R.id.tvCountry);
			artistYear = (TextView) myFragmentView.findViewById(R.id.tvArtistYear);
			artistInfo = (TextView) myFragmentView.findViewById(R.id.tvArtistInfo);
			artistName.setText(artist.get(0));
			artistCountry.setText("Country: " + artist.get(1));
			artistYear.setText("Born: " + artist.get(2) + "\nDeath: " + artist.get(3) );
			artistInfo.setText(artist.get(4));
			iv = (ImageView) myFragmentView.findViewById(R.id.artistIV);
			iv.setImageBitmap(bm);
		}
	}
}