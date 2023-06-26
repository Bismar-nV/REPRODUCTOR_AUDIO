package bo.univalleSucre.android.sadowsound;

import bo.univalleSucre.android.medialibrary.MediaLibrary;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

/**
 * CursorAdapter backed by MediaLibrary playlists.
 */
public class PlaylistAdapter extends CursorAdapter implements Handler.Callback {

	private static final String[] PROJECTION = new String[] {
		MediaLibrary.PlaylistSongColumns._ID,
		MediaLibrary.PlaylistSongColumns.SONG_ID,
		MediaLibrary.SongColumns.TITLE,
		MediaLibrary.AlbumColumns.ALBUM,
		MediaLibrary.ContributorColumns.ARTIST,
		MediaLibrary.SongColumns.ALBUM_ID,
		MediaLibrary.SongColumns.DURATION,
	};

	private final Context mContext;
	private final Handler mWorkerHandler;
	private final Handler mUiHandler;
	private final LayoutInflater mInflater;

	private long mPlaylistId;

	private boolean mEditable;

	/**
	 * Create a playlist adapter.
	 *
	 * @param context A context to use.
	 * @param worker A looper running a worker thread (to run queries on).
	 */
	public PlaylistAdapter(Context context, Looper worker)
	{
		super(context, null, false);

		mContext = context;
		mUiHandler = new Handler(this);
		mWorkerHandler = new Handler(worker, this);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * Set the id of the backing playlist.
	 *
	 * @param id The id of a playlist.
	 */
	public void setPlaylistId(long id)
	{
		mPlaylistId = id;
		mWorkerHandler.sendEmptyMessage(MSG_RUN_QUERY);
	}

	/**
	 * Enabled or disable edit mode. Edit mode adds a drag grabber to the left
	 * side a views and a delete button to the right side of views.
	 *
	 * @param editable True to enable edit mode.
	 */
	public void setEditable(boolean editable)
	{
		mEditable = editable;
		notifyDataSetInvalidated();
	}

	/**
	 * Update the values in the given view.
	 */
	@Override
	public void bindView(View view, Context context, Cursor cursor)
	{
		DraggableRow dview = (DraggableRow)view;
		final String title = cursor.getString(2);
		final String album = cursor.getString(3);
		final String artist = cursor.getString(4);

		ViewHolder holder = new ViewHolder();
		holder.title = title;
		holder.id = cursor.getLong(1);

		dview.setupLayout(DraggableRow.LAYOUT_DRAGGABLE);
		dview.showDragger(mEditable);
		dview.setText(title, album+", "+artist);
		dview.setTag(holder);
		dview.setDuration(cursor.getLong(6));

		LazyCoverView cover = dview.getCoverView();
		cover.setCover(MediaUtils.TYPE_ALBUM, cursor.getLong(5), null);
	}

	/**
	 * Generate a new view.
	 */
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent)
	{
		return mInflater.inflate(R.layout.draggable_row, parent, false);
	}

	/**
	 * Re-run the query. Should be run on worker thread.
	 */
	public static final int MSG_RUN_QUERY = 1;
	/**
	 * Update the cursor. Must be run on UI thread.
	 */
	public static final int MSG_UPDATE_CURSOR = 2;

	@Override
	public boolean handleMessage(Message message)
	{
		switch (message.what) {
		case MSG_RUN_QUERY: {
			Cursor cursor = runQuery();
			mUiHandler.sendMessage(mUiHandler.obtainMessage(MSG_UPDATE_CURSOR, cursor));
			break;
		}
		case MSG_UPDATE_CURSOR:
			changeCursor((Cursor)message.obj);
			break;
		default:
			return false;
		}

		return true;
	}

	/**
	 * Query the playlist songs.
	 *
	 * @return The resulting cursor.
	 */
	private Cursor runQuery()
	{
		QueryTask query = MediaUtils.buildPlaylistQuery(mPlaylistId, PROJECTION);
		return query.runQuery(mContext);
	}

	/**
	 * Moves a song in the playlist
	 * @param from original position of item
	 * @param to destination of item
	 **/
	public void moveItem(int from, int to)
	{
		if (from == to)
			// easy mode
			return;

		int count = getCount();
		if (to >= count || from >= count)
			// this can happen when the adapter changes during the drag
			return;

		MediaLibrary.movePlaylistItem(mContext, getItemId(from), getItemId(to));
		changeCursor(runQuery());
	}

	public void removeItem(int position) {
		MediaLibrary.removeFromPlaylist(mContext, MediaLibrary.PlaylistSongColumns._ID+"="+getItemId(position), null);
		mUiHandler.sendEmptyMessage(MSG_RUN_QUERY);
	}


}
