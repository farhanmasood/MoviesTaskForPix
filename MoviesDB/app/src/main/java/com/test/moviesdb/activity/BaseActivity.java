package com.test.moviesdb.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Farhan on 1/22/2018.
 */

/*
 * Baseactivity class to have code which will be common in all activities
 * Mostly the abstract functions which will be implemented in each activity
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Function call to setup the current activities view
        setContentView(setActivityLayout());

        //Calling all other abstract methods in sequence
        initViews();
        initValues();
        initValuesInViews();
        setListenersOnViews();
    }

    protected abstract int setActivityLayout();

    protected abstract void initViews();

    protected abstract void initValues();

    protected abstract void initValuesInViews();

    protected abstract void setListenersOnViews();
}