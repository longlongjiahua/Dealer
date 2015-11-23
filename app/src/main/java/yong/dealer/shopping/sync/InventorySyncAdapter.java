package yong.dealer.shopping.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.format.Time;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import yong.dealer.shopping.ShoppingActivity;
import yong.dealer.shopping.data.ShoppingContract;
import yong.dealer.shopping.data.ShoppingItem;
import yong.dealer.shopping.data.ShoppingContract;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

import yong.dealer.R;

public class InventorySyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = InventorySyncAdapter.class.getSimpleName();
    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 2;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
   // private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final int NOTIFICATION_ID = 2001;

    private ArrayList<ShoppingItem> products = new ArrayList<ShoppingItem>();


    public InventorySyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        JsonReader reader;
        try {
            URL url = new URL(Constants.INVENTORYLIST_URL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
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
            fillDatabases();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            return;
        }
    }

    public void fillDatabases(){
        if(products.size()==0)
            return;
        for(ShoppingItem one :products) {
            String category = one.category;
            ContentValues categoryValues = new ContentValues();

            categoryValues.put(ShoppingContract.CategoryEntry.COLUMN_NAME, category);
            Uri uri = getContext().getContentResolver().insert(ShoppingContract.CategoryEntry.CONTENT_URI, categoryValues);
            long category_id = Long.valueOf(uri.getLastPathSegment());
            Log.i(LOG_TAG, "" + category_id);
            ArrayList<ShoppingItem.Food> foods = (ArrayList) one.food;
            //Vector is synchronized
            Vector<ContentValues> cVVector = new Vector<ContentValues>(foods.size());
            for (ShoppingItem.Food food : foods) {
                ContentValues foodValues = new ContentValues();
                //Need to optimize
                //generate too many object;
                //foodValues.clear();
                foodValues.put(ShoppingContract.InventoryEntry.COLUMN_NAME, food.name);
                foodValues.put(ShoppingContract.InventoryEntry.COLUMN_CATEGORY_ID, category_id);
                foodValues.put(ShoppingContract.InventoryEntry.COLUMN_CALORIE, food.nutrition.calories);
                foodValues.put(ShoppingContract.InventoryEntry.COLUMN_CARBOH, food.nutrition.carbohydrate);
                foodValues.put(ShoppingContract.InventoryEntry.COLUMN_FAT, food.nutrition.fat);
                Log.i(LOG_TAG, "" + food.nutrition.fat);
                cVVector.add(foodValues);
            }

            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                getContext().getContentResolver().bulkInsert(ShoppingContract.InventoryEntry.CONTENT_URI, cvArray);
            }
        }
        notifyInventory();
    }


    private void notifyInventory() {
        Context context = getContext();
        //checking the last update and notify if it' the first of the day
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String displayNotificationsKey = context.getString(R.string.pref_enable_notifications_key);
        boolean displayNotifications = prefs.getBoolean(displayNotificationsKey,
                Boolean.parseBoolean(context.getString(R.string.pref_enable_notifications_default)));

        if ( displayNotifications ) {

            String lastNotificationKey = context.getString(R.string.pref_last_notification);
            long lastSync = prefs.getLong(lastNotificationKey, 0);
            String title = context.getString(R.string.app_name);
            String contentText = "New data updated";
            Resources resources = context.getResources();

            if (System.currentTimeMillis() - lastSync >= SYNC_INTERVAL) {

                    // NotificationCompatBuilder is a very convenient way to build backward-compatible
                    // notifications.  Just throw in some data.
                Log.i(LOG_TAG, "notification");
                Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_contact_picture);
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getContext())
                                    //.setColor(resources.getColor(R.color.light_blue))
                                    .setSmallIcon(R.drawable.ic_launcher)
                                    //.setLargeIcon(R.drawable.ic_launcher)
                                    .setContentTitle(title)
                                    .setContentText(contentText);

                    // Make something interesting happen when the user clicks on the notification.
                    // In this case, opening the app is sufficient.
                    Intent resultIntent = new Intent(context, ShoppingActivity.class);

                    // The stack builder object will contain an artificial back stack for the
                    // started Activity.
                    // This ensures that navigating backward from the Activity leads out of
                    // your application to the Home screen.
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    mBuilder.setContentIntent(resultPendingIntent);

                    NotificationManager mNotificationManager =
                            (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    // WEATHER_NOTIFICATION_ID allows you to update the notification later on.
                    mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

                    //refreshing last sync
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putLong(lastNotificationKey, System.currentTimeMillis());
                    editor.commit();
                }
        }
    }




    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        InventorySyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
