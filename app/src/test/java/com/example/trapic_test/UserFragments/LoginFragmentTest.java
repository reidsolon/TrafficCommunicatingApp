package com.example.trapic_test.UserFragments;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
@RunWith(MockitoJUnitRunner.class)
public class LoginFragmentTest {

    @Test
    public void testingLogin(){
        LoginFragment loginFragment = new LoginFragment();

        String matcher = "Success Login";
        String result = loginFragment.validate("pllideau@gmail.com", "CTU@123456");
        assertThat(result,is(matcher));

    }

}