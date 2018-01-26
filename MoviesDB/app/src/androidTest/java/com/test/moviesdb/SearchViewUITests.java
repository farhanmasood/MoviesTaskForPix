package com.test.moviesdb;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import com.test.moviesdb.activity.MainActivity;
import com.test.moviesdb.database.DatabaseHandler;

import org.hamcrest.Matchers;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

/**
 * Created by Farhan on 1/26/2018.
 */

/**
 * UI test, which will execute on an Android device.
 * This class have all the UI tests which I have written to test the functionality of
 * searchView with suggestions e.g is it properly loading suggestions
 */

public class SearchViewUITests extends ActivityInstrumentationTestCase2<MainActivity> {

    Context context;
    String sampleSuggestionText="batman";
    DatabaseHandler suggestionsDatabase;

    public SearchViewUITests() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        context=getActivity();
        suggestionsDatabase=new DatabaseHandler(context);
    }

    // function to test if performing a click inside searchView displays suggestions list
    public void testLoadingOfSuggestionsOnSearchViewClick()
    {
        List<String> suggestionsList=suggestionsDatabase.getLastt10Suggestions();
        //if existing suggestion list is empty, adding a suggestion to test
        if(suggestionsList.isEmpty())
        {
            suggestionsDatabase.addSuggestion("godfather ");
            suggestionsList=suggestionsDatabase.getLastt10Suggestions();
        }
        //Performing click in searchview to gain focus
        onView(withId(R.id.search_view)).perform(click());

        //checking if suggestion list is displayed
        onView(withText(suggestionsList.get(0)))
                .inRoot(withDecorView(not(Matchers.is(getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }


    public void testSpecificSuggestionDisplayInSearchView() {
        //Gain focus of searchView
        onView(withId(R.id.search_view)).perform(click());

        //inserting a sample suggestion to suggestions list
        onView(isAssignableFrom(EditText.class)).perform(typeText(sampleSuggestionText), pressImeActionButton());

        // hide keyboard
        pressBack();

        // Clear the text in search field
        onView(isAssignableFrom(EditText.class)).perform(clearText());

        // Enter the first letter of the previously searched word
        onView(isAssignableFrom(EditText.class)).perform(typeText("b"));

        // Check if the entered suggestions appeared on character press
        onView(withText(sampleSuggestionText))
                .inRoot(withDecorView(not(Matchers.is(getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }

}
