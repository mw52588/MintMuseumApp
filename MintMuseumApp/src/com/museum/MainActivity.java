package com.museum;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PushService;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

//Main activity app begins!
/*Team 7 Mint Museum Application
 	Matthew Wiener mwiener@uncc.edu
 	Garvey Jackson gjacks13@gmail.com
	Carl Vanhorn cvanhorn@uncc.edu
	Brenita Jones bjone114@uncc.edu
	Seree Thao sthao@uncc.edu
	
 * This project targets high school art teachers and entice them to bring their classes
	to the museum. This project will utilize a mobile app to achieve this goal. We will
	create an app which can be implemented on tablets and smartphones with the
	Android platform. The app will showcase two pieces of artwork from the Mint
	Museum. The user will be able to click on the image of the pieces of artwork and gain
	information such as: Why did the artist choose to do the artwork? What is the
	symbolism of the piece? The app will also be able to reveal concepts such as the
	symmetry used by the artist. The app will present high resolution images and where
	applicable interviews with the artist.
 * 
 * 
 * 
 */
public class MainActivity extends SherlockFragmentActivity {
	
	Button button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Check the connection and set parse initialization.
		ConnectionInformation ci = new ConnectionInformation(
				getApplicationContext());
		
			Parse.initialize(this, "JoBoV30qUpocI35hV5zZImoPaAKi9M0Rpjsk4I1W",
					"kHQFymJ2daKdSrStae0MwzaU8MTCXWeEOVwQCLpC");
			ParseAnalytics.trackAppOpened(getIntent());

			setContentView(R.layout.main_activity);

			final ActionBar ab = getSupportActionBar();
			ab.setTitle(R.string.app_name);
			ab.show();
			Button artist = (Button) findViewById(R.id.selectedButton);
			//start the artist activity on click.
			artist.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(MainActivity.this, ArtistActivity.class);
					startActivity(intent);
				}
			});
			
			
			Button artwork = (Button) findViewById(R.id.browseArtwork);
			//start artwork activity on click.
			artwork.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(MainActivity.this, BrowseArtwork.class);
					startActivity(intent);
				}
			});
			
			Button admin = (Button) findViewById(R.id.adminButton);
			//start the admin interface activity on click
			admin.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(MainActivity.this, AdminActivity.class);
					startActivity(intent);

				}
			});
	}
	
	//Inflate the actionbar.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}
}
