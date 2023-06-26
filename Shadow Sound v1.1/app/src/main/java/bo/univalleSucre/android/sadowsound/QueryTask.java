package bo.univalleSucre.android.sadowsound;

import bo.univalleSucre.android.medialibrary.MediaLibrary;

import android.content.Context;
import android.database.Cursor;

/**
 * Represents a pending query.
 */
public class QueryTask {
	public final String table;
	public final String[] projection;
	public final String selection;
	public final String[] selectionArgs;
	public String sortOrder;

	/**
	 * Used for {@link SongTimeline#addSongs(android.content.Context, QueryTask)}.
	 * One of SongTimeline.MODE_*.
	 */
	public int mode;

	/**
	 * Type of the group being query. One of MediaUtils.TYPE_*.
	 */
	public int type;

	/**
	 * Data. Required value depends on value of mode. See individual mode
	 * documentation for details.
	 */
	public long data;

	/**
	 * Create the tasks. All arguments are passed directly to
	 * MediaLibrary.runQuery().
	 */
	public QueryTask(String table, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		this.table = table;
		this.projection = projection;
		this.selection = selection;
		this.selectionArgs = selectionArgs;
		this.sortOrder = sortOrder;
	}

	/**
	 * Run the query. Should be called on a background thread.
	 *
	 * @param context The Context to use
	 */
	public Cursor runQuery(Context context) {
		return MediaLibrary.queryLibrary(context, table, projection, selection, selectionArgs, sortOrder);
	}
}
