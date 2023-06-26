package bo.univalleSucre.android.sadowsound.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.ActionProvider;
import android.view.SubMenu;
import android.view.View;

public class FancyMenuItem implements MenuItem {
	private int mId;
	private int mIconRes;
	private CharSequence mTitle;
	private Intent mIntent;
	private boolean mSpacer;
	private Drawable mIcon;
	private Context mContext;

	FancyMenuItem(Context context, int id) {
		mContext = context;
		mId = id;
	}

	/**
	 * Returns whether or not this is a spacer
	 *
	 * @return true if this is a spacer
	 */
	public boolean isSpacer() {
		return mSpacer;
	}

	public FancyMenuItem setIsSpacer(boolean spacer) {
		mSpacer = spacer;
		return this;
	}

	@Override
	public CharSequence getTitle() {
		return mTitle;
	}

	@Override
	public Drawable getIcon() {
		if (mIconRes != 0)
			return mContext.getResources().getDrawable(mIconRes);
		return mIcon;
	}

	@Override
	public FancyMenuItem setIcon(Drawable drawable) {
		mIcon = drawable;
		return this;
	}

	@Override
	public FancyMenuItem setIcon(int res) {
		mIconRes = res;
		return this;
	}

	@Override
	public FancyMenuItem setIntent(Intent intent) {
		mIntent = intent;
		return this;
	}

	@Override
	public FancyMenuItem setTitle(CharSequence title) {
		mTitle = title;
		return this;
	}

	@Override
	public FancyMenuItem setTitle(int title) {
		return setTitle(mContext.getResources().getString(title));
	}

	@Override
	public Intent getIntent() {
		return mIntent;
	}

	@Override
	public int getItemId() {
		return mId;
	}

	// Dummy functions to satisfy MenuItem interface.
	@Override
	public ActionProvider getActionProvider() {
		return null;
	}

	@Override
	public CharSequence getTitleCondensed() {
		return null;
	}

	@Override
	public ContextMenu.ContextMenuInfo getMenuInfo() {
		return null;
	}

	@Override
	public FancyMenuItem setActionProvider(ActionProvider actionProvider) {
		return this;
	}

	@Override
	public FancyMenuItem setActionView(int view) {
		return this;
	}

	@Override
	public FancyMenuItem setAlphabeticShortcut(char alphaChar) {
		return this;
	}

	@Override
	public FancyMenuItem setCheckable(boolean checkable) {
		return this;
	}

	@Override
	public FancyMenuItem setChecked(boolean checked) {
		return this;
	}

	@Override
	public FancyMenuItem setEnabled(boolean enabled) {
		return this;
	}

	@Override
	public FancyMenuItem setNumericShortcut(char numericChar) {
		return this;
	}

	@Override
	public FancyMenuItem setOnActionExpandListener(MenuItem.OnActionExpandListener listener) {
		return this;
	}

	@Override
	public FancyMenuItem setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener menuItemClickListener) {
		return this;
	}

	@Override
	public FancyMenuItem setShortcut(char numericChar, char alphaChar) {
		return this;
	}

	@Override
	public FancyMenuItem setShowAsActionFlags(int actionEnum) {
		return this;
	}

	@Override
	public FancyMenuItem setTitleCondensed(CharSequence text) {
		return this;
	}

	@Override
	public FancyMenuItem setVisible(boolean visible) {
		return this;
	}

	@Override
	public MenuItem setActionView(View view) {
		return this;
	}

	@Override
	public SubMenu getSubMenu() {
		return null;
	}

	@Override
	public View getActionView() {
		return null;
	}

	@Override
	public boolean collapseActionView() {
		return false;
	}

	@Override
	public boolean expandActionView() {
		return false;
	}

	@Override
	public boolean hasSubMenu() {
		return false;
	}

	@Override
	public boolean isActionViewExpanded() {
		return false;
	}

	@Override
	public boolean isCheckable() {
		return false;
	}

	@Override
	public boolean isChecked() {
		return false;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isVisible() {
		return true;
	}

	@Override
	public char getAlphabeticShortcut() {
		return 0;
	}

	@Override
	public char getNumericShortcut() {
		return 0;
	}

	@Override
	public int getGroupId() {
		return 0;
	}

	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public void setShowAsAction(int actionEnum) {
	}
}
