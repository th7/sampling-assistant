package com.hartland.sampling.assistant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Locale;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;

import com.hartland.sampling.assistant.utilities.ParserHelper;

public class WorkerService extends IntentService {
	public static final String TAG = "WorkerService";
	// public static final int ID = AssistantApp.WORKER_SERVICE_ID;
	static final boolean D = AssistantApp.D;

	private AssistantData mAssistantData;
	// private AssistantApp mAssistantApp;

	public static final String REQUEST_CODE = "request_code";
	public static final String TABLE_NAME = "table_name";
	public static final String FILE_PATH = "file_path";
	public static final String DONE_CODE = "done_code";

	// request codes
	public static final int PARSE_CSV = 1;
	public static final int INSERT_CSV = 2;
	public static final int TEST_QUERY = 3;
	public static final int SELECT_MATERIALS = 4;

	// public static final int SELECT_PLAN = 5;
//	public static final int SAVE_PLAN_EDIT = 6;

	public static final int SAVE_MATERIAL_SELECT = 7;
	public static final int SAVE_MATERIAL_DETAIL = 8;
	public static final int SELECT_PLANS = 9;
	public static final int SAVE_PLAN = 10;
	public static final int DELETE_PLAN = 11;
	public static final int SELECT_ALL_LIST_MATERIALS = 12;
	public static final int SELECT_PLAN_MATERIAL = 13;
	public static final int DELETE_PLAN_MATERIAL = 14;
	public static final int SAVE_NEW_PLAN_MATERIAL = 15;
	public static final int SELECT_ALL_EXTRAS = 16;
	public static final int SAVE_OEL_EDIT = 17;
	public static final int SELECT_DEFAULT_OEL = 18;
	public static final int SAVE_NEW_OEL = 19;
	public static final int DELETE_OEL = 20;

	public static final int PARSE_INSERT_ALL = 21;
	public static final int INSTALL_DB = 22;

	public static final int NOTIFY_DONE = 99;
	// used as done code only
	public static final int PLAN_MODIFIED = 100;

	public static final int DO_NOTHING = -2;

	public WorkerService() {
		super("WorkerService");
	}

	@Override
	public void onCreate() {
		if (D)
			Log.v(TAG, "onCreate");
		super.onCreate();
		// doBindService();
		// mAssistantApp = (AssistantApp) getApplication();
		mAssistantData = new AssistantData(this);
	}

	@Override
	public void onDestroy() {
		if (D)
			Log.d(TAG, "onDestroy");
		super.onDestroy();
		// doUnbindService();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (D) {
			String caller = intent.getStringExtra("caller");
			if (!caller.equals("send a string identifying caller")) {
				// crash if no caller sent
			}
			Log.d(TAG, "onHandleIntent caller: " + caller);
		}

		switch (intent.getIntExtra(REQUEST_CODE, -1)) {
		case -1:
			Log.e(TAG, "request_code error");
			break;
		case DO_NOTHING:
			if (D)
				Log.d(TAG, "do nothing");
			break;
		case PARSE_CSV:
			if (D)
				Log.i(TAG, "parseCsv for: " + intent.getStringExtra(FILE_PATH));
			ParserHelper.parseCsv(intent.getStringExtra(FILE_PATH));
			break;
		case INSERT_CSV:
			if (D)
				Log.i(TAG, "insert into: " + intent.getStringExtra(TABLE_NAME));
			mAssistantData.dbOpenBegin();
			if (mAssistantData.insert(intent.getStringExtra(TABLE_NAME),
					ParserHelper.getLastParsed())) {
				mAssistantData.dbSuccessful();
			}
			mAssistantData.dbEndClose();
			break;
		case TEST_QUERY:
			mAssistantData.dbOpenBegin();
			mAssistantData.selectTable(intent.getStringExtra(TABLE_NAME));
			mAssistantData.dbEndClose();
			break;
		case SAVE_MATERIAL_SELECT:
			saveMaterialSelect(intent);
			break;
		case SAVE_MATERIAL_DETAIL:
			saveMaterialDetail(intent);
			break;
		// case SELECT_PLAN:
		// selectPlan(intent);
		// break;
//		case SAVE_PLAN_EDIT:
//			savePlanEdit(intent);
//			break;
		case SELECT_PLANS:
			selectPlans(intent);
			break;
		case SAVE_PLAN:
			savePlan(intent);
			break;
		case DELETE_PLAN:
			deletePlan(intent);
			break;
		case SELECT_ALL_LIST_MATERIALS:
			selectAllListMaterials(intent);
			break;
		case SELECT_PLAN_MATERIAL:
			selectPlanMaterial(intent);
			break;
		case DELETE_PLAN_MATERIAL:
			deletePlanMaterial(intent);
			break;
		case SAVE_NEW_PLAN_MATERIAL:
			saveNewPlanMaterial(intent);
			break;
		case SELECT_ALL_EXTRAS:
			selectAllExtras(intent);
			break;
		case SAVE_OEL_EDIT:
			saveOelEdit(intent);
			break;
		case SELECT_DEFAULT_OEL:
			selectDefaultOel(intent);
			break;
		case SAVE_NEW_OEL:
			saveNewOel(intent);
			break;
		case DELETE_OEL:
			deleteOel(intent);
			break;
		case PARSE_INSERT_ALL:
			parseInsertAll();
			break;
		case INSTALL_DB:
			try {
				installDb();
			} catch (IOException e) {
				Log.e(TAG, "install db failed with exception: " + e);
			}
			break;
		case NOTIFY_DONE:
			notifyDone(intent);
			break;

		}
	}

