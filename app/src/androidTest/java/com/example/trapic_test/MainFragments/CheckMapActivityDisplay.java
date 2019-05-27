package com.example.trapic_test.MainFragments;

import android.view.View;

import com.example.trapic_test.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.rule.ActivityTestRule;

import static org.junit.Assert.*;

public class CheckMapActivityDisplay {

    @Rule
    public ActivityTestRule<MapFragment> mapFragmentActivityTestRule = new ActivityTestRule<MapFragment>(MapFragment.class);
    MapFragment mapFragment;

    @Before
    public void setUp(){
        mapFragment = mapFragmentActivityTestRule.getActivity();
    }

    @Test
    public void testMapDisplay(){
        assertNotNull(mapFragment.findViewById(R.id.mapView));
    }

    @After
    public void after(){

    }

}