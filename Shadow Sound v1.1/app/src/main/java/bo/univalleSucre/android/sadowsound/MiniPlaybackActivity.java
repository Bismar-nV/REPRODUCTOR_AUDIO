package bo.univalleSucre.android.sadowsound;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

/**
 * Compact playback activity that displays itself like a dialog. That is, the
 * window is not fullscreen but only as large as it needs to be. Includes a
 * CoverView and control buttons.
 */
public class MiniPlaybackActivity extends PlaybackActivity {
	@Override
	public void onCreate(Bundle state)
	{
		ThemeHelper.setTheme(this, R.style.PopupDialog);
		super.onCreate(state);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mini_playback);

		mCoverView = (CoverView)findViewById(R.id.cover_view);
		mCoverView.setOnClickListener(this);
		mCoverView.setup(mLooper, this, CoverBitmap.STYLE_OVERLAPPING_BOX);

		bindControlButtons();
	}

	@Override
	public void onClick(View view)
	{
		switch (view.getId()) {
		case R.id.cover_view:
			startActivity(new Intent(this, FullPlaybackActivity.class));
			finish();
			break;
		default:
			super.onClick(view);
		}
	}
}
