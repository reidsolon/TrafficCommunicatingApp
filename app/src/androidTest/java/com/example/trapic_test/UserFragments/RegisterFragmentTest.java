package com.example.trapic_test.UserFragments;

import android.app.Activity;
import android.app.Instrumentation;

import com.example.trapic_test.AccountActivation;
import com.example.trapic_test.MainFragment;
import com.example.trapic_test.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.rule.ActivityTestRule;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

public class RegisterFragmentTest {

    @Rule
    public ActivityTestRule<RegisterFragment> mMainFragment = new ActivityTestRule<RegisterFragment>(RegisterFragment.class);
    private RegisterFragment mainFragmentActivity;
    private String reg_uName, reg_userEmail, reg_userPw, reg_userFname, reg_userLname, expectedSuccess, expectedFailure, reg_userPw2;

    Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(AccountActivation.class.getName(), null, false);

    @Before
    public void setUp() throws Exception {
        mainFragmentActivity = mMainFragment.getActivity();

        //Test Datas
        reg_userFname = "Ezekiel";
        reg_userLname = "Villacabo";
        reg_userEmail = "ezekezek@example.com";
        reg_uName = "ezekiel_villacabo";
        reg_userPw = "Ezekiel@1234";
        reg_userPw2 = "Ezekiel@1234";

        expectedFailure = "Not added!";
        expectedSuccess = "Account Registered Successfully";

    }

    @Test
    public void CheckRegisterWithAppropriateDatas(){

        //if activity layout is not null
        assertNotNull(mainFragmentActivity);

        //Input data on firstname field, so as the other.
        onView(withId(R.id.reg_fname)).perform(typeText(reg_userFname));
        //Close the keyboard, so as the other.
        closeSoftKeyboard();

        onView(withId(R.id.reg_lname)).perform(typeText(reg_userLname));

        closeSoftKeyboard();

        onView(withId(R.id.reg_email)).perform(typeText(reg_userEmail));

        closeSoftKeyboard();

        onView(withId(R.id.reg_pw1)).perform(typeText(reg_userPw));

        closeSoftKeyboard();

        onView(withId(R.id.reg_pw2)).perform(typeText(reg_userPw));

        closeSoftKeyboard();

        onView(withId(R.id.reg_btn)).perform(click());

        //check if the toast message matches with the expected output
        onView(withText("Account Registered Successfully")).inRoot(withDecorView(not(is(mMainFragment.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));

        mMainFragment.getActivity().finish();

        Activity secondActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 5000);

        assertNotNull(secondActivity);

    }

    @After
    public void tearDown() throws Exception {

        mMainFragment = null;
    }
}