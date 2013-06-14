package com.hartland.sampling.assistant.utilities;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.ContentValues;
import android.util.Log;

import com.hartland.sampling.assistant.AssistantApp;
import com.hartland.sampling.assistant.AssistantData;

public final class ParserHelper {
	private static final String TAG = "ParserHelper";	
	// holds data from most recently parsed .csv file
	private static ArrayList<ContentValues> mLastParsed;
	
	/**
	 * Parses .csv file specified by
	 * intent.getStringExtra(WorkerService.FILE_PATH). The first row of the .csv
	 * must be keys which match the column title from the corresponding table in
	 * the DB (CHECK CASE!!).
	 * 
	 * <p>
	 * For foreign keys, a single header may have three items separated by ":"
	 * as foreign_table:foreign_column:local_column in that order. For example,
	 * "materials:cas:material" will return the _id from a row in materials
	 * WHERE cas = {value} and then insert that _id in place of {value} in the
	 * column material.
	 * <p>
	 * A single header may also have two items as foreign_table:foreign_column
	 * to start a chain of building where arguments, with
	 * foreign_column:local_column to finish the chain.
	 * 
	 * 
	 * <p>
	 * Columns referencing foreign tables should have the header
	 * foreign_table:foreign_column:local_column.
	 * 
	 * <p>
	 * Chained sets of arguments for where statements should have the headers
	 * foreign_table:foreign_column1, foreign_column2, foreign_column3, ...,
	 * foreign_column4:local_column. For example, "plans:name, parent_plan:plan"
	 * 
	 * @param intent
	 * @return ContentValues
	 */
	public static void parseCsv(String filePath) {
		Log.d(TAG, "parseCsv");

		AssistantData mAssistantData = AssistantApp.getAssistantData();
		
		mAssistantData.dbOpenBegin();

		Boolean first = true;
		ArrayList<String> keys = new ArrayList<String>();
		ArrayList<ContentValues> contentValues = new ArrayList<ContentValues>();
		String line;

		mLastParsed = null;
		try {
			Log.v(TAG, "inside fis/reader try");
			FileInputStream fis = new FileInputStream(filePath);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					fis));
			try {
				Log.v(TAG, "inside readLine try");
				while ((line = reader.readLine()) != null) {
					Log.v(TAG, "inside reader.readLine while");

					// Check if first row, do header stuff
					if (first) {
						for (String token : line.split(",")) {
							Log.v(TAG, "parsing item '" + token
									+ "' in first row");
							keys.add(token);
							Log.v(TAG, "keys.get(" + (keys.size() - 1)
									+ ") == " + token);
						}
						first = false;
					} else {
						// Not first row, build ContentValues objects
						Log.v(TAG, "begin parsing row(base 1) "
								+ (contentValues.size() + 2));

						int keysIndex = 0;
						ContentValues values = new ContentValues();
						Boolean buildingWhereArgs = false;
						String table = null;
						ArrayList<String> whereArgs = new ArrayList<String>();
						String where = null;

						// loops through columns in this row
						for (String token : line.split(",")) {
							Log.v(TAG,
									"parsing item '" + token
											+ "' in row(base 1) "
											+ (contentValues.size() + 2));
							if (token == "" || token.length() == 0) {
								token = null;
								Log.v(TAG, "token set to null");
							}

							if (keys.get(keysIndex).contains(":")) {
								// is a foreign column, get _id and values.put
								// key(from split) and id
								ArrayList<String> foreignKeyArrayList = splitForeignKey(keys
										.get(keysIndex));
								if (foreignKeyArrayList.size() == 2
										&& buildingWhereArgs == false) {
									// start building args
									Log.d(TAG, "start building args");
									table = foreignKeyArrayList.get(0);
									whereArgs.clear();
									where = null;
									whereArgs.add(token);
									where = buildWhere(where,
											foreignKeyArrayList.get(1));
									buildingWhereArgs = true;
								} else if (foreignKeyArrayList.size() == 2
										&& buildingWhereArgs) {
									// finish building args and run query
									Log.v(TAG,
											"finish building args and run query");
									whereArgs.add(token);
									where = buildWhere(where,
											foreignKeyArrayList.get(0));

									long id = mAssistantData.queryForeignId(
											table, where, whereArgs);
									if (id == -1) {
										Log.e(TAG, "Query error. Returning.");
										reader.close();
										mAssistantData.dbEndClose();
										return;
									}
									Log.v(TAG,
											"values.put with key == foreignKeyArrayList.get(1) == "
													+ foreignKeyArrayList
															.get(1)
													+ " and value == " + id);
									values.put(foreignKeyArrayList.get(1), id);
									buildingWhereArgs = false;
								} else if (foreignKeyArrayList.size() == 3
										&& buildingWhereArgs == false) {
									table = foreignKeyArrayList.get(0);
									where = foreignKeyArrayList.get(1) + " = ?";

									whereArgs.clear();
									whereArgs.add(token);
									long id = mAssistantData.queryForeignId(
											table, where, whereArgs);
									if (id == -1) {
										Log.e(TAG, "Query error. Returning.");
										reader.close();
										mAssistantData.dbEndClose();
										return;
									}

									Log.v(TAG,
											"values.put with key == foreignKeyArrayList.get(2) == "
													+ foreignKeyArrayList
															.get(2)
													+ " and value == " + id);
									values.put(foreignKeyArrayList.get(2), id);
								} else {

									Log.e(TAG,
											"Unexpected number of foreign parameters == "
													+ foreignKeyArrayList
															.size()
													+ " and buildingWhereArgs == "
													+ buildingWhereArgs
													+ ". Returning.");
									reader.close();
									mAssistantData.dbEndClose();
									return;
								}
							} else if (buildingWhereArgs) {
								// continue building args
								Log.v(TAG, "continue building args");
								whereArgs.add(token);
								where = buildWhere(where, keys.get(keysIndex));
							} else {
								// not a foreign column, values.put standard key
								// and token
								Log.v(TAG,
										"values.put with key == "
												+ keys.get(keysIndex)
												+ " and value == " + token);
								values.put(keys.get(keysIndex), token);
							}
							keysIndex++;
						}
						contentValues.add(values);
					}
				}
				reader.close();
				mLastParsed = contentValues;
				mAssistantData.dbEndClose();
			} catch (IOException e) {
				Log.e(TAG, "BufferedReader exception " + e.toString());
			}
		} catch (FileNotFoundException e) {
			Log.e(TAG, "FileInputStream exception " + e.toString());
		}
	}
	
	/**
	 * Appends to a where statement using AND. If where is null, it starts the
	 * statement.
	 */
	private static String buildWhere(String where, String toAppend) {
		if (toAppend != null) {
			if (where == null) {
				where = toAppend + " = ?";
			} else {
				where = where + " AND " + toAppend + " = ?";
			}
		} else {
			Log.e(TAG, "null append value passed to buildWhere");
		}
		return where;
	}

	/**
	 * Returns an arraylist with the header split at ":".
	 * 
	 * @param String
	 * @return ArrayList<String>
	 */
	private static ArrayList<String> splitForeignKey(String toSplit) {
		Log.v(TAG, "splitForeignKey with string " + toSplit);
		ArrayList<String> tableColumn = new ArrayList<String>();
		for (String token : toSplit.split(":")) {
			tableColumn.add(token);
		}
		Log.v(TAG, "split to " + tableColumn.toString());
		return tableColumn;
	}

	public static ArrayList<ContentValues> getLastParsed() {
		return mLastParsed;
	}
}
