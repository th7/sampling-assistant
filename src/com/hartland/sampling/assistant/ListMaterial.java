package com.hartland.sampling.assistant;

import java.util.ArrayList;

import android.database.Cursor;
import android.util.Log;

public class ListMaterial {
	@SuppressWarnings("unused")
	private static final String TAG = "ListMaterial";
	static final boolean D = AssistantApp.D;

	static final int NAME = 1;

	private final long mId;
	private final String mName;
	private final String mCas;
	private final double mMolW;

	// working copy of materials that is changed along with UI in
	// MaterialSelectActivity, saved into
	// Plan or discarded
	// private static ArrayList<ListMaterial> sListMaterials;

	// holds all materials
	// private static ArrayList<ListMaterial> sAllListMaterials;

	public ListMaterial(Cursor cursor) {
		int _idIndex = cursor.getColumnIndex("_id");
		int casIndex = cursor.getColumnIndex("cas");
		int mol_wIndex = cursor.getColumnIndex("mol_w");
		int nameIndex = cursor.getColumnIndex("name");

		mId = cursor.getLong(_idIndex);
		mCas = cursor.getString(casIndex);
		mMolW = cursor.getDouble(mol_wIndex);
		mName = cursor.getString(nameIndex);

	}

	public ListMaterial(ListMaterial listMaterial) {
		mId = listMaterial.mId;
		mCas = listMaterial.mCas;
		mMolW = listMaterial.mMolW;
		mName = listMaterial.mName;
	}
	
	public ListMaterial(long id, String name, String cas, double molW) {
		mId = id;
		mName = name;
		mCas = cas;
		mMolW = molW;
	}

	// public static ArrayList<ListMaterial> getListMaterials() {
	// ArrayList<ListMaterial> lm = sListMaterials;
	// sListMaterials = null;
	// return lm;
	// }
	//
	// public static long[] getListMaterialIds() {
	// int size;
	// if (sListMaterials != null) {
	// size = sListMaterials.size();
	// } else {
	// size = 0;
	// }
	// long[] materialIds = new long[size];
	// for (int i = 0; i < size; i++) {
	// materialIds[i] = sListMaterials.get(i).getId();
	// }
	// return materialIds;
	// }
	//
	// public static ArrayList<ListMaterial> getAllListMaterials() {
	// ArrayList<ListMaterial> alm = sAllListMaterials;
	// sAllListMaterials = null;
	// return alm;
	// }
	//
	// public static void clearListMaterials() {
	// sListMaterials = null;
	// }
	//
	// public static void setListMaterials(ArrayList<ListMaterial>
	// listMaterials) {
	// if (sListMaterials != null) {
	// if (D)
	// Log.w(TAG, "setting new sListMaterials with previous not null");
	// }
	// sListMaterials = listMaterials;
	// }
	//
	// public static void newListMaterials(ArrayList<PlanMaterial>
	// planMaterials) {
	// if (sListMaterials != null) {
	// if (D)
	// Log.w(TAG, "setting new sListMaterials with previous not null");
	// }
	// if (planMaterials != null) {
	// sListMaterials = new ArrayList<ListMaterial>(planMaterials);
	// } else {
	// sListMaterials = new ArrayList<ListMaterial>();
	// }
	// }
	//
	// public static void setAllListMaterials(ArrayList<ListMaterial>
	// listMaterials) {
	// if (sListMaterials != null) {
	// if (D)
	// Log.w(TAG, "setting new sListMaterials with previous not null");
	// }
	// sListMaterials = listMaterials;
	// }

	public long getId() {
		return mId;
	}

	public String getName() {
		return mName;
	}

	public String getCas() {
		return mCas;
	}

	public double getMolW() {
		return mMolW;
	}

	public static void log(String TAG, ListMaterial listMaterial) {
		if (D)
			Log.v(TAG, "CAS#: " + listMaterial.getCas() + " NAME: "
					+ listMaterial.getName());
	}

	public static void log(String TAG, ArrayList<ListMaterial> listMaterials) {
		if (!D)
			return;
		for (ListMaterial listMaterial : listMaterials) {
			log(TAG, listMaterial);
		}
	}

}
