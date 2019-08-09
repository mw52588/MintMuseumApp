package com.museum;

import java.io.InputStream;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.parse.ParseObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DetailViewMaker extends SherlockActivity {
	ImageMap creationImage = null;
	AlertDialog creationDialog = null;
	String artworkName = "";
	String image1= "";
	double x_touch = 0;
	double y_touch = 0;
	
	String coordString = "";
	String pointInfo = "";
	String pointName = "";
	
	private AlertDialog dialog = null;
	RelativeLayout parentLayout = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_view_maker_layout);
		final ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
	
		
		artworkName = getIntent().getExtras().getString("artworkName");
		image1 = getIntent().getExtras().getString("image1");
		
		Toast.makeText(this, "Tap the image where you would like to create a touch point", Toast.LENGTH_LONG).show();
		creationImage = (ImageMap)findViewById(R.id.createDetailViewImage);
		parentLayout = (RelativeLayout)findViewById(R.id.parentLayoutCreateDetailView);
		setImage(image1);
		
	}
	
	public void setImage(String url){
		new DownloadImageTask().execute(url);
		
	}
	
	public void uploadCoordinate(){
		
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
			
			result.setDensity(Bitmap.DENSITY_NONE);
			
			creationImage.setImageBitmap(result);
			
			creationImage.addOnImageMapClickedHandler(new ImageMap.OnImageMapClickedHandler() {

				@Override
				public void onImageMapClicked(int id) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onImageMapClicked() {
					createNewCoordinate(creationImage.getcreationCoordinates());
					
				}

				@Override
				public void onBubbleClicked(String info) {
					// TODO Auto-generated method stub
					
				}
			});
			parentLayout.invalidate();
			}
		
		  
		}
	private void createNewCoordinate(double[] cordArray){
		Double x = cordArray[0];
		Double y = cordArray[1];
		
		int iX = x.intValue();
		int iY = y.intValue();
		
		coordString = iX+","+iY+",20";
	
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dialog_createtouchpoint, null);
		
		//Building dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Creating Touchpoint")
		.setView(layout)
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog = builder.create();
		dialog.show(); 
		
		Button saveButton = (Button)dialog.findViewById(R.id.saveTouchButton);
		
		saveButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TextView nameView = (TextView) dialog.findViewById(R.id.create_touchName);
				TextView infoView = (TextView)dialog.findViewById(R.id.createTouchInfo);
				
				pointName = nameView.getText().toString();
				pointInfo = infoView.getText().toString();
				
				if (pointName != "" && pointInfo != "") {
					ParseObject newCoordinate = new ParseObject("Coordinates");
					newCoordinate.put("coords", coordString);
					newCoordinate.put("for", artworkName);
					newCoordinate.put("identifier", "blank");
					newCoordinate.put("info", pointInfo);
					newCoordinate.put("name", pointName);
					newCoordinate.put("shape", "circle");
					newCoordinate.saveInBackground();
					
					pointName = "";
					pointInfo = "";
					dialog.dismiss();
					Toast.makeText(DetailViewMaker.this, "The TouchPoint for this image has been added to the database", Toast.LENGTH_LONG).show();
				}
				else{
					Toast.makeText(DetailViewMaker.this, "Please fill in the text boxes", Toast.LENGTH_LONG).show();
				}
			}
		});
		
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



