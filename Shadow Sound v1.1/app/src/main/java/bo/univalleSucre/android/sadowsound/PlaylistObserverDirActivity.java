package bo.univalleSucre.android.sadowsound;

import android.os.Bundle;
import android.content.SharedPreferences;

import java.io.File;
import java.util.ArrayList;

public class PlaylistObserverDirActivity extends FolderPickerActivity {

	@Override  
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.filebrowser_start);

		// Start at currently configured directory.
		String current = SharedPrefHelper.getSettings(this).getString(PrefKeys.PLAYLIST_SYNC_FOLDER, PrefDefaults.PLAYLIST_SYNC_FOLDER);
		setCurrentDir(new File(current));
	}


	@Override
	public void onFolderPicked(File directory, ArrayList<String> a, ArrayList<String> b) {
		SharedPreferences.Editor editor = SharedPrefHelper.getSettings(this).edit();
		editor.putString(PrefKeys.PLAYLIST_SYNC_FOLDER, directory.getAbsolutePath());
		editor.apply();
		finish();
	}

}
