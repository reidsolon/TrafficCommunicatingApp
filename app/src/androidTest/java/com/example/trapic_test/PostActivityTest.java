package com.example.trapic_test;

import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.Espresso.*;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

public class PostActivityTest {
    @Rule
    public ActivityTestRule<PostActivity> postActivityRule = new ActivityTestRule<PostActivity>(PostActivity.class);
    private PostActivity postActivity = null;
    String type, location, caption_test;

    @Before
    public void setUp() throws Exception {
        postActivity = postActivityRule.getActivity();
        type = "Construction Area";
        location = "Unname Tugobngan Consolacion";
        caption_test = "Testing....";

    }

    @Test
    public void testPostWithoutPhoto(){

        onView(withId(R.id.event_type)).perform(replaceText(type));
        onView(withId(R.id.event_location)).perform(replaceText(location));

        onView(withId(R.id.event_caption)).perform(typeText(caption_test));

        onView(withId(R.id.post_btn)).perform(click());
        //check if the toast message matches with the expected output
        onView(withText("Something is empty!")).inRoot(withDecorView(not(is(postActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));

    }

    @After
    public void tearDown() throws Exception {
    }
}