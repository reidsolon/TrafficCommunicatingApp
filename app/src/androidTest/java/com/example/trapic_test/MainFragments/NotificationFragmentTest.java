package com.example.trapic_test.MainFragments;

import com.example.trapic_test.MainFragment;
import com.example.trapic_test.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

public class NotificationFragmentTest {

    @Rule
    public ActivityTestRule<MainFragment> mainFragmentActivityTestRule = new ActivityTestRule<MainFragment>(MainFragment.class);
    MainFragment mainFragment = null;

    @Before
    public void setUp() throws Exception {
        mainFragment = mainFragmentActivityTestRule.getActivity();
    }

    @Test
    public void testNotificationRecyclerView(){
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @After
    public void tearDown() throws Exception {
    }
}