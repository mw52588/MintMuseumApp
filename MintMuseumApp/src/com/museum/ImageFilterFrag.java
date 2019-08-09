package com.museum;

import java.io.InputStream;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;


//Fragment that provides information for color filtering with presets.  Also allows user to view the gallery and image mappings.
public class ImageFilterFrag extends SherlockFragment {

	ImageView imageView;
	SeekBar Bar1, Bar2, Bar3;
	Spinner redSpinner, greenSpinner, blueSpinner;
	TextView colorInfo;
	String artistName;
	String artworkName;
	String image1;
	Bitmap immutableBitmap;
	Bitmap bm;
	View myFragmentView;
	List<String> rgb1, rgb2, rgb3, rgb4;
	List<Integer> v1, v2, v3, v4;
	List<ParseObject> query1;
	Button preset1, preset2, preset3, preset4, grayscale, reset, detailViewButton, galleryViewButton;
	String[] rgbColors = { "Red", "Green", "Blue" }; // R G B value array.
	boolean isDetail, isGallery;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		//Get bundle data and set buttons and imageviews.
		artworkName = bundle.getString("artworkName");
		artistName = bundle.getString("artistName");
		image1 = bundle.getString("image1");
		isDetail = bundle.getBoolean("isDetail", false);
		isGallery = bundle.getBoolean("isGallery",false);
		myFragmentView = inflater.inflate(R.layout.imagefilter_frag, container,
				false); // Inflate the fragment layout.
		imageView = (ImageView) myFragmentView.findViewById(R.id.imageView11);

		preset1 = (Button) myFragmentView.findViewById(R.id.preset1);
		preset2 = (Button) myFragmentView.findViewById(R.id.preset2);
		preset3 = (Button) myFragmentView.findViewById(R.id.preset3);
		preset4 = (Button) myFragmentView.findViewById(R.id.preset4);
		reset = (Button) myFragmentView.findViewById(R.id.resetPresets);
		grayscale = (Button) myFragmentView.findViewById(R.id.grayscl);
		detailViewButton = (Button) myFragmentView.findViewById(R.id.detailViewButton);
		detailViewButton.setVisibility(View.INVISIBLE);
		
		galleryViewButton = (Button) myFragmentView.findViewById(R.id.galleryViewButton);
		galleryViewButton.setVisibility(View.INVISIBLE);
		Bar1 = (SeekBar) myFragmentView.findViewById(R.id.bar11);
		Bar2 = (SeekBar) myFragmentView.findViewById(R.id.bar22);
		Bar3 = (SeekBar) myFragmentView.findViewById(R.id.bar3);

		redSpinner = (Spinner) myFragmentView.findViewById(R.id.roption1);
		greenSpinner = (Spinner) myFragmentView.findViewById(R.id.goption1);
		blueSpinner = (Spinner) myFragmentView.findViewById(R.id.boption);

		colorInfo = (TextView) myFragmentView
				.findViewById(R.id.displaycolorInfo);

		ArrayAdapter<String> redArrayAdapter = new ArrayAdapter<String>(
				getActivity().getApplicationContext(), R.layout.spinner_item,
				rgbColors);
		// redArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		redSpinner.setAdapter(redArrayAdapter);
		redSpinner.setSelection(0);

		ArrayAdapter<String> greenArrayAdapter = new ArrayAdapter<String>(
				getActivity().getApplicationContext(), R.layout.spinner_item,
				rgbColors);
		// greenArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		greenSpinner.setAdapter(greenArrayAdapter);
		greenSpinner.setSelection(1);

		ArrayAdapter<String> blueArrayAdapter = new ArrayAdapter<String>(
				getActivity().getApplicationContext(), R.layout.spinner_item,
				rgbColors);
		// blueArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		blueSpinner.setAdapter(blueArrayAdapter);
		blueSpinner.setSelection(2);

		Bar1.setOnSeekBarChangeListener(colorBarChangeListener);
		Bar2.setOnSeekBarChangeListener(colorBarChangeListener);
		Bar3.setOnSeekBarChangeListener(colorBarChangeListener);

