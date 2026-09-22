package com.chasinglemons.empeg;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.widget.ListAdapter;

public class ImageListPreference extends ListPreference {
	private int[] resourceIds = null;

	public ImageListPreference(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray typedArray = context.obtainStyledAttributes(attrs,
			R.styleable.ImageListPreference);

        TypedArray images = context.getResources().obtainTypedArray(
                typedArray.getResourceId(R.styleable.ImageListPreference_lensImages, 0));
        resourceIds = new int[images.length()];
        for (int i = 0; i < images.length(); i++) {
            resourceIds[i] = images.getResourceId(i, 0);
        }
        images.recycle();
        typedArray.recycle();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onPrepareDialogBuilder(Builder builder) {
		int index = findIndexOfValue(getSharedPreferences().getString(
			getKey(), "0"));

		ListAdapter listAdapter = new ImageArrayAdapter(getContext(),
			R.layout.listitem, getEntries(), resourceIds, index, getSharedPreferences().getString("suomi35_empeg", ""));

		// Order matters.

		builder.setTitle("Select lens color");
		
		builder.setAdapter(listAdapter, this);
		super.onPrepareDialogBuilder(builder);

	}
}
