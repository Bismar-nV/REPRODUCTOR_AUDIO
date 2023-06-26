package bo.univalleSucre.android.sadowsound;

import bo.univalleSucre.android.medialibrary.MediaLibrary;

import android.content.Context;
import android.database.Cursor;
import java.util.ArrayList;

public class PlayCountsHelper {

	public PlayCountsHelper() {
	}

	/**
	 * Counts this song object as 'played' or 'skipped'
	 */
	public static void countSong(Context context, Song song, boolean played) {
		final long id = Song.getId(song);
		MediaLibrary.updateSongPlayCounts(context, id, played);
	}



	/**
	 * Returns a sorted array list of most often listen song ids
	 */
	public static ArrayList<Long> getTopSongs(Context context, int limit) {
		ArrayList<Long> payload = new ArrayList<Long>();
		Cursor cursor = MediaLibrary.queryLibrary(context, MediaLibrary.TABLE_SONGS, new String[]{ MediaLibrary.SongColumns._ID }, MediaLibrary.SongColumns.PLAYCOUNT+" > 0", null, MediaLibrary.SongColumns.PLAYCOUNT+" DESC");
		while (cursor.moveToNext() && limit > 0) {
			payload.add(cursor.getLong(0));
			limit--;
		}
		cursor.close();
		return payload;
	}

}
