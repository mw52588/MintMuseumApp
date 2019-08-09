package com.museum;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;

public class AdminActivity extends SherlockActivity {

	public TextView error;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ConnectionInformation ci = new ConnectionInformation(
				getApplicationContext());
		Parse.initialize(this, "JoBoV30qUpocI35hV5zZImoPaAKi9M0Rpjsk4I1W",
				"kHQFymJ2daKdSrStae0MwzaU8MTCXWeEOVwQCLpC");

		setContentView(R.layout.admin_activity);

		final ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		error = (TextView) findViewById(R.id.error);
		((Button) findViewById(R.id.login)).getBackground().setColorFilter(
				Color.parseColor("#50B52D"), Mode.SRC_ATOP);
	}
	//Get the value from edittext field
	private String getLoginUserName() {
		return ((EditText) findViewById(R.id.username)).getText().toString();
	}
	//Get value from edittextfield
	private String getLoginPwd() {
		return ((EditText) findViewById(R.id.pwd)).getText().toString();
	}

	//reset the forms in the login layout.
	private void resetLoginInfo() {
		EditText username = (EditText) findViewById(R.id.username);
		username.setText("");
		EditText password = (EditText) findViewById(R.id.pwd);
		password.setText("");

	}

	//Connect to the server and check if login details are correct.
	public void loginCallback(View v) {

		ConnectionInformation ci = new ConnectionInformation(
				getApplicationContext());
		if (ci.isConnectingToInternet()) {
			final ProgressDialog dialog = ProgressDialog.show(this, "",
					"Logging in ...", true);
			ParseUser.logInInBackground(getLoginUserName(), getLoginPwd(),
					new LogInCallback() {
						public void done(ParseUser user, ParseException e) {
							if (error.getVisibility() == View.VISIBLE) {
								error.setVisibility(View.INVISIBLE);
							}
							dialog.dismiss();
							//Check if username and password are correct.
							if (user != null) {
								Log.d("Logged In", "Successfully logged in");

								// Toast.makeText(getApplicationContext(),
								// "User logged in successfully",
								// Toast.LENGTH_LONG).show();
								Intent intent = new Intent();
								intent.setClass(AdminActivity.this,
										AdminInterfaceActivity.class);
								startActivity(intent);
								//Set error message.
							} else {
								Log.d("Error Loggin In", "Not logged in");
								error.setVisibility(View.VISIBLE);
								resetLoginInfo();

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

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {

		case android.R.id.home:
			startActivity(new Intent(AdminActivity.this, MainActivity.class));
			break;

		default:
			return super.onOptionsItemSelected(menuItem);
		}
		return true;
	}
}