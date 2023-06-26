package bo.univalleSucre.android.medialibrary;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.util.Log;


public class MediaMigrations {
	/**
	 * Migrate to 20180416
	 * That is: populate NAME_SORT in the new source database
	 *
	 * @param dbh the database to work on
	 * @param fromDb the name of the source database
	 * @param toDb the name of the target database
	 **/
	static void migrate_to_20180416(SQLiteDatabase dbh, String fromDb, String toDb) {
		Cursor cursor = dbh.query(fromDb, new String[]{MediaLibrary.PlaylistColumns._ID, MediaLibrary.PlaylistColumns.NAME}, null, null, null, null, null);
		while (cursor.moveToNext()) {
			long id = cursor.getLong(0);
			String name = cursor.getString(1);
			String key = MediaLibrary.keyFor(name);

			Log.v("VanillaMusic", "migrate_to_20180416 -> id="+id+", name="+name+" -> key = "+key);
			ContentValues v = new ContentValues();
			v.put(MediaLibrary.PlaylistColumns._ID, id);
			v.put(MediaLibrary.PlaylistColumns.NAME, name);
			v.put(MediaLibrary.PlaylistColumns.NAME_SORT, key);
			dbh.insert(toDb, null, v);
		}
		cursor.close();
	}

}
