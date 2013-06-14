package com.hartland.sampling.assistant;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.database.Cursor;
import android.util.Log;

//defacto extends ListPlan by holding a ListPlan
public class Plan {
	private static final String TAG = "Plan";
	private static final boolean D = AssistantApp.D;

	private int mMinTime, mMaxTime, mPrefTime;
	private String mName;
	private long mId;
	private long mParentPlan;
	private long mCreated, mEdited;

	// holds all plan specific materials
	// TODO: used LinkedList?
	public ArrayList<PlanMaterial> mPlanMaterials;

	// private final ListPlan mParentListPlan;

	public Plan() {
		if (D)
			Log.v(TAG, "constructor()");
		// mMaterialsNames = new ArrayList<String>();
		mPlanMaterials = new ArrayList<PlanMaterial>();
		mName = "";
		mCreated = Calendar.getInstance().getTimeInMillis();
		mEdited = mCreated;
		mMinTime = 15;
		mPrefTime = 480;
		mMaxTime = 600;
		mId = 0;
	}

	public Plan(Cursor cursor, ArrayList<PlanMaterial> planMaterials) {
		if (D)
			Log.v(TAG, "Plan(Cursor, ArrayList<PlanMaterial>)");
		mPlanMaterials = planMaterials;

		int idIndex = cursor.getColumnIndex("_id");
		int min_timeIndex = cursor.getColumnIndex("min_time");
		int max_timeIndex = cursor.getColumnIndex("max_time");
		int pref_timeIndex = cursor.getColumnIndex("pref_time");
		int nameIndex = cursor.getColumnIndex("name");
		int parent_planIndex = cursor.getColumnIndex("parent_plan");
		int createdIndex = cursor.getColumnIndex("created");
		int editedIndex = cursor.getColumnIndex("edited");

		mId = cursor.getLong(idIndex);
		mMinTime = cursor.getInt(min_timeIndex);
		mMaxTime = cursor.getInt(max_timeIndex);
		mPrefTime = cursor.getInt(pref_timeIndex);
		mName = cursor.getString(nameIndex);
		mParentPlan = cursor.getLong(parent_planIndex);
		mCreated = cursor.getLong(createdIndex);
		mEdited = cursor.getLong(editedIndex);
	}

	public Plan(Plan plan) {

		if (D)
			Log.d(TAG, "Plan(Plan)");

		mId = plan.getId();

		// if (mId == -1) {
		// Log.e(TAG, "mId = -1");
		// return;
		// } else if (mId == 0) {
		// if (D)
		// Log.i(TAG, "mId = 0");
		// mPlanMaterials = new ArrayList<PlanMaterial>();
		// return;
		// }

		mMinTime = plan.getMinTime();
		mMaxTime = plan.getMaxTime();
		mPrefTime = plan.getPrefTime();
		mName = plan.getName();
		mParentPlan = plan.getParentPlan();
		mCreated = plan.getCreated();
		mEdited = plan.getEditedLong();

		ArrayList<PlanMaterial> pms = plan.mPlanMaterials;
		if (pms != null && pms.size() > 0) {
			mPlanMaterials = new ArrayList<PlanMaterial>(
					plan.mPlanMaterials.size());
			for (PlanMaterial planMaterial : plan.mPlanMaterials) {
				mPlanMaterials.add(planMaterial.copy());
			}

			// mOldMaterialIds = plan.getMaterialIds();

		} else {
			mPlanMaterials = new ArrayList<PlanMaterial>();
		}
	}

	public Plan copy() {
		return new Plan(this);
	}

	public String getMaterialsString() {
		// TODO: fix hard coded
		String materials = "No materials";
		ArrayList<String> materialsNames = getMaterialsNames();
		if (materialsNames != null && materialsNames.size() > 0) {
			int size = materialsNames.size();
			materials = "";
			for (int i = 0; i < size; i++) {
				materials = materials + materialsNames.get(i);
				if (i != size - 1) {
					materials = materials + ", ";
				}
			}
		}
		return materials;
	}

	private ArrayList<String> getMaterialsNames() {
		ArrayList<String> materialsNames = new ArrayList<String>(
				mPlanMaterials.size());
		for (PlanMaterial planMaterial : mPlanMaterials) {
			materialsNames.add(planMaterial.getParentListMaterial().getName());
		}
		return materialsNames;
	}

	public long getId() {
		return mId;
	}

