package bo.univalleSucre.android.sadowsound.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.PaintDrawable;
import android.graphics.Path;
import android.widget.TextView;

public class ArrowedText extends TextView {
	/**
	 * Context this view uses.
	 */
	Context mContext;
	/**
	 * Display density of the device.
	 */
	float mDensity;
	/**
	 * Color of the arrow on the left side.
	 */
	int mArrowColor;
	/**
	 * Controls the width of the drawn arrow in DIP.
	 */
	float mArrowWidth;
	/**
	 * 'padding' space (used left side) for the arrow to consume in DIP.
	 */
	float mArrowPadding;

	public ArrowedText(Context context) {
		super(context);
		mContext = context;
		mDensity = context.getResources().getDisplayMetrics().density;
	}

	/**
	 * Configures the width of the arrow.
	 */
	public void setArrowWidthDIP(float w) {
		mArrowWidth = w * mDensity;
	}

	/**
	 * Configures how much space the arrow uses on the left side.
	 */
	public void setArrowPaddingDIP(float p) {
		mArrowPadding = p * mDensity;
	}

	/**
	 * Configures the padding of this view in DIP.
	 */
	public void setPaddingDIP(float left, float top, float right, float bottom) {
		setPadding((int)(left*mDensity),
				   (int)(top*mDensity),
				   (int)(right*mDensity),
				   (int)(bottom*mDensity));
	}

	/**
	 * Configures the minimal width of this view in DIP.
	 */
	public void setMinWidthDIP(float w) {
		setMinWidth((int)(w*mDensity));
	}

	/**
	 * Set arrow and background color of this view.
	 */
	public void setColors(int colA, int colB) {
		PaintDrawable bg = new PaintDrawable(colB);
		setBackgroundDrawable(bg);
		mArrowColor = colA;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(mArrowColor);
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);

		int h = canvas.getHeight();
	    float m = h/2.0f;
		Path path = new Path();
		path.moveTo(0, 0);
		path.lineTo(mArrowPadding, 0);
		path.lineTo(mArrowPadding + mArrowWidth, m);
		path.lineTo(mArrowPadding, h);
		path.lineTo(0, h);
		path.lineTo(0,0);
		path.close();
		canvas.drawPath(path, paint);
		super.onDraw(canvas);
	}
}
