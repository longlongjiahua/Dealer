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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.app.INotificationSideChannel;

import yong.dealer.shopping.data.ShoppingContract.InventoryEntry;
import yong.dealer.shopping.data.ShoppingContract.CategoryEntry;


/**
 * Manages a local database for weather data.
 */
public class ShoppingDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "shopping.db";

    public ShoppingDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static final String COLUMN_SHORT_DESC = "short_desc";
    public static final String COLUMN_CALORIE = "calorie";
    public static final String COLUMN_CARBOH = "carbohydrate";
    public static final String COLUMN_FAT = "fat";
    public static final String COLUMN_PROTEIN = "protein";

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold locations.  A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude
        final String SQL_CREATE_INVENTORY_TABLE = "CREATE TABLE " + InventoryEntry.TABLE_NAME + " (" +
                InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                InventoryEntry.COLUMN_CATEGORY_ID + " INTEGER, " +
                InventoryEntry.COLUMN_NAME + " TEXT  NOT NULL, " +
                InventoryEntry.COLUMN_SHORT_DESC + " TEXT, " +
                InventoryEntry.COLUMN_CALORIE + " TEXT, " +
                InventoryEntry.COLUMN_CARBOH + " TEXT, " +
                InventoryEntry.COLUMN_FAT + " TEXT " +
                InventoryEntry.COLUMN_PROTEIN + " TEXT " +

                " );";
        final String SQL_CREATE_CATEGORY_TABLE = "CREATE TABLE " + CategoryEntry.TABLE_NAME + " (" +
                CategoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CategoryEntry.COLUMN_NAME + " TEXT NOT NULL " +
                ")";


        sqLiteDatabase.execSQL(SQL_CREATE_INVENTORY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CATEGORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + InventoryEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CategoryEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
