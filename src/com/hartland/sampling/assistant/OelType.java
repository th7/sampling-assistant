package com.hartland.sampling.assistant;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class OelType extends MiscObject {

	public OelType(Cursor cursor) {
		super(cursor);
		// TODO Auto-generated constructor stub
	}

	public static class SpinnerAdapter extends ArrayAdapter<OelType> {

		private ArrayList<OelType> mObjects;
		private Context mContext;
		private int mSpinnerRow;
		private int mTextViewSpinnerRow;

		public SpinnerAdapter(Context context, int spinnerRow,
				int textViewSpinnerRow, ArrayList<OelType> objects) {
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
			OelType object = mObjects.get(position);
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
}
