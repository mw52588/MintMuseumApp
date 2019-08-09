package com.museum;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
//Activity to allow users to view additional images from this particular piece of artwork.
public class GalleryViewActivity extends SherlockActivity {
	CoverFlow coverflow;
	String artworkName;
	private List<ParseObject> artQuery;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.galleryview);
		Parse.initialize(this, "JoBoV30qUpocI35hV5zZImoPaAKi9M0Rpjsk4I1W",
				"kHQFymJ2daKdSrStae0MwzaU8MTCXWeEOVwQCLpC");
		final ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		Bundle data = getIntent().getExtras();
		artworkName = data.getString("artworkName");
		getData(artworkName);

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

	public void getData(String artworkName) {

		ConnectionInformation ci = new ConnectionInformation(
				getApplicationContext());
		if (ci.isConnectingToInternet()) {
			ParseQuery query = new ParseQuery("Gallery"); //query results with class Gallery.
			query.whereEqualTo("artworkName", artworkName.toString());
			query.setLimit(10); //Keep query limit to 10 to conserve memory.
			query.findInBackground(new FindCallback() {

				@Override
				public void done(List<ParseObject> objects, ParseException e) {
					// TODO Auto-generated method stub
					if (e == null) {
						artQuery = objects;
						if (artQuery.isEmpty()) {

						} else {
							coverflow = (CoverFlow) findViewById(R.id.gallery2);
							coverflow.setSpacing(10);
							coverflow.setAdapter(new GalleryAdapter(
									getApplicationContext(),
									R.layout.galleryview, artQuery));
						}
					} else {
						Log.d("score", "Error: " + e.getMessage());
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
	}

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
	
	
	//sets ups the custom ArrayAdapter and populates it with the query.
	public class GalleryAdapter extends ArrayAdapter<ParseObject> {

		private final Context context;
		private int resource;
		private List<ParseObject> im;

		// private int itemBackground;

		public GalleryAdapter(Context c, int resource, List<ParseObject> im) {
			super(c, resource, im);
			context = c;
			this.resource = resource;
			this.im = im;
			TypedArray a = obtainStyledAttributes(R.styleable.MyGallery);
			// itemBackground =
			// a.getResourceId(R.styleable.MyGallery_android_galleryItemBackground,
			// 0);
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
			ParseFile image = (ParseFile) parseObject.get("galleryImage");
			image.getDataInBackground(new GetDataCallback() {
				Bitmap bm;

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
