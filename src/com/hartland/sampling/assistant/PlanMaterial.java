package com.hartland.sampling.assistant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

public class PlanMaterial {
	private static final String TAG = "PlanMaterial";
	static final boolean D = AssistantApp.D;

	static final int NAME = 1;
	static final int LOQ_PER_MAX_FLOW_PER_OEL = 2;

	private Oel mOel;
	private Monster mMonster;
	private double mFlow;

	// private boolean mManualOel, mManualMonster = false;

	// should reference related lists in the plan rather than create their own
	// objects
	private ArrayList<Monster> mMonsters;
	private ArrayList<Oel> mOels;
	private final ListMaterial mParentListMaterial;

	//used for deciding when/how to highlight OELs
	private boolean mIsC, mIsStel, mIsHighestC, mIsHighestStel, mHasC, mHasStel;

	public PlanMaterial(long planId, ListMaterial listMaterial) {
		mParentListMaterial = listMaterial;

	}

	public PlanMaterial(ListMaterial listMaterial, Cursor cursor,
			ArrayList<Monster> monsters, ArrayList<Oel> oels) {
		if (D)
			Log.d(TAG,
					"constructor ListMaterial, Cursor, PlanMonsters, PlanOels");

		mParentListMaterial = listMaterial;

		mMonsters = monsters;
		mOels = oels;

		long oelId, monsterId;
		double flow;

		int indexManualOel = cursor.getColumnIndex("manual_oel");
		int indexManualMonster = cursor.getColumnIndex("manual_monster");
		int indexFlow = cursor.getColumnIndex("manual_flow");

		oelId = cursor.getLong(indexManualOel);
		monsterId = cursor.getLong(indexManualMonster);
		flow = cursor.getDouble(indexFlow);

		boolean useBest = true;

		for (Monster monster : mMonsters) {
			if (monster.getId() == monsterId) {
				mMonster = monster;
				useBest = false;
				break;
			}
		}

		if (useBest) {
			mMonster = Monster.sortPlanMonsters(mMonsters,
					Monster.LOQ_PER_MAX_FLOW).get(0);
		}

		boolean useLowest = true;
		for (Oel oel : mOels) {
			if (oel.getId() == oelId) {
				mOel = oel;
				useLowest = false;
				break;
			}
		}
		if (useLowest) {
			mOel = Oel.sortPlanOels(mOels, Oel.VALUE,
					mParentListMaterial.getMolW()).get(0);
		}

		if (flow > 0) {
			mFlow = flow;
		}
	}

	private PlanMaterial(PlanMaterial planMaterial) {
		// super(planMaterial);

		mParentListMaterial = planMaterial.getParentListMaterial();

		ArrayList<Oel> newPlanOels = new ArrayList<Oel>(planMaterial
				.getPlanOels().size());
		for (Oel oel : planMaterial.getPlanOels()) {
			newPlanOels.add(oel.copy());
		}
		mOels = newPlanOels;

		ArrayList<Monster> newPlanMonsters = new ArrayList<Monster>(
				planMaterial.getPlanMonsters().size());
		for (Monster monster : planMaterial.getPlanMonsters()) {
			newPlanMonsters.add(monster.copy());
		}
		mMonsters = newPlanMonsters;

		try {
			long oelId = planMaterial.getPlanOel().getId();
			for (Oel oel : mOels) {
				if (oel.getId() == oelId) {
					mOel = oel;
					break;
				}
			}
		} catch (NullPointerException e) {
			Log.d(TAG, "caught " + e + "-->setting mOel to lowest");
			mOel = Oel.sortPlanOels(mOels, Oel.VALUE,
					mParentListMaterial.getMolW()).get(0);
		}

		try {
			long monsterId = planMaterial.getPlanMonster().getId();
			for (Monster monster : mMonsters) {
				if (monster.getId() == monsterId) {
					mMonster = monster;
					break;
				}
			}
		} catch (NullPointerException e) {
			Log.d(TAG, "caught " + e + "-->setting mMonster to best");
			mMonster = Monster.sortPlanMonsters(mMonsters,
					Monster.LOQ_PER_MAX_FLOW).get(0);
		}

		mFlow = planMaterial.getFlow();

	}

	public PlanMaterial copy() {
		return new PlanMaterial(this);
	}

	// public static PlanMaterial createPlanMaterial(FetalMaterial
	// fetalMaterial,
	// Plan plan) {
	// ArrayList<FetalMaterial> fetalMaterials = new ArrayList<FetalMaterial>(
	// 1);
	// fetalMaterials.add(fetalMaterial);
	// return createPlanMaterials(fetalMaterials, plan).get(0);
	// }

