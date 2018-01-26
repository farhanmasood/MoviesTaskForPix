package com.test.moviesdb;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.test.moviesdb.database.DatabaseHandler;
import com.test.moviesdb.utils.Constant;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Farhan on 1/26/2018.
 */

/**
 * Instrumented test, which will execute on an Android device.
 * This class have all the tests related to Suggestions database
 * which we are using to display suggestions in searchView
 */
@RunWith(AndroidJUnit4.class)
public class SuggestionsDatabaseInstrumentedTests {

    DatabaseHandler suggestionsDBForTest;
    Context appContext;

    // Initializing attributes before tests are performed
    @Before
    public void init() throws Exception {
        // Context of the app under test.
        appContext = InstrumentationRegistry.getTargetContext();
        suggestionsDBForTest=new DatabaseHandler(appContext);

        //First inserting total of 10 suggestions in DB
        suggestionsDBForTest.addSuggestion("batman");
        suggestionsDBForTest.addSuggestion("love");
        suggestionsDBForTest.addSuggestion("spider man");
        suggestionsDBForTest.addSuggestion("ice age");
        suggestionsDBForTest.addSuggestion("terminator");
        suggestionsDBForTest.addSuggestion("transporter");
        suggestionsDBForTest.addSuggestion("godfather");
        suggestionsDBForTest.addSuggestion("angry men");
        suggestionsDBForTest.addSuggestion("pulp fiction");
        suggestionsDBForTest.addSuggestion("fight club");
    }

    /*
     * The test function will check the status of the application context it should not be null
     */
    @Test
    public void testContextNotNull() throws Exception {
        assertNotEquals("Test App Context is not null",null,appContext);
    }

    /*
     * The test function will test the initial database entries
     */
    @Test
    public void testInitialDatabase() throws Exception {
        //getting suggestions list from DB
        List<String> suggestionsList=suggestionsDBForTest.getLastt10Suggestions();
        //First index should display the last movie suggestion inserted in this case fight club
        assertEquals("Test first index of the returned suggestions list from DB","fight club",suggestionsList.get(0));
        //Last index should display first movie inserted which was batman in this case
        assertEquals("Test last index of the returned suggestions list from DB","batman",suggestionsList.get(9));
    }

    /*
     * The test function verifies how database gets affected after 11th entry is inserted
     */
    @Test
    public void testInsertionAfter10Suggestions() throws Exception {

        //inserting 11th entry into the database
        suggestionsDBForTest.addSuggestion("matrix");

        //getting updated list from database after 11th suggestion inserted
        List<String> suggestionsList=suggestionsDBForTest.getLastt10Suggestions();
        //The last entry batman should be deleted
        assertNotEquals("Test last index old value","batman",suggestionsList.get(9));
        //The last entry should be replaced by second last entry which is "love" in our case
        assertEquals("Test last index new value","love",suggestionsList.get(9));
        //The new 11th entry "matrix" added should be at top of the list
        assertEquals("Test last index new value","matrix",suggestionsList.get(0));
    }

    /*
     * The test function verifies how database gets affected if an existing suggestions is inserted
     */
    @Test
    public void testDuplicateEntryInsertionIntoDB() throws Exception {
        // getting suggestions list from DB before insertion
        List<String> suggestionsList=suggestionsDBForTest.getLastt10Suggestions();
        //Index 5 should contain terminator
        assertEquals("Test fifth index","terminator",suggestionsList.get(5));
        //inserting a duplicate value
        suggestionsDBForTest.addSuggestion("terminator");
        //getting suggestions again after inserting a duplicate value
        suggestionsList=suggestionsDBForTest.getLastt10Suggestions();
        //The duplicate inserted value should move to top
        assertEquals("Test first index","terminator",suggestionsList.get(0));
        //The previous index should not contain duplicate value
        assertNotEquals("Test first index","terminator",suggestionsList.get(5));
    }

    /*
     * The test function verifies that the size of returned suggestions from database doesn't exceed 10
     */
    @Test
    public void testSuggestionsListSize() throws Exception {
        // getting suggestions list from DB
        List<String> suggestionsList=suggestionsDBForTest.getLastt10Suggestions();
        //Testing the size of the suggestions list
        assertEquals("Suggestions list size",10,suggestionsList.size());

        //inserting 2 extra entries
        suggestionsDBForTest.addSuggestion("psycho");
        suggestionsDBForTest.addSuggestion("city of angels");

        //getting suggestions again after inserting two more entries
        suggestionsList=suggestionsDBForTest.getLastt10Suggestions();
        //The size of the suggestions list should still not exceed 10
        assertEquals("Suggestions list size",10,suggestionsList.size());
    }
}
