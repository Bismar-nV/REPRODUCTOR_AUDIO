package bo.univalleSucre.android.sadowsound;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.RemoteViews;

/**
 * 2x2 widget that shows cover art in the background and playback controls in
 * a semi-transparent widget on top of the cover.
 */
public class WidgetD extends AppWidgetProvider {
	private static boolean sEnabled;

	@Override
	public void onEnabled(Context context)
	{
		sEnabled = true;
	}

	@Override
	public void onDisabled(Context context)
	{
		sEnabled = false;
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager manager, int[] ids)
	{
		Song song = null;
		int state = 0;

		if (PlaybackService.hasInstance()) {
			PlaybackService service = PlaybackService.get(context);
			song = service.getSong(0);
			state = service.getState();
		}

		sEnabled = true;
		updateWidget(context, manager, song, state);
	}

	/**
	 * Check if there are any instances of this widget placed.
	 */
	public static void checkEnabled(Context context, AppWidgetManager manager)
	{
		sEnabled = manager.getAppWidgetIds(new ComponentName(context, WidgetD.class)).length != 0;
	}

	/**
	 * Populate the widgets with the given ids with the given info.
	 *
	 * @param context A Context to use.
	 * @param manager The AppWidgetManager that will be used to update the
	 * widget.
	 * @param song The current Song in PlaybackService.
	 * @param state The current PlaybackService state.
	 */
	public static void updateWidget(Context context, AppWidgetManager manager, Song song, int state)
	{
		if (!sEnabled)
			return;

		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_d);
		Bitmap cover = null;

		if ((state & PlaybackService.FLAG_NO_MEDIA) != 0) {
			views.setViewVisibility(R.id.buttons, View.GONE);
			views.setViewVisibility(R.id.title, View.GONE);
			views.setInt(R.id.artist, "setText", R.string.no_songs);
		} else if (song == null) {
			views.setViewVisibility(R.id.buttons, View.VISIBLE);
			views.setViewVisibility(R.id.title, View.GONE);
			views.setInt(R.id.artist, "setText", R.string.app_name);
		} else {
			views.setViewVisibility(R.id.title, View.VISIBLE);
			views.setViewVisibility(R.id.buttons, View.VISIBLE);
			views.setTextViewText(R.id.title, song.title);
			views.setTextViewText(R.id.artist, song.artist);
			cover = song.getMediumCover(context);
		}

		if (cover == null) {
			views.setImageViewResource(R.id.cover, R.drawable.fallback_cover_large);
		} else {
			views.setImageViewBitmap(R.id.cover, cover);
		}

		boolean playing = (state & PlaybackService.FLAG_PLAYING) != 0;
		views.setImageViewResource(R.id.play_pause, playing ? R.drawable.pause : R.drawable.play);
		views.setImageViewResource(R.id.end_action, SongTimeline.FINISH_ICONS[PlaybackService.finishAction(state)]);
		views.setImageViewResource(R.id.shuffle, SongTimeline.SHUFFLE_ICONS[PlaybackService.shuffleMode(state)]);

		Intent intent;
		PendingIntent pendingIntent;
		int flags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME;

		intent = new Intent(context, LibraryActivity.class).setAction(Intent.ACTION_MAIN);
		pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
		views.setOnClickPendingIntent(R.id.cover, pendingIntent);

		intent = ShortcutPseudoActivity.getIntent(context, PlaybackService.ACTION_TOGGLE_PLAYBACK);
		pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
		views.setOnClickPendingIntent(R.id.play_pause, pendingIntent);

		intent = ShortcutPseudoActivity.getIntent(context, PlaybackService.ACTION_NEXT_SONG);
		pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
		views.setOnClickPendingIntent(R.id.next, pendingIntent);

		intent = ShortcutPseudoActivity.getIntent(context, PlaybackService.ACTION_PREVIOUS_SONG);
		pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
		views.setOnClickPendingIntent(R.id.previous, pendingIntent);

		intent = ShortcutPseudoActivity.getIntent(context, PlaybackService.ACTION_CYCLE_SHUFFLE);
		pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
		views.setOnClickPendingIntent(R.id.shuffle, pendingIntent);

		intent = ShortcutPseudoActivity.getIntent(context, PlaybackService.ACTION_CYCLE_REPEAT);
		pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
		views.setOnClickPendingIntent(R.id.end_action, pendingIntent);

		manager.updateAppWidget(new ComponentName(context, WidgetD.class), views);
	}
}