	// public static ArrayList<PlanMaterial> createPlanMaterials(
	// ArrayList<FetalMaterial> fetalMaterials, Plan plan) {
	// ArrayList<PlanMaterial> planMaterials = new ArrayList<PlanMaterial>(
	// fetalMaterials.size());
	//
	// for (FetalMaterial fm : fetalMaterials) {
	// planMaterials.add(new PlanMaterial(fm, plan));
	// }
	//
	// return planMaterials;
	// }

	// public PlanMaterial(ListMaterial material) {
	// super(material);
	// // should only be used when adding materials to plan via
	// // MaterialSelectActivity, thus no check for manual monster/oel needed
	// }

	// private double getLoqPerMaxFlowPerOel() {
	// double loqPerMaxFlow, oel;
	//
	// // if (mMonster != null) {
	// // loqPerMaxFlow = mMonster.getLoq() / mMonster.getMaxFlow();
	// // } else {
	// // Monster monster = Monster.sortMonsters(mMonsters,
	// // Monster.LOQ_PER_MAX_FLOW).get(0);
	// // loqPerMaxFlow = monster.getLoq() / monster.getMaxFlow();
	// // }
	//
	// loqPerMaxFlow = mMonster.getLoq() / mMonster.getMaxFlow();
	// oel = mOel.getValue();
	//
	// // if (mOel != null) {
	// // oel = mOel.getValue();
	// // } else {
	// // oel = Oel.sortOels(mOels, Oel.VALUE,
	// getMolW()).get(0).getValue();
	// // }
	//
	// return loqPerMaxFlow / oel;
	//
	// }

	public ListMaterial getParentListMaterial() {
		return mParentListMaterial;
	}

	public Oel getPlanOel() {
		return mOel;
	}

	public Oel getLowestPlanOel() {
		Oel lowestPlanOel = Oel.sortPlanOels(mOels, Oel.VALUE,
				mParentListMaterial.getMolW()).get(0);

		return lowestPlanOel;
	}

	public boolean lowestOel() {
		if (MyMath.compareOel(mOel, getLowestPlanOel(),
				mParentListMaterial.getMolW()) > 0) {
			return false;
		} else {
			return true;
		}
	}

	public boolean hasStel() {
		for (Oel oel : mOels) {
			if (oel.getOelType().getName().equals("STEL")) {
				return true;
			}
		}
		return false;
	}

	public boolean hasC() {
		for (Oel oel : mOels) {
			if (oel.getOelType().getName().equals("C")) {
				return true;
			}
		}
		return false;
	}

	public Oel getPlanOel(String oelName) {
		for (Oel oel : mOels) {
			if (oel.getName().equals(oelName)) {
				return oel;
			}
		}
		Log.e(TAG, "oel not found by name: " + oelName);
		return null;
	}

	public Monster getPlanMonster() {
		return mMonster;
	}

	public Monster getBestPlanMonster() {
		Monster bestPlanMonster = Monster.sortPlanMonsters(mMonsters,
				Monster.LOQ_PER_MAX_FLOW).get(0);

		return bestPlanMonster;
	}

	public boolean bestMonster() {
		if (MyMath.compareLoqPerMaxFlow(mMonster, getBestPlanMonster()) > 0) {
			return false;
		} else {
			return true;
		}
	}

	public ArrayList<Monster> getPlanMonsters() {
		return mMonsters;
	}

	public ArrayList<Oel> getPlanOels() {
		return mOels;
	}

	public void setPlanOel(Oel oel) {
		if (D)
			Log.d(TAG, "set mPLanOel to:");
		Oel.log(TAG, oel);
		mOel = oel;
		// TODO: recalc loqPer
	}

	public void setPlanMonster(Monster monster) {
		mMonster = monster;
		// TODO: recalc loqPer...
	}

	public void setPlanMonsters(ArrayList<Monster> monsters) {
		mMonsters = monsters;
	}

	public void setPlanOels(ArrayList<Oel> oels) {
		mOels = oels;
	}

	public double getFlow() {
		return mFlow;
	}

	public void setFlow(double flow) {
		mFlow = flow;
		// TODO: recalc loqPer...
	}

	public ArrayList<String> getMethodNames() {
		ArrayList<String> methodNames = new ArrayList<String>();

		for (Monster monster : mMonsters) {
			methodNames.add(monster.getMethodName());
		}

		Set<String> set = new HashSet<String>(methodNames);

		return new ArrayList<String>(set);
	}

