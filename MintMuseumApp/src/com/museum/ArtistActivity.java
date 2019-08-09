package com.museum;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.parse.Parse;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

//ArtistActivity inflates the layout with either 2 fragments for tablet view or 1 fragment for smartphone.
public class ArtistActivity extends SherlockFragmentActivity {
	public ArtistActivity artistActivity = null;
	public static Context context;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_artist); //multiple layout files based on screen size.
		Parse.initialize(this, "JoBoV30qUpocI35hV5zZImoPaAKi9M0Rpjsk4I1W",
				"kHQFymJ2daKdSrStae0MwzaU8MTCXWeEOVwQCLpC");
		final ActionBar ab = getSupportActionBar();
		ab.setTitle(R.string.app_name);
		ab.setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {       
		switch (menuItem.getItemId()) {

	    case android.R.id.home:
	    	 startActivity(new Intent(ArtistActivity.this, MainActivity.class)); 
	         break;

	    default:
	        return super.onOptionsItemSelected(menuItem);
	    }
		return true;
    }
}