package com.example.taskmaster;

import android.content.Context;

import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
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

    @Test
    public void navigateToAddTask(){
//        TaskData taskData2 = AppDatabase.getInstance(MainActivity.class).taskDao().getTaskByID(0);

        onView(withId(R.id.btn_add_task)).perform(click());
        onView(withId(R.id.edit_task_title)).perform(typeText("Task 10") ,closeSoftKeyboard());
        onView(withId(R.id.edit_task_desc)).perform(typeText("Do your home work") ,closeSoftKeyboard());
        onView(withId(R.id.btn_submit_task)).perform(click());

    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.taskmaster", appContext.getPackageName());
    }
}