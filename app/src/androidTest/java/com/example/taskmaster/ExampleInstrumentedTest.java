package com.example.taskmaster;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;


import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.example.taskmaster.data.AppDatabase;
import com.example.taskmaster.data.TaskData;

import java.util.List;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityActivityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);
    private RecyclerView recyclerView;

    @Test
    public void editUserName(){
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Setting")).perform(click());
        onView(withId(R.id.edit_user_name)).perform(typeText("Ahmad") ,closeSoftKeyboard());
        onView(withId(R.id.btn_save)).perform(click());
        onView(withId(R.id.text_user)).check(matches(withText("team 2 : Ahmad Tasks"))) ;
    }

    @Test
    public void navigateToAddTask(){
        onView(withId(R.id.btn_add_task)).perform(click());
        onView(withId(R.id.edit_task_title)).perform(typeText("Task 12") ,closeSoftKeyboard());
        onView(withId(R.id.edit_task_desc)).perform(typeText("Do your home work") ,closeSoftKeyboard());
        onView(withId(R.id.btn_submit_task)).perform(click());
        activityActivityScenarioRule.getScenario().onActivity(activity -> {
               recyclerView = activity.findViewById(R.id.recycler_view);
        });
        int item = recyclerView.getAdapter().getItemCount();
        onView(withId(R.id.recycler_view)).perform(RecyclerViewActions.scrollToPosition(item-1));
        onView(withId(R.id.recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(item-1 , click()));
        onView(withId(R.id.text_title)).check(matches(withText("Task 12")));
        onView(withId(R.id.text_description)).check(matches(withText("Do your home work")));
        onView(withId(R.id.text_status)).check(matches(withText("new")));

    }



    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.taskmaster", appContext.getPackageName());
    }
}