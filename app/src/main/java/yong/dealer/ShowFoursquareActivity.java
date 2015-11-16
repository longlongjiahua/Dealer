package yong.dealer;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

public class ShowFoursquareActivity extends Activity{
    private LatLng userPosition;
    private ArrayList<FoursquareVenue> venuesList;
    private ListView lvFoursquare;
    private FoursquareArrayAdapter foursquareArrayAdapter;


    final String CLIENT_ID = "DDB2HBLVMVPO1FLHQ4SQBIWSGLI1TLPHWAAG1PWTFUR4Z15A";
    final String CLIENT_SECRET = "3WAAF1CBJFQ3LTY2LZV5HHFJSEMHKTWG1EPYN4SABRJAMSHE";
    private static final String TAG = "ShowFoursquareActivity";

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_foursquare);
        lvFoursquare = (ListView) findViewById(R.id.list_foursquare);

        Bundle b = getIntent().getExtras();
        userPosition = new LatLng(b.getDouble("lat"), b.getDouble("lon"));

        new FourquareAsync().execute();
   }
    private class FourquareAsync extends AsyncTask<String, Void, String> {
        String temp;
        @Override
        protected String doInBackground(String... urls){
            // make Call to the url
            String queryFoursqure = "https://api.foursquare.com/v2/venues/search?client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET
                    + "&v=20130815&ll="+userPosition.latitude+","+userPosition.longitude+"&radius=200" + "&limit=100"+"&intent=browse";
            Log.i(TAG, queryFoursqure);
            temp = makeCall(queryFoursqure);
            return "";
        }

        @Override
        protected void onPreExecute() {
            // we can start a progress bar here
        }

        @Override
        protected void onPostExecute(String result) {
            if (temp == null) {
                // we have an error to the call
                // we can also stop the progress bar
            } else {
                // all things went right

                // parseFoursquare venues search result
                venuesList = (ArrayList) parseFoursquare(temp);
                Collections.sort(venuesList);


                for (int i = 0; i< venuesList.size(); i++) {

                    Log.i(TAG, "NAME::" + venuesList.get(i).getName());
                }
                if(venuesList.size()>10){
                    venuesList=new ArrayList<FoursquareVenue> (venuesList.subList(0, 10));//Here fromIndex is inclusive and toIndex is exclusive.
                }

                  foursquareArrayAdapter = new FoursquareArrayAdapter(ShowFoursquareActivity.this, venuesList);
                lvFoursquare.setAdapter(foursquareArrayAdapter);
                lvFoursquare.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        FoursquareVenue four =foursquareArrayAdapter.getItem(i);

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("currentlocation",four.getName());
                        if(getParent()==null) {

                            setResult(Activity.RESULT_OK, resultIntent);
                        }
                        else {
                            getParent().setResult(Activity.RESULT_OK, resultIntent);
                        }

                        Log.i("Selected::", four.getName());

                       // finish();
                        ShowFoursquareActivity.this.finish();
                   }
                });
            }
        }
    }

    public static String makeCall(String url) {

        // string buffers the url
        StringBuffer buffer_string = new StringBuffer(url);
        String replyString = "";

        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(buffer_string.toString());

        try {
            // get the responce of the httpclient execution of the url
            HttpResponse response = httpclient.execute(httpget);
            InputStream is = response.getEntity().getContent();

            // buffer input stream the result
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayBuffer baf = new ByteArrayBuffer(20);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }
            // the result as a string is ready for parsing
            replyString = new String(baf.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // trim the whitespaces
        return replyString.trim();
    }

    private  ArrayList parseFoursquare(final String response) {

        ArrayList<FoursquareVenue> temp = new ArrayList<FoursquareVenue>();
        try {

            // make an jsonObject in order to parse the response
            JSONObject jsonObject = new JSONObject(response);

            // make an jsonObject in order to parse the response
            if (jsonObject.has("response")) {
//has/get JSONObject|JSONArray("key")
//if within object
                if (jsonObject.getJSONObject("response").has("venues")) {
                    JSONArray jsonArray = jsonObject.getJSONObject("response").getJSONArray("venues");
                    Log.i("Length::", ""+jsonArray.length());
                    for (int i = 0; i<jsonArray.length(); i++) {
                        JSONObject locationObj = jsonArray.getJSONObject(i).getJSONObject("location");
                        FoursquareVenue poi = new FoursquareVenue();
                        if(locationObj.has("address")){
                            poi.setAddress(locationObj.getString("address"));
                        }
                        if(locationObj.has("distance")){
                            poi.setDistance(locationObj.getInt("distance"));
                        }
                        if (jsonArray.getJSONObject(i).has("name")) {
                            poi.setName(jsonArray.getJSONObject(i).getString("name"));
                            temp.add(poi);
                        }
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList();
        }
        return temp;

    }

}