	public ArrayList<String> getMediaNames(String methodName) {
		ArrayList<String> mediaNames = new ArrayList<String>();

		for (Monster monster : mMonsters) {
			if (monster.getMethodName().equals(methodName)) {
				mediaNames.add(monster.getMediaName());
			}
		}

		return mediaNames;
	}

	// public ArrayList<String> getOelNames() {
	// ArrayList<String> oelNames = new ArrayList<String>();
	//
	// for (Oel planOel : mOels) {
	// oelNames.add(planOel.getName());
	// }
	//
	// Set<String> set = new HashSet<String>(oelNames);
	//
	// return new ArrayList<String>(set);
	// }

	public Monster getPlanMonster(String methodName, String mediaName) {
		for (Monster monster : mMonsters) {
			if (monster.getMethodName().equals(methodName)
					&& monster.getMediaName().equals(mediaName)) {
				return monster;
			}
		}
		if (D)
			Log.e(TAG, "not found");
		return mMonster;
	}

	public static final ArrayList<PlanMaterial> sortPlanMaterials(
			ArrayList<PlanMaterial> planMaterials, int property) {
		return sortPlanMaterials(planMaterials, property, false);
	}

	public static final ArrayList<PlanMaterial> sortPlanMaterials(
			ArrayList<PlanMaterial> planMaterials, int property, boolean reverse) {
		switch (property) {
		// TODO: compare by other properties
		case PlanMaterial.LOQ_PER_MAX_FLOW_PER_OEL:
			Collections.sort(planMaterials, new MaterialLpmfpoComparator());
			break;
		case PlanMaterial.NAME:
			Collections.sort(planMaterials, new MaterialNameComparator());
			break;
		}
		return planMaterials;
	}

	private final static class MaterialLpmfpoComparator implements
			Comparator<PlanMaterial> {
		public int compare(PlanMaterial m1, PlanMaterial m2) {
			return MyMath.compareLoqPerMaxFlowPerOel(m1, m2);
		}
	}

	private final static class MaterialNameComparator implements
			Comparator<PlanMaterial> {
		public int compare(PlanMaterial m1, PlanMaterial m2) {
			return m1.getParentListMaterial().getName()
					.compareTo(m2.getParentListMaterial().getName());
		}
	}

	public static final int getOelPercentColor(SharedPreferences prefs,
			int oelPercent) {
		if (!prefs.getBoolean("pref_key_color_oel_percent", true))
			return Color.TRANSPARENT;

		if (oelPercent < 1) {
			// TODO: handle error
			return Color.RED;
		} else if (oelPercent > Integer.parseInt(prefs.getString(
				"pref_key_show_red", "0"))) {
			return Color.RED;
		} else if (oelPercent > Integer.parseInt(prefs.getString(
				"pref_key_show_orange", "0"))) {
			return Color.argb(255, 255, 127, 0);
		} else if (oelPercent > Integer.parseInt(prefs.getString(
				"pref_key_show_yellow", "0"))) {
			return Color.YELLOW;
		} else if (oelPercent <= Integer.parseInt(prefs.getString(
				"pref_key_show_yellow", "0"))) {
			return Color.TRANSPARENT;
		}
		return Color.RED;
	}

	public void setOelBackgroundColors(SharedPreferences prefs, int oelPercent,
			TextView textViewOelPercent, int duration, TextView textViewOelName) {
		boolean oelPercentIsTransparent = setOelPercentBackgroundColor(prefs,
				oelPercent, textViewOelPercent);
		boolean oelIsLowest = lowestOel();
		boolean colorByType = prefs.getBoolean("pref_key_color_oel_type", true);
		boolean ignoreUnderYellow = prefs.getBoolean(
				"pref_key_oel_type_restrict", false);

		int findCDuration = Integer.parseInt(prefs.getString(
				"pref_key_highlight_c", String.valueOf(Integer.MAX_VALUE)));
		int findStelDuration = Integer.parseInt(prefs.getString(
				"pref_key_highlight_stel", String.valueOf(Integer.MAX_VALUE)));

		setBackgroundColorBooleans(mOel);

		if (!oelIsLowest) {
			textViewOelName.setBackgroundColor(Color.argb(255, 255, 127, 0));
			return;
		}

		if (!colorByType) {
			textViewOelName.setBackgroundColor(Color.TRANSPARENT);
			return;
		}

		if (ignoreUnderYellow && oelPercentIsTransparent) {
			textViewOelName.setBackgroundColor(Color.TRANSPARENT);
			return;
		}

		if (!mHasStel && !mHasC) {
			textViewOelName.setBackgroundColor(Color.TRANSPARENT);
			return;
		}

		if (duration > findStelDuration && duration > findCDuration) {
			textViewOelName.setBackgroundColor(Color.TRANSPARENT);
			return;
		}

		if (duration > findCDuration && !mHasStel) {
			textViewOelName.setBackgroundColor(Color.TRANSPARENT);
			return;
		}

		if (duration <= findStelDuration && mHasStel && !mIsHighestStel
				&& !mIsStel) {
			textViewOelName.setBackgroundColor(Color.YELLOW);
			return;
		} else if (duration <= findCDuration && mHasC && !mIsHighestC && !mIsC) {
			textViewOelName.setBackgroundColor(Color.YELLOW);
			return;
		}

		Log.e(TAG, "reached end of setOelBackgroundColors for material "
				+ mParentListMaterial.getName());
		textViewOelName.setBackgroundColor(Color.RED);
	}

