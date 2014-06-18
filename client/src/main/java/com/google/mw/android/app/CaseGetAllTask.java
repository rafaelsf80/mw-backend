package com.google.mw.android.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.util.DateTime;
import com.google.mw.backend.caseApi.CaseApi;
import com.google.mw.backend.caseApi.model.CaseBean;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class CaseGetAllTask extends AsyncTask<String, Integer, Integer>{

	ProgressDialog pd = null;
	private final String TAG = getClass().getSimpleName();

	private String givenName, familyName, password, segment;

	private Context mContext;
	private CaseApi mCaseApi;
	private ListEntryAdapter mListEntryAdapter;
	
	public void setContext(Context ctx) {
		mContext = ctx;
	}

	public void setListAdapter(ListEntryAdapter adapter) {
		mListEntryAdapter = adapter;
	}
	
	public void setService(CaseApi rgtr) {		
		mCaseApi = rgtr;
	}

	protected void onPreExecute()
	{
		// Show progressDialog
		pd = new ProgressDialog(mContext);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setTitle(mContext.getResources().getString(R.string.registerTitle));
		pd.setMessage(mContext.getResources().getString(R.string.registerMessage));
		pd.setMax(100);
		pd.show();
	}

	@Override
	protected Integer doInBackground(String... params) {

		givenName = params[0];	
		familyName = params[1];
		password = params[2];
		segment = params[3];
		Log.d(TAG, "Registering: " + givenName + " " + familyName + " " + password + " " + segment);

		publishProgress( 25 );

		/* Call the Cloud Endpoint service */
		try {			
			List<CaseBean> cases = mCaseApi.getAllCases().execute().getItems();        			
			Log.d(TAG, "Unfiltered(" + String.valueOf(cases.size()) + "): "+cases.toString());
			
			// Check incoming data for null pointers to avoid exceptions
			for (int i = 0; i < cases.size(); i++) {
				if (cases.get(i).getDateClosed() == null)
					cases.get(i).setDateClosed(new DateTime(new Date(), TimeZone.getTimeZone("UTC")));
				if (cases.get(i).getDateCreated() == null)
					cases.get(i).setDateCreated(new DateTime(new Date(), TimeZone.getTimeZone("UTC")));
				if (cases.get(i).getStatus() == null)
					cases.get(i).setStatus("CLOSED");
				if (cases.get(i).getOwner() == null)
					cases.get(i).setOwner("");
			}
			
			// Filter by owner and assign to Main.array
			Main.array.clear();
			for (int i = 0; i < cases.size(); i++) {
				if (cases.get(i).getOwner().compareTo(Main.Owner) == 0)
					Main.array.add( cases.get(i) );
			}
			Log.d(TAG, "Filtered(" + String.valueOf(Main.array.size()) + "): "+Main.array.toString());
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		publishProgress( 100 );
		//return lastStatusCode;
		return 1;
	}

	@Override
	protected void onProgressUpdate(final Integer... progress) {
		pd.setProgress(progress[0]);
	}

	@Override
	protected void onPostExecute(Integer statusCode)
	{
		Log.d(TAG, "onPostExecute");
		mListEntryAdapter.notifyDataSetChanged();
		pd.dismiss();		
	}
}
