package com.hartland.sampling.assistant;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AssistantData {
	public static final String TAG = "AssistantData";
	static final boolean D = AssistantApp.D;

	Context context;
	private DbHelper dbHelper;
	private SQLiteDatabase db;

	public static final String DB_NAME = "assistant.db";
	public static final int DB_VERSION = 1;

	public AssistantData(Context context) {
		this.context = context;
		dbHelper = new DbHelper();
	}

	public void dbOpenBegin() {

		if (D)
			Log.v(TAG, "dbOpenBegin");
		if (db != null && db.isOpen()) {
			if (D)
				Log.i(TAG, "database is already open");
		} else if (db == null || db.isOpen() == false) {
			db = dbHelper.getWritableDatabase();
		}

		if (db != null && db.inTransaction()) {
			if (D)
				Log.i(TAG, "transaction in progress");
		}
		db.beginTransaction();

	}

	public void dbSuccessful() {
		if (D)
			Log.v(TAG, "dbSuccessful");
		db.setTransactionSuccessful();
	}

	public void dbEndClose() {
		if (D)
			Log.v(TAG, "dbEndClose");
		db.endTransaction();
		closeDb();
	}

	private void closeDb() {
		if (db != null) {
			if (db.isOpen()) {
				if (D)
					Log.v(TAG, "closing db");
				db.close();
			}
		}
	}

	public void dbEndCloseAll() {
		if (db != null && db.isOpen()) {
			if (db.inTransaction()) {
				db.endTransaction();
				dbEndCloseAll();
			}
			db.close();
		}
	}

	/**
	 * Insert values into a table. Replaces on conflict. Returns true if
	 * successful.
	 * 
	 * @param table
	 * @param contentValues
	 * @return
	 */
	public boolean insert(String table, ArrayList<ContentValues> contentValues) {
		if (D)
			Log.v(TAG, "insert--as batch");
		Boolean result;
		int i = -1;
		try {
			for (i = 0; i < contentValues.size(); i++) {
				if (db.insertWithOnConflict(table, null, contentValues.get(i),
						SQLiteDatabase.CONFLICT_REPLACE) == -1) {

				}
			}
			if (D)
				Log.d(TAG, "batch insert succeeded");
			result = true;
		} catch (Exception e) {
			Log.w(TAG, "insert failed with exception: " + e);
			Log.d(TAG, "insert failed for content value at index " + i);
			logContentValues(contentValues.get(i));
			result = false;
		}

		return result;
	}

	/**
	 * Insert values into a table. Replaces on conflict. Returns rowId if
	 * successful, -1 if not.
	 * 
	 * @param table
	 * @param contentValues
	 * @return
	 */
	public long insert(String table, ContentValues contentValues) {
		if (D)
			Log.v(TAG, "insert with table: " + table);
		long result;
		try {
			result = db.insertWithOnConflict(table, null, contentValues,
					SQLiteDatabase.CONFLICT_REPLACE);

			if (D)
				Log.d(TAG, "insert succeeded on row _id: " + result);
		} catch (Exception e) {
			Log.w(TAG, "insert failed with exception: " + e);
			logContentValues(contentValues);
			result = -1;
		}

		return result;
	}

	public boolean update(String table, ContentValues contentValues, long rowId) {
		if (D)
			Log.v(TAG, "update with table: '" + table + "' and rowId: '"
					+ rowId + "'");
		Boolean result;
		String[] whereArgs = { String.valueOf(rowId) };
		try {
			long updated = db.updateWithOnConflict(table, contentValues,
					"_id = ?", whereArgs, SQLiteDatabase.CONFLICT_FAIL);
			result = true;
			if (D)
				Log.d(TAG, "update succeeded. " + updated + " row(s) updated");
		} catch (Exception e) {
			Log.w(TAG, "update failed with exception: " + e);
			result = false;
		}
		return result;
	}

	public ArrayList<OelType> selectOelTypes() {
		ArrayList<OelType> oelTypes;
		Cursor cursor = select("oel_types", null, null, null);
		if (cursor.moveToFirst()) {
			int size = cursor.getCount();
			oelTypes = new ArrayList<OelType>(size);
			do {
				oelTypes.add(new OelType(cursor));
			} while (cursor.moveToNext());
		} else {
			Log.e(TAG, "no oel types found");
			return null;
		}
		return oelTypes;
	}

	public ArrayList<OelSource> selectOelSources() {
		ArrayList<OelSource> oelSources;
		Cursor cursor = select("oel_sources", null, null, null);
		if (cursor.moveToFirst()) {
			int size = cursor.getCount();
			oelSources = new ArrayList<OelSource>(size);
			do {
				oelSources.add(new OelSource(cursor));
			} while (cursor.moveToNext());
		} else {
			Log.e(TAG, "no oel sources found");
			return null;
		}
		return oelSources;
	}

	public ArrayList<Unit> selectUnits() {
		ArrayList<Unit> units;
		Cursor cursor = select("units", null, null, null);
		if (cursor.moveToFirst()) {
			int size = cursor.getCount();
			units = new ArrayList<Unit>(size);
			do {
				units.add(new Unit(cursor));
			} while (cursor.moveToNext());
		} else {
			Log.e(TAG, "no units found");
			return null;
		}
		return units;
	}

	// public boolean updatePlanMaterials(long planId, long[] materialsIds,
	// long[] oldMaterialsIds) {
	// if (D)
	// Log.v(TAG, "updatePlanMaterials");
	// // check for and delete any materials that are no longer present
	// for (long oldMaterialsId : oldMaterialsIds) {
	// boolean delete = true;
	// for (long materialsId : materialsIds) {
	// if (oldMaterialsId == materialsId) {
	// delete = false;
	// break;
	// }
	// }
	// if (delete) {
	// int result = delete(
	// "plans_materials",
	// "plan = ? AND material = ?",
	// new String[] { String.valueOf(planId),
	// String.valueOf(oldMaterialsId) });
	//
	// if (result == -1)
	// return false;
	// }
	// }
	//
	// // check for and add any new materials
	// ContentValues values = new ContentValues();
	// for (long materialsId : materialsIds) {
	// boolean add = true;
	// for (long oldMaterialsId : oldMaterialsIds) {
	// if (materialsId == oldMaterialsId) {
	// add = false;
	// break;
	// }
	// }
	// if (add) {
	// values.clear();
	// values.put("plan", planId);
	// values.put("material", materialsId);
	// // TODO: add additional values
	// if (insert("plans_materials", values) == -1)
	// return false;
	// }
	// }
	// return true;
	// }

	public boolean updatePlanMaterials(long planId, long[] materialIds,
			long[] oldMaterialIds) {
		if (D)
			Log.v(TAG, "updatePlanMaterials");
		// check for and delete any materials that are no longer present
		for (long oldMaterialId : oldMaterialIds) {
			boolean delete = true;
			for (long materialId : materialIds) {
				if (oldMaterialId == materialId) {
					delete = false;
					break;
				}
			}
			if (delete) {
				int result = delete(
						"plans_materials",
						"plan = ? AND material = ?",
						new String[] { String.valueOf(planId),
								String.valueOf(oldMaterialId) });

				if (result == -1)
					return false;
			}
		}

		// check for and add any new materials
		ContentValues values = new ContentValues();
		for (long materialId : materialIds) {
			boolean insert = true;
			for (long oldMaterialId : oldMaterialIds) {
				if (materialId == oldMaterialId) {
					insert = false;
					break;
				}
			}
			if (insert) {
				values.clear();
				values.put("plan", planId);
				values.put("material", materialId);
				// values.put("manual_oel", planMaterial.getOel().getId());
				// values.put("manual_monster",
				// planMaterial.getMonster().getId());
				// values.put("manual_flow", planMaterial.getFlow());
				if (insert("plans_materials", values) == -1)
					return false;
			} else {
				// update
				values.clear();
				values.put("plan", planId);
				values.put("material", materialId);
				// values.put("manual_oel", planMaterial.getOel().getId());
				// values.put("manual_monster",
				// planMaterial.getMonster().getId());
				// values.put("manual_flow", planMaterial.getFlow());

				String[] whereArgs = new String[] { String.valueOf(planId),
						String.valueOf(materialId) };
				int rows = db.updateWithOnConflict("plans_materials", values,
						"plan = ? AND material = ?", whereArgs,
						SQLiteDatabase.CONFLICT_REPLACE);
				if (rows == 0) {
					return false;
				}

			}
		}
		return true;
	}

	public boolean updateMaterialDetails(long planId, long materialId,
			ContentValues values) {
		if (D)
			Log.v(TAG, "updateMaterialDetails");
		Boolean result;
		String[] whereArgs = { String.valueOf(planId),
				String.valueOf(materialId) };
		try {
			long updated = db.updateWithOnConflict("plans_materials", values,
					"plan = ? AND material = ?", whereArgs,
					SQLiteDatabase.CONFLICT_FAIL);
			result = true;
			if (D)
				Log.d(TAG, "update succeeded. " + updated + " row(s) updated");
		} catch (Exception e) {
			Log.w(TAG, "update failed with exception: " + e);
			result = false;
		}
		return result;
	}

	public boolean updateOel(long oelId, ContentValues values) {
		if (D)
			Log.d(TAG, "updateOel");
		Boolean result;
		String[] whereArgs = { String.valueOf(oelId) };

		try {
			long updated = db.updateWithOnConflict("oels", values, "_id = ?",
					whereArgs, SQLiteDatabase.CONFLICT_FAIL);
			result = true;
			if (D)
				Log.d(TAG, "update succeeded. " + updated + " row(s) updated");
		} catch (Exception e) {
			Log.w(TAG, "update failed with exception: " + e);
			result = false;
		}
		return result;
	}

	public long insertOel(ContentValues values) {
		if (D)
			Log.d(TAG, "insertOel");
		long newId;

		try {
			newId = db.insert("oels", null, values);
		} catch (Exception e) {
			Log.w(TAG, "insert failed with Exception: " + e);
			return -1;
		}
		return newId;
	}

	public int delete(String table, String whereClause, String[] whereArgs) {
		if (D)
			Log.v(TAG, "delete");
		int result;
		try {
			result = db.delete(table, whereClause, whereArgs);
			if (D)
				Log.d(TAG, "delete succeeded with " + result + " rows deleted");
		} catch (Exception e) {
			Log.w(TAG, "delete failed with exception: " + e);
			return -1;
		}
		return result;

	}

	public boolean deletePlanMaterials(long planId, ArrayList<Long> toDelete) {
		if (D)
			Log.v(TAG, "deletePlanMaterials");
		String planIdString = String.valueOf(planId);

		for (long id : toDelete) {

			String[] whereArgs = new String[] { planIdString,
					String.valueOf(id) };
			try {
				db.delete("plans_materials", "plan = ? AND material = ?",
						whereArgs);
			} catch (Exception e) {
				Log.w(TAG, "deletePlanMaterials failed with exception: " + e);
				return false;
			}
		}
		return true;
	}

	public boolean deleteOel(long id) {
		if (D)
			Log.d(TAG, "deleteOel");
		String idString = String.valueOf(id);

		String[] whereArgs = new String[] { idString };
		try {
			db.delete("oels", "_id = ?", whereArgs);
		} catch (Exception e) {
			Log.w(TAG, "deleteOel failed with exception:" + e);
			return false;
		}
		return true;
	}

	// private boolean savePlanPlanMaterials(long planId,
	// ArrayList<PlanMaterial> planMaterials) {
	// ContentValues values = new ContentValues();
	// for (PlanMaterial planMaterial : planMaterials) {
	// values.clear();
	// values.put("plan", planId);
	// values.put("material", planMaterial.getId());
	// values.put("manual_oel", planMaterial.getPlanOel().getId());
	// values.put("manual_monster", planMaterial.getPlanMonster().getId());
	// values.put("manual_flow", planMaterial.getFlow());
	// if (insert("plans_materials", values) == -1)
	// return false;
	// }
	// return true;
	// }

	public long queryForeignId(String table, String finalWhere,
			ArrayList<String> whereArgs) {

		long id = -1;
		String[] projection = { "_id" };
		String[] finalWhereArgs = new String[whereArgs.size()];
		finalWhereArgs = whereArgs.toArray(finalWhereArgs);
		if (D)
			Log.v(TAG, String.format("queryForeignId(%s, %s, %s, %s", table,
					projection[0], finalWhere, finalWhereArgs[0]));
		Cursor cursor = query(table, projection, finalWhere, finalWhereArgs);
		if (cursor == null) {
			// error logged in AssistantData.query
			return -1;
		} else if (cursor.moveToFirst()) {
			id = cursor.getLong(cursor.getColumnIndex(projection[0]));
		} else {
			// empty cursor, return with mLastParsed
			// still null
			Log.e(TAG, "Query returned empty cursor for WHERE " + finalWhere
					+ ", " + whereArgs.toString());
		}
		cursor.close();
		return id;
	}

	// public Cursor selectPlans() {
	// if (D)
	// Log.v(TAG, "selectPlans");
	// // TODO: consolidate select functions
	// Cursor cursor;
	// String sqlPlans = "SELECT * FROM plans WHERE parent_plan = ?";
	// String[] argsPlans = { String.valueOf(parent) };
	//
	// try {
	// cursor = db.rawQuery(sqlPlans, argsPlans);
	// } catch (Exception e) {
	// Log.e(TAG, "rawQuery " + sqlPlans + "; " + argsPlans[0] + " failed");
	// return null;
	// }
	//
	// if (D)
	// logCursor(cursor);
	// return cursor;
	// }

	// public Cursor select(String table, String[] columns) {
	// if (D)
	// Log.v(TAG, "select");
	//
	// Cursor cursor;
	//
	// try {
	// cursor = db.query(table, columns, null, null, null, null, null);
	// } catch (Exception e) {
	// Log.e(TAG, "select failed with exception: " + e);
	// return null;
	// }
	// if (D)
	// logCursor(cursor);
	// return cursor;
	// }

	private Cursor select(String table, String[] columns, String where,
			String[] whereArgs) {
		if (D)
			Log.d(TAG, "select");

		Cursor cursor;

		try {
			cursor = db.query(table, columns, where, whereArgs, null, null,
					null);
		} catch (Exception e) {
			Log.e(TAG, "select failed with exception: " + e);
			return null;
		}
		if (D)
			logCursor(cursor);
		return cursor;
	}

	// private Cursor selectMaterials(long plan_id) {
	// if (D)
	// Log.d(TAG, "selectMaterials with plan_id: '" + plan_id + "'");
	// Cursor cursor;
	// String sqlMaterials =
	// "SELECT materials.*, plans_materials.manual_oel, plans_materials.manual_monster, plans_materials.manual_flow FROM materials INNER JOIN plans_materials ON materials._id = plans_materials.material INNER JOIN plans ON plans_materials.plan = plans._id WHERE plans._id = ?";
	// String[] argsMaterials = { String.valueOf(plan_id) };
	//
	// try {
	// cursor = db.rawQuery(sqlMaterials, argsMaterials);
	// } catch (Exception e) {
	// Log.e(TAG, "exception: " + e);
	// Log.e(TAG, "rawQuery " + sqlMaterials + "; " + argsMaterials[0]
	// + " failed");
	// return null;
	// }
	//
	// if (D)
	// logCursor(cursor);
	// return cursor;
	// }

	private ArrayList<Long> selectMaterialsIds(long planId) {
		if (D)
			Log.d(TAG, "selectMaterialsIds");
		String sql = "SELECT material FROM plans_materials WHERE plan = ?";
		String[] selectionArgs = new String[] { String.valueOf(planId) };
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		ArrayList<Long> materialIds = new ArrayList<Long>(cursor.getCount());
		int index = cursor.getColumnIndex("material");
		if (cursor.moveToFirst()) {
			do {
				materialIds.add(cursor.getLong(index));
			} while (cursor.moveToNext());
			cursor.close();
		} else {
			if (D)
				Log.i(TAG, "no materials");
		}

		return materialIds;
	}

	// public ArrayList<FetalMaterial> selectFetalMaterials(long plan_id) {
	// if (D)
	// Log.d(TAG, "selectMaterials with plan_id: '" + plan_id + "'");
	// Cursor cursor;
	// String sqlMaterials =
	// "SELECT materials.*, plans_materials.manual_oel, plans_materials.manual_monster, plans_materials.manual_flow FROM materials INNER JOIN plans_materials ON materials._id = plans_materials.material INNER JOIN plans ON plans_materials.plan = plans._id WHERE plans._id = ?";
	// String[] argsMaterials = { String.valueOf(plan_id) };
	//
	// try {
	// cursor = db.rawQuery(sqlMaterials, argsMaterials);
	// } catch (Exception e) {
	// Log.e(TAG, "exception: " + e);
	// Log.e(TAG, "rawQuery " + sqlMaterials + "; " + argsMaterials[0]
	// + " failed");
	// return null;
	// }
	//
	// ArrayList<FetalMaterial> fetalMaterials = null;
	// if (cursor.moveToFirst()) {
	// fetalMaterials = new ArrayList<FetalMaterial>(cursor.getCount());
	// do {
	// fetalMaterials.add(new FetalMaterial(cursor));
	// } while (cursor.moveToNext());
	// }
	//
	// if (D)
	// logCursor(cursor);
	// return fetalMaterials;
	// }

	// private Cursor selectMaterials(ArrayList<Long> materialsIds) {
	// if (D)
	// Log.d(TAG, "selectMaterials (arraylist)");
	// int size = materialsIds.size();
	// Cursor cursor;
	//
	// String where = "plans_materials.material IN (";
	// String[] selectionArgs = new String[size];
	//
	// for (int i = 0; i < size; i++) {
	// where = where + "?,";
	// selectionArgs[i] = String.valueOf(materialsIds.get(i));
	// if (D)
	// Log.v(TAG, "selectionArgs[" + i + "]: " + selectionArgs[i]);
	// }
	// where = where.substring(0, where.length() - 1);
	// where = where + ")";
	// String s =
	// "SELECT materials.*, plans_materials.manual_oel, plans_materials.manual_monster, plans_materials.manual_flow FROM materials INNER JOIN plans_materials ON materials._id = plans_materials.material INNER JOIN plans ON plans_materials.plan = plans._id WHERE ";
	// String sql = s + where;
	//
	// try {
	// cursor = db.rawQuery(sql, selectionArgs);
	// } catch (Exception e) {
	// Log.e(TAG, "exception: " + e);
	// Log.e(TAG, "rawQuery " + sql + "; " + selectionArgs[0] + " failed");
	// return null;
	// }
	//
	// if (D)
	// logCursor(cursor);
	// return cursor;
	// }

	// private ArrayList<Long> selectMaterialIds(long planId) {
	// if (D)
	// Log.d(TAG, "selectMaterialIds");
	// Cursor cursor = db.query("plans_materials",
	// new String[] { "material" }, "plan = ?",
	// new String[] { String.valueOf(planId) }, null, null, null);
	// ArrayList<Long> materialIds = null;
	// if (cursor.moveToFirst()) {
	// materialIds = new ArrayList<Long>(cursor.getCount());
	// int indexId = cursor.getColumnIndex("material");
	// do {
	// materialIds.add(cursor.getLong(indexId));
	// } while (cursor.moveToNext());
	// }
	// return materialIds;
	// }

	// public ArrayList<Monster> selectPlanMonsters(
	// ArrayList<FetalMaterial> fetalMaterials) {
	// if (D)
	// Log.d(TAG, "selectMonsters");
	//
	// int size = fetalMaterials.size();
	// String where = "material IN (";
	// String[] selectionArgs = new String[size];
	//
	// for (int i = 0; i < size; i++) {
	// where = where + "?,";
	// selectionArgs[i] = String.valueOf(fetalMaterials.get(i).getId());
	// if (D)
	// Log.v(TAG, "selectionArgs[" + i + "]: " + selectionArgs[i]);
	// }
	// where = where.substring(0, where.length() - 1);
	// where = where + ")";
	//
	// String sql =
	// "SELECT monsters._id, monsters.loq, monsters.min_flow, monsters.max_flow, materials._id AS materials_id, materials.name AS materials_name, materials.cas AS materials_cas, materials.mol_w AS materials_mol_w, labs.name AS labs_name, units.name AS units_name, equipment_types.name AS equipment_types_name, media.name AS media_name, media_types.name AS media_types_name, methods.name AS methods_name FROM monsters INNER JOIN materials ON monsters.material=materials._id INNER JOIN labs ON monsters.lab=labs._id INNER JOIN units ON monsters.loq_unit=units._id INNER JOIN equipment_types ON monsters.equipment_type=equipment_types._id INNER JOIN media ON monsters.media=media._id INNER JOIN media_types ON media.media_type=media_types._id INNER JOIN methods ON monsters.method=methods._id WHERE "
	// + where;
	//
	// Cursor cursor = rawQuery(sql, selectionArgs);
	// ArrayList<Monster> planMonsters = Monster
	// .createPlanMonsters(cursor);
	// if (D) {
	// Log.v(TAG, "logging new monsters");
	// Monster.logPlanMonsters(planMonsters);
	// }
	// return planMonsters;
	// }

	private ArrayList<Monster> selectPlanMonsters(long materialId) {
		if (D)
			Log.d(TAG, "selectMonsters");

		// int size = fetalMaterials.size();
		String where = "material = ?";
		String[] selectionArgs = new String[] { String.valueOf(materialId) };

		String sql = "SELECT monsters._id, monsters.loq, monsters.min_flow, monsters.max_flow, materials._id AS materials_id, materials.name AS materials_name, materials.cas AS materials_cas, materials.mol_w AS materials_mol_w, labs.name AS labs_name, units.name AS units_name, equipment_types.name AS equipment_types_name, media.name AS media_name, media_types.name AS media_types_name, methods.name AS methods_name FROM monsters INNER JOIN materials ON monsters.material=materials._id INNER JOIN labs ON monsters.lab=labs._id INNER JOIN units ON monsters.loq_unit=units._id INNER JOIN equipment_types ON monsters.equipment_type=equipment_types._id INNER JOIN media ON monsters.media=media._id INNER JOIN media_types ON media.media_type=media_types._id INNER JOIN methods ON monsters.method=methods._id WHERE "
				+ where;

		Cursor cursor = rawQuery(sql, selectionArgs);
		ArrayList<Monster> monsters = Monster.createPlanMonsters(cursor);
		if (D) {
			Log.v(TAG, "logging new monsters");
			Monster.logPlanMonsters(monsters);
		}
		cursor.close();
		return monsters;
	}

	// public ArrayList<Oel> selectPlanOels(
	// ArrayList<FetalMaterial> fetalMaterials) {
	// if (D)
	// Log.d(TAG, "selectOels");
	//
	// int size = fetalMaterials.size();
	// String where = null;
	// String[] selectionArgs = new String[size];
	//
	// where = " WHERE material IN (";
	//
	// // add needed question marks to WHERE String and corresponding
	// // arguments to selectionArgs[]
	// for (int i = 0; i < size; i++) {
	// where = where + "?,";
	// selectionArgs[i] = String.valueOf(fetalMaterials.get(i).getId());
	// }
	// // delete final comma, add )
	// where = where.substring(0, where.length() - 1);
	// where = where + ")";
	//
	// String sql =
	// "SELECT oels._id, oels.oel_source, oels.material, oels.value, oels.oel_unit, oels.oel_type, oel_sources.name AS oel_sources_name, oel_types.name AS oel_types_name, units.name AS units_name FROM oels INNER JOIN oel_sources ON oels.oel_source=oel_sources._id INNER JOIN oel_types ON oels.oel_type=oel_types._id INNER JOIN units ON oels.oel_unit=units._id"
	// + where;
	//
	// Cursor cursor = rawQuery(sql, selectionArgs);
	// ArrayList<Oel> planOels = new ArrayList<Oel>();
	// if (cursor.moveToFirst()) {
	// do {
	// planOels.add(new Oel(cursor));
	// } while (cursor.moveToNext());
	// } else {
	// Log.e(TAG, "selectOels returned empty cursor");
	// }
	// if (D)
	// Log.v(TAG, "oels.size(): " + planOels.size());
	// return planOels;
	// }

	private ArrayList<Oel> selectPlanOels(long materialId) {
		if (D)
			Log.d(TAG, "selectOels");

		String where = "material = ?";
		String[] selectionArgs = new String[] { String.valueOf(materialId) };

		String sql = "SELECT oels._id, oels.oel_source, oels.material, oels.value, oels.oel_unit, oels.oel_type, oel_sources.name AS oel_sources_name, oel_types.name AS oel_types_name, units.name AS units_name FROM oels INNER JOIN oel_sources ON oels.oel_source=oel_sources._id INNER JOIN oel_types ON oels.oel_type=oel_types._id INNER JOIN units ON oels.oel_unit=units._id WHERE "
				+ where;

		Cursor cursor = rawQuery(sql, selectionArgs);
		ArrayList<Oel> oels = new ArrayList<Oel>();
		if (cursor.moveToFirst()) {
			int indexSource = cursor.getColumnIndex("oel_source");
			int indexType = cursor.getColumnIndex("oel_type");
			int indexUnit = cursor.getColumnIndex("oel_unit");
			OelSource oelSource = null;
			OelType oelType = null;
			Unit unit = null;
			do {
				long oelSourceId = cursor.getLong(indexSource);
				long oelTypeId = cursor.getLong(indexType);
				long unitId = cursor.getLong(indexUnit);
				for (OelSource os : AssistantApp.allOelSources) {
					if (oelSourceId == os.getId()) {
						oelSource = os;
					}
				}
				for (OelType ot : AssistantApp.allOelTypes) {
					if (oelTypeId == ot.getId()) {
						oelType = ot;
					}
				}
				for (Unit u : AssistantApp.allUnits) {
					if (unitId == u.getId()) {
						unit = u;
					}
				}
				oels.add(new Oel(cursor, oelSource, oelType, unit));
			} while (cursor.moveToNext());
		} else {
			Log.e(TAG, "selectOels returned empty cursor");
		}
		cursor.close();

		if (D)
			Log.v(TAG, "oels.size(): " + oels.size());
		return oels;
	}

	// public Oel selectDefaultOel() {
	// if (D)
	// Log.d(TAG, "selectDefaultOel");
	//
	// // TODO: fix hard-coded
	// String[] selectionArgs = new String[] { "USER" };
	//
	// OelSource oelSource = -1;
	// long oelTypeId = -1;
	// long unitId = -1;
	//
	// Cursor cursor = db.query("oel_sources", null, "name = ?",
	// selectionArgs, null, null, null, "1");
	// logCursor(cursor);
	// int index = cursor.getColumnIndex("_id");
	// if (cursor.moveToFirst()) {
	// oelSourceId = cursor.getLong(index);
	// }
	// cursor.close();
	//
	// // TODO: fix hard-coded
	// selectionArgs = new String[] { "TWA" };
	//
	// cursor = db.query("oel_types", null, "name = ?", selectionArgs, null,
	// null, null, "1");
	// index = cursor.getColumnIndex("_id");
	// if (cursor.moveToFirst()) {
	// oelTypeId = cursor.getLong(index);
	// }
	// cursor.close();
	//
	// // TODO: fix hard-coded
	// selectionArgs = new String[] { "mg/m3" };
	//
	// cursor = db.query("units", null, "name = ?", selectionArgs, null, null,
	// null, "1");
	// index = cursor.getColumnIndex("_id");
	// if (cursor.moveToFirst()) {
	// unitId = cursor.getLong(index);
	// }
	// cursor.close();
	//
	// if (oelSourceId == -1 || oelTypeId == -1 || unitId == -1) {
	// Log.e(TAG, "a select operation failed");
	// return null;
	// }
	// Oel oel = new Oel(oelSourceId, oelTypeId, unitId);
	//
	// return oel;
	// }

	public PlanMaterial selectPlanMaterial(long planId,
			ListMaterial listMaterial) {
		if (D)
			Log.d(TAG, "selectPlanMaterial");
		// TODO: select from plans_materials as well as materials

		ArrayList<Oel> oels = selectPlanOels(listMaterial.getId());
		ArrayList<Monster> monsters = selectPlanMonsters(listMaterial.getId());

		String sql = "SELECT manual_monster, manual_oel, manual_flow FROM plans_materials WHERE plans_materials.material = ? AND plans_materials.plan = ?";
		String[] selectionArgs = new String[] {
				String.valueOf(listMaterial.getId()), String.valueOf(planId) };
		Cursor cursor = rawQuery(sql, selectionArgs);
		PlanMaterial planMaterial = null;
		if (cursor.moveToFirst()) {
			planMaterial = new PlanMaterial(listMaterial, cursor, monsters,
					oels);
		}
		cursor.close();

		return planMaterial;
	}

	public ArrayList<Plan> selectPlans() {
		if (D)
			Log.d(TAG, "selectListPlans");

		Cursor cursorPlans = select("plans", null, null, null);
		ArrayList<Plan> plans = new ArrayList<Plan>(cursorPlans.getCount());
		if (cursorPlans.moveToFirst()) {
			// Cursor cursorMaterials;
			do {
				int planIdIndex = cursorPlans.getColumnIndex("_id");
				long planId = cursorPlans.getLong(planIdIndex);
				ArrayList<Long> materialsIds = selectMaterialsIds(planId);
				ArrayList<PlanMaterial> planMaterials = new ArrayList<PlanMaterial>(
						materialsIds.size());
				// Cursor cursorMaterials = selectMaterials(planId);
				// if (cursorMaterials.moveToFirst()) {
				// ArrayList<ListMaterial> listMaterials = new
				// ArrayList<ListMaterial>(
				// cursorMaterials.getCount());
				// do {
				// listMaterials.add(new ListMaterial(cursorMaterials));
				// } while (cursorMaterials.moveToNext());
				//
				// listPlans.add(new ListPlan(cursorPlans, listMaterials));
				//
				// } else {
				// if (D)
				// Log.d(TAG, "cursorMaterials is empty");
				// listPlans.add(new ListPlan(cursorPlans,
				// new ArrayList<ListMaterial>()));
				// }
				// cursorMaterials.close();
				for (Long materialId : materialsIds) {
					for (ListMaterial listMaterial : AssistantApp.allListMaterials) {
						if (materialId == listMaterial.getId()) {
							planMaterials.add(selectPlanMaterial(planId,
									listMaterial));
						}
					}
				}
				plans.add(new Plan(cursorPlans, planMaterials));
			} while (cursorPlans.moveToNext());
			cursorPlans.close();

		} else {
			Log.i(TAG, "cursorPlans is empty");
		}

		return plans;
	}

	// private String[] selectOelUnits() {
	// // TODO: select units which are appropriate for use with an oel (parts
	// // per or mass/volume)
	// return null;
	// }

	public ArrayList<ListMaterial> selectAllListMaterials() {
		if (D)
			Log.d(TAG, "selectAllMaterials");
		Cursor cursor = selectTable("materials");
		ArrayList<ListMaterial> listMaterials = new ArrayList<ListMaterial>(
				cursor.getCount());
		if (cursor.moveToFirst()) {
			do {
				listMaterials.add(new ListMaterial(cursor));
			} while (cursor.moveToNext());

		} else {
			Log.e(TAG, "cursor from selectTable('materials') is empty");
		}
		cursor.close();
		return listMaterials;
	}

	public ListMaterial selectListMaterial(long id) {
		String where = "_id = ?";
		String[] whereArgs = new String[] { String.valueOf(id) };
		Cursor cursor = select("materials", null, where, whereArgs);
		ListMaterial listMaterial = new ListMaterial(cursor);
		cursor.close();
		return listMaterial;
	}

	// private static long[] getIds(Cursor cursor) {
	// long[] ids = new long[0];
	// if (cursor.moveToFirst()) {
	// int size = cursor.getCount();
	// ids = new long[size];
	// for (int i = 0; i < size; i++) {
	// cursor.moveToPosition(i);
	// ids[i] = cursor.getLong(cursor.getColumnIndex("_id"));
	// }
	// }
	// return ids;
	// }

	private Cursor query(String table, String[] columns, String where,
			String[] whereArgs) {
		if (D)
			Log.v(TAG, "query");
		Cursor cursor;
		try {
			cursor = db.query(table, columns, where, whereArgs, null, null,
					null);
		} catch (Exception e) {
			Log.e(TAG, "query failed with exception: " + e.toString());
			return null;
		}

		if (D)
			logCursor(cursor);
		return cursor;
	}

	// private Cursor query(String table, String[] columns, String where,
	// String[] whereArgs, String groupBy, String having, String orderBy) {
	// if (D)
	// Log.v(TAG, "query");
	// Cursor cursor;
	// try {
	// cursor = db.query(table, columns, where, whereArgs, groupBy,
	// having, orderBy);
	// } catch (Exception e) {
	// Log.e(TAG, "query failed with exception: " + e.toString());
	// return null;
	// }
	//
	// if (D)
	// logCursor(cursor);
	// return cursor;
	// }

	private Cursor rawQuery(String sql, String[] selectionArgs) {
		if (D)
			Log.v(TAG, "rawQuery");
		Cursor cursor;
		try {
			cursor = db.rawQuery(sql, selectionArgs);
		} catch (Exception e) {
			Log.e(TAG, "rawQuery failed with exception: " + e);
			return null;
		}

		if (D)
			logCursor(cursor);
		return cursor;
	}

	public Cursor selectTable(String tableName) {
		if (D)
			Log.v(TAG, "selectTable with tableName: '" + tableName + "'");
		String table = tableName;
		String[] columns = null;
		String where = null;
		String[] whereArgs = null;
		Cursor cursor = db.query(table, columns, where, whereArgs, null, null,
				null);
		if (D)
			logCursor(cursor);

		return cursor;
	}

	private void logCursor(Cursor cursor) {
		Log.v(TAG, "logCursor");

		if (cursor.moveToFirst()) {
			String[] columns = cursor.getColumnNames();
			String rowHeaders = "H| ";
			for (String column : columns) {
				rowHeaders = rowHeaders + column + ", ";
			}
			Log.v(TAG, rowHeaders);
			do {
				String row = (cursor.getPosition() + "| ");
				for (String column : columns) {
					row = row + cursor.getString(cursor.getColumnIndex(column))
							+ ", ";
				}
				Log.v(TAG, row);
			} while (cursor.moveToNext());
		} else {
			Log.w(TAG, "cursor is empty");
		}

	}

	private void logContentValues(ContentValues contentValues) {
		Log.v(TAG, "logContentValues");
		Set<Entry<String, Object>> entries = contentValues.valueSet();
		for (Entry<String, Object> entry : entries) {
			Log.v(TAG,
					"Key: " + entry.getKey() + " and value: "
							+ entry.getValue());
		}
	}

	public String[] getTables() {
		if (D)
			Log.v(TAG, "getTables");
		Cursor tables = db.rawQuery(
				"SELECT * FROM sqlite_master WHERE type = 'table'", null);
		ArrayList<String> result = new ArrayList<String>();
		if (tables.moveToFirst()) {

			do {
				result.add(tables.getString(tables.getColumnIndex("name")));
			} while (tables.moveToNext());
		}
		String[] finalResult = result.toArray(new String[result.size()]);
		return finalResult;
	}

	public void nuke() {
		Log.w(TAG, "Nuked = " + context.deleteDatabase(DB_NAME));
	}

	/**
	 * Drops the table then runs onCreate to recreate it.
	 * 
	 * @param table
	 */
	public void dropTable(String table) {
		Log.w(TAG, "dropTable '" + table + "'");
		db = dbHelper.getWritableDatabase();

		try {
			db.execSQL("DROP TABLE " + table);
		} catch (SQLException e) {
			Log.e(TAG, "drop table failed with exception: " + e.toString());
		}
		dbHelper.onCreate(db);
		db.close();
	}

	class DbHelper extends SQLiteOpenHelper {

		public DbHelper() {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.d(TAG, "onCreate database");

			db.execSQL("CREATE TABLE IF NOT EXISTS materials (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT NULL, cas TEXT NOT NULL, mol_w REAL NOT NULL, UNIQUE(cas))");

			db.execSQL("CREATE TABLE IF NOT EXISTS oel_sources (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT NOT NULL, UNIQUE (name))");

			db.execSQL("CREATE TABLE IF NOT EXISTS oel_types (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT NOT NULL, UNIQUE (name))");

			db.execSQL("CREATE TABLE IF NOT EXISTS equipment_types (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT NOT NULL, UNIQUE (name))");

			db.execSQL("CREATE TABLE IF NOT EXISTS media_types (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT NOT NULL, UNIQUE (name))");

			db.execSQL("CREATE TABLE IF NOT EXISTS units (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT NOT NULL, UNIQUE (name))");

			db.execSQL("CREATE TABLE IF NOT EXISTS oels (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, oel_type int NOT NULL, oel_source INT NOT NULL, value INT NOT NULL, oel_unit INT NOT NULL, material INT NOT NULL, FOREIGN KEY(oel_type) REFERENCES oel_types(_id) ON DELETE CASCADE, FOREIGN KEY(material) REFERENCES materials(_id) ON DELETE CASCADE, FOREIGN KEY(oel_source) REFERENCES oel_sources(_id) ON DELETE SET NULL, FOREIGN KEY(oel_unit) REFERENCES units(_id) ON DELETE CASCADE)");

			db.execSQL("CREATE TABLE IF NOT EXISTS media (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT NOT NULL, media_type INT NOT NULL, FOREIGN KEY(media_type) REFERENCES media_types(_id) ON DELETE SET NULL, UNIQUE(name))");

			db.execSQL("CREATE TABLE IF NOT EXISTS methods (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT NOT NULL, UNIQUE (name))");

			db.execSQL("CREATE TABLE IF NOT EXISTS labs (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT NOT NULL, UNIQUE (name))");

			db.execSQL("CREATE TABLE IF NOT EXISTS monsters (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, material INTEGER NOT NULL, lab INT NOT NULL, loq REAL NOT NULL, loq_unit INT NOT NULL, media INT NOT NULL, equipment_type INT NULL, method INT NOT NULL, min_flow REAL NOT NULL, max_flow REAL NOT NULL, UNIQUE(material, lab, loq_unit, media, method), FOREIGN KEY (material) REFERENCES materials(_id) ON DELETE CASCADE, FOREIGN KEY (lab) REFERENCES labs(_id) ON DELETE CASCADE, FOREIGN KEY (loq_unit) REFERENCES units(_id) ON DELETE CASCADE, FOREIGN KEY (media) REFERENCES media(_id) ON DELETE CASCADE, FOREIGN KEY (equipment_type) REFERENCES equipment_types(_id) ON DELETE SET NULL, FOREIGN KEY(method) REFERENCES methods(_id) ON DELETE CASCADE)");

			db.execSQL("CREATE TABLE IF NOT EXISTS plans (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, loq_percent INT NULL, min_time INT NULL, max_time INT NULL, pref_time INT NULL, parent_plan INT NOT NULL, name TEXT NOT NULL, created INT NOT NULL, edited INT NOT NULL, UNIQUE(name, parent_plan))");

			db.execSQL("CREATE TABLE IF NOT EXISTS plans_materials (plan INTEGER NOT NULL, material INT NOT NULL, manual_oel INT NOT NULL DEFAULT 0, manual_monster INT NOT NULL DEFAULT 0, manual_flow REAL NOT NULL DEFAULT 0, PRIMARY KEY (plan, material), FOREIGN KEY(plan) REFERENCES plans(_id) ON DELETE CASCADE, FOREIGN KEY(material) REFERENCES materials(_id) ON DELETE CASCADE)");

			// db.execSQL("CREATE INDEX named_locations_i ON named_locations(name)");

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub

		}

		/**
		 * Sets PRAGMA foreign_keys=ON
		 */
		@Override
		public void onOpen(SQLiteDatabase db) {
			super.onOpen(db);
			if (!db.isReadOnly()) {
				db.execSQL("PRAGMA foreign_keys=ON;");
			}
		}

	}

}
