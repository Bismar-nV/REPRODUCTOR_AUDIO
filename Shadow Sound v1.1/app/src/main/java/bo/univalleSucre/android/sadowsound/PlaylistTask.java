
package bo.univalleSucre.android.sadowsound;

import java.util.ArrayList;

/**
 * Represents a pending playlist manipulation
 */
public class PlaylistTask {

	/**
	 * ID of this playlist to manipulate
	 */
	public long playlistId;
	/**
	 * Name of this playlist (used for the toast message)
	 */
	public String name;
	/**
	 * Populate playlist using this QueryTask
	 */
	public QueryTask query;
	/**
	 * Populate playlist using this audioIds
	 */
	public ArrayList<Long> audioIds;


	public PlaylistTask(long playlistId, String name) {
		this.playlistId = playlistId;
		this.name = name;
	}

}
