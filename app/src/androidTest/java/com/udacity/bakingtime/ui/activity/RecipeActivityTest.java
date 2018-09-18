package com.udacity.bakingtime.ui.activity;


import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.udacity.bakingtime.R;
import com.udacity.bakingtime.ui.fragment.RecipeListFragment;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

// Used Espresso test recorder to generate the base for the test.
// Without manually adding the init to launch the fragment, the test would fail as Espresso isn't aware of the fragment.
// Reference: https://developer.android.com/studio/test/espresso-test-recorder
@SmallTest
@RunWith(AndroidJUnit4.class)
public class RecipeActivityTest {

    private static final String RECIPE_LIST_FRAGMENT = "recipe_list_tag";

    @Rule
    public ActivityTestRule<RecipeActivity> mActivityTestRule = new ActivityTestRule<>(RecipeActivity.class);


    @Before
    public void init() {
        // Load the Recipe List fragment before beginning test
        mActivityTestRule.getActivity()
                .getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_fragment_container, RecipeListFragment.newInstance(), RECIPE_LIST_FRAGMENT)
                .commit();
    }


    @Test
    public void recipeListTest() {

        // check that initial recipe list recyclerView displays
        ViewInteraction recyclerView = onView(allOf(withId(R.id.recipe_recyclerview), childAtPosition(allOf(withId(R.id.activity_fragment_container), childAtPosition(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class), 1)), 0), isDisplayed()));
        recyclerView.check(matches(isDisplayed()));

        // Check that the 2nd item in the recyclerview is displayed with text "Cheesecake"
        ViewInteraction textView = onView(allOf(withId(R.id.recipe_item_textview),
                withText("Cheesecake"),
                childAtPosition(childAtPosition(withId(R.id.recipe_item_cardview), 0), 2),
                isDisplayed()));
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