	public int getMinTime() {
		return mMinTime;
	}

	public int getMaxTime() {
		return mMaxTime;
	}

	public int getPrefTime() {
		return mPrefTime;
	}

	public String getName() {
		return mName;
	}

	public long getParentPlan() {
		return mParentPlan;
	}

	public long getCreated() {
		return mCreated;
	}

	public long getEditedLong() {
		return mEdited;
	}

	public String getEditedString() {
		return DateFormat.getDateTimeInstance(DateFormat.SHORT,
				DateFormat.SHORT).format(new Date(mEdited));
	}

	public void setName(String name) {
		mName = name;
	}

	public void setMinTime(int minTime) {
		mMinTime = minTime;
	}

	public void setMaxTime(int maxTime) {
		mMaxTime = maxTime;
	}

	public void setPrefTime(int prefTime) {
		mPrefTime = prefTime;
	}

	public void setId(long id) {
		mId = id;
	}

	public void setEdited(long edited) {
		mEdited = edited;
	}

	public void addPlanMaterial(PlanMaterial planMaterial) {
		if (D)
			Log.d(TAG, "addPlanMaterial");
		// mParentListPlan.addListMaterial(planMaterial.getParentListMaterial());
		// mPlanMaterials.add(planMaterial);
		if (PlanEditActivity.sSelectedMaterialsAdapter != null) {
			PlanEditActivity.sSelectedMaterialsAdapter.add(planMaterial);
		}
	}

	public void removePlanMaterial(long id) {
		int size = mPlanMaterials.size();
		for (int i = size - 1; i >= 0; i--) {
			if (id == mPlanMaterials.get(i).getParentListMaterial().getId()) {
				// mPlanMaterials.remove(i);
				if (PlanEditActivity.sSelectedMaterialsAdapter != null) {
					PlanEditActivity.sSelectedMaterialsAdapter
							.remove(mPlanMaterials.get(i));
				}
			}
		}
		// mParentListPlan.removeListMaterial(id);
	}

	public void sortPlanMaterials(int property) {
		PlanMaterial.sortPlanMaterials(mPlanMaterials, property);
		if (PlanEditActivity.sSelectedMaterialsAdapter != null) {
			// TODO: needed? mSelectedMaterialsAdapter.notifyDataSetChanged();
		}

	}

	public ArrayList<Monster> getPlanMonsters() {
		ArrayList<Monster> monsters = new ArrayList<Monster>();
		for (PlanMaterial planMaterial : mPlanMaterials) {
			monsters.addAll(planMaterial.getPlanMonsters());
		}
		return monsters;
	}

	public ArrayList<Oel> getPlanOels() {
		ArrayList<Oel> oels = new ArrayList<Oel>();
		for (PlanMaterial planMaterial : mPlanMaterials) {
			oels.addAll(planMaterial.getPlanOels());
		}
		return oels;
	}

	public long[] getMaterialIds() {
		int size;
		if (mPlanMaterials != null) {
			size = mPlanMaterials.size();
		} else {
			size = 0;
		}
		long[] materialIds = new long[size];
		for (int i = 0; i < size; i++) {
			materialIds[i] = mPlanMaterials.get(i).getParentListMaterial()
					.getId();
		}
		return materialIds;
	}

	public static void log(String TAG, Plan plan) {
		if (D)
			Log.v(TAG, "NAME: " + plan.getName());
		Log.v(TAG, "#Materials: " + plan.mPlanMaterials.size());
	}

	public boolean planMaterialsNotNull() {
		if (mPlanMaterials == null) {
			return false;
		} else {
			return true;
		}
	}

	public PlanMaterial getPlanMaterial(int planMaterialIndex) {
		return mPlanMaterials.get(planMaterialIndex);
	}

	public PlanMaterial getPlanMaterial(long materialId) {
		for (PlanMaterial planMaterial : mPlanMaterials) {
			if (planMaterial.getParentListMaterial().getId() == materialId) {
				return planMaterial;
			}
		}
		Log.e(TAG, "material with ID: " + materialId + " not found");
		return null;
	}

	public boolean hasPlanMaterial(long planMaterialId) {
		for (int i = 0; i < mPlanMaterials.size(); i++) {
			if (mPlanMaterials.get(i).getParentListMaterial().getId() == planMaterialId) {
				return true;
			}
		}
		return false;
	}

}
