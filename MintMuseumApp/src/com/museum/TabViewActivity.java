package com.museum;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.view.MenuItem;
import com.parse.Parse;
import com.parse.ParseObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

//Activity that provides 3 fragment tabs, Image filter, Artwork Info, Artist Info
public class TabViewActivity extends SherlockFragmentActivity {
	String image1;
	String artistPhoto;
	List<ParseObject> artworkQuery;
	List<ParseObject> artPhoto;
	String artworkInfo, artistName, artworkName, artworkObjectId, year;
	ImageView artistImage, artworkImage;
	// ArrayList<String> artwork;
	boolean isArtwork = false;
	TabHost mTabHost;
	boolean isDetail;
	boolean isGallery;
	ViewPager mViewPager;
	ArrayList<String> artist = new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final ActionBar actionBar = getSupportActionBar();
		Intent i = getIntent();
		Parse.initialize(this, "JoBoV30qUpocI35hV5zZImoPaAKi9M0Rpjsk4I1W",
				"kHQFymJ2daKdSrStae0MwzaU8MTCXWeEOVwQCLpC");
		
		//Get passed in extras.
		isDetail = i.getBooleanExtra("isDetail", false);
		isArtwork = i.getBooleanExtra("isArtwork", false);
		isGallery = i.getBooleanExtra("isGallery", false);
		image1 = i.getStringExtra("image1");
		artist = i.getStringArrayListExtra("artist");
		artistName = i.getStringExtra("artistName");
		artworkInfo = i.getStringExtra("artworkInfo");
		artworkName = i.getStringExtra("artworkName");
		artworkObjectId = i.getStringExtra("artworkObjectId");
		year = i.getStringExtra("artworkYear");
		artistPhoto = i.getStringExtra("artistPhoto");
		//Set the actionbar to tab view.
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		Tab tabD = actionBar.newTab();
		tabD.setText("Image Filter");
		tabD.setTabListener(new TabListener<ImageFilterFrag>(this, "ImageFilter", ImageFilterFrag.class));
		Bundle d = new Bundle();
		d.putString("image1", image1);
		d.putString("artworkName", artworkName);
		d.putString("artistName", artistName);
		d.putBoolean("isDetail", isDetail);
		d.putBoolean("isGallery", isGallery);
		tabD.setTag(d);
		actionBar.addTab(tabD);
		
		Tab tabB = actionBar.newTab();
		tabB.setText("Artwork Info");
		tabB.setTabListener(new TabListener<ArtworkFrag>(this, "Artwork",ArtworkFrag.class));
		Bundle b = new Bundle();
		b.putString("artworkInfo", artworkInfo);
		b.putString("artworkName", artworkName);
		b.putString("image1", image1);
		b.putString("artworkYear", year);
		b.putString("artistName", artistName);
		tabB.setTag(b);
		actionBar.addTab(tabB);
		
		Tab tabC = actionBar.newTab();
		tabC.setText("Artist Info");
		tabC.setTabListener(new TabListener<ArtistFrag>(this, "Artist",ArtistFrag.class));
		Bundle c = new Bundle();
		c.putString("artistPhoto", artistPhoto);
		c.putStringArrayList("artist", artist);
		tabC.setTag(c);
		actionBar.addTab(tabC);

		if (savedInstanceState != null) {
			int savedIndex = savedInstanceState.getInt("SAVED_INDEX");
			getActionBar().setSelectedNavigationItem(savedIndex);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putInt("SAVED_INDEX", getActionBar()
				.getSelectedNavigationIndex());
	}
	
	//Check to see which activity it came from and go back to the correct activity.
	@Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {       
		switch (menuItem.getItemId()) {

	    case android.R.id.home:
	    	 if (isArtwork) {
	    		 startActivity(new Intent(TabViewActivity.this, BrowseArtwork.class));
	    	 }
	    	 else {
	    		 startActivity(new Intent(TabViewActivity.this, ArtistActivity.class)); 
	    	 }
	         break;

	    default:
	        return super.onOptionsItemSelected(menuItem);
	    }
		return true;
    }

	//Listener to check to see which tab it's currently on and correctly detaches and attaches fragments on switch.
	public static class TabListener<T extends Fragment> implements
			ActionBar.TabListener {

		public final Activity myActivity;
		private final String myTag;
		private final Class<T> myClass;

		public TabListener(Activity activity, String tag, Class<T> cls) {
			myActivity = activity;
			myTag = tag;
			myClass = cls;
		}
		//Attach the fragment when selected.
		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			
			Fragment myFragment = ((FragmentActivity) myActivity).getSupportFragmentManager().findFragmentByTag(myTag);
	
			// Check if the fragment is already initialized
			if (myFragment == null) {
				// If not, instantiate and add it to the activity
				myFragment = Fragment.instantiate(myActivity, myClass.getName(), (Bundle)tab.getTag());
				
				//ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
				
				ft.add(android.R.id.content, myFragment, myTag);
				
			} else {
				// If it exists, simply attach it in order to show it
				ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
				ft.attach(myFragment);
			}

		}
		//Detach the fragment when unselected.
		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {

			Fragment myFragment = ((FragmentActivity) myActivity).getSupportFragmentManager().findFragmentByTag(myTag);

			if (myFragment != null) {
				// Detach the fragment, because another one is being attached
				ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
				//ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				ft.detach(myFragment);	
			}
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub

		}
	}
}
