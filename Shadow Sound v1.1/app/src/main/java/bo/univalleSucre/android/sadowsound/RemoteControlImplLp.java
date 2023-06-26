package bo.univalleSucre.android.sadowsound;

import android.annotation.TargetApi;
import android.content.Context;

@TargetApi(21)
public class RemoteControlImplLp implements RemoteControl.Client {
	/**
	 * This is just a placeholder implementation: On API 21, media buttons are handled in MediaSessionTracker.
	 */
	public RemoteControlImplLp(Context context) {
	}

	public void initializeRemote() {
	}

	public void unregisterRemote() {
	}

	public void reloadPreference() {
	}

	public void updateRemote(Song song, int state, boolean keepPaused) {
	}
}
