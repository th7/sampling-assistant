package com.hartland.sampling.assistant;

import android.database.Cursor;

public class OelSource {
	private final String mName;
	private final long mId;
	private final boolean mIsProtected;

	public OelSource(Cursor cursor) {
		int indexName = cursor.getColumnIndex("name");
		int indexId = cursor.getColumnIndex("_id");

		mName = cursor.getString(indexName);
		mId = cursor.getLong(indexId);
		if (mName.equals(OelEditActivity.EDITABLE_OEL_SOURCE)) {
			mIsProtected = false;
		} else {
			mIsProtected = true;
		}
	}

	public String getName() {
		return mName;
	}

	public long getId() {
		return mId;
	}

	public boolean isProtected() {
		return mIsProtected;
	}
}
