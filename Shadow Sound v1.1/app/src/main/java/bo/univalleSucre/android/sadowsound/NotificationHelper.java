package bo.univalleSucre.android.sadowsound;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;


public class NotificationHelper {
	/**
	 * The notification manager instance we are using.
	 */
	private NotificationManager mNotificationManager;
	/**
	 * The name of the channel to use for these notifications.
	 */
	private String mChannelId;

	/**
	 * Creates and returns a new NotificationHelper.
	 *
	 * @param context the context to use.
	 * @param channelId the ID of the notification channel to create.
	 * @param name the user visible name of the channel.
	 */
	public NotificationHelper(Context context, String channelId, String name) {
		mNotificationManager = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
		mChannelId = channelId;

		NotificationChannel channel = new NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_LOW);
		mNotificationManager.createNotificationChannel(channel);
	}

	/**
	 * Returns a new NotificationCompat.Builder.
	 *
	 * @param context the context to use
	 */
	public NotificationCompat.Builder getNewBuilder(Context context) {
		return new NotificationCompat.Builder(context, mChannelId);
	}

	/**
	 * Post a notification to be shown in the status bar.
	 *
	 * @param id the id of this notification.
	 * @param notification the notification to display.
	 */
	public void notify(int id, Notification notification) {
		mNotificationManager.notify(id, notification);
	}

	/**
	 * Cancels a notification.
	 *
	 * @param id the id to cancel
	 */
	public void cancel(int id) {
		mNotificationManager.cancel(id);
	}
}
