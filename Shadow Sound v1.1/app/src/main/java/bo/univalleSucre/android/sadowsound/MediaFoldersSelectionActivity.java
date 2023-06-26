package bo.univalleSucre.android.sadowsound;

import bo.univalleSucre.android.medialibrary.MediaLibrary;

import android.os.Bundle;
import android.content.SharedPreferences;

import java.io.File;
import java.util.ArrayList;

public class MediaFoldersSelectionActivity extends FolderPickerActivity {

	private SharedPreferences.Editor mPrefEditor;

	@Override  
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.media_folders_header);

		MediaLibrary.Preferences prefs = MediaLibrary.getPreferences(this);
		File startPath = FileUtils.getFilesystemBrowseStart(this);

		// Setup UI and enable tritastic options.
		enableTritasticSelect(true, prefs.mediaFolders, prefs.blacklistedFolders);
		enableTritasticSpinner(true);
		// ...and jump to the folder.
		setCurrentDir(startPath);
	}


	@Override
	public void onFolderPicked(File directory, ArrayList<String> included, ArrayList<String> excluded) {
		MediaLibrary.Preferences prefs = MediaLibrary.getPreferences(this);
		prefs.mediaFolders = included;
		prefs.blacklistedFolders = excluded;
		MediaLibrary.setPreferences(this, prefs);
		finish();
	}

}
