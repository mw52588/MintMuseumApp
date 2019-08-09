package com.museum;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.RequestPasswordResetCallback;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AdminInterfaceActivity extends Activity {

	private Button addUserButton=null, changePassButton=null,
			addArtist = null, deleteArtist = null,
			modifyArtistButton = null, addDetailView = null,
			deleteWork = null,modifyArtworkButton = null,
			addFilterPresetButton= null;

	private ParseUser currentUser = null;
	private final String signedinPrefixText = "SIGNED IN AS: ";
	private TextView signedAsText = null;

	private EditText dialogUserName = null, 
			dialogPass = null,
			dialogEmail = null;

	private AlertDialog dialog = null;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.admininterface);

		ConnectionInformation ci = new ConnectionInformation(getApplicationContext());
		if (ci.isConnectingToInternet()) {
			Parse.initialize(this, "JoBoV30qUpocI35hV5zZImoPaAKi9M0Rpjsk4I1W", "kHQFymJ2daKdSrStae0MwzaU8MTCXWeEOVwQCLpC");
			bindButtons();
		} else {

			AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
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

		/*
	    final ParseObject artwork = new ParseObject("Artwork");
	    artwork.put("artName", "artName");
	    artwork.put("artworkInfo", "artworkInfo");
	    artwork.put("parent", ParseObject.createWithoutData("Artist", "HN7MbxMvEg"));

	    artwork.saveInBackground();
		 */	    
	}

	@Override
	public void finish() {
		ParseUser.logOut();
		super.finish();
	}

	@Override
	public void onBackPressed() {
		ParseUser.logOut();
		super.onBackPressed();
	}

	@Override
	protected void onStop() {
		ParseUser.logOut();
		super.onStop();
	}

	public void bindButtons(){
		currentUser = ParseUser.getCurrentUser();
		signedAsText = (TextView)findViewById(R.id.signedInTextView);

		signedAsText.setText(signedinPrefixText+currentUser.getEmail());

		addUserButton = (Button) findViewById(R.id.adduser);
		changePassButton = (Button) findViewById(R.id.changepassword);

		addDetailView = (Button) findViewById(R.id.addaDetailView);

		addFilterPresetButton = (Button)findViewById(com.museum.R.id.add_filter_preset);

		addUserButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				inflateSignUp();

			}
		});

		changePassButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				changePassword();
			}
		});

		addDetailView.setOnClickListener(new OnClickListener () {

			@Override
			public void onClick(View v) {
				inflateAddDetailView();

			}

		});
		addFilterPresetButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				inflateAddFilterPreset();
			}
		});
	}

	public void changePassword(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Would You Like To Change The Password For (" +
				ParseUser.getCurrentUser().getUsername()+")?");

		builder.setPositiveButton("Change Password", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, int which) {
				ParseUser user = ParseUser.getCurrentUser();

				if (currentUser != null) {
					ParseUser.requestPasswordResetInBackground(user.getEmail(), new RequestPasswordResetCallback(){

						@Override
						public void done(ParseException e) {
							if(e == null){
								Toast.makeText(AdminInterfaceActivity.this, "An Email Has Been Sent With Reset Instructions", Toast.LENGTH_LONG).show();
								//dialog.dismiss();
							}
							else{

								//dialog.dismiss();
								Toast.makeText(AdminInterfaceActivity.this, "Failed To Reset Password" + e.getMessage(), Toast.LENGTH_LONG).show();
							}

						}

					});
				} else {
					Toast.makeText(getParent(), "User Not Regcognized", Toast.LENGTH_LONG).show();
				}


			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog = builder.create();
		dialog.show();
	}

	public void inflateSignUp(){
		//Preparing views
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dialog_layout, null);
		//layout_root should be the name of the "top-level" layout node in the dialog_layout.xml file.
		dialogUserName = (EditText) layout.findViewById(R.id.dialog_userName);
		dialogPass = (EditText) layout.findViewById(R.id.dialog_Password);
		dialogEmail = (EditText) layout.findViewById(R.id.dialog_email);

		//Building dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(layout);
		builder.setPositiveButton("Create User", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, int which) {
				ParseUser user = new ParseUser();

				user.setUsername(dialogUserName.getText().toString());
				user.setPassword(dialogPass.getText().toString());
				user.setEmail(dialogEmail.getText().toString());

				user.signUpInBackground(new SignUpCallback() {

					@Override
					public void done(ParseException e) {
						if(e == null){
							//Sign-up succedded
							Toast.makeText(AdminInterfaceActivity.this, "User Created", Toast.LENGTH_LONG).show();
							dialog.dismiss();
						}
						else{
							//Sign up failed
							dialog.dismiss();
							Toast.makeText(AdminInterfaceActivity.this, "Failed To Create User: " + e.getMessage(), Toast.LENGTH_LONG).show();
						}

					}
				});

			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog = builder.create();
		dialog.show();  
	}

	public void inflateAddDetailView(){
		new RemoteDataTask().execute("detail");
	}

	public void inflateAddFilterPreset(){
		new RemoteDataTask().execute("filter");
	}

	private class RemoteDataTask extends
	AsyncTask<String, Integer,String> {
		ProgressDialog progressDialog;
		String type = "";
	
		ParseQuery query = new ParseQuery("Artwork");
		
		@Override
		protected String doInBackground(String... params) {
			type = params[0];
			
			return type;
		}

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(AdminInterfaceActivity.this, "",
					"Loading...", true);
			super.onPreExecute();

		}

		@Override
		protected void onPostExecute(String returnedList) {
			
			query.findInBackground(new FindCallback() {
				
				@Override
				public void done(List<ParseObject> objects, ParseException e) {
					ArrayList<ParseObject> returnedList= new ArrayList<ParseObject>();
					returnedList.addAll(objects);
					if(e == null){
						
						LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
						View layout = inflater.inflate(R.layout.list_admin_image_add_dialog, null);
						
						//Building dialog
						AlertDialog.Builder builder = new AlertDialog.Builder(AdminInterfaceActivity.this);
						
						if(type.equalsIgnoreCase("filter")){
							builder.setMessage("Select an image you would like to add a filter preset for");
						}
						else if(type.equalsIgnoreCase("detail")){
							builder.setMessage("Select an image you would like to add a detailview for");
						}
						

						builder.setView(layout)
						.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});
						dialog = builder.create();
						dialog.show(); 
						
						//create adapter input
						ArrayAdapter<ParseObject> adapter = new CustomAdapter(dialog.getContext(),R.layout.list_admin_image_add_item, returnedList);

						ListView listView = (ListView) dialog.findViewById(R.id.dialog_addItems);

						listView.setAdapter(adapter);

						listView.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> arg0, View view, int arg2,long arg3) {
								if(type.equalsIgnoreCase("filter")){
									RelativeLayout temp = (RelativeLayout) view;
									ArrayList<String> tempvalues = (ArrayList<String>) temp.getTag();
									
									Intent i = new Intent(AdminInterfaceActivity.this, FilterPresetMaker.class);
									i.putExtra("artworkName", tempvalues.get(0));
									i.putExtra("artistName", tempvalues.get(1));
									i.putExtra("image1", tempvalues.get(2));
									dialog.dismiss();
									startActivity(i);
								}
								else if(type.equalsIgnoreCase("detail")){
									RelativeLayout temp = (RelativeLayout) view;
									ArrayList<String> tempvalues = (ArrayList<String>) temp.getTag();
									
									Intent i = new Intent(AdminInterfaceActivity.this, DetailViewMaker.class);
									i.putExtra("artworkName", tempvalues.get(0));
									i.putExtra("artistName", tempvalues.get(1));
									i.putExtra("image1", tempvalues.get(2));
									dialog.dismiss();
									startActivity(i);
								}
								else{
									Toast.makeText(AdminInterfaceActivity.this, "There was an error", Toast.LENGTH_LONG).show();
								}
							}
						});
						
						progressDialog.dismiss();
						
					}
					else{
						Log.d("Artwork", "Error: " + e.getMessage());
					}
					
				}
			});
			
			
			
		}

	}

	public class CustomAdapter extends ArrayAdapter<ParseObject> {

		Context context;
		int resource;
		ArrayList<ParseObject> returnedList = null;
		ImageView thumbnail;

		public CustomAdapter(Context context,int resource,ArrayList<ParseObject> objects) {
			super(context, resource, objects);
			this.context=context;
			this.resource = resource;
			returnedList = objects;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.list_admin_image_add_item, parent, false);

			TextView titleText = (TextView) rowView.findViewById(R.id.dialog_arworkName);
			TextView artistText = (TextView) rowView.findViewById(R.id.dialog_byArtist);
			thumbnail = (ImageView) rowView.findViewById(R.id.thumbn);
			RelativeLayout layoutItem = (RelativeLayout) rowView.findViewById(R.id.dialog_add_parent_layout);
			
			ArrayList<String> tagValues = new ArrayList<String>();
		
			ParseObject parseObject = returnedList.get(position);

			//ParseFile image = (ParseFile) parseObject.get("image1");
			String image1 = parseObject.getParseFile("image1").getUrl();
			
			String artwo = parseObject.getString("artworkName");
			String arti = parseObject.getString("artistName");
			titleText.setText(artwo);
			artistText.setText("By: " + arti);
			
			tagValues.add(parseObject.getString("artworkName"));
			tagValues.add(parseObject.getString("artistName"));
			tagValues.add(image1);
			
			layoutItem.setTag(tagValues);
			dialog.invalidateOptionsMenu();

			return rowView;
		}
	}
}