package bo.univalleSucre.android.sadowsound;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageButton;


public class VanillaImageButton extends ImageButton {

	private Context mContext;
	private static int mNormalTint;
	private static int mActiveTint;

	public VanillaImageButton(Context context) {
		this(context, null);
	}

	public VanillaImageButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public VanillaImageButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		mNormalTint = fetchAttrColor(R.attr.controls_normal);
		mActiveTint = fetchAttrColor(R.attr.controls_active);

		updateImageTint(-1);
	}

	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);
		this.updateImageTint(resId);
	}

	private void updateImageTint(int resHint) {
		int filterColor = mNormalTint;

		switch (resHint) {
			case R.drawable.repeat_active:
			case R.drawable.repeat_current_active:
			case R.drawable.stop_current_active:
			case R.drawable.shuffle_active:
			case R.drawable.shuffle_album_active:
			case R.drawable.random_active:
				filterColor = mActiveTint;
		}

		this.setColorFilter(filterColor);
	}

	private int fetchAttrColor(int attr) {
		TypedValue typedValue = new TypedValue();
		TypedArray a = mContext.obtainStyledAttributes(typedValue.data, new int[] { attr });
		int color = a.getColor(0, 0);
		a.recycle();
		return color;
	}

}
