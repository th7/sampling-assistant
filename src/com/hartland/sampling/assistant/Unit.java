package com.hartland.sampling.assistant;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class Unit extends MiscObject {

	private final boolean mIsVolume;

	public Unit(Cursor cursor) {
		super(cursor);
		for (String nonVolumeUnit : AssistantApp.NON_VOLUME_UNITS) {
			if (getName().equals(nonVolumeUnit)) {
				mIsVolume = false;
				return;
			}
		}
		mIsVolume = true;
	}

	public static class SpinnerAdapter extends ArrayAdapter<Unit> {

		private ArrayList<Unit> mObjects;
		private Context mContext;
		private int mSpinnerRow;
		private int mTextViewSpinnerRow;

		public SpinnerAdapter(OelEditActivity context, int spinnerRow,
				int textViewSpinnerRow, ArrayList<Unit> objects) {
			super(context, spinnerRow, textViewSpinnerRow, objects);
			mObjects = objects;
			mContext = context;
			mSpinnerRow = spinnerRow;
			mTextViewSpinnerRow = textViewSpinnerRow;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(mSpinnerRow, null);
			}
			Unit object = mObjects.get(position);
			if (object != null) {
				TextView textViewName = (TextView) v
						.findViewById(mTextViewSpinnerRow);

				if (textViewName != null) {
					textViewName.setText(object.getName());
				}

			}
			return v;
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			return getView(position, convertView, parent);
		}
	}

	public boolean isVolume() {
		return mIsVolume;
	}

	public static ArrayList<Unit> getVolumeUnits(ArrayList<Unit> allUnits) {
		ArrayList<Unit> volumeUnits = new ArrayList<Unit>();

		for (Unit unit : allUnits) {
			if (unit.isVolume()) {
				volumeUnits.add(unit);
			}
		}

		return volumeUnits;
	}
}