//	private void savePlanEdit(Intent intent) {
//		if (D)
//			Log.d(TAG, "savePlanEdit");
//
//		// int listPlanIndex = intent.getIntExtra("listPlanIndex", -1);
//
//		// ListPlan listPlan;
//		Plan plan;
//		plan = PlanEditActivity.getPlanToSave(TAG);
//		if (plan == null) {
//			Log.e(TAG, "Data.getPlanToSave() is null. Returning without save.");
//			return;
//		}
//		// if (plan.getParentListPlan().getId() == 0) {
//		// if (Data.newId > 0) {
//		// plan.getParentListPlan().setId(Data.newId);
//		// Data.newId = 0;
//		// } else {
//		// Log.e(TAG,
//		// "plan.getId() == 0 && Data.newId <= 0. Returning without save.");
//		// return;
//		// }
//		// }
//
//		ContentValues contentValues = new ContentValues();
//		// contentValues.clear();
//		contentValues.put("name", plan.getName());
//		contentValues.put("pref_time", plan.getPrefTime());
//		contentValues.put("min_time", plan.getMinTime());
//		contentValues.put("max_time", plan.getMaxTime());
//		contentValues.put("edited", plan.getEditedLong());
//		contentValues.put("created", plan.getCreated());
//
//		// TODO: not used, constraint violated if not present
//		contentValues.put("parent_plan", 0);
//
//		boolean success = true;
//
//		// update plans table (name, times)
//		mAssistantData.dbOpenBegin();
//		// check if new plan indicated by id of 0
//
//		if (mAssistantData.update("plans", contentValues, plan.getId())) {
//			// do nothing, success remains true
//		} else {
//			Log.e(TAG, "error saving plan edit -- update failed");
//			success = false;
//		}
//
//		if (success) {
//			mAssistantData.dbSuccessful();
//		} else {
//			Log.e(TAG, "error updating Plan.");
//		}
//
//		mAssistantData.dbEndClose();
//	}

	private void saveMaterialSelect(Intent intent) {
		if (D)
			Log.d(TAG, "saveMaterialSelect");

		long planId = intent.getLongExtra("planId", -1);
		long[] materialIds = intent.getLongArrayExtra("materialIds");
		long[] oldMaterialIds = intent.getLongArrayExtra("oldMaterialIds");
		mAssistantData.dbOpenBegin();
		if (mAssistantData.updatePlanMaterials(planId, materialIds,
				oldMaterialIds)) {
			mAssistantData.dbSuccessful();
			if (D)
				Log.d(TAG, "updatePlanMaterials succeeded");
		} else {
			Log.e(TAG, "error updating plan materials");
		}
		mAssistantData.dbEndClose();
	}

	private void saveMaterialDetail(Intent intent) {
		if (D)
			Log.d(TAG, "saveMaterialDetail");
		ContentValues values = new ContentValues();
		values.put("manual_oel", intent.getLongExtra("oelId", -1));
		values.put("manual_monster", intent.getLongExtra("monsterId", -1));
		values.put("manual_flow", intent.getDoubleExtra("flow", -1));
		long planId = intent.getLongExtra("planId", -1);
		long materialId = intent.getLongExtra("materialId", -1);

		boolean success = true;
		mAssistantData.dbOpenBegin();
		if (mAssistantData.updateMaterialDetails(planId, materialId, values)) {
			// do nothing, success remains true
		} else {
			Log.e(TAG,
					"error saving material detail -- update plans_materials failed");
			success = false;
		}

		values.clear();
		values.put("pref_time", intent.getIntExtra("duration", -1));

		if (mAssistantData.update("plans", values, planId)) {
			// do nothing, success remains true
		} else {
			Log.e(TAG,
					"error saving material detail -- update plans table failed");
			success = false;
		}

		if (success) {
			mAssistantData.dbSuccessful();
		} else {
			Log.e(TAG, "error saving material details.");
		}

		mAssistantData.dbEndClose();
	}

	// private void selectPlan(Intent intent) {
	// if (D)
	// Log.d(TAG, "selectPlan");
	// int listPlanIndex = intent.getIntExtra("listPlanIndex", -1);
	// ListPlan listPlan = PlanListActivity.sPlans.get(listPlanIndex);
	// if (listPlan == null) {
	// Log.e(TAG,
	// "Data.listPlan == null. Returning without creating plan.");
	// return;
	// }
	//
	// PlanEditActivity.sPlan = new Plan(listPlan);
	// }

	private void selectPlans(Intent intent) {
		if (D)
			Log.d(TAG, "selectListPlans");
		mAssistantData.dbOpenBegin();
		PlanListActivity.sPlans = mAssistantData.selectPlans();
		mAssistantData.dbEndClose();
	}

	private void savePlan(Intent intent) {
		if (D)
			Log.d(TAG, "saveListPlan");

		Plan plan = PlanListActivity.getPlanToSave(TAG);
		if (plan == null) {
			Log.e(TAG,
					"Data.getListPlanToSave() == null. Returning without save.");
			return;
		}
		int planIndex = intent.getIntExtra("planIndex", -1);

		mAssistantData.dbOpenBegin();
		ContentValues contentValues = new ContentValues();

		contentValues.put("name", plan.getName());
		contentValues.put("pref_time", plan.getPrefTime());
		contentValues.put("min_time", plan.getMinTime());
		contentValues.put("max_time", plan.getMaxTime());
		contentValues.put("edited", plan.getEditedLong());
		contentValues.put("created", plan.getCreated());

		// TODO: not used, constraint violated if not present
		contentValues.put("parent_plan", 0);

		long planId = plan.getId();

		// update plans table (name, times)
		if (planId > 0) {
			if (D)
				Log.d(TAG, "planId != -1, updating");
			if (mAssistantData.update("plans", contentValues, planId)) {
				mAssistantData.dbSuccessful();
			} else {
				Log.e(TAG, "error saving plan edit -- update failed");
			}
		} else if (planId == 0) {
			if (D)
				Log.d(TAG, "planId == 0, inserting new plan");
			planId = mAssistantData.insert("plans", contentValues);
			if (planId > 0) {
				PlanListActivity.sPlans.get(planIndex).setId(planId);
				// if (Data.newId == 0) {
				// Data.newId = listPlanId;
				// } else {
				// Log.e(TAG,
				// "Data.newId != 0. Resetting to -1 to indicate error.");
				// Data.newId = -1;
				// }
				mAssistantData.dbSuccessful();
				// mAssistantApp.getTempPlan().setId(planId);
				if (D)
					Log.d(TAG, "inserted plan with _id: " + planId);
			} else {
				Log.e(TAG, "Error inserting new plan. Setting listPlan to null");
				plan = null;
			}
			// Mailbox.sendListPlan(editedListPlan, PlanListActivity.TAG,
			// WorkerService.TAG);
		}

		mAssistantData.dbEndClose();
	}

	// private void saveListPlans(Intent intent) {
	// if (D)
	// Log.d(TAG, "saveListPlans");
	//
	// // TODO only update new/deleted
	//
	// ArrayList<ListPlan> listPlans = Data.listPlans;
	// if (listPlans == null) {
	// Log.e(TAG, "Data.listPlans == null. Returning without save.");
	// return;
	// }
	//
	// mAssistantData.dbOpenBegin();
	// ContentValues contentValues = new ContentValues();
	// long planId = -1;
	// boolean success = true;
	// for (ListPlan listPlan : listPlans) {
	//
	// contentValues.clear();
	// contentValues.put("name", listPlan.getName());
	// contentValues.put("pref_time", listPlan.getPrefTime());
	// contentValues.put("min_time", listPlan.getMinTime());
	// contentValues.put("max_time", listPlan.getMaxTime());
	// contentValues.put("edited", listPlan.getEditedLong());
	// contentValues.put("created", listPlan.getCreated());
	//
	// // TODO: not used, constraint violated if not present
	// contentValues.put("parent_plan", 0);
	//
	// planId = listPlan.getId();
	//
	// // update plans table (name, times)
	// if (planId > 0) {
	// if (D)
	// Log.d(TAG, "planId > 0, updating");
	// if (mAssistantData.update("plans", contentValues, planId)) {
	// } else {
	// Log.e(TAG, "error saving plan edit -- update failed");
	// success = false;
	// }
	// } else if (planId == 0) {
	// if (D)
	// Log.d(TAG, "planId == 0, inserting new plan");
	// planId = mAssistantData.insert("plans", contentValues);
	// if (planId != -1) {
	// listPlan.setId(planId);
	// if (Data.newId == 0) {
	// Data.newId = planId;
	// } else {
	// Log.e(TAG, "Data.newId != 0. Resetting to -1 to indicate error.");
	// Data.newId = -1;
	// }
	// if (D)
	// Log.d(TAG, "inserted plan with _id: " + planId);
	// } else {
	// Log.e(TAG, "Error inserting new plan.");
	// success = false;
	// }
	// } else {
	// Log.e(TAG, "planId = " + planId);
	// success = false;
	// }
	// }
	//
	// if (success) {
	// mAssistantData.dbSuccessful();
	// if (D)
	// Log.d(TAG, "ListPlan inserts/updates successful");
	// } else {
	// Log.e(TAG, "Error -- nothing saved");
	// }
	// mAssistantData.dbEndClose();
	// }

	private void deletePlan(Intent intent) {
		long planId = intent.getLongExtra("listPlanId", -1);
		if (D)
			Log.d(TAG, "planId: " + planId);
		String whereClause = "_id = ?";
		String[] whereArgs = new String[] { String.valueOf(planId) };
		mAssistantData.dbOpenBegin();
		if (mAssistantData.delete("plans", whereClause, whereArgs) != -1) {
			mAssistantData.dbSuccessful();

		} else {
			Log.e(TAG, "delete failed");
		}
		mAssistantData.dbEndClose();
	}

	private void selectAllListMaterials(Intent intent) {
		if (D)
			Log.d(TAG, "selectAllListMaterials");
		mAssistantData.dbOpenBegin();
		AssistantApp.allListMaterials = mAssistantData.selectAllListMaterials();
		mAssistantData.dbEndClose();
	}

	private void selectPlanMaterial(Intent intent) {
		// Plan.log(TAG, PlanEditActivity.sPlan);
		if (D)
			Log.d(TAG, "selectPlanMaterial");
		long planId, materialId;
		// int listPlanIndex = intent.getIntExtra("listPlanIndex", -1);
		planId = intent.getLongExtra("planId", -1);
		materialId = intent.getLongExtra("materialId", -1);
		mAssistantData.dbOpenBegin();
		PlanMaterial planMaterial = null;
		// ListMaterial listMaterial =
		// mAssistantData.selectListMaterial(materialId);

		for (ListMaterial listMaterial : AssistantApp.allListMaterials) {
			if (listMaterial.getId() == materialId) {
				planMaterial = mAssistantData.selectPlanMaterial(planId,
						listMaterial);
				break;
			}
		}

		// planMaterial = mAssistantData.selectPlanMaterial(planId,
		// listMaterial);
		mAssistantData.dbEndClose();
		if (planMaterial != null) {
			if (D)
				Log.v(TAG, "adding PlanMaterial with ID: "
						+ planMaterial.getParentListMaterial().getId());
			if (PlanEditActivity.planMaterialsToAdd == null) {
				PlanEditActivity.planMaterialsToAdd = new LinkedList<PlanMaterial>();
			}
			PlanEditActivity.planMaterialsToAdd.add(planMaterial);
		} else {
			Log.e(TAG, "ListMaterial corresponding to ID '" + materialId
					+ "' not found. No plan material added.");
		}
		Plan.log(TAG, PlanEditActivity.sPlan);

		// Data.listPlan = Data.plan;
	}

	private void deletePlanMaterial(Intent intent) {
		// Plan.log(TAG, PlanEditActivity.sPlan);
		if (D)
			Log.d(TAG, "deletePlanMaterial");
		long planId, materialId;
		planId = intent.getLongExtra("planId", -1);
		materialId = intent.getLongExtra("materialId", -1);

		ArrayList<Long> materialIds = new ArrayList<Long>(1);
		materialIds.add(materialId);
		mAssistantData.dbOpenBegin();
		if (mAssistantData.deletePlanMaterials(planId, materialIds)) {
			mAssistantData.dbSuccessful();
			if (PlanEditActivity.planMaterialsToRemove == null) {
				PlanEditActivity.planMaterialsToRemove = new LinkedList<Long>();
			}
			PlanEditActivity.planMaterialsToRemove.add(materialId);
		}
		mAssistantData.dbEndClose();

		Plan.log(TAG, PlanEditActivity.sPlan);

	}

	private void saveNewPlanMaterial(Intent intent) {
		if (D)
			Log.d(TAG, "saveNewPlanMaterial");
		ContentValues contentValues = new ContentValues();
		long planId = intent.getLongExtra("planId", -1);
		long materialId = intent.getLongExtra("materialId", -1);
		if (D)
			Log.v(TAG, "planId == " + planId + " and materialId == "
					+ materialId);

		contentValues.put("plan", String.valueOf(planId));
		contentValues.put("material", String.valueOf(materialId));

		mAssistantData.dbOpenBegin();
		if (mAssistantData.insert("plans_materials", contentValues) != -1) {
			mAssistantData.dbSuccessful();
		}
		mAssistantData.dbEndClose();
	}

	private void selectAllExtras(Intent intent) {
		if (D)
			Log.d(TAG, "selectAllExtras");
		mAssistantData.dbOpenBegin();
		AssistantApp.allOelSources = mAssistantData.selectOelSources();
		AssistantApp.allOelTypes = mAssistantData.selectOelTypes();
		AssistantApp.allUnits = mAssistantData.selectUnits();
		mAssistantData.dbEndClose();
	}

	private void saveOelEdit(Intent intent) {
		if (D)
			Log.d(TAG, "saveOelEdit");
		Oel oelToSave = OelEditActivity.getOelToSave(TAG);

		if (oelToSave.isReadOnly()) {
			Log.e(TAG,
					"attempted to save read-only OEL. Returning without update.");
			return;
		}

		long oelId = oelToSave.getId();

		ContentValues values = new ContentValues();
		values.put("oel_source", oelToSave.getOelSource().getId());
		values.put("material", oelToSave.getMaterialId());
		values.put("value", oelToSave.getValue());
		values.put("oel_unit", oelToSave.getUnit().getId());
		values.put("oel_type", oelToSave.getOelType().getId());

		mAssistantData.dbOpenBegin();
		if (mAssistantData.updateOel(oelId, values)) {
			mAssistantData.dbSuccessful();
		}
		mAssistantData.dbEndClose();

	}

	private void saveNewOel(Intent intent) {
		if (D)
			Log.d(TAG, "sveNewOel");
		Oel oelToAdd = OelEditActivity.getOelToAdd(TAG);

		ContentValues values = new ContentValues();
		values.put("oel_source", oelToAdd.getOelSource().getId());
		values.put("material", oelToAdd.getMaterialId());
		values.put("value", oelToAdd.getValue());
		values.put("oel_unit", oelToAdd.getUnit().getId());
		values.put("oel_type", oelToAdd.getOelType().getId());

		mAssistantData.dbOpenBegin();
		long newId = mAssistantData.insertOel(values);
		if (newId > 0) {
			mAssistantData.dbSuccessful();
			oelToAdd.setId(newId);
		}
		mAssistantData.dbEndClose();
	}

	private void selectDefaultOel(Intent intent) {
		if (D)
			Log.d(TAG, "selectDefaultOel");
		OelSource defaultOelSource = null;
		Unit defaultUnit = null;
		OelType defaultOelType = null;
		for (OelSource os : AssistantApp.allOelSources) {
			if (os.getName().equals(OelEditActivity.EDITABLE_OEL_SOURCE)) {
				defaultOelSource = os;
			}
		}
		for (Unit u : AssistantApp.allUnits) {
			if (u.getName().equals(OelEditActivity.DEFAULT_UNIT)) {
				defaultUnit = u;
			}
		}
		for (OelType ot : AssistantApp.allOelTypes) {
			if (ot.getName().equals(OelEditActivity.DEFAULT_OEL_TYPE)) {
				defaultOelType = ot;
			}
		}

		AssistantApp.defaultOel = new Oel(defaultOelSource, defaultUnit,
				defaultOelType, OelEditActivity.DEFAULT_OEL_VALUE);
	}

	private void deleteOel(Intent intent) {
		if (D)
			Log.d(TAG, "deleteOel");
		long id = intent.getLongExtra("_id", -1);
		mAssistantData.dbOpenBegin();
		if (mAssistantData.deleteOel(id)) {
			mAssistantData.dbSuccessful();
		}
		mAssistantData.dbEndClose();

	}

	private void notifyDone(Intent intent) {
		if (D)
			Log.d(TAG, "notifyDone");
		Intent doneIntent = new Intent(AssistantApp.BROADCAST_ACTION);
		doneIntent.replaceExtras(intent);
		// doneIntent.putExtra(WorkerService.DONE_CODE,
		// intent.getIntExtra(WorkerService.DONE_CODE, -1));
		sendBroadcast(doneIntent);
	}

	private void parseInsertAll() {
		if (D)
			Log.d(TAG, "parseInsertAll");
		loadFileList();
		String[] tables = { "oel_sources", "oel_types", "materials", "labs",
				"equipment_types", "methods", "media_types", "media", "units",
				"oels", "monsters", "plans", "plans_materials" };
		Intent intent = new Intent(this, WorkerService.class);

		for (String table : tables) {
			Log.v(TAG, "for table: " + table);
			// get files for this table
			ArrayList<String> files = new ArrayList<String>();
			for (String file : AssistantApp.mFileList) {
				if (convertFilenameToTablename(file).equals(table)) {
					files.add(file);
					Log.v(TAG, "added file: " + file);
				}
			}

			for (String file : files) {
				Log.v(TAG, "for file: " + file);
				// parse
				intent.putExtra("caller", TAG + " parseInsertAll parse");
				intent.putExtra(WorkerService.REQUEST_CODE,
						WorkerService.PARSE_CSV);
				intent.putExtra(WorkerService.FILE_PATH,
						AssistantApp.mPath.getPath() + "/" + file);
				startService(intent);

				// insert
				intent.putExtra("caller", TAG + " parseInsertAll insert");
				intent.putExtra(WorkerService.REQUEST_CODE,
						WorkerService.INSERT_CSV);
				intent.putExtra(WorkerService.TABLE_NAME, table);
				startService(intent);

			}

		}
	}

	private void installDb() throws IOException {
		if (D)
			Log.d(TAG, "installDb");
		mAssistantData.dbOpenBegin();
		mAssistantData.dbEndClose();

		InputStream myInput = this.getAssets().open("assistant.db");

		String outFile = "/data/data/com.hartland.sampling.assistant/databases/assistant.db";
		OutputStream myOutput = new FileOutputStream(outFile);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		myOutput.flush();
		myOutput.close();
		myInput.close();

	}

	// end methods called by intent, begin supporting methods

	private void loadFileList() {
		Log.d(TAG, "loadFileList...");
		try {
			Boolean mkdirs = AssistantApp.mPath.mkdirs();
			Log.d(TAG, "...mPath.mkdirs() == " + mkdirs + "...");
		} catch (SecurityException e) {
			Log.e(TAG, "unable to write on the sd card " + e.toString());
		}
		if (AssistantApp.mPath.exists()) {
			Log.d(TAG, "...mPath.exists == true");
			FilenameFilter filter = new FilenameFilter() {
				public boolean accept(File dir, String filename) {
					return filename.contains(".csv");
				}
			};
			AssistantApp.mFileList = AssistantApp.mPath.list(filter);
			sortFilenames();

		} else {
			Log.d(TAG, "...mPath.exists == false");
			AssistantApp.mFileList = new String[0];
		}
	}

	private void sortFilenames() {
		Log.d(TAG, "sortFilenames");
		int size = AssistantApp.mFileList.length;
		ArrayList<String> files = new ArrayList<String>(size);
		for (int i = 0; i < size; i++) {
			files.add(AssistantApp.mFileList[i]);
		}
		Collections.sort(files);
		String[] orderedFiles = new String[size];
		for (int i = 0; i < size; i++) {
			orderedFiles[i] = files.get(i);
		}

		AssistantApp.mFileList = orderedFiles;
	}

	private String convertFilenameToTablename(String filename) {
		return filename.toLowerCase(Locale.US).split("\\.")[0].replaceAll(
				"\\d", "");
	}

}
