package com.google.mw.android.app;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.protocol.HTTP;

import java.util.List;

public class About extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_about);
		TextView txLink = (TextView) this.findViewById(R.id.about_link_text);
		txLink.setMovementMethod(LinkMovementMethod.getInstance());

		Button btWebsite = (Button) this.findViewById(R.id.btWebsite);
		btWebsite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Uri webpage = Uri.parse(getString(R.string.about_site_URL));
				Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
			    startActivity(webIntent);	
			}
		});
		
		Button btContact = (Button) this.findViewById(R.id.btContact);
		btContact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Do something in response to button click
		    	sendMail();
	
			}
		});
	}


	public void sendMail()
	{
		
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		// The intent does not have a URI, so declare the "text/plain" MIME type
		emailIntent.setType(HTTP.PLAIN_TEXT_TYPE);
		//emailIntent.setData(Uri.parse("contact_ebc@google.com"));
		
		
		//force using Gmail
		emailIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
		emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {getString(R.string.about_email_contact)}); // recipients
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.about_email_subject));
		emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.about_email_content));
		
		// Verify it resolves
		PackageManager packageManager = getPackageManager();
		List<ResolveInfo> activities = packageManager.queryIntentActivities(emailIntent, 0);
		boolean isIntentSafe = activities.size() > 0;
		
		// Start an activity if it's safe
		if (isIntentSafe) {
		    startActivity(emailIntent);
		}
		else
		{
			//To change to set it dynamically
			Toast.makeText(getApplicationContext(), "No email service available", Toast.LENGTH_LONG).show();
		}
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.about, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_main:
			Intent i = new Intent(getApplicationContext(), Main.class);
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