	private void setBackgroundColorBooleans(Oel oel) {
		mIsStel = isStel(oel);
		mIsC = isC(oel);

		mHasStel = false;
		mHasC = false;
		mIsHighestC = true;
		mIsHighestStel = true;

		if (mIsStel) {
			mHasStel = true;
			mIsHighestC = false;
		}
		if (mIsC) {
			mHasC = true;
			mIsHighestStel = false;
		}
		if (!mIsC && !mIsStel) {
			mIsHighestStel = false;
			mIsHighestC = false;
		}

		for (Oel o : mOels) {
			if (!mHasStel) {
				if (o.getOelType().getName().equals("STEL")) {
					mHasStel = true;
				}
			}

			if (!mHasC) {
				if (o.getOelType().getName().equals("C")) {
					mHasStel = true;
				}
			}

			if (mIsHighestStel && mIsStel) {
				if (o.getOelType().getName().equals("STEL")
						&& MyMath.compareOel(o, oel, getParentListMaterial()
								.getMolW()) > 0) {
					mIsHighestStel = false;
				}
			}

			if (mIsHighestC && mIsC) {
				if (o.getOelType().getName().equals("C")
						&& MyMath.compareOel(o, oel, getParentListMaterial()
								.getMolW()) > 0) {
					mIsHighestC = false;
				}
			}
		}

	}

	private boolean isStel(Oel oel) {
		if (oel.getOelType().equals("STEL")) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isC(Oel oel) {
		if (oel.getOelType().equals("C")) {
			return true;
		} else {
			return false;
		}
	}

	private boolean setOelPercentBackgroundColor(SharedPreferences prefs,
			int oelPercent, TextView textViewOelPercent) {
		if (!prefs.getBoolean("pref_key_color_oel_percent", true)) {
			textViewOelPercent.setBackgroundColor(Color.TRANSPARENT);
			return true;
		}

		if (oelPercent < 1) {
			// TODO: handle error
			textViewOelPercent.setBackgroundColor(Color.RED);
			return false;
		} else if (oelPercent > Integer.parseInt(prefs.getString(
				"pref_key_show_red", "0"))) {
			textViewOelPercent.setBackgroundColor(Color.RED);
			return false;
		} else if (oelPercent > Integer.parseInt(prefs.getString(
				"pref_key_show_orange", "0"))) {
			textViewOelPercent.setBackgroundColor(Color.argb(255, 255, 127, 0));
			return false;
		} else if (oelPercent > Integer.parseInt(prefs.getString(
				"pref_key_show_yellow", "0"))) {
			textViewOelPercent.setBackgroundColor(Color.YELLOW);
			return false;
		} else if (oelPercent <= Integer.parseInt(prefs.getString(
				"pref_key_show_yellow", "0"))) {
			textViewOelPercent.setBackgroundColor(Color.TRANSPARENT);
			return true;
		}
		Log.e(TAG, "setOelPercentBackgroundColor defaulting to RED");
		textViewOelPercent.setBackgroundColor(Color.RED);
		return false;
	}

	public static void logPlanMaterials(ArrayList<PlanMaterial> planMaterials) {
		Log.d(TAG, "logging materials");
		String headers = "Name|CAS|OEL name";
		Log.v(TAG, headers);
		for (PlanMaterial planMaterial : planMaterials) {
			String row = planMaterial.getParentListMaterial().getName() + " "
					+ planMaterial.getParentListMaterial().getCas() + " "
					+ planMaterial.getPlanOel().getName();
			Log.v(TAG, row);
		}
	}

}
