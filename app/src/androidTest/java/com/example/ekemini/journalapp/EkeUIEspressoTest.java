package com.example.ekemini.journalapp;

import android.support.test.espresso.Espresso;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EkeUIEspressoTest {

    @Rule
    public ActivityTestRule<EkeMainActivity> mActivityRule =
            new ActivityTestRule<>(EkeMainActivity.class);

    @Test
    public void googleLoginButtonTest() {
        Espresso.onView(withText("Password!")).check(matches(isDisplayed()));

    }


}
