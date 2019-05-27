package com.example.trapic_test.MainFragments;

import android.os.Handler;

import com.example.trapic_test.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

public class TestPersonalRoute {

    @Rule
    public ActivityTestRule<MapFragment> mapFragmentActivityTestRule = new ActivityTestRule<MapFragment>(MapFragment.class);
    MapFragment mapFragment;
    String login_userEmail, login_userPw;

    @Before
    public void setUp(){
        mapFragment = mapFragmentActivityTestRule.getActivity();

        login_userEmail = "pllideau@gmail.com";
        login_userPw = "CTU@123456";
    }

    @Test
    public void testMapDisplay(){

        assertNotNull(mapFragment.findViewById(R.id.mapView));

        onView(withId(R.id.my_loc2)).perform(click());

        onView(withId(R.id.mapView)).perform(swipeRight());

        onView(withId(R.id.mapView)).perform(click());

        onView(withId(R.id.route_info)).check(matches(isDisplayed()));

        //check if the toast message matches with the expected output
        onView(withText("Route is displayed!")).inRoot(withDecorView(not(is(mapFragmentActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));

    }

    @After
    public void after(){

    }
}