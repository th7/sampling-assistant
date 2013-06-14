package com.hartland.sampling.assistant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public final class Oel {
	private static final String TAG = "Oel";

	private static final boolean D = AssistantApp.D;

	public static final int VALUE = 1;
	public static final int MATERIAL_ID = 2;
	public static final int SOURCE = 3;

	public static final long USER = 1;

	private final OelSource mOelSource;
	// private final long mOelSource;

	private long mMaterial;

	private Unit mUnit;

	private OelType mOelType;

	private long mId;
	private double mValue;
	// private String mOelTypeName;

	// private final String mOelSourceName;
	// private String mOelUnitName;

	private final boolean mReadOnly;

	// public Oel(OelSource oelSource, long oelTypeId, long unitId){
	// mOelSource = oelSource;
	// mUnit = unitId;
	// mOelType = oelTypeId;
	// mId = 0;
	// mValue = 1;
	// //TODO: fix hard-coded
	// mOelTypeName = "TWA";
	// //TODO: fix hard-coded
	// mOelSourceName = "USER";
	// //TODO: fix hard-coded
	// mOelUnitName = "mg/m3";
	//
	// mReadOnly = false;
	// }
	public Oel(Cursor cursor, OelSource oelSource, OelType oelType, Unit unit) {
		int indexMaterial, indexValue;

		indexMaterial = cursor.getColumnIndex("material");
		// indexUnit = cursor.getColumnIndex("oel_unit");
		// indexOelType = cursor.getColumnIndex("oel_type");
		indexValue = cursor.getColumnIndex("value");
		// indexOelTypesName = cursor.getColumnIndex("oel_types_name");
		// indexOelSourcesName = cursor.getColumnIndex("oel_sources_name");
		// indexOelUnitName = cursor.getColumnIndex("units_name");

		mOelSource = oelSource;
		mOelType = oelType;
		mUnit = unit;
		mMaterial = cursor.getLong(indexMaterial);
		// mUnit = cursor.getLong(indexUnit);
		// mOelType = cursor.getLong(indexOelType);
		mValue = cursor.getDouble(indexValue);
		// mOelTypeName = cursor.getString(indexOelTypesName);
		// mOelSourceName = cursor.getString(indexOelSourcesName);
		// mOelUnitName = cursor.getString(indexOelUnitName);

		// boolean readOnly = false;
		// for (String protectedSource : AssistantApp.PROTECTED_SOURCES) {
		// if (mOelSourceName.equals(protectedSource)) {
		// readOnly = true;
		// break;
		// }
		// }

		if (mOelSource.isProtected()) {
			mReadOnly = true;
		} else {
			mReadOnly = false;
		}
		// mReadOnly = readOnly;

		int indexId = cursor.getColumnIndex("_id");
		mId = cursor.getLong(indexId);

	}

	private Oel(Oel oel) {
		mId = oel.getId();
		mOelSource = oel.getOelSource();
		mMaterial = oel.getMaterialId();
		mUnit = oel.getUnit();
		mOelType = oel.getOelType();
		mValue = oel.getValue();
		// mOelTypeName = oel.getTypeName();
		// mOelSourceName = oel.getSourceName();
		// mOelUnitName = oel.getUnitName();
		mReadOnly = oel.isReadOnly();
	}

	public Oel(OelSource defaultOelSource, Unit defaultUnit,
			OelType defaultOelType, double defaultOelValue) {
		mOelSource = defaultOelSource;
		mUnit = defaultUnit;
		mOelType = defaultOelType;
		mValue = defaultOelValue;

		if (mOelSource.isProtected()) {
			mReadOnly = true;
		} else {
			mReadOnly = false;
		}
	}

	public boolean isReadOnly() {
		return mReadOnly;
	}
	
	public boolean isC() {
		if (getOelType().getName().equals("C")){
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isStel() {
		if (getOelType().getName().equals("STEL")){
			return true;
		} else {
			return false;
		}
	}

	public Oel copy() {
		return new Oel(this);
	}

	public long getId() {
		return mId;
	}

	public OelSource getOelSource() {
		return mOelSource;
	}

	public long getMaterialId() {
		return mMaterial;
	}

	public Unit getUnit() {
		return mUnit;
	}

	public OelType getOelType() {
		return mOelType;
	}

	public double getValue() {
		return mValue;
	}

	// public String getTypeName() {
	// return mOelTypeName;
	// }

	// public String getSourceName() {
	// return mOelSourceName;
	// }
	//
	// public String getUnitName() {
	// return mOelUnitName;
	// }

	public String getName() {
		return String.valueOf(getValue()) + " " + mUnit.getName() + " "
				+ mOelSource.getName() + " " + mOelType.getName();
	}

	public static final ArrayList<Oel> limitPlanOels(ArrayList<Oel> oels,
			int property, long argument) {
		int size = oels.size();

		switch (property) {
		case MATERIAL_ID:

			for (int i = size - 1; i >= 0; i--) {
				if (oels.get(i).getMaterialId() != argument) {
					oels.remove(i);
				}
			}
			return oels;
		case SOURCE:
			switch ((int) argument) {
			case (int) USER:
				String arg = "USER";
				for (int i = size - 1; i >= 0; i--) {
					if (!oels.get(i).getOelSource().getName().equals(arg)) {
						oels.remove(i);
					}
				}
				return oels;
			default:
				return null;
			}
		default:
			return null;
		}
	}

	public static final ArrayList<Oel> sortPlanOels(ArrayList<Oel> oels,
			int property, double molW) {
		return sortPlanOels(oels, property, molW, false);
	}

	public static final ArrayList<Oel> sortPlanOels(ArrayList<Oel> oels,
			int property, double molW, boolean reverse) {
		switch (property) {
		// TODO: compare by other properties
		case VALUE:
			Collections.sort(oels, new OelValueComparator(molW));
			break;
		}
		return oels;
	}

	private final static class OelValueComparator implements Comparator<Oel> {
		double mMolW;

		public OelValueComparator(double molW) {
			mMolW = molW;
		}

		public int compare(Oel o1, Oel o2) {
			return MyMath.compareOel(o1, o2, mMolW);
		}
	}

	public static ArrayList<String> getOelNames(ArrayList<Oel> oels) {
		ArrayList<String> oelNames = new ArrayList<String>(oels.size());

		for (Oel oel : oels) {
			oelNames.add(oel.getName());
		}

		return oelNames;
		// Set<String> set = new HashSet<String>(oelNames);
		//
		// return new ArrayList<String>(set);
	}

	public void setUnit(Unit unit) {
		if (D)
			Log.d(TAG, "setOelUnitName");
		if (!mReadOnly) {
			mUnit = unit;
		} else {
			Log.e(TAG, "attempted to set mUnit on read-only");
		}
	}

	public void setOelType(OelType oelType) {
		if (D)
			Log.d(TAG, "setOelTypeName");
		if (!mReadOnly) {
			mOelType = oelType;
		} else {
			Log.e(TAG, "attempted to set mOelType on read-only");
		}
	}

	public void setOelValue(double value) {
		if (D)
			Log.d(TAG, "setOelValue");
		if (!mReadOnly) {
			mValue = value;
		} else {
			Log.e(TAG, "attempted to set mValue on read-only");
		}
	}

	public void setId(long newId) {
		if (D)
			Log.d(TAG, "setId");
		if (!mReadOnly) {
			mId = newId;
		} else {
			Log.e(TAG, "attempted to set mId on read-only");
		}
	}

	public void setMaterial(long newMaterial) {
		if (D)
			Log.d(TAG, "setId");
		if (!mReadOnly) {
			mMaterial = newMaterial;
		} else {
			Log.e(TAG, "attempted to set mMaterial on read-only");
		}
	}

	public static void log(String TAG, Oel oel) {
		if (D)
			Log.v(TAG, "SOURCE, etc: " + oel.getName());
	}

	public static void log(String TAG, ArrayList<Oel> oels) {
		if (!D)
			return;
		for (Oel oel : oels) {
			log(TAG, oel);
		}
	}
	
	public static class SpinnerAdapter extends ArrayAdapter<Oel> {

		private ArrayList<Oel> mObjects;
		private Context mContext;
		private int mSpinnerRow;
		private int mTextViewSpinnerRow;

		public SpinnerAdapter(OelEditActivity context, int spinnerRow,
				int textViewSpinnerRow, ArrayList<Oel> objects) {
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
			Oel object = mObjects.get(position);
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
