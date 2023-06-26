package bo.univalleSucre.android.sadowsound;

public interface TimelineCallback {
	/**
	 * Called when the song timeline position/size has changed
	 */
	void onPositionInfoChanged();
	/**
	 * The library contents changed and should be invalidated
	 */
	void onMediaChange();
	/**
	 * Notification about a change in the timeline
	 */
	void onTimelineChanged();
	/**
	 * Updates song at 'delta'
	 */
	void replaceSong(int delta, Song song);
	/**
	 * Sets the currently active song
	 */
	void setSong(long uptime, Song song);
	/**
	 * Sets the current playback state
	 */
	void setState(long uptime, int state);
	/**
	 * The view/activity should re-create itself due to a theme change
	 */
	void recreate();
}
