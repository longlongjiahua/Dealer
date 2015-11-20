package yong.dealer.shopping.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
/**
 * Defines table and column names of database.
 */
public class ShoppingContract {

	public static final String CONTENT_AUTHORITY = "yong.dealer.shopping";

	// Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
	// the content provider.
	public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

	// Possible paths (appended to base content URI for possible URI's)

	public static final String PATH_INVENTORY = "inventory";
	public static final String PATH_CATEGORY = "category";

	// To make it easy to query for the exact date, we normalize all dates that go into
	// the database to the start of the the Julian day at UTC.
	/*
	   public interface BaseColumns{
	   public static final String _ID = "_id";
	   public static final String _COUNT = "_count";
	   }
	   Using a column named _id (the constant value of BaseColumns._ID) is required by CursorAdapter,
	   implementations of a ContentProvider and other places where you hand off a Cursor to the Android platform to do things for you.
	    For example, the adapter of a ListView uses the _ID column to give you the unique ID of the list item clicked in OnItemClickListener.onItemClick(),
	    without you having to explicitly specify what your ID column is every time.
	 */

	/*
	   calorie calories200(837 kJ)10%
	   carboh carbohydrate186(779 kJ) 
	   fat 6.2(26.0 kJ) 
	   protein 8.2(34.3 kJ)
	/* Inner class that defines the table contents of the shopping item table */

	public static final class CategoryEntry implements BaseColumns {

		public static final Uri CONTENT_URI =
			BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORY).build();
		////two Content Types: ContentResolver.CURSOR_ITEM_BASE_TYPE for single records
		//ContentResolver.CURSOR_DIR_BASE_TYPE for multiple records

		public static final String CONTENT_TYPE =
			ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;
		public static final String CONTENT_ITEM_TYPE =
			ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;

		public static final String TABLE_NAME = "category";
		// Weather id as returned by API, to identify the icon to be used
		public static final String COLUMN_NAME = "type";

		public static Uri buildCategoryUri(long id) {
			return ContentUris.withAppendedId(CONTENT_URI, id);
		}

		public static long getDateFromUri(Uri uri) {
			return Long.parseLong(uri.getPathSegments().get(1));
		}
	}

	public static final class InventoryEntry implements BaseColumns {

		public static final Uri CONTENT_URI =
			BASE_CONTENT_URI.buildUpon().appendPath(PATH_INVENTORY).build();
		////two Content Types: ContentResolver.CURSOR_ITEM_BASE_TYPE for single records
		//ContentResolver.CURSOR_DIR_BASE_TYPE for multiple records

		public static final String CONTENT_TYPE =
			ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;
		public static final String CONTENT_ITEM_TYPE =
			ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

		public static final String TABLE_NAME = "inventory";
		// Weather id as returned by API, to identify the icon to be used
		public static final String COLUMN_CATEGORY_ID = "category_id";
		public static final String COLUMN_NAME = "name";
		public static final String COLUMN_SHORT_DESC = "short_desc";
		public static final String COLUMN_CALORIE = "calorie";
		public static final String COLUMN_CARBOH = "carbohydrate";
		public static final String COLUMN_FAT = "fat";
		public static final String COLUMN_PROTEIN = "protein";


		


		public static Uri buildInventoryUri(int id) {
			return ContentUris.withAppendedId(CONTENT_URI, id);
		}

		public static Uri buildInventoryWithInventoryID(int id) {

			return CONTENT_URI.buildUpon()
				.appendQueryParameter(InventoryEntry._ID, new Integer(id).toString()).build();
		}

		public static long getInventoryIDFromUri(Uri uri) {
			return Long.parseLong(uri.getPathSegments().get(0));
		}

		public static long getInventoryFromUri(Uri uri) {
			return Long.parseLong(uri.getPathSegments().get(2));
		}
		public static int getCategoryIDFromUri(Uri uri){
			return Integer.parseInt(uri.getPathSegments().get(1));
		}
	}
}
