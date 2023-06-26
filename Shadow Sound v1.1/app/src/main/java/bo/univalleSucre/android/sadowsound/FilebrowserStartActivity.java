package bo.univalleSucre.android.sadowsound;

import android.os.Bundle;
import android.content.SharedPreferences;

import java.io.File;
import java.util.ArrayList;
public class FilebrowserStartActivity extends FolderPickerActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.filebrowser_start);

		// Make sure that we display the current selection
		File startPath = FileUtils.getFilesystemBrowseStart(this);
		setCurrentDir(startPath);
	}


	@Override
	public void onFolderPicked(File directory, ArrayList<String> a, ArrayList<String> b) {
		SharedPreferences.Editor editor = SharedPrefHelper.getSettings(this).edit();
		editor.putString(PrefKeys.FILESYSTEM_BROWSE_START, directory.getAbsolutePath());
		editor.apply();
		finish();
	}

}
