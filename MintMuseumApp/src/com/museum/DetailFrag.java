package com.museum;

import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;

import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DetailFrag extends SherlockFragment {
	ArrayList<String> a;
	CoverFlow coverflow;
	private List<ParseObject> artQuery;
	ArrayList<String> data;
	Bitmap[] bmp;
	String artPhoto;
	String image1;
	ParseObject parseObject;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//Inflate the view of the fragment.
		View view = inflater
				.inflate(R.layout.detail_fragment, container, false);
		return view;
	}
	//GetData retrieves the query results with artist and artistphoto parameters and sets it to the coverflow custom class.
	public void getData(final ArrayList<String> artist, String artistPhoto) {
		this.a = artist;
		this.artPhoto = artistPhoto;
		
		coverflow = (CoverFlow) getView().findViewById(R.id.gallery1);
		//Check internet conenctivity
		ConnectionInformation ci = new ConnectionInformation(getActivity());
		if (ci.isConnectingToInternet()) {
			//query from class "Artwork"
			ParseQuery query = new ParseQuery("Artwork");
			// query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
			query.whereStartsWith("artistName", artist.get(0).toString());
			//async method with callback to ui thread.
			query.findInBackground(new FindCallback() {

				@Override
				public void done(List<ParseObject> arg0, ParseException arg1) {
					if (arg1 == null) {
						artQuery = arg0;
						final List<ParseObject> images = artQuery;
						coverflow.setSpacing(10);
						coverflow.setAdapter(new ImageAdapter(getActivity(),
								R.layout.detail_fragment, images));
						//When someone clicks on coverflow gallery item check to see if values are not null then 
						//create a button to show next activity and pass in the correct values as extras.
						coverflow
								.setOnItemClickListener(new OnItemClickListener() {
									public void onItemClick(
											AdapterView<?> parent, View v,
											int position, long id) {

										parseObject = images.get(position);
										final String artistPhoto = artPhoto;
										Button button = (Button) getActivity()
												.findViewById(
														R.id.selectedButton);

										TextView tv = (TextView) getActivity()
												.findViewById(R.id.text);
										if (images.get(position).getString(
												"artworkName") != null
												&& !images
														.get(position)
														.getString(
																"artworkName")
														.isEmpty()) {
											tv.setText("Selected "
													+ images.get(position)
															.getString(
																	"artworkName"));

											button.setVisibility(View.VISIBLE);
											button.setText(images.get(position)
													.getString("artworkName"));
											button.setOnClickListener(new MyClickListener(
													position) {
												//On click check if items are not null then pass it into next activity as extras.
												@Override
												public void onClick(View v) {
													Intent intent = new Intent();
													intent.setClass(
															getActivity(),
															TabViewActivity.class);
													ArrayList<String> data = new ArrayList<String>();
													String image1, artworkName, artistName, artworkInfo, artworkObjectId, artworkYear;
													boolean isDetail, isGallery;
													image1 = parseObject
															.getParseFile(
																	"image1")
															.getUrl();
													artworkName = parseObject
															.getString("artworkName");
													artistName = parseObject
															.getString("artistName");
													artworkInfo = parseObject
															.getString("artworkInfo");
													artworkObjectId = parseObject
															.getString("objectId");
													artworkYear = parseObject
															.getString("year");
													isDetail = parseObject
															.getBoolean("isDetail");
													isGallery = parseObject
															.getBoolean("isGallery");
													if (artworkName != null
															&& !artworkName
																	.isEmpty()
															|| artworkInfo != null
															&& !artworkInfo
																	.isEmpty()
															|| artistName != null
															&& !artistName
																	.isEmpty()) {
														intent.putExtra(
																"artworkName",
																artworkName);
														intent.putExtra(
																"artworkInfo",
																artworkInfo);
														intent.putExtra(
																"artistName",
																artistName);
														intent.putExtra(
																"artworkObjectId",
																artworkObjectId);
														intent.putExtra(
																"artistPhoto",
																artistPhoto);
														intent.putExtra(
																"artworkYear",
																artworkYear);
														intent.putExtra(
																"image1",
																image1);
														intent.putStringArrayListExtra(
																"artist", a);
														intent.putExtra(
																"isDetail",
																isDetail);
														intent.putExtra(
																"isGallery",
																isGallery);
														startActivity(intent);
													} else {
														Toast.makeText(
																getActivity(),
																"Not enough information provided, please check back later!",
																Toast.LENGTH_LONG)
																.show();
													}
												}
											});

											// ---display the images selected---
										} else {
											button.setVisibility(View.INVISIBLE);
											tv.setText("");
										}
									}
								});
					} else {
						return;
					}

				}

			});
			
		} else {
			//No internet connection.
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

	}

	//Create bitmaps with reflection images from the original image parameter
	public static Bitmap createReflectedImage(Bitmap originalImage) {
		// The gap we want between the reflection and the original image
		final int reflectionGap = 4;

		int width = originalImage.getWidth();
		int height = originalImage.getHeight();

		// This will not scale but will flip on the Y axis
		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		// Create a Bitmap with the flip matrix applied to it.
		// We only want the bottom half of the image
		Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
				height / 2, width, height / 2, matrix, false);

		// Create a new bitmap with same width but taller to fit reflection
		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 2), Config.ARGB_4444);

		// Create a new Canvas with the bitmap that's big enough for
		// the image plus gap plus reflection
		Canvas canvas = new Canvas(bitmapWithReflection);
		// Draw in the gap
		Paint defaultPaint = new Paint();

		defaultPaint.setAntiAlias(true);
		defaultPaint.setFilterBitmap(true);
		// Draw in the original image
		canvas.drawBitmap(originalImage, 0, 0, defaultPaint);

		canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);
		// Draw in the reflection
		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap,
				defaultPaint);

		// Create a shader that is a linear gradient that covers the reflection
		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0,
				originalImage.getHeight(), 0, bitmapWithReflection.getHeight()
						+ reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
		// Set the paint to use this shader (linear gradient)
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);
		reflectionImage.recycle();
		originalImage.recycle();

		return bitmapWithReflection;
	}

	//Scale the sample bitmap.
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

	
	//Used to decode images onto imageview.
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

	//Custom OnclickListener to check position of clicked item.
	private class MyClickListener implements OnClickListener {

		private int position;

		public MyClickListener(int position) {
			this.position = position;
		}

		public void onClick(View v) {

			System.out.println("position " + getPosition() + " clicked.");
		}

		public int getPosition() {
			return position;
		}

	}

	//Custom ArrayAdapter in order use coverflow image in a gallery.
	public class ImageAdapter extends ArrayAdapter<ParseObject> {

		private final Context context;
		private int resource;
		private List<ParseObject> im;

		// private int itemBackground;

		public ImageAdapter(Context c, int resource, List<ParseObject> im) {
			super(c, resource, im);
			context = c;
			this.resource = resource;
			this.im = im;
			TypedArray a = getActivity().obtainStyledAttributes(
					R.styleable.MyGallery);
			// itemBackground =
			// a.getResourceId(R.styleable.MyGallery_android_galleryItemBackground,
			// 0);
			//recycle this image.
			a.recycle();

		}

		public int getCount() {
			return im.size();
		}

		public ParseObject getItem(int position) {
			return im.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		//Get the view of the coverflow gallery from query item.
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ImageView imageview;
			ParseObject parseObject = getItem(position);
			if (convertView == null) {
				imageview = new ImageView(context);
				// bm[position] = ((BitmapDrawable)
				// imageView.getDrawable()).getBitmap();
			} else {
				imageview = (ImageView) convertView;
			}

			// imageView.setBackgroundResource(itemBackground);
			ParseFile image = (ParseFile) parseObject.get("image1");
			image.getDataInBackground(new GetDataCallback() {
				Bitmap bm;
				//Make sure bitmaps are scaled properly to avoid memory out of bounds errors.
				@SuppressWarnings("deprecation")
				@Override
				public void done(byte[] data, com.parse.ParseException e) {
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inJustDecodeBounds = true;
					BitmapFactory
							.decodeByteArray(data, 0, data.length, options);
					options.inSampleSize = calculateInSampleSize(options,
							options.outWidth, options.outHeight);
					options.inJustDecodeBounds = false;

					bm = BitmapFactory.decodeByteArray(data, 0, data.length,
							options);
					bm = createReflectedImage(bm);
					imageview.setScaleType(ImageView.ScaleType.FIT_XY);
					boolean tabletSize = getResources().getBoolean(
							R.bool.isTablet);
					boolean isTabletSeven = getResources().getBoolean(
							R.bool.isSevenInch);
					
					//set the size based on screen width of the device.
					if (tabletSize == true && isTabletSeven == false) {
						imageview.setLayoutParams(new CoverFlow.LayoutParams(
								320, 433));

					} else if (tabletSize == true && isTabletSeven == true) {
						imageview.setLayoutParams(new CoverFlow.LayoutParams(
								320, 433));
					} else {
						imageview.setLayoutParams(new CoverFlow.LayoutParams(
								160, 216));
					}
					imageview.setImageBitmap(bm);
				}

			});
			return imageview;
		}
	}
}
