package bo.univalleSucre.android.sadowsound;

import android.widget.BaseAdapter;

/**
 * Abstract adapter that implements sorting for its data.
 */
public abstract class SortableAdapter extends BaseAdapter {
    /**
     * The human-readable descriptions for each sort mode.
     */
    int[] mSortEntries;
    /**
     * The index of the current sort mode in mSortValues, or
     * the inverse of the index (in which case sort should be descending
     * instead of ascending).
     */
    int mSortMode;

    /**
     * Return the available sort modes for this adapter.
     *
     * @return An array containing the resource ids of the sort mode strings.
     */
    public int[] getSortEntries() {
        return mSortEntries;
    }

    /**
     * Set the sorting mode. The adapter should be re-queried after changing
     * this.
     *
     * @param mode The index of the sort mode in the sort entries array. If this
     * is negative, the inverse of the index will be used and sort order will
     * be reversed.
     */
    public void setSortMode(int mode) {
        mSortMode = getIndexForSortMode(mode) < mSortEntries.length ? mode : 0;
    }

    /**
     * Return the current sort mode set on this adapter.
     * This is the index or the bitwise inverse of the index if the sort is descending.
     */
    public int getSortMode() {
        return mSortMode;
    }

    /**
     * Return the current sort mode index.
     */
    public int getSortModeIndex() {
        return getIndexForSortMode(mSortMode);
    }

    /**
     * Return if the current sort mode is reversed.
     */
    public boolean isSortDescending() {
        return mSortMode < 0;
    }

    /**
     * Returns the sort mode that should be used if no preference is saved. This
     * may very based on the active limiter.
     */
    abstract public int getDefaultSortMode();

    /**
     * Return the key used for loading/saving the sort mode for this adapter.
     */
    abstract public String getSortSettingsKey();

    private int getIndexForSortMode(int mode) {
        return mode < 0 ? ~mode : mode; // 'negative' modes are actually inverted indexes
    }
}
