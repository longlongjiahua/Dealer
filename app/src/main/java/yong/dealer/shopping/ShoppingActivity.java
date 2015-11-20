/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package yong.dealer.shopping;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.VelocityTracker;
import android.view.animation.AnticipateOvershootInterpolator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Vector;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import yong.dealer.shopping.data.ShoppingItem;
import yong.dealer.shopping.data.ShoppingContract.InventoryEntry;

import yong.dealer.R;

import yong.dealer.shopping.data.ShoppingContract;


public class ShoppingActivity extends AppCompatActivity implements InventoryFragment.Callback {

    private final String LOG_TAG = ShoppingActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private boolean mTwoPane;
    private String mLocation;
    private ArrayList<ShoppingItem> products = new ArrayList<ShoppingItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
        Log.i(LOG_TAG, "SHOPING STSRT");

        InventoryFragment inventoryFragment =  ((InventoryFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_inventory));
        fetchAndParseData();
        fillDatabases();

        Log.i(LOG_TAG, products.get(0).category);
        Log.i(LOG_TAG, products.get(0).food.get(1).name);
        //Log.i(LOG_TAG, products.get(0).foods.get(0).name);
        //InventoryFragment.setUseTodayLayout(!mTwoPane);

       // ShoppingSyncAdapter.initializeSyncAdapter(this);
    }
    public void fillDatabases(){
        if(products.size()==0)
            return;
        ContentValues foodValues = new ContentValues();

        for(ShoppingItem one :products) {
            String category = one.category;
            ContentValues categoryValues = new ContentValues();

            categoryValues.put(ShoppingContract.CategoryEntry.COLUMN_NAME, category);
            Uri uri = getContentResolver().insert(ShoppingContract.CategoryEntry.CONTENT_URI, categoryValues);
            long category_id = Long.valueOf(uri.getLastPathSegment());
            Log.i(LOG_TAG, "" + category_id);
            ArrayList<ShoppingItem.Food> foods = (ArrayList) one.food;
            //Vector is synchronized

            foodValues.clear();
            Vector<ContentValues> cVVector = new Vector<ContentValues>(foods.size());
            for (ShoppingItem.Food food : foods) {
                foodValues.put(InventoryEntry.COLUMN_NAME, food.name);
                foodValues.put(InventoryEntry.COLUMN_CATEGORY_ID, category_id);
                foodValues.put(InventoryEntry.COLUMN_CALORIE, food.nutrition.calories);
                foodValues.put(InventoryEntry.COLUMN_CARBOH, food.nutrition.carbohydrate);
                foodValues.put(InventoryEntry.COLUMN_FAT, food.nutrition.fat);
                Log.i(LOG_TAG, "" + food.nutrition.fat);
            }
            cVVector.add(foodValues);
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                getContentResolver().bulkInsert(InventoryEntry.CONTENT_URI, cvArray);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

      /*
            DetailFragment df = (DetailFragment)getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if ( null != df ) {
                df.onLocationChanged(location);
            }
            mLocation = location;
        */
    }

    @Override
    public void onItemSelected(Uri contentUri) {
        /*
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, contentUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();

        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
       */
    }
    public  void fetchAndParseData() {

        JsonReader reader;
        InputStream inputStream =null;
        try {
            inputStream = getResources().openRawResource(
                    getResources().getIdentifier("raw/itemlist",
                            "raw", getPackageName()));
        } catch (Exception e) {

        }
        if (inputStream != null) {
            try {
                Gson gson = new Gson();

                reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
                reader.beginArray();
                while (reader.hasNext()) {
                    ShoppingItem product = gson.fromJson(reader, ShoppingItem.class);
                    //log.info("COINAPTYPE::" + Product.getType());
                    products.add(product);
                }
                reader.endArray();
                reader.close();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                return;  //this means the network connection is not good or IO
            }
        }
    }
}
