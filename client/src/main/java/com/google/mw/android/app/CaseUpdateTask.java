package com.google.mw.android.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.mw.backend.caseApi.CaseApi;
import com.google.mw.backend.caseApi.model.CaseBean;

import java.io.IOException;


public class CaseUpdateTask extends AsyncTask<String, Integer, Integer>{
	
	ProgressDialog pd = null;
	private final String TAG = getClass().getSimpleName();
	
	private Context mContext;
	private CaseApi mCaseApi;
	private CaseBean mBean;
	
	private String givenName, familyName, password, segment;

	  
    public void setContext(Context ctx) {
		mContext = ctx;
	}

	public void setService(CaseApi rgtr) {		
		mCaseApi = rgtr;
	}
	
	public void setData(CaseBean bean) {		
		mBean = bean;
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

		/* Calls the Cloud Endpoint service */
		try {
			Log.d(TAG, mBean.toString());

            //TODO: add the code to send the case to Cloud Endpoint
            //TODO: TRIM for the student start package
			mCaseApi.updateCase(mBean).execute();
			
		} catch (IOException e) {

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
		pd.dismiss();	
	}

}
