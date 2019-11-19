package com.met.auth;

import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.met.auth.login.Login;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


//@RunWith(AndroidJUnit4.class)
public class LoginTest {

    @Rule public final ActivityScenarioRule<Login> loginActivityScenarioRule = new ActivityScenarioRule<>(Login.class);

    @Test
    public void shouldShowBlankFieldError(){
        onView(withId(R.id.login_button)).perform(click());
        onView(withId(R.id.email_input_layout)).check(ViewAssertions.matches(hasErrorText("The field is required")));
    }

    @Test
    public void shouldShowIncorrectEmailError(){
        onView(withId(R.id.email_textfield)).perform(typeText("my_incorrect_email.com"));
        onView(withId(R.id.email_input_layout)).check(ViewAssertions.matches(hasErrorText("Incorrect email address")));
    }


}
