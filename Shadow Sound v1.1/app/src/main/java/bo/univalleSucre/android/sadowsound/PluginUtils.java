package bo.univalleSucre.android.sadowsound;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Common plugin utilities and constants reside here.
 * Note that public `action` and `extra` strings are common over all plugins, so please keep them in sync.
 */

public class PluginUtils {

    private PluginUtils() {
    }

    // these actions are for passing between main player and plugins
    public static final String ACTION_REQUEST_PLUGIN_PARAMS = "ch.univalleSucre.android.sadowsound.action.REQUEST_PLUGIN_PARAMS"; // broadcast
    public static final String ACTION_HANDLE_PLUGIN_PARAMS = "ch.univalleSucre.android.sadowsound.action.HANDLE_PLUGIN_PARAMS"; // answer
    public static final String ACTION_LAUNCH_PLUGIN = "ch.univalleSucre.android.sadowsound.action.LAUNCH_PLUGIN"; // targeted at selected by user

    // these are used by plugins to describe themselves
    public static final String EXTRA_PARAM_PLUGIN_NAME = "ch.univalleSucre.android.sadowsound.extra.PLUGIN_NAME";
    public static final String EXTRA_PARAM_PLUGIN_APP = "ch.univalleSucre.android.sadowsound.extra.PLUGIN_APP";
    public static final String EXTRA_PARAM_PLUGIN_DESC = "ch.univalleSucre.android.sadowsound.extra.PLUGIN_DESC";

    // this is passed to plugin when it is selected by user
    public static final String EXTRA_PARAM_URI = "ch.univalleSucre.android.sadowsound.extra.URI";
    public static final String EXTRA_PARAM_SONG_TITLE = "ch.univalleSucre.android.sadowsound.extra.SONG_TITLE";
    public static final String EXTRA_PARAM_SONG_ALBUM = "ch.univalleSucre.android.sadowsound.extra.SONG_ALBUM";
    public static final String EXTRA_PARAM_SONG_ARTIST = "ch.univalleSucre.android.sadowsound.extra.SONG_ARTIST";

    static final String EXTRA_PLUGIN_MAP = "ch.univalleSucre.android.sadowsound.internal.extra.PLUGIN_MAP";

	/**
	 * Find all sadowsound plugins by their very specific broadcast receiver intent.
	 * @param ctx context to use when resolving packages.
	 * @return list of all resolved plugins, may be empty but never null
	 */
	@NonNull
	private static List<ResolveInfo> resolvePlugins(Context ctx) {
		PackageManager pm = ctx.getPackageManager();
		Intent filter = new Intent(ACTION_REQUEST_PLUGIN_PARAMS);
		return pm.queryBroadcastReceivers(filter, PackageManager.MATCH_DISABLED_COMPONENTS);
	}

	/**
	 * Checks whether does any of sadowsound plugins exist on this Android system
	 * @param ctx context to use when resolving packages
	 * @return true if at least one plugin is installed, false otherwise
	 */
    public static boolean checkPlugins(Context ctx) {
        return !resolvePlugins(ctx).isEmpty();
    }

	/**
	 * Same as {@link #resolvePlugins(Context)} but in a map convenient for showing in dialogs.
	 * @param ctx ctx context to use when resolving packages
	 * @return all sadowsound plugins in a map App Name <-> App Info
	 */
	@NonNull
	public static Map<String, ApplicationInfo> getPluginMap(Context ctx) {
		PackageManager pm = ctx.getPackageManager();
		List<ResolveInfo> resolved = resolvePlugins(ctx);

		Map<String, ApplicationInfo> pluginMap = new HashMap<>(resolved.size());
		for (ResolveInfo ri : resolved) {
			ApplicationInfo appInfo = ri.activityInfo.applicationInfo;
			pluginMap.put(appInfo.loadLabel(pm).toString(), appInfo);
		}
		return pluginMap;
	}
}
