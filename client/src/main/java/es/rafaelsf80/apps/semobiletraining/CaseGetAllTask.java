package es.rafaelsf80.apps.semobiletraining;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.util.DateTime;
import com.google.mw.backend.caseApi.CaseApi;
import com.google.mw.backend.caseApi.model.CaseBean;
import com.google.mw.backend.caseApi.model.CaseBeanCollection;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

//import caseApi.CaseApi;
//import caseApi.CaseApi.GetAllCases;
//import caseApi.model.CaseBean;
//import caseApi.model.CaseBeanCollection;


public class CaseGetAllTask extends AsyncTask<String, Integer, Integer>{

    //final CaseApi caseApiService;

    ProgressDialog pd = null;
    private final String TAG = getClass().getSimpleName();

    private String givenName, familyName, password, segment;

    private Context mContext;
    private CaseApi mCaseApi;


    private ListEntryAdapter mListEntryAdapter;

//    public CaseGetAllTask() {
//        super();
//        CaseApi.Builder builder = new CaseApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null);
//        caseApiService = builder.build();
//
//    }

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

		/* Calls the Cloud Endpoint service */



        //GetAllCases g;
        CaseBeanCollection c;


        try {
            //changed this over to 1 line getting a List<CaseBean>, i think it is clearer for the students
//            g = mCaseApi.getAllCases();
//            Log.d(TAG, "g: " + g.toString());
//            c = g.execute();

            List<CaseBean> cases = mCaseApi.getAllCases().execute().getItems();

            //Main.array = c.getItems();
            Main.array = cases;
            Log.d(TAG, Main.array.toString());
            // Check incoming data for null pointers to avoid exceptions
            for (int i = 0; i < Main.array.size(); i++) {
                if (Main.array.get(i).getDateClosed() == null)
                    Main.array.get(i).setDateClosed(new DateTime(new Date(), TimeZone.getTimeZone("UTC")));
                if (Main.array.get(i).getDateCreated() == null)
                    Main.array.get(i).setDateCreated(new DateTime(new Date(), TimeZone.getTimeZone("UTC")));
                if (Main.array.get(i).getStatus() == null)
                    Main.array.get(i).setStatus("CLOSED");
            }
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