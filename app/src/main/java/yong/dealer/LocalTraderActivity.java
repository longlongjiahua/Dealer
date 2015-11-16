package yong.dealer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
    import android.location.LocationManager;
    import android.net.ConnectivityManager;
    import android.net.NetworkInfo;
    import android.os.AsyncTask;
    import android.os.Bundle;
    import android.support.v4.app.FragmentActivity;
    import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
    import android.widget.EditText;
    import android.widget.TextView;
    import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.GooglePlayServicesUtil;

//import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


    import org.apache.http.HttpResponse;
    import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
    import org.apache.http.entity.StringEntity;
    import org.apache.http.impl.client.DefaultHttpClient;
    import org.apache.http.message.BasicHeader;
    import org.apache.http.params.HttpConnectionParams;
    import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.BufferedReader;
    import java.io.IOException;
    import java.io.InputStream;
    import java.io.InputStreamReader;

import yong.dealer.chat.MainActivity;



public class LocalTraderActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener,
            LocationListener,
            View.OnClickListener
    {
        TextView tvIsConnected;
        EditText etName,etAddress,etInfo;
        Button btnPost;
        Button btnCurrentLocation;
        //private LocationClient mLocationClient;
        public LatLng userPosition = new LatLng(46, -100);
        int RQS_GooglePlayServices = 1;
        private static final double CAMERA_LATITUDE_OFFSET_FOR_INFO_WINDOW = 0.0005;
        private boolean movedCamera = false;
            private static final String TAG = "LocalTraderActivity";
        private static final int REQUEST_CODE_CURRENT_ADDRESS = 0;
       private GoogleMap mMap; // Might be null if Google Play services APK is not available.
        protected GoogleApiClient mGoogleApiClient;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_local_trader);
            setUpMapIfNeeded();
            tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);
            etName = (EditText) findViewById(R.id.etName);
            etAddress = (EditText) findViewById(R.id.etAddress);
            etInfo = (EditText) findViewById(R.id.etInfo);
            btnPost = (Button) findViewById(R.id.btnPost);
            btnCurrentLocation = (Button) findViewById(R.id.btCurrentlocation);
            registerReceivers();
            // check if you are connected or not
             connectedAction(isConnected());
            // add click listener to Button "POST"
            btnPost.setOnClickListener(this);
            btnCurrentLocation.setOnClickListener(this);

        }
        @Override
        public boolean onCreateOptionsMenu(final Menu menu)
        {
            super.onCreateOptionsMenu(menu);
            getMenuInflater().inflate(R.menu.localtrader, menu);
            return true;
        }

        @Override
        public boolean onPrepareOptionsMenu(final Menu menu) {
            super.onPrepareOptionsMenu(menu);
            menu.findItem(R.id.chat_with_local_trader).setVisible(true);
            return true;
        }
        @Override
        public boolean onOptionsItemSelected(final MenuItem item) {
            switch (item.getItemId()) {
                case R.id.chat_with_local_trader:
                    startActivity(new Intent(this, MainActivity.class ));
                    return true;
            }
            return super.onOptionsItemSelected(item);
        }


                @Override
                public void onResume() {
                    super.onResume();
                    setUpMapIfNeeded();
                    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
                    if (resultCode == ConnectionResult.SUCCESS) {
                        // Toast.makeText(getApplicationContext(),
                        //  "isGooglePlayServicesAvailable SUCCESS",
                        // Toast.LENGTH_LONG).show();
                    } else {
                        GooglePlayServicesUtil.getErrorDialog(resultCode, this, RQS_GooglePlayServices);
                    }
                    Location mLocation = getLocation();
                    if (mLocation != null) {
                        userPosition = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                    }
                    //Log.i("Position::" + userPosition.latitude + "::" + userPosition.longitude);
                    // create the fragment and data the first time

                }

        private static final LocationRequest REQUEST = LocationRequest.create()
                .setInterval(5000)         // 5 seconds
                .setFastestInterval(16)    // 16ms = 60fps
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        protected synchronized void buildGoogleApiClient() {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        /**
         * Callback called when disconnected from GCore. Implementation of {@link ConnectionCallbacks}.
         */
        @Override
        public void onConnectionSuspended(int arg0) {
            mGoogleApiClient.connect();
        }
        public Location getLocation() {
            Location mLocation = null;
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                mLocation = LocationServices.FusedLocationApi
                        .getLastLocation(mGoogleApiClient);
            }
            if(mLocation ==null) {
                LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                if (locationManager != null) {
                    Location lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (lastKnownLocationGPS != null) {
                        return lastKnownLocationGPS;
                    } else {
                        return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
            }
            return mLocation;
        }

        public void onLocationChanged(Location location) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            //log.info("POSITION_ON::" + location.getLatitude() + "::" + location.getLongitude());
            userPosition = latLng;
            //mapArrayAdapter.setUserPosition(latLng);
            //mapArrayAdapter.notifyDataSetChanged();
            if (movedCamera)
                return;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            mMap.animateCamera(cameraUpdate); //Once animateCamera finished, onCameraChange get called.
            movedCamera = true;
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(userPosition).title("Marker"));

        }

        @Override
        public void onConnected(Bundle connectionHint) {
        }
        @Override
        public void onConnectionFailed(ConnectionResult result) {
            //log.info("OnconnectionFailed::");
            if (!result.isSuccess()) {
                // log.info("OnconnectionFailed::inSide");
                if (movedCamera)
                    return;
                userPosition = new LatLng(46, -100);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(userPosition, 12);
                mMap.animateCamera(cameraUpdate); //Once animateCamera finished, onCameraChange get called.
                movedCamera = true;

                // Do nothing
            }
        }


        private void setUpMapIfNeeded() {
            // Do a null check to confirm that we have not already instantiated the map.
            if (mMap == null) {
                // Try to obtain the map from the SupportMapFragment.
                mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.trader_map))
                        .getMap();
                // Check if we were successful in obtaining the map.
                if (mMap != null) {
                    setUpMap();
                }
            }
        }

        private void setUpMap() {
            mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        }
        public boolean isConnected() {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {

                return true;
            } else
                return false;
        }


        @Override
        public void onClick(View view) {

            switch(view.getId()){
                case R.id.btnPost:
                    if(validate()) {
                        new HttpAsyncTask().execute();
                      //Toast.makeText(getBaseContext(), "Enter some data!", Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.btCurrentlocation:
                    Intent i = new Intent(this, ShowFoursquareActivity.class);
                    Bundle b = new Bundle();
                    b.putDouble("lat", userPosition.latitude);
                    b.putDouble("lon", userPosition.longitude);
                    i.putExtras(b);
                    startActivityForResult(i, REQUEST_CODE_CURRENT_ADDRESS);
                    break;
            }

        }
        @Override
               protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == REQUEST_CODE_CURRENT_ADDRESS && resultCode == Activity.RESULT_OK) {
                String currentLocation = (String) data.getExtras().getString("currentlocation");
                if (currentLocation != null) {
                    etAddress.setText(currentLocation);
                }

            }
        }

        void mPost(){
            String path = "https://instacoin.net/json/get_post_data.php";

            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            // Limit
            HttpResponse response;
            JSONObject json = new JSONObject();
            try {
                HttpPost post = new HttpPost(path);
                // json.put("service", "GOOGLE");
                json.put("name",etName.getText().toString());
                json.put("address",etAddress.getText().toString());
		json.put("lon", ""+userPosition.longitude);
                json.put("lat", ""+ userPosition.latitude);
                Log.i("json Object", json.toString());
                post.setHeader("json", json.toString());
                StringEntity se = new StringEntity(json.toString());
                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                post.setEntity(se);
                response = client.execute(post);
        /* Checking response */
                if (response != null) {
                    InputStream in = response.getEntity().getContent(); // Get the

                    String a = convertStreamToString(in);
                    Log.i("Read from Server", a);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        private static String convertStreamToString(InputStream is) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        }

        private class HttpAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... urls) {
                mPost();

                return "done";
            }
            // onPostExecute displays the results of the AsyncTask.
            @Override
            protected void onPostExecute(String result) {
                Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
            }
        }

        private boolean validate(){
            if(etName.getText().toString().trim().equals(""))
                return false;
            else if(etAddress.getText().toString().trim().equals(""))
                return false;
            else
                return true;
        }

        private void registerReceivers() {
            registerReceiver(mConnReceiver,
                    new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }



        private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
                connectedAction(!noConnectivity);

            }
        };
        private void connectedAction(boolean connected){
            if(!connected){
                tvIsConnected.setText("You are NOT connected");
                    btnPost.setEnabled(false);
                    btnCurrentLocation.setEnabled(false);
                }
            else {
                tvIsConnected.setBackgroundColor(0xFF00CC00);
                tvIsConnected.setText("You are connected");
                btnPost.setEnabled(true);
                btnCurrentLocation.setEnabled(true);
            }

        }
        @Override
        protected void onDestroy() {
            unregisterReceiver(mConnReceiver);
            super.onDestroy();
        }

    }
