package bo.univalleSucre.android.sadowsound;

import android.content.Context;


public class RemoteControl {

	/**
	 * Returns a RemoteControl.Client implementation
	 */
	public RemoteControl.Client getClient(Context context) {
		return new RemoteControlImplLp(context);
	}

	/**
	 * Interface definition of our RemoteControl API
	 */
	public interface Client {
		public void initializeRemote();
		public void unregisterRemote();
		public void reloadPreference();
		public void updateRemote(Song song, int state, boolean keepPaused);
	}
}
