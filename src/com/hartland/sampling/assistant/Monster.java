package com.hartland.sampling.assistant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import android.database.Cursor;
import android.util.Log;

public class Monster {
	private static final String TAG = "Monster";
	private static final boolean D = AssistantApp.D;

	public static final int ID = 1;
	public static final int LOQ = 2;
	public static final int MIN_FLOW = 3;
	public static final int MAX_FLOW = 4;
	public static final int LOQ_PER_MAX_FLOW = 5;
	public static final int LOQ_PER_MAX_FLOW_PER_OEL = 6;
	public static final int MATERIAL_ID = 7;
	public static final int OEL = 8;

	private final long mId, mMaterialId;
	private final double mMinFlow, mMaxFlow, mLoq;
	private PlanMaterial mPlanMaterial;

	private final String mLoqUnitName, mMediaName, mMediaTypeName,
			mEquipmentTypeName, mLabName, mMethodName;

	private Monster(Cursor cursor) {
		int indexId, indexLoq, indexMinFlow, indexMaxFlow, indexLoqUnitName, indexMediaName, indexMediaTypeName, indexEquipmentTypeName, indexLabName, indexMId, indexMethodName;

		indexId = cursor.getColumnIndex("_id");

		indexLoqUnitName = cursor.getColumnIndex("units_name");
		indexMediaName = cursor.getColumnIndex("media_name");
		indexMediaTypeName = cursor.getColumnIndex("media_types_name");
		indexEquipmentTypeName = cursor.getColumnIndex("equipment_types_name");
		indexLabName = cursor.getColumnIndex("labs_name");
		indexMethodName = cursor.getColumnIndex("methods_name");

		indexLoq = cursor.getColumnIndex("loq");
		indexMinFlow = cursor.getColumnIndex("min_flow");
		indexMaxFlow = cursor.getColumnIndex("max_flow");

		indexMId = cursor.getColumnIndex("materials_id");

		mId = cursor.getLong(indexId);

		mLoqUnitName = cursor.getString(indexLoqUnitName);
		mMediaName = cursor.getString(indexMediaName);
		mMediaTypeName = cursor.getString(indexMediaTypeName);
		mEquipmentTypeName = cursor.getString(indexEquipmentTypeName);
		mLabName = cursor.getString(indexLabName);
		mMethodName = cursor.getString(indexMethodName);

		mLoq = cursor.getDouble(indexLoq);
		mMinFlow = cursor.getDouble(indexMinFlow);
		mMaxFlow = cursor.getDouble(indexMaxFlow);

		mMaterialId = cursor.getLong(indexMId);

	}

	private Monster(Monster monster) {
		mId = monster.getId();

		mLoqUnitName = monster.getLoqUnitName();
		mMediaName = monster.getMediaName();
		mMediaTypeName = monster.getMediaTypeName();
		mEquipmentTypeName = monster.getEquipmentTypeName();
		mLabName = monster.getLabName();
		mMethodName = monster.getMethodName();

		mLoq = monster.getLoq();
		mMinFlow = monster.getMinFlow();
		mMaxFlow = monster.getMaxFlow();

		mMaterialId = monster.getMaterialId();
	}

	public Monster copy() {
		return new Monster(this);
	}

	public static ArrayList<Monster> createPlanMonsters(Cursor cursor) {
		ArrayList<Monster> monsters = null;
		if (cursor.moveToFirst()) {
			monsters = new ArrayList<Monster>(cursor.getCount());
			do {
				monsters.add(new Monster(cursor));
			} while (cursor.moveToNext());
		}
		return monsters;
	}

	public String getMethodName() {
		return mMethodName;
	}

	public String getMediaName() {
		return mMediaName;
	}

	public String getMediaTypeName() {
		return mMediaTypeName;
	}

	public String getLabName() {
		return mLabName;
	}

	public String getLoqUnitName() {
		return mLoqUnitName;
	}

	public String getEquipmentTypeName() {
		return mEquipmentTypeName;
	}

