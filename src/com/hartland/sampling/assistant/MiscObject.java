package com.hartland.sampling.assistant;

import android.database.Cursor;

public abstract class MiscObject {
	private final String mName;
	private final long mId;

	public MiscObject(Cursor cursor) {
		int indexName = cursor.getColumnIndex("name");
		int indexId = cursor.getColumnIndex("_id");

		mName = cursor.getString(indexName);
		mId = cursor.getLong(indexId);
	}

	public String getName() {
		return mName;
	}

	public long getId() {
		return mId;
	}
}