		redSpinner.setOnItemSelectedListener(colorSpinnerSelectedListener);
		greenSpinner.setOnItemSelectedListener(colorSpinnerSelectedListener);
		blueSpinner.setOnItemSelectedListener(colorSpinnerSelectedListener);
		ConnectionInformation ci = new ConnectionInformation(getActivity());
		if (ci.isConnectingToInternet()) {
			ParseQuery query = new ParseQuery("Preset");  //Query results with class Preset
			//query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
			query.whereEqualTo("artworkName", artworkName.toString());

			query.findInBackground(new FindCallback() {

				@Override
				public void done(List<ParseObject> objects, ParseException e) {
					// TODO Auto-generated method stub
					if (e == null) {
						//Check if items are there and set it to the 0th element.
						if (objects.size() > 0) {
							Log.d("object", "Retrieved " + objects.size() + " objects");
							rgb1 = objects.get(0).getList("Preset1RGB");
							v1 = objects.get(0).getList("Preset1Values");
							rgb2 = objects.get(0).getList("Preset2RGB");
							v2 = objects.get(0).getList("Preset2Values");
							rgb3 = objects.get(0).getList("Preset3RGB");
							v3 = objects.get(0).getList("Preset3Values");
							rgb4 = objects.get(0).getList("Preset4RGB");
							v4 = objects.get(0).getList("Preset4Values");
						}
					} else {
						Log.d("object", "Error: " + e.getMessage());
					}
				}
			});
			
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
		//Color presets button check if query results are null otherwise set the correct color preset.
		preset1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if ( rgb1 != null || v1 != null) {
					setColorPreset(imageView, v1.get(0), v1.get(1), v1.get(2),rgb1.get(0), rgb1.get(1), rgb1.get(2));  //Set the color filter with these paramets from the query.
				}
				else {
					//setColorPreset(imageView, 255, 255, 255, "red", "green", "blue");
				}
			}
		});

		preset2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				if (rgb2 != null || v2 != null) {
					setColorPreset(imageView, v2.get(0), v2.get(1), v2.get(2),
							rgb2.get(0), rgb2.get(1), rgb2.get(2));
				}
				else {
					//setColorPreset(imageView, 255, 255, 255, "red", "green", "blue");
				}
			}
		});

		preset3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (rgb3 != null || v3 != null ) {
					setColorPreset(imageView, v3.get(0), v3.get(1), v3.get(2),
							rgb3.get(0), rgb3.get(1), rgb3.get(2));
				}
				else {
					//setColorPreset(imageView, 255, 255, 255, "red", "green", "blue");
				}
			}

		});

		preset4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if (rgb4 != null || v4 != null) {
					setColorPreset(imageView, v4.get(0), v4.get(1), v4.get(2),
							rgb4.get(0), rgb4.get(1), rgb4.get(2));
				}
				else {
					
					//setColorPreset(imageView, 255, 255, 255, "red", "green", "blue");
				}

			}

		});
		
		//Reset the image back to normal on click.
		reset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setColorPreset(imageView, 255, 255, 255, "red", "green", "blue");
			}

		});
		
		//sets the image to grayscale on click.
		grayscale.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setColorPreset(imageView, 255, 255, 255, "green", "green",
						"green");
			}

		});
		
			detailViewButton.setVisibility(View.VISIBLE);
			detailViewButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(getActivity(),
							DetailViewActivity.class);
					intent.putExtra("artworkName", artworkName);
					intent.putExtra("artistName", artistName);
					intent.putExtra("image1", image1);
					startActivity(intent);
				}
			});
		
		
		//Go to the gallery view activity and pass in artworkName as extras.
		galleryViewButton.setVisibility(View.VISIBLE);
			galleryViewButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
				
					Intent intent = new Intent();
					intent.setClass(getActivity(),
						GalleryViewActivity.class);
					intent.putExtra("artworkName", artworkName);
					startActivity(intent);
				}
			});
		
		if (ci.isConnectingToInternet()) {
			new DownloadImageTask().execute(image1);  //Download the image as an asynctask.
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

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	//Listener to check for changes in the spinner item.
	OnItemSelectedListener colorSpinnerSelectedListener = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			setColorFilter(imageView);
			// TODO Auto-generated method stub

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}
	};

	private void setColorPreset(ImageView iv, int value1, int value2,
			int value3, String position1, String position2, String position3) {
		//Make sure position is lower case.
		position1 = position1.toLowerCase();
		position2 = position2.toLowerCase();
		position3 = position3.toLowerCase();
		int pos1 = 0, pos2 = 1, pos3 = 2;

		//set the positions from string to correct integer values
		if (position1.equals("red")) {
			pos1 = 0;
		} else if (position1.equals("green")) {
			pos1 = 1;
		} else if (position1.equals("blue")) {
			pos1 = 2;
		}
		
		if (position2.equals("red")) {
			pos2 = 0;
		} else if (position2.equals("green")) {
			pos2 = 1;
		} else if (position2.equals("blue")) {
			pos2 = 2;
		}

		if (position3.equals("red")) {
			pos3 = 0;
		} else if (position3.equals("green")) {
			pos3 = 1;
		} else if (position3.equals("blue")) {
			pos3 = 2;
		}
		//Check to make sure presets are in the correct range.
		if (value1 >= 0 && value1 <= 255 && value2 >= 0 && value2 <= 255
				&& value3 >= 0 && value3 <= 255) {
			Bar1.setProgress(value1);
			Bar2.setProgress(value2);
			Bar3.setProgress(value3);
		} else {
			Bar1.setProgress(255);
			Bar2.setProgress(255);
			Bar3.setProgress(255);
		}
		//Set the spinner to the correct element in the array.
		redSpinner.setSelection(pos1);
		greenSpinner.setSelection(pos2);
		blueSpinner.setSelection(pos3);

		float a, b, c, f, g, h, k, l, m;
		// initialize values to 0.
		a = b = c = f = g = h = k = l = m = 0;

		float[] colorMatrix = { a, b, c, 0, 0, // red
				f, g, h, 0, 0, // green
				k, l, m, 0, 0, // blue
				0, 0, 0, 1, 0 // alpha
		};

		//set the colorfilter to the colorMatrix array.
		ColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
		//set the filter to the imageview.
		iv.setColorFilter(colorFilter);
		setColorFilter(iv);

	}

	private void setColorFilter(ImageView iv) {

		/*
		 * Used 5x4 matrix to transform color and alpha components from artist
		 * bitmap.
		 * 
		 * Color is calculated using: R' = a*R + b*G + c*B + d*A + e; G' = f*R +
		 * g*G + h*B + i*A + j; B' = k*R + l*G + m*B + n*A + o; A' = p*R + q*G +
		 * r*B + s*A + t;
		 */

		float value1 = ((float) Bar1.getProgress()) / 255;
		float value2 = ((float) Bar2.getProgress()) / 255;
		float value3 = ((float) Bar3.getProgress()) / 255;

		int redColorSource = redSpinner.getSelectedItemPosition();
		int greenColorSource = greenSpinner.getSelectedItemPosition();
		int blueColorSource = blueSpinner.getSelectedItemPosition();

		float a, b, c, f, g, h, k, l, m;
		// initialize values to 0.
		a = b = c = f = g = h = k = l = m = 0;

		String colorCombination = "";

		colorCombination += "RED = ";
		switch (redColorSource) {
		case 0:
			a = value1;
			colorCombination += "red x " + String.valueOf(value1) + "\n";
			break;
		case 1:
			b = value1;
			colorCombination += "green x " + String.valueOf(value1) + "\n";
			break;
		case 2:
			c = value1;
			colorCombination += "blue x " + String.valueOf(value1) + "\n";
			break;
		}

		colorCombination += "GREEN = ";
		switch (greenColorSource) {
		case 0:
			f = value2;
			colorCombination += "red x " + String.valueOf(value2) + "\n";
			break;
		case 1:
			g = value2;
			colorCombination += "green x " + String.valueOf(value2) + "\n";
			break;
		case 2:
			h = value2;
			colorCombination += "blue x " + String.valueOf(value2) + "\n";
			break;
		}

		colorCombination += "BLUE = ";
		switch (blueColorSource) {
		case 0:
			k = value3;
			colorCombination += "red x " + String.valueOf(value3) + "\n";
			break;
		case 1:
			l = value3;
			colorCombination += "green x " + String.valueOf(value3) + "\n";
			break;
		case 2:
			m = value3;
			colorCombination += "blue x " + String.valueOf(value3) + "\n";
			break;
		}

		float[] colorMatrix = { a, b, c, 0, 0, // red
				f, g, h, 0, 0, // green
				k, l, m, 0, 0, // blue
				0, 0, 0, 1, 0 // alpha
		};

		colorInfo.setText(colorCombination);

		ColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);

		iv.setColorFilter(colorFilter);
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
			ImageView iv;
			result = getResizedBitmap(result, 866, 640);
			iv = (ImageView) myFragmentView.findViewById(R.id.imageView11);

			iv.setImageBitmap(result);
			setColorFilter(imageView);
		}
	}
	
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromResource(byte[] data,
			int offset, int length, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, offset, length, options);
		// BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		// return BitmapFactory.decodeResource(res, resId, options);
		return BitmapFactory.decodeByteArray(data, 0, length, options);
	}

	//Listens to the changes from the seekbar and sets the color filter appropriately based on where the seekbar is located.
	OnSeekBarChangeListener colorBarChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
			setColorFilter(imageView);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}
	};
	
	

}