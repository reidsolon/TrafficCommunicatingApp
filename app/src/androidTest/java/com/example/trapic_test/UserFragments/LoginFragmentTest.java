package com.example.trapic_test.UserFragments;

import com.example.trapic_test.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

public class LoginFragmentTest {

    @Rule
    public ActivityTestRule<LoginFragment> loginFragmentActivityTestRule = new ActivityTestRule<LoginFragment>(LoginFragment.class);
    LoginFragment loginFragment = null;
    String test_userName, test_userPass;

    @Before
    public void setUp() throws Exception {
        loginFragment = new LoginFragment();

        test_userName = "solon@gmail.com";
        test_userPass = "solon123";
    }

    @Test
    public void testLoginWithData(){
        onView(withId(R.id.log_email)).perform(typeText(test_userName));
        closeSoftKeyboard();

        onView(withId(R.id.log_pw)).perform(typeText(test_userPass));

        onView(withId(R.id.log_btn)).perform(click());
    }

    @After
    public void tearDown() throws Exception {
    }
}