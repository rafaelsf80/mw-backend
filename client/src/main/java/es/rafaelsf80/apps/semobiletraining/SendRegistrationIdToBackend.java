package es.rafaelsf80.apps.semobiletraining;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
 * messages to your app. Not needed for this demo since the device sends upstream messages
 * to a server that echoes back the message using the 'from' address in the message.
 */

public class SendRegistrationIdToBackend extends AsyncTask<String, Integer, Integer>{

    ProgressDialog pd = null;
    private final String TAG = getClass().getSimpleName();

    private String regid;

    private Context mContext;

    public void setContext(Context ctx) {
        mContext = ctx;
    }

    protected void onPreExecute()
    {
        // Show progressDialog
        //pd = new ProgressDialog(mContext);
        //pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		/*pd.setTitle("ProgressDialog");
		pd.setMessage("Registering to backend ...");*/
        //pd.show();
    }

    @Override
    protected Integer doInBackground(String... params) {

        String hostName = "1-dot-decent-envoy-503.appspot.com/gcmdemo";
        regid = params[0];
        Integer result = 1;

        Log.d(TAG, "Registering in background: "+regid);
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://" + hostName);

            // Request parameters and other properties.
            List<NameValuePair> postParams = new ArrayList<NameValuePair>(2);
            postParams.add(new BasicNameValuePair("regid", regid));
            httppost.setEntity(new UrlEncodedFormEntity(postParams, "UTF-8"));

            //Execute and get the response.
            HttpResponse response = httpclient.execute(httppost);
            Log.d(TAG, "POST request return code: " + String.valueOf(response.getStatusLine().getStatusCode()));

            if(response.getStatusLine().getStatusCode()==200 || response.getStatusLine().getStatusCode()== 405){
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    InputStream instream; //entity.getContent();
                    try {
                        String content =  EntityUtils.toString(entity);
                        Log.d(TAG, "Response: " + content);

                        // do something useful
                        // check return
                    } finally {
                        //instream.close();
                    }
                }
                result = 1;
            }
        } catch (IOException e) {
            Log.d(TAG, "error: " + e.toString());
            result = 0;
        }
        return result;
    }


    @Override
    protected void onPostExecute(Integer result)
    {
        //pd.dismiss();

        if (result == 1)
            Toast.makeText(mContext, "regid successfully registered", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(mContext, "regid error"  , Toast.LENGTH_LONG).show();

    }
}