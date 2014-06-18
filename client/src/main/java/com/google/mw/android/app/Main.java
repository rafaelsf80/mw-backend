package com.google.mw.android.app;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.util.DateTime;
import com.google.mw.android.app.CaseDetails.ICaseDetails;
import com.google.mw.android.app.ListEntryAdapter.IAdapter;
import com.google.mw.backend.caseApi.CaseApi;
import com.google.mw.backend.caseApi.model.CaseBean;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class Main extends Activity {
	
	private final String TAG = getClass().getSimpleName();
		
	
	
	public static String Owner = "Rafa";
		
	// Widgets
	private ImageView imGoogleLogo;
	//private EditText etFirstName, etLastName, etPassword;
	private String firstName = "", lastName = "", segment = "", password = "";
	String[] folderId;
	String[] folderTitle;
    private TextView tvStatusConnect;
    private TextView tvStatusConnectMessage;

    private ProgressDialog pd;
    
    public static Context mContext = null;
    
	public ListView list;
	public static ListEntryAdapter listadapter = null;
	CaseApi mCaseApi;
	public static List<CaseBean> array = null;
    
    JSONObject jsObject;
    
    //Connectivity management
    public static final String WIFI = "Wi-Fi";
    public static final String ANY = "Any";
    
    // Whether there is a Wi-Fi connection.
    private static boolean wifiConnected = false;
    // Whether there is a mobile connection.
    private static boolean mobileConnected = false;
    // Whether the display should be refreshed.
    public static boolean refreshDisplay = true;
    
    // Final message to display
    public static String statusConnectivity = "";
    public static String statusMessage = "";

    // The user's current network preference setting.
    public static String sPref = null;
    
    // The BroadcastReceiver that tracks network connectivity changes.
    private NetworkReceiver receiver = new NetworkReceiver();
    
    public static String errorMessage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.d(TAG, "onCreate()");
        
        mContext = this;
        array = new ArrayList<CaseBean>();
           
        // Launch Google Cloud Messaging
        GcmClientTask gcmClient = new GcmClientTask();   
        gcmClient.setContext(mContext);
        gcmClient.start();
             
        // Register BroadcastReceiver to track connection changes.
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);
      
        // Create HTTP connection to Cloud Endpoint
        CaseApi.Builder builder = new CaseApi.Builder(
				  AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null);
		mCaseApi = builder.setHttpRequestInitializer(new HttpRequestInitializer() {
	          @Override
	          public void initialize(HttpRequest httpRequest) {
	            //credential.initialize(httpRequest);
	            httpRequest.setConnectTimeout(3 * 60000);  // 3 minutes connect timeout
	            httpRequest.setReadTimeout(3 * 60000);  // 3 minutes read timeout
	          }
	        }).build();	
		
		 imGoogleLogo = (ImageView) this.findViewById(R.id.imGoogleLogo);
         TextView tvICRegistrationDashboard = (TextView) this.findViewById(R.id.tvICRegistrationDashboard);
           
        // display in the list
		list=(ListView)findViewById(R.id.list);
		
		// Only for debugging
		//if (array.size() == 0)
		//	defaultCases();

		listadapter=new ListEntryAdapter(Main.this, array);
		list.setAdapter(listadapter);
		
		// message regarding connectivity status
		tvStatusConnect = (TextView) this.findViewById(R.id.networkConnection);
		tvStatusConnectMessage = (TextView) this.findViewById(R.id.networkMessage);

		// Launch volley query to get folderId from Internet, to be included in the spinner 
		//RequestQueue queue = Volley.newRequestQueue(this);
		//String urlFolders = mContext.getResources().getString(R.string.urlFolders);

		networkWidget();

         /* Listener to launch Case Details dialog */
 		ListEntryAdapter.regListener(new IAdapter() {
	
			@Override
			public void itemClick(CaseBean bean) {
 				
 				Intent i = new Intent(Main.this, CaseDetails.class);
 				i.putExtra("CASEBEAN_ID", bean.getId());
 				i.putExtra("CASEBEAN_LATITUDE", bean.getLatitude());
 				i.putExtra("CASEBEAN_LONGITUDE", bean.getLongitude());
				i.putExtra("CASEBEAN_TITLE", bean.getTitle());
				i.putExtra("CASEBEAN_OWNER", bean.getOwner());
				i.putExtra("CASEBEAN_CREATED", bean.getDateCreated().toStringRfc3339());
				i.putExtra("CASEBEAN_CLOSED", bean.getDateClosed().toStringRfc3339());
				i.putExtra("CASEBEAN_STATUS", bean.getStatus());
				i.putExtra("CASEBEAN_COMMENTS", bean.getComments());	
				startActivity(i);			
 			}
 		});
 		
 		 /* Listener to launch UpdateCaseTask */
 		CaseDetails.regListener(new ICaseDetails() {
	
			@Override
			public void updateClick(CaseBean bean) {
				
				CaseUpdateTask task = new CaseUpdateTask( );
				task.setService( mCaseApi );
				task.setContext( mContext );
				task.setData(bean);
				task.execute( new String[]{firstName, lastName, password, segment} );	 					
 			}
 		});
    }
    
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
        Log.d(TAG, "onResume()"); 
        
    	CaseGetAllTask task = new CaseGetAllTask( );
		task.setContext( mContext );
		task.setListAdapter(listadapter);
		task.setService( mCaseApi );
		task.execute( new String[]{firstName, lastName, password, segment} );	
    }
 
    private void defaultCases() {
		// init the arraylist for listEnrtyAdapter
		Main.array.clear();

		CaseBean bean1 = new CaseBean();
		bean1.setTitle("Failure on network");
		bean1.setOwner("Rafa1");
		bean1.setDateCreated(new DateTime(new Date(), TimeZone.getTimeZone("UTC")));
		bean1.setDateClosed(new DateTime(new Date(), TimeZone.getTimeZone("UTC")));
		Main.array.add(bean1);

		CaseBean bean2 = new CaseBean();
		bean2.setTitle("Incidence device");
		bean2.setOwner("Rafa2");
		bean2.setDateCreated(new DateTime(new Date(), TimeZone.getTimeZone("UTC")));
		bean2.setDateClosed(new DateTime(new Date(), TimeZone.getTimeZone("UTC")));
		Main.array.add(bean2);

		CaseBean bean3 = new CaseBean();
		bean3.setTitle("Outage");
		bean3.setOwner("Rafa3");
		bean3.setDateCreated(new DateTime(new Date(), TimeZone.getTimeZone("UTC")));
		bean3.setDateClosed(new DateTime(new Date(), TimeZone.getTimeZone("UTC")));
		Main.array.add(bean3);

		CaseBean bean4 = new CaseBean();
		bean4.setTitle("Incidence auto");
		bean4.setOwner("Rafa4");
		bean4.setDateCreated(new DateTime(new Date(), TimeZone.getTimeZone("UTC")));
		bean4.setDateClosed(new DateTime(new Date(), TimeZone.getTimeZone("UTC")));
		Main.array.add(bean4);

		CaseBean bean5 = new CaseBean();
		bean5.setTitle("Another one");
		bean5.setOwner("Rafa5");
		bean5.setDateCreated(new DateTime(new Date(), TimeZone.getTimeZone("UTC")));
		bean5.setDateClosed(new DateTime(new Date(), TimeZone.getTimeZone("UTC")));
		Main.array.add(bean5);

	}
   
    // Refreshes the display if the network connection and the
    // pref settings allow it.

    public void networkWidget() {

        // Gets the user's network preference settings
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Retrieves a string value for the preferences. The second parameter
        // is the default value to use if a preference value is not found.
        sPref = sharedPrefs.getString("listPref", "Wi-Fi");

        //updateConnectedFlags();

    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            this.unregisterReceiver(receiver);
        }
    }
    
    // Checks the network connection and sets the wifiConnected and mobileConnected
    // variables accordingly.
    private void updateConnectedFlags() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            wifiConnected = false;
            mobileConnected = false;
        }
        displayStatus();
    }
    
    // Uses AsyncTask subclass to download the XML feed from stackoverflow.com.
    // This avoids UI lock up. To prevent network operations from
    // causing a delay that results in a poor user experience, always perform
    // network operations on a separate thread from the UI.
    private void displayStatus() {
    	
    	//connected with WIFI or mobile data
    	if(wifiConnected || mobileConnected)
    	{
    		statusConnectivity = getString(R.string.status_connectivity_OK);
    		//preferences set to WIFI and is connected to WIFI , this is a success
        	if(wifiConnected)
        		statusMessage = getString(R.string.status_message_successWIFI);
        	//if preferences is WIFI, set the GoogleGuest wifi
        	else if((sPref.equals(WIFI)) && (mobileConnected))
        		statusMessage = getString(R.string.status_message_successMobile);
        	// set the preferences to WIFI
        	else
        		statusMessage = getString(R.string.status_message_preferences);
    	} 
    	//error : no network
    	else 
    	{
        	
        	statusConnectivity = getString(R.string.status_connectivity_NOK);
        	statusMessage = getString(R.string.status_message_error);
        }
        
        tvStatusConnect.setText(statusConnectivity);
        tvStatusConnectMessage.setText(statusMessage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_about:
                Intent i = new Intent(getApplicationContext(), About.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    /**
    *
    * This BroadcastReceiver intercepts the android.net.ConnectivityManager.CONNECTIVITY_ACTION,
    * which indicates a connection change. It checks whether the type is TYPE_WIFI.
    * If it is, it checks whether Wi-Fi is connected and sets the wifiConnected flag in the
    * main activity accordingly.
    *
    */
  public class NetworkReceiver extends BroadcastReceiver {

       @Override
       public void onReceive(Context context, Intent intent) {
           ConnectivityManager connMgr =
                   (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
           NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

           updateConnectedFlags();
           
       }
   }    
}