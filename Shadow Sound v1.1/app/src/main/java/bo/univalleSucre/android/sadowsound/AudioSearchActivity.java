package bo.univalleSucre.android.sadowsound;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Window;

public class AudioSearchActivity extends PlaybackActivity {
	/**
	 * The AsyncTask used to perform the query.
	 */
	private AudioSearchWorker mWorker;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		Intent intent = getIntent();
		String action = (intent == null ? null : intent.getAction());
		if (action == null || !action.equals(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH)) {
			finish();
			return;
		}

		if (PermissionRequestActivity.requestPermissions(this, intent)) {
			finish();
			return;
		}

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Fixme: add a progress dialog.

		String query = intent.getExtras().getString(SearchManager.QUERY);
		mWorker = new AudioSearchWorker();
		mWorker.execute(query);
	}

	/**
	 * Called by AudioSearchWorker after the search completed.
	 */
	private void onSearchCompleted() {
		finish();
	}

	/**
	 * Async worker class used to perform the search
	 */
	private class AudioSearchWorker extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... search) {
			Context ctx = getApplicationContext();
			MediaAdapter adapter = new MediaAdapter(ctx, MediaUtils.TYPE_SONG, null, null);
			adapter.setFilter(search[0]);

			QueryTask query = adapter.buildSongQuery(Song.FILLED_PROJECTION);
			query.mode = SongTimeline.MODE_PLAY;

			PlaybackService service = PlaybackService.get(ctx);
			service.pause();
			service.setShuffleMode(SongTimeline.SHUFFLE_ALBUMS);
			service.emptyQueue();
			service.addSongs(query);
			if (service.getTimelineLength() > 0) {
				service.play();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void nil) {
			onSearchCompleted();
		}
	}

}
