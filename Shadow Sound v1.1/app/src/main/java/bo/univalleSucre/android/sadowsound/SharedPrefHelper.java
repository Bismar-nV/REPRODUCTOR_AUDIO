package bo.univalleSucre.android.sadowsound;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class SharedPrefHelper {

	/**
	 * Cached app-wide SharedPreferences instance.
	 */
	private static SharedPreferences sSettings;

	private SharedPrefHelper(){};

	/**
	 * Return the SharedPreferences instance containing
	 * Application settings.
	 */
	public static SharedPreferences getSettings(Context context)
	{
		if (sSettings == null)
			sSettings = PreferenceManager.getDefaultSharedPreferences(context);
		return sSettings;
	}


}
