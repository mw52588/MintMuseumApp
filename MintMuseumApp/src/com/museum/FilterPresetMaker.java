package com.museum;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class FilterPresetMaker extends SherlockActivity {
	ImageView imageView;
	SeekBar Bar1, Bar2, Bar3;
	Spinner redSpinner, greenSpinner, blueSpinner;
	TextView colorInfo;
	String artistName;
	String artworkName;
	String image1;
	Bitmap immutableBitmap;
	String an;
	Bitmap bm;
	float value1, value2, value3;

	int redColorSource;
	int greenColorSource;
	int blueColorSource;
	
	List<String> rgb1, rgb2, rgb3, rgb4;
	List<Integer> v1, v2, v3, v4;
	List<ParseObject> query1;
	Button preset1, preset2, preset3, preset4, addFilter, reset,
			detailViewButton, galleryViewButton;
	String[] rgbColors = { "Red", "Green", "Blue" }; // R G B value array.
	boolean isDetail, isGallery;

	int saveValue1, saveValue2, saveValue3;
	String savePosition1, savePosition2, savePosition3;
	int currentPresetCount = 0;

	ArrayList<ParseObject> returnedList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filter_preset_maker_layout);
		final ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		currentPresetCount = 0;
		returnedList = new ArrayList<ParseObject>();

		artworkName = getIntent().getExtras().getString("artworkName");
		image1 = getIntent().getExtras().getString("image1");

		imageView = (ImageView) findViewById(R.id.presetiv);

		reset = (Button) findViewById(R.id.presetresetPresets);
		addFilter = (Button) findViewById(R.id.presetaddFilter);
		Bar1 = (SeekBar) findViewById(R.id.presetbar1);
		Bar2 = (SeekBar) findViewById(R.id.presetbar2);
		Bar3 = (SeekBar) findViewById(R.id.presetbar3);

		redSpinner = (Spinner) findViewById(R.id.presetroption);
		greenSpinner = (Spinner) findViewById(R.id.presetgoption);
		blueSpinner = (Spinner) findViewById(R.id.presetboption);

		colorInfo = (TextView) findViewById(R.id.presetdisplaycolorInfo);

		ArrayAdapter<String> redArrayAdapter = new ArrayAdapter<String>(
				getApplicationContext(), R.layout.spinner_item, rgbColors);
		// redArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		redSpinner.setAdapter(redArrayAdapter);
		redSpinner.setSelection(0);

		ArrayAdapter<String> greenArrayAdapter = new ArrayAdapter<String>(
				getApplicationContext(), R.layout.spinner_item, rgbColors);
		// greenArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		greenSpinner.setAdapter(greenArrayAdapter);
		greenSpinner.setSelection(1);

		ArrayAdapter<String> blueArrayAdapter = new ArrayAdapter<String>(
				getApplicationContext(), R.layout.spinner_item, rgbColors);
		// blueArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		blueSpinner.setAdapter(blueArrayAdapter);
		blueSpinner.setSelection(2);

		Bar1.setOnSeekBarChangeListener(colorBarChangeListener);
		Bar2.setOnSeekBarChangeListener(colorBarChangeListener);
		Bar3.setOnSeekBarChangeListener(colorBarChangeListener);

		redSpinner.setOnItemSelectedListener(colorSpinnerSelectedListener);
		greenSpinner.setOnItemSelectedListener(colorSpinnerSelectedListener);
		blueSpinner.setOnItemSelectedListener(colorSpinnerSelectedListener);

		ConnectionInformation ci = new ConnectionInformation(
				getApplicationContext());
		if (ci.isConnectingToInternet()) {
			ParseQuery query = new ParseQuery("Preset");
			query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
			query.whereEqualTo("artworkName", artworkName);

			query.findInBackground(new FindCallback() {

				@Override
				public void done(List<ParseObject> objects, ParseException e) {
					// TODO Auto-generated method stub
					if (e == null) {
						if (objects.size() > 0) {
							Log.d("object", "Retrieved " + objects.size()
									+ " objects");
							an = objects.get(0).getString("artistName");
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

		reset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setColorPreset(imageView, 255, 255, 255, "red", "green", "blue");
				setColorFilter(imageView);
			}

		});

		addFilter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// show progress dialog

				value1 = ((float) Bar1.getProgress()) / 255;
				value2 = ((float) Bar2.getProgress()) / 255;
				value3 = ((float) Bar3.getProgress()) / 255;

				redColorSource = redSpinner.getSelectedItemPosition();
				greenColorSource = greenSpinner.getSelectedItemPosition();
				blueColorSource = blueSpinner.getSelectedItemPosition();

				// get current presets for current image
				boolean newValue = true;
				ParseQuery query = new ParseQuery("Preset");
				query.whereStartsWith("artworkName", artworkName);
				int presetNum = 0;

				query.findInBackground(new FindCallback() {

					@Override
					public void done(List<ParseObject> objects, ParseException e) {

						if (e == null) {
							returnedList.addAll(objects);
							
							Integer rgbInts[] = new Integer[3];
							rgbInts[0] = redColorSource;
							rgbInts[1] = greenColorSource;
							rgbInts[2] = blueColorSource;
							String rgbString[] = new String[3];
							if (rgbInts[0] == 0) {
								rgbString[0] = "red";
							} else if (rgbInts[0] == 1) {
								rgbString[0] = "green";
							} else if (rgbInts[0] == 2) {
								rgbString[0] = "blue";
							}

							if (rgbInts[1] == 0) {
								rgbString[1] = "red";
							} else if (rgbInts[1] == 1) {
								rgbString[1] = "green";
							} else if (rgbInts[1] == 2) {
								rgbString[1] = "blue";
							}

							if (rgbInts[2] == 0) {
								rgbString[2] = "red";
							} else if (rgbInts[2] == 1) {
								rgbString[2] = "green";
							} else if (rgbInts[2] == 2) {
								rgbString[2] = "blue";
							}

							Integer sliderValues[] = new Integer[3];
							sliderValues[0] = (int) (value1 * 255);
							sliderValues[1] = (int) (value2 * 255);
							sliderValues[2] = (int) (value3 * 255);

							int count = 0;
							boolean notThere = false;
							for (ParseObject o : returnedList) {

								if (o.getList("Preset1Values")== null || o.getList("Preset1RGB") == null) {
									
									o.put("Preset1RGB", Arrays.asList(rgbString));
									o.put("Preset1Values",
											Arrays.asList(sliderValues));
									// colorPreset.put("artworkName", artworkName);
									o.saveInBackground();
									Toast.makeText(
											getApplicationContext(),
											"Preset Values created" + rgbString[0]
													+ rgbString[1] + rgbString[2],
											Toast.LENGTH_LONG).show();
									notThere = true;
								}

								else if (o.getList("Preset2Values")== null || o.getList("Preset2RGB") == null) {
									
									o.put("Preset2RGB", Arrays.asList(rgbString));
									o.put("Preset2Values",
											Arrays.asList(sliderValues));
									// colorPreset.put("artworkName", artworkName);
									o.saveInBackground();
									Toast.makeText(
											getApplicationContext(),
											"Preset Values created" + rgbString[0]
													+ rgbString[1] + rgbString[2],
											Toast.LENGTH_LONG).show();
									notThere = true;
								}

								else if (o.getList("Preset3Values")== null || o.getList("Preset3RGB") == null) {
									
									o.put("Preset3RGB", Arrays.asList(rgbString));
									o.put("Preset3Values",
											Arrays.asList(sliderValues));
									// colorPreset.put("artworkName", artworkName);
									o.saveInBackground();
									Toast.makeText(
											getApplicationContext(),
											"Preset Values created" + rgbString[0]
													+ rgbString[1] + rgbString[2],
											Toast.LENGTH_LONG).show();
									notThere = true;
								}

								else if (o.getList("Preset4Values")== null || o.getList("Preset4RGB") == null) {
									
									o.put("Preset4RGB", Arrays.asList(rgbString));
									o.put("Preset4Values",
											Arrays.asList(sliderValues));
									// colorPreset.put("artworkName", artworkName);
									o.saveInBackground();
									Toast.makeText(
											getApplicationContext(),
											"Preset Values created" + rgbString[0]
													+ rgbString[1] + rgbString[2],
											Toast.LENGTH_LONG).show();
									notThere = true;
								}

								else {
									Toast.makeText(getApplicationContext(),
											"Already Preset Values!", Toast.LENGTH_LONG)
											.show();
									notThere = true;
								}
								count++;

							}

							if (notThere == false) {
								ParseObject colorPreset = new ParseObject("Preset");
								colorPreset.put("Preset1RGB", Arrays.asList(rgbString));
								colorPreset.put("Preset1Values",
										Arrays.asList(sliderValues));
								colorPreset.put("artworkName", artworkName);
								colorPreset.saveInBackground();
							}
							
							
							
							
							
							
							
						} else {
							Log.d("Preset", "Error: " + e.getMessage());
						}

					}
				});
				

				int compsaveValue1, compsaveValue2, compsaveValue3;
				String compsavePosition1, compsavePosition2, compsavePosition3;

				// if the values are not the same as an existing preset create a
				// new one && preset# !> 4
				/*
				 * for(ParseObject o: returnedList){
				 * 
				 * for(int i = 0; i< presetNum; i++){ List<String>
				 * comparergbStrings = null; List<Integer> compareSliderVals =
				 * null; comparergbStrings = o.getList("Preset" + i+1 +"RGB");
				 * compareSliderVals = o.getList("Preset" + i+1 +"Values");
				 * 
				 * compsavePosition1 =comparergbStrings.get(0);
				 * compsavePosition2 =comparergbStrings.get(1);
				 * compsavePosition3 =comparergbStrings.get(2);
				 * 
				 * compsaveValue1 = compareSliderVals.get(0); compsaveValue2 =
				 * compareSliderVals.get(1); compsaveValue3 =
				 * compareSliderVals.get(2);
				 * 
				 * if(!compsavePosition1.equalsIgnoreCase(rgbString[0]) &&
				 * !compsavePosition2.equalsIgnoreCase(rgbString[1]) &&
				 * !compsavePosition3.equalsIgnoreCase(rgbString[2]) &&
				 * compsaveValue1 != (int)value1 && compsaveValue2 !=
				 * (int)value2 && compsaveValue3 != (int)value3 ){ //if preset 1
				 * full go to preset 2 ... until 4 reached
				 * 
				 * 
				 * 
				 * 
				 * } else{ Toast.makeText(getApplicationContext(),
				 * "This preset already exists and will not be saved",
				 * Toast.LENGTH_LONG).show(); i=100;
				 * 
				 * }
				 */
			}
		});

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
	}

	private void setColorPreset(ImageView iv, int value1, int value2,
			int value3, String position1, String position2, String position3) {
		position1 = position1.toLowerCase();
		position2 = position2.toLowerCase();
		position3 = position3.toLowerCase();
		int pos1 = 0, pos2 = 1, pos3 = 2;

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

		ColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);

		iv.setColorFilter(colorFilter);

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

		saveValue1 = (int) value1;
		saveValue2 = (int) value2;
		saveValue3 = (int) value3;

		int redColorSource = redSpinner.getSelectedItemPosition();
		int greenColorSource = greenSpinner.getSelectedItemPosition();
		int blueColorSource = blueSpinner.getSelectedItemPosition();

		if (redColorSource == 0) {
			savePosition1 = "red";
		} else if (redColorSource == 1) {
			savePosition1 = "green";
		} else if (redColorSource == 2) {
			savePosition1 = "blue";
		}

		if (greenColorSource == 0) {
			savePosition2 = "red";
		} else if (greenColorSource == 1) {
			savePosition2 = "green";
		} else if (greenColorSource == 2) {
			savePosition2 = "blue";
		}

		if (blueColorSource == 0) {
			savePosition3 = "red";
		} else if (blueColorSource == 1) {
			savePosition3 = "green";
		} else if (blueColorSource == 2) {
			savePosition3 = "blue";
		}

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
				// Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			ImageView iv;

			iv = (ImageView) findViewById(R.id.presetiv);

			iv.setImageBitmap(result);
			setColorFilter(imageView);
		}
	}

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
