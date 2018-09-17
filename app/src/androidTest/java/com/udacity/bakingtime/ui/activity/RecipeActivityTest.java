package com.udacity.bakingtime.ui.activity;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.udacity.bakingtime.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;


// Used Espresso test recorder to generate the base for the test. Then I manually modified to ensure that it runs correctly.
// Without the manual modifications, the test wouldn't run successfully at all after initial creation.
// Reference: https://developer.android.com/studio/test/espresso-test-recorder
@LargeTest
@RunWith(AndroidJUnit4.class)
public class RecipeActivityTest {

    @Rule
    public ActivityTestRule<RecipeActivity> mActivityTestRule = new ActivityTestRule<>(RecipeActivity.class);

    @Test
    public void recipeActivityTest() {
        ViewInteraction recyclerView = onView(allOf(withId(R.id.recipe_recyclerview), childAtPosition(allOf(withId(R.id.activity_fragment_container), childAtPosition(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class), 1)), 0), isDisplayed()));
        recyclerView.check(matches(isDisplayed()));

        ViewInteraction textView = onView(allOf(withId(R.id.recipe_item_textview), withText("Cheesecake"), childAtPosition(childAtPosition(withId(R.id.recipe_item_cardview), 0), 2), isDisplayed()));
        textView.check(matches(withText("Cheesecake")));

        ViewInteraction textView2 = onView(allOf(withId(R.id.recipe_item_textview), withText("Cheesecake"), childAtPosition(childAtPosition(withId(R.id.recipe_item_cardview), 0), 2), isDisplayed()));
        textView2.check(matches(withText("Cheesecake")));

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction recyclerView2 = onView(allOf(withId(R.id.recipe_recyclerview), childAtPosition(withId(R.id.activity_fragment_container), 0)));
        recyclerView2.perform(actionOnItemAtPosition(2, click()));

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView3 = onView(allOf(withId(R.id.recipe_step_item_textview), withText("2. Prep the cookie crust."), childAtPosition(allOf(withId(R.id.recipe_step_item_constraint_layout), childAtPosition(withId(R.id.recipe_step_recyclerview), 2)), 0), isDisplayed()));
        textView3.check(matches(withText("2. Prep the cookie crust.")));

        ViewInteraction textView4 = onView(allOf(withText("Nutella Pie"), childAtPosition(allOf(withId(R.id.recipe_activity_toolbar), childAtPosition(withId(R.id.recipe_activity_app_bar), 0)), 1), isDisplayed()));
        textView4.check(matches(withText("Nutella Pie")));

        ViewInteraction recyclerView3 = onView(allOf(withId(R.id.recipe_step_recyclerview), childAtPosition(withId(R.id.recipe_ingredient_step_framelayout), 3)));
        recyclerView3.perform(actionOnItemAtPosition(4, click()));

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView5 = onView(allOf(withId(R.id.recipe_step_instructions_textView), withText("Step 4. Beat together the nutella, mascarpone, 1 teaspoon of salt, and 1 tablespoon of vanilla on medium speed in a stand mixer or high speed with a hand mixer until fluffy."), childAtPosition(allOf(withId(R.id.recipe_instructions_constraint), childAtPosition(withId(R.id.recipe_step_instructions_fragment), 0)), 0), isDisplayed()));
        textView5.check(matches(withText("Step 4. Beat together the nutella, mascarpone, 1 teaspoon of salt, and 1 tablespoon of vanilla on medium speed in a stand mixer or high speed with a hand mixer until fluffy.")));

        ViewInteraction textView6 = onView(allOf(withText("Nutella Pie"), childAtPosition(allOf(withId(R.id.recipe_activity_toolbar), childAtPosition(withId(R.id.recipe_activity_app_bar), 0)), 1), isDisplayed()));
        textView6.check(matches(withText("Nutella Pie")));

        ViewInteraction button = onView(allOf(withId(R.id.recipe_step_content_previous_button), childAtPosition(allOf(withId(R.id.recipe_instructions_constraint), childAtPosition(withId(R.id.recipe_step_instructions_fragment), 0)), 1), isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction button2 = onView(allOf(withId(R.id.recipe_step_content_next_button), childAtPosition(allOf(withId(R.id.recipe_instructions_constraint), childAtPosition(withId(R.id.recipe_step_instructions_fragment), 0)), 2), isDisplayed()));
        button2.check(matches(isDisplayed()));

    }

    private static Matcher<View> childAtPosition(final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent) && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
