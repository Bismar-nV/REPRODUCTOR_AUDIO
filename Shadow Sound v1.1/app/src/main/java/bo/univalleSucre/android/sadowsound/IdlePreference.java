package bo.univalleSucre.android.sadowsound;

//import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.res.Resources;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * A preference that provides a dialog with a slider for idle time.
 *
 * The slider produces a value in seconds from 60 (1 minute) to 21600
 * (6 hours). The values range on an approximately exponential scale.
 */
public class IdlePreference extends DialogPreference implements SeekBar.OnSeekBarChangeListener {
	private static final int DEFAULT_VALUE = 3600;
	private static final int MIN = 60;
	private static final int MAX = 21600;

	/**
	 * The current idle timeout displayed on the slider. Will not be persisted
	 * until the dialog is closed.
	 */
	private int mValue;
	/**
	 * The view in which the value is displayed.
	 */
	private TextView mValueText;

	public IdlePreference(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	@Override
	public CharSequence getSummary()
	{
		return formatTime(getPersistedInt(DEFAULT_VALUE));
	}


	@Override
	protected View onCreateDialogView()
	{
		View view = super.onCreateDialogView();

		mValue = getPersistedInt(DEFAULT_VALUE);

		mValueText = (TextView)view.findViewById(R.id.value);

		SeekBar seekBar = (SeekBar)view.findViewById(R.id.seek_bar);
		seekBar.setMax(1000);
		seekBar.setProgress((int)(Math.pow((float)(mValue - MIN) / (MAX - MIN), 0.25f) * 1000));
		seekBar.setOnSeekBarChangeListener(this);

		updateText();

		return view;
	}


	/**
	 * Format seconds into a human-readable time description.
	 *
	 * @param value The time, in seconds.
	 * @return A human-readable string, such as "1 hour, 21 minutes"
	 */
	private String formatTime(int value)
	{
		Resources res = getContext().getResources();
		StringBuilder text = new StringBuilder();
		if (value >= 3600) {
			int hours = value / 3600;
			text.append(res.getQuantityString(R.plurals.hours, hours, hours));
			text.append(", ");
			int minutes = value / 60 - hours * 60;
			text.append(res.getQuantityString(R.plurals.minutes, minutes, minutes));
		} else {
			int minutes = value / 60;
			text.append(res.getQuantityString(R.plurals.minutes, minutes, minutes));
			text.append(", ");
			int seconds = value - minutes * 60;
			text.append(res.getQuantityString(R.plurals.seconds, seconds, seconds));
		}
		return text.toString();
	}

	/**
	 * Update the text view with the current value.
	 */
	private void updateText()
	{
		mValueText.setText(formatTime(mValue));
	}

	@Override
	protected void onDialogClosed(boolean positiveResult)
	{
		if (positiveResult && shouldPersist()) {
			persistInt(mValue);
			notifyChanged();
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		// Approximate an exponential curve with x^4. Produces a value from MIN-MAX.
		if (fromUser) {
			float value = seekBar.getProgress() / 1000.0f;
			value *= value;
			value *= value;
			mValue = (int)(value * (MAX - MIN)) + MIN;
			updateText();
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar)
	{
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar)
	{
	}
}
