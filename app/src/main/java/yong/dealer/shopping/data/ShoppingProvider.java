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
package yong.dealer.shopping.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import yong.dealer.shopping.data.ShoppingContract.InventoryEntry;
import yong.dealer.shopping.data.ShoppingContract.CategoryEntry;

public class ShoppingProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ShoppingDbHelper mOpenHelper;
    static final int CATEGORY =1;
    static final int INVENTORY = 10;
    static final int INVENTORY_WITH_CATEGORY = 11;
    static final int INVENTORY_WITH_ID = 12;
    //private static final SQLiteQueryBuilder sWeatherByLocationSettingQueryBuilder;
    private static final SQLiteQueryBuilder sInventoryQueryBuilder;

    static{
        sInventoryQueryBuilder = new SQLiteQueryBuilder();
        
        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sInventoryQueryBuilder.setTables(
                InventoryEntry.TABLE_NAME + " INNER JOIN " +
                        CategoryEntry.TABLE_NAME +
                        " ON " + InventoryEntry.TABLE_NAME +
                        "." + InventoryEntry.COLUMN_CATEGORY_ID +
                        " = " + CategoryEntry.TABLE_NAME +
                        InventoryEntry._ID);
    }

    //location.location_setting = ?
    private static final String sCategorySelection =
            CategoryEntry.TABLE_NAME+
                    "." + CategoryEntry.COLUMN_NAME + " = ? ";

    //location.location_setting = ? AND date >= ?
    private static final String sInventoryIDSelection =
            InventoryEntry.TABLE_NAME+
                    "." + InventoryEntry._ID + " = ? ";

    private Cursor getInventoryByID(Uri uri, String[] projection, String sortOrder) {
       Long inventoryID = InventoryEntry.getInventoryIDFromUri(uri);
        return sInventoryQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sInventoryIDSelection, //selection
                new String[]{inventoryID.toString()},  //selection argument
                null,
                null,
                sortOrder
        );
    }

    private Cursor getInventoryCategory(Uri uri, String[] projection, String sortOrder) {
        int categoryID= InventoryEntry.getCategoryIDFromUri(uri);

        return sInventoryQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sCategorySelection, //selection
                new String[]{new Integer(categoryID).toString()},  //selection argument
                null,
                null,
                sortOrder
        );
    }



    //https://www.youtube.com/watch?v=jTPYie9A7iI about urimatcher
    static UriMatcher buildUriMatcher() {
        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ShoppingContract.CONTENT_AUTHORITY;
        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, ShoppingContract.PATH_INVENTORY,INVENTORY);
        matcher.addURI(authority, ShoppingContract.PATH_INVENTORY + "/*", INVENTORY_WITH_CATEGORY);
        matcher.addURI(authority, ShoppingContract.PATH_INVENTORY + "/#", INVENTORY_WITH_ID);

        matcher.addURI(authority, ShoppingContract.PATH_CATEGORY, CATEGORY);
        // * maches any characters, # maches any numbers
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new ShoppingDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case INVENTORY_WITH_ID:
                return InventoryEntry.CONTENT_ITEM_TYPE;
            case INVENTORY_WITH_CATEGORY:
                return InventoryEntry.CONTENT_TYPE;
            case INVENTORY:
                return InventoryEntry.CONTENT_TYPE;
            case CATEGORY:
                return CategoryEntry.CONTENT_TYPE;


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "inventory/#"
            case INVENTORY_WITH_CATEGORY:
            {
                retCursor = getInventoryCategory(uri, projection, sortOrder);
                break;
            }
            // "inventory/#"
            case INVENTORY_WITH_ID: {
                retCursor = getInventoryByID(uri, projection, sortOrder);
                break;
            }
            // "inventory"
            case INVENTORY: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        InventoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /*
        Student: Add the ability to insert Locations to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case INVENTORY: {
                long _id = db.insert(InventoryEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = InventoryEntry.buildInventoryUri((int)_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case CATEGORY: {
                long _id = db.insert(CategoryEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = InventoryEntry.buildInventoryUri((int)_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case INVENTORY:
                rowsDeleted = db.delete(
                        InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }


    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case INVENTORY:
                rowsUpdated = db.update(InventoryEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(InventoryEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