	public long getId() {
		return mId;
	}

	public long getMaterialId() {
		return mMaterialId;
	}

	public PlanMaterial getPlanMaterial() {
		return mPlanMaterial;
	}

	public double getLoq() {
		return mLoq;
	}

	public double getLoqPerMaxFlow() {
		return mLoq / mMaxFlow;
	}

	public double getMinFlow() {
		return mMinFlow;
	}

	public double getMaxFlow() {
		return mMaxFlow;
	}

	public void setPlanMaterial(PlanMaterial planMaterial) {
		mPlanMaterial = planMaterial;
	}

	/**
	 * Gets unique material ids for these monsters
	 **/
	public static ArrayList<Long> getMaterialIds(
			ArrayList<Monster> monsters) {
		if (D)
			Log.d(TAG, "getMaterialIds");
		Set<Long> materialIds = new HashSet<Long>();

		for (Monster monster : monsters) {
			materialIds.add(monster.getMaterialId());
		}

		ArrayList<Long> uniqueIds = new ArrayList<Long>(materialIds);
		if (D)
			Log.v(TAG, String.format("ID: %s", uniqueIds));

		return uniqueIds;
	}

	public static final ArrayList<Monster> sortPlanMonsters(
			ArrayList<Monster> monsters, int property) {
		return sortPlanMonsters(monsters, property, false);
	}

	public static final ArrayList<Monster> sortPlanMonsters(
			ArrayList<Monster> monsters, int property, boolean reverse) {
		switch (property) {
		case Monster.LOQ:
			Collections.sort(monsters, new MonsterLoqComparator(reverse));
			break;
		case Monster.LOQ_PER_MAX_FLOW:
			Collections.sort(monsters, new MonsterLoqPerMaxFlowComparator(
					reverse));
			break;
		}
		return monsters;
	}

	private final static class MonsterLoqComparator implements
			Comparator<Monster> {
		private boolean mReverse;

		public MonsterLoqComparator(boolean reverse) {
			mReverse = reverse;
		}

		public int compare(Monster m1, Monster m2) {

			int result = MyMath.compareLoq(m1, m2);

			if (!mReverse) {
				return result;
			} else {
				return -1 * result;
			}
		}
	}

	private static final class MonsterLoqPerMaxFlowComparator implements
			Comparator<Monster> {
		private boolean mReverse;

		public MonsterLoqPerMaxFlowComparator(boolean reverse) {
			mReverse = reverse;
		}

		public int compare(Monster m1, Monster m2) {

			int result = MyMath.compareLoqPerMaxFlow(m1, m2);

			if (!mReverse) {
				return result;
			} else {
				return -1 * result;
			}
		}

	}

	public static void logPlanMonsters(ArrayList<Monster> monsters) {
		String rowHeader = "H|_id|loq|min_flow|max_flow|labs_name|units_name|equipment_types_name|media_name|media_types_name|methods_name";
		Log.v(TAG, rowHeader);
		for (int i = 0; i < monsters.size(); i++) {
			String row = String.valueOf(i) + "| ";
			row = row + String.valueOf(monsters.get(i).getId()) + "|";
			row = row + String.valueOf(monsters.get(i).getLoq()) + "|";
			row = row + String.valueOf(monsters.get(i).getMinFlow()) + "|";
			row = row + String.valueOf(monsters.get(i).getMaxFlow()) + "|";
			row = row + String.valueOf(monsters.get(i).getLabName()) + "|";
			row = row + String.valueOf(monsters.get(i).getLoqUnitName())
					+ "|";
			row = row
					+ String.valueOf(monsters.get(i).getEquipmentTypeName())
					+ "|";
			row = row + String.valueOf(monsters.get(i).getMediaName())
					+ "|";
			row = row + String.valueOf(monsters.get(i).getMediaTypeName())
					+ "|";
			row = row + String.valueOf(monsters.get(i).getMethodName())
					+ "|";

			Log.v(TAG, row);
		}
	}
}
