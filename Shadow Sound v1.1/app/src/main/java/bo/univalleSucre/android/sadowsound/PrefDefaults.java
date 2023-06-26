package bo.univalleSucre.android.sadowsound;

import android.os.Environment;
import java.io.File;

/**
 * SharedPreference default values. Must be kept in sync with keys in res/xml/prefs_*.xml.
 */
public class PrefDefaults {
	private PrefDefaults() {
		// Private constructor to hide implicit one.
	}

	public static final Action  COVER_LONGPRESS_ACTION = Action.Nothing;
	public static final Action  COVER_PRESS_ACTION = Action.Nothing;
	public static final String  DEFAULT_ACTION_INT = "9";
	public static final String  DEFAULT_PLAYLIST_ACTION = "3";
	public static final boolean COVERLOADER_ANDROID = true;
	public static final boolean COVERLOADER_VANILLA = true;
	public static final boolean COVERLOADER_SHADOW = true;
	public static final boolean COVERLOADER_INLINE = true;
	public static final boolean COVER_ON_LOCKSCREEN = true;
	public static final boolean DISABLE_LOCKSCREEN = false;
	public static final String DISPLAY_MODE = "2";
	public static final boolean DOUBLE_TAP = false;
	public static final boolean ENABLE_SHAKE = false;
	public static final boolean HEADSET_ONLY = false;
	public static final boolean HEADSET_PAUSE = true;
	public static final int     IDLE_TIMEOUT = 3600;
	public static final int     LIBRARY_PAGE = 0;
	public static final String  NOTIFICATION_ACTION = "0";
	public static final String  NOTIFICATION_VISIBILITY = "0";
	public static final boolean NOTIFICATION_NAG = false;
	public static final boolean PLAYBACK_ON_STARTUP = false;
	public static final boolean SCROBBLE = false;
	public static final Action  SHAKE_ACTION = Action.NextSong;
	public static final int     SHAKE_THRESHOLD = 80;
	public static final boolean STOCK_BROADCAST = true;
	public static final Action  SWIPE_DOWN_ACTION = Action.Nothing;
	public static final Action  SWIPE_UP_ACTION = Action.Nothing;
	public static final String  TAB_ORDER = null;
	public static final boolean USE_IDLE_TIMEOUT = false;
	public static final boolean VISIBLE_CONTROLS = true;
	public static final boolean VISIBLE_EXTRA_INFO = false;
	public static final boolean ENABLE_TRACK_REPLAYGAIN = false;
	public static final boolean ENABLE_ALBUM_REPLAYGAIN = false;
	public static final int     REPLAYGAIN_BUMP = 75; // seek bar is 150 -> 75 == middle == 0
	public static final int     REPLAYGAIN_UNTAGGED_DEBUMP = 150; // seek bar is 150 -> == 0
	public static final boolean ENABLE_READAHEAD = false;
	public static final String  SELECTED_THEME = "7";
	public static final String  FILESYSTEM_BROWSE_START = "";
	public static final int     VOLUME_DURING_DUCKING = 50;
	public static final int     AUTOPLAYLIST_PLAYCOUNTS = 0;
	public static final boolean IGNORE_AUDIOFOCUS_LOSS = false;
	public static final boolean ENABLE_SCROLL_TO_SONG = false;
	public static final boolean QUEUE_ENABLE_SCROLL_TO_SONG = false;
	public static final boolean KEEP_SCREEN_ON = false;
	public static final String  PLAYLIST_SYNC_MODE = "0";
	public static final String  PLAYLIST_SYNC_FOLDER = (new File(Environment.getExternalStorageDirectory(), "Playlists")).getAbsolutePath();
	public static final boolean PLAYLIST_EXPORT_RELATIVE_PATHS = false;
	public static final boolean JUMP_TO_ENQUEUED_ON_PLAY = true;
	public static final boolean DISABLE_GAPLESS_PLAYBACK = false;
}
