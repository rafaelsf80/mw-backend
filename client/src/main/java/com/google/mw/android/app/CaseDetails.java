package com.google.mw.android.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.api.client.util.DateTime;
import com.google.mw.backend.caseApi.model.CaseBean;

public class CaseDetails extends Activity {

	private final String TAG = getClass().getSimpleName();

	private CaseBean bean;

	LinearLayout llMain;
	TextView tvCaseTitle, tvCaseOwner, tvCreatedDate, tvStatus;
	EditText etClosedDate, etComments;
	Button btUpdateCase;
	
	Handler mHandler;

	public interface ICaseDetails {
    	void updateClick(CaseBean bean);
    }

	private static ICaseDetails mICaseDetails;
	
	public static void regListener(ICaseDetails iTabConnect) {
    	mICaseDetails = iTabConnect;
    }
    
    public static void unregisterListener () {
		mICaseDetails = null;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.case_details);
		Log.d(TAG, "onCreate()");
		
		Intent i = getIntent();
		bean = new CaseBean();
		bean.setId( i.getLongExtra("CASEBEAN_ID", 0));
		bean.setLongitude( i.getDoubleExtra("CASEBEAN_LONGITUDE", 0 ));
		bean.setLatitude( i.getDoubleExtra("CASEBEAN_LATITUDE", 0 ));	
		bean.setTitle( i.getStringExtra("CASEBEAN_TITLE"));
		bean.setOwner( i.getStringExtra("CASEBEAN_OWNER"));
		bean.setDateCreated( new DateTime(i.getStringExtra("CASEBEAN_CREATED")) );
		bean.setDateClosed( new DateTime(i.getStringExtra("CASEBEAN_CLOSED")) );
		bean.setStatus(i.getStringExtra("CASEBEAN_STATUS"));
		bean.setComments(i.getStringExtra("CASEBEAN_COMMENTS"));
		

		tvCaseTitle = (TextView) findViewById(R.id.tv_case_title);
		tvCaseOwner = (TextView) findViewById(R.id.tv_owner);
		tvCreatedDate = (TextView) findViewById(R.id.tv_created_date);
		etClosedDate = (EditText) findViewById(R.id.tv_closed_date);
		tvStatus = (TextView) findViewById(R.id.tv_status);
		etComments = (EditText) findViewById(R.id.tv_comments);

		btUpdateCase = (Button) findViewById(R.id.bt_update_case);

		tvCaseTitle.setText(bean.getTitle());
		tvCaseOwner.setText(bean.getOwner());

		// Parse Date and Time to show it in a more human readable format
		tvCreatedDate.setText(parseDateTime(bean.getDateCreated().toStringRfc3339()));
		etClosedDate.setText(parseDateTime(bean.getDateClosed().toStringRfc3339()));
		
		// Case status, change background color of the layout accordingly
		String status = bean.getStatus();		
		tvStatus.setText( status );
		llMain = (LinearLayout)findViewById(R.id.ll_case_details);
		if (status.contains("EMERGENCY"))		
			llMain.setBackgroundColor( getResources().getColor(R.color.red_color) );
		else if (status.contains("ACTIVE")) 
			llMain.setBackgroundColor( getResources().getColor(R.color.yellow_color) );
		else 
			llMain.setBackgroundColor( getResources().getColor(R.color.green_color) );
		
		// Case comments
		etComments.setText(bean.getComments());

		mHandler = new Handler(new Callback() {
			
			@Override
			public boolean handleMessage(Message msg) {
				
				DateTime closedDate = (DateTime) msg.obj;
				etClosedDate.setText(parseDateTime(closedDate.toStringRfc3339()));
				bean.setDateClosed( closedDate );
				return true;
			}
		});

		etClosedDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// Show DatePickerDialog
				DateTimePickerDialog dateTimePickerDialog = 
						new DateTimePickerDialog(CaseDetails.this, mHandler, bean);
				dateTimePickerDialog.show();
			}
		});

		btUpdateCase.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				new AlertDialog.Builder(CaseDetails.this)
	    	    .setTitle("Case Closure")
	    	    .setMessage("Are you sure ? This can not be undone")
	    	    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	    	        public void onClick(DialogInterface dialog, int which) { 
	    	        	
	    	        	bean.setComments(etComments.getText().toString());
	    				if (mICaseDetails != null) 		
	    					mICaseDetails.updateClick(bean);
	    				// Close this activity
	    				finish();
	    				dialog.dismiss();	    	    
	    	        }
	    	     })
	    	    .setNegativeButton("No", new DialogInterface.OnClickListener() {
	    	        public void onClick(DialogInterface dialog, int which) { 
	    	        	dialog.dismiss();
	    	        }
	    	     })
	    	     .show();				
			}
		});		
	}

	/**
    *
    * Parses dateTime format in RFC3339 into a more human readable.
    * 
    * Example: converts 2014-05-31T08:51:32.590+02:00 into 2014-05-31 08:51
	* (removing the middle "T, seconds and timezone)
	* 
    */
	private String parseDateTime(String date) {
		
		// Parsing 2014-05-31T08:51:32.590+02:00 into 2014-05-31 08:51
		// Removes the middle "T, seconds and timezone

		String parsedDate = null;		
		parsedDate = date.replace("T", " ").substring(0, 16);
		return parsedDate;
	}
}
