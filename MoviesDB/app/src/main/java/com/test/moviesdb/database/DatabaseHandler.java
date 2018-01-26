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

/*
 * Database handler class for the purpose of storage and retrieval of suggestions.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

	private static final String TAG = DatabaseHandler.class.getSimpleName();

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "suggestionsManager";

	// Suggestions table name
	private static final String TABLE_SUGGESTIONS = "suggestions";

	// Suggestions Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_SUGGESTION = "suggestion";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_SUGGESTIONS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_SUGGESTION + " TEXT " + ")";
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
			//delete suggestion if exists so it can be added at the end
			deleteSuggestion(suggestion);

			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(KEY_SUGGESTION, suggestion.toLowerCase());

			// Inserting suggestion
			db.insert(TABLE_SUGGESTIONS, null, values);
			db.close();
		}catch (Exception e)
		{
			Log.i(TAG,e.toString());
		}
		//to control the database to only store last 10 suggestions
		adjustSuggestionsCount();
	}

	//function to control only 10 suggestions in table
	private void adjustSuggestionsCount() {
		String countQuery = "SELECT  * FROM " + TABLE_SUGGESTIONS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		if(cursor.getCount() > 10)
		{
			//if the count increases 10 suggestions remove oldest suggestion
			if (cursor.moveToFirst()) {
				deleteSuggestion(Integer.parseInt(cursor.getString(0)));
			}
		}
		cursor.close();
		db.close();
	}

	// Helper function to delete a suggestions based on id
	private void deleteSuggestion(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_SUGGESTIONS, KEY_ID + " = ?",
				new String[] { String.valueOf(id) });
		db.close();
	}

	// Helper function to delete a suggestions based on suggestion text
	private void deleteSuggestion(String s) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_SUGGESTIONS, KEY_SUGGESTION + " = ?",
				new String[] { String.valueOf(s) });
		db.close();
	}

	// function to get last 10 suggestions
	public List<String> getLastt10Suggestions() {
		List<String> suggestionsList = new ArrayList<String>();
		// select query with limit of 10 and descending order to show last suggestion on top
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
