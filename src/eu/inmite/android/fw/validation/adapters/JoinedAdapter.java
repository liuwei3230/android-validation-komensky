package eu.inmite.android.fw.validation.adapters;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;
import eu.inmite.android.fw.validation.annotations.Joined;
import eu.inmite.android.fw.validation.exception.UiValidationException;
import eu.inmite.android.fw.validation.iface.IFieldAdapter;

import java.lang.annotation.Annotation;

/**
 * @author Tomas Vondracek
 */
public class JoinedAdapter implements IFieldAdapter<String[]> {

	@Override
	public String[] getFieldValue(Annotation annotation, Object target, View fieldView) {
		final int[] viewIds = ((Joined) annotation).value();
		final View[] views;

		if (target instanceof Activity) {
			views = new View[viewIds.length];
			Activity activity = (Activity) target;
			for (int i = 0; i < viewIds.length; i++) {
				int id = viewIds[i];
				views[i] = activity.findViewById(id);
			}
		} else if (target instanceof Fragment) {
			Fragment fragment = (Fragment) target;
			View view = fragment.getView();
			views = findViewsInView(viewIds, view);
		} else if (target instanceof View) {
			views = findViewsInView(viewIds, (View) target);
		} else {
			throw new UiValidationException("unknown target " + target);
		}

		final String[] fieldValues = new String[views.length];
		for (int i = 0; i < views.length; i++) {
			View view = views[i];
			fieldValues[i] = valueFromView(view);
		}
		return fieldValues;
	}

	private String valueFromView(View view) {
		IFieldAdapter<String> adapter = FieldAdapterFactory.getAdapterForField(view, null);
		if (adapter != null) {
			return adapter.getFieldValue(null, null, view);
		}
		return null;
	}

	private View[] findViewsInView(int[] viewIds, View target) {
		View[] views = new View[viewIds.length];
		for (int i = 0; i < viewIds.length; i++) {
			int id = viewIds[i];
			views[i] = target.findViewById(id);
		}
		return views;
	}
}
