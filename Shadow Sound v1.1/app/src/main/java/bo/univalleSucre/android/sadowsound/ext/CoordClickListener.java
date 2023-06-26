package bo.univalleSucre.android.sadowsound.ext;

import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


public class CoordClickListener
	implements
	    AdapterView.OnItemLongClickListener
	  , View.OnTouchListener {
	/**
	 * Interface to implement by the callback
	 */
	public interface Callback {
		boolean onItemLongClickWithCoords(AdapterView<?> parent, View view, int position, long id, float x, float y);
	}
	/**
	 * The registered callback consumer
	 */
	private Callback mCallback;
	/**
	 * Last X position seen.
	 */
	private float mPosX;
	/**
	 * Last Y position seen.
	 */
	private float mPosY;

	/**
	 * Create a new CoordClickListener instance
	 *
	 * @param cb the callback consumer.
	 */
	public CoordClickListener(Callback cb) {
		mCallback = cb;
	}

	/**
	 * Register a view for long click observation.
	 *
	 * @param view the view to listen for long clicks
	 */
	public void registerForOnItemLongClickListener(ListView view) {
		view.setOnItemLongClickListener(this);
		view.setOnTouchListener(this);
	}

	/**
	 * Implementation of onItemLongClickListener interface
	 */
	@Override
	public boolean onItemLongClick (AdapterView<?>parent, View view, int position, long id) {
		return mCallback.onItemLongClickWithCoords(parent, view, position, id, mPosX, mPosY);
	}

	/**
	 * Implementation of OnTouchListener interface
	 */
	@Override
	public boolean onTouch(View view, MotionEvent ev) {
		mPosX = ev.getX();
		mPosY = ev.getY();
		// Not handled: we just observe.
		return false;
	}
}
