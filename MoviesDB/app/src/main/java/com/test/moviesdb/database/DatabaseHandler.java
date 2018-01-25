package com.test.moviesdb.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.test.moviesdb.activity.MainActivity;

public class DatabaseHandler extends SQLiteOpenHelper {

	private static final String TAG = DatabaseHandler.class.getSimpleName();

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "suggestionsManager";

	// Contacts table name
	private static final String TABLE_SUGGESTIONS = "suggestions";

	// Contacts Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_SUGGESTION = "suggestion";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_SUGGESTIONS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_SUGGESTION + " TEXT UNIQUE" + ")";
		db.execSQL(CREATE_CONTACTS_TABLE);
	}
	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUGGESTIONS);
		// Create tables again
		onCreate(db);
	}

	/**
	 * Database necessary helper operations
	 */

	// Adding new suggestion
	public void addSuggestion(String suggestion) {

		try {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(KEY_SUGGESTION, suggestion.toLowerCase());
			// Inserting Row
			db.insert(TABLE_SUGGESTIONS, null, values);
			db.close(); // Closing database connection
		}catch (Exception e)
		{
			Log.i(TAG,e.toString());
		}


	}

	
	// Getting to 10 suggestions
	public List<String> getLastt10Suggestions() {
		List<String> suggestionsList = new ArrayList<String>();
		// Select All Query
		//String selectQuery = "SELECT  * FROM " + TABLE_SUGGESTIONS;

		String selectQuery = "SELECT * FROM " + TABLE_SUGGESTIONS+" ORDER BY "+KEY_ID+" DESC LIMIT 10";

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				suggestionsList.add(cursor.getString(1));
			} while (cursor.moveToNext());
		}

		return suggestionsList;
	}
}
