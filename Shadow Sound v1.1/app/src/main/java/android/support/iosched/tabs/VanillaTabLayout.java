package android.support.iosched.tabs;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;


/**
 * Envoltorio sencillo para SlidingTabLayout que se encarga de establecer valores predeterminados
 * razonables para cada plataforma.
 */
public class VanillaTabLayout extends SlidingTabLayout {

	public VanillaTabLayout(Context context) {
		this(context, null);
	}

	public VanillaTabLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public VanillaTabLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setSelectedIndicatorColors(context.getResources().getColor(bo.univalleSucre.android.sadowsound.R.color.tabs_active_indicator));
		setDistributeEvenly(false);
	}

	/**
	 * Sobreescritura de los colores de texto por defecto.
	 */
	@Override
	protected TextView createDefaultTabView(Context context) {
		TextView view = super.createDefaultTabView(context);
		view.setTextColor(getResources().getColorStateList(bo.univalleSucre.android.sadowsound.R.color.tab_text_selector));
		view.setBackgroundResource(bo.univalleSucre.android.sadowsound.R.drawable.unbound_ripple_light);
		view.setMaxLines(1);
		view.setEllipsize(TextUtils.TruncateAt.END);
		view.setTextSize(14);
		return view;
	}

}
