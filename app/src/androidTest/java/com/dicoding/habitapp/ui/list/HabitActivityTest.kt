package com.dicoding.habitapp.ui.list

import android.app.Activity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import com.dicoding.habitapp.R
import com.dicoding.habitapp.ui.add.AddHabitActivity
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test

//TODO 16 : Write UI test to validate when user tap Add Habit (+), the AddHabitActivity displayed
class HabitActivityTest {

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(HabitListActivity::class.java)

    @Test
    fun testAddHabitActivityDisplayed() {
        onView(withId(R.id.fab))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.add_ed_title))
            .check(matches(isDisplayed()))
        onView(withId(R.id.add_ed_minutes_focus))
            .check(matches(isDisplayed()))
        onView(withId(R.id.add_tv_start_time))
            .check(matches(isDisplayed()))

        val addHabitActivity = getActivityInstance()
        assertTrue(addHabitActivity?.javaClass == AddHabitActivity::class.java)
    }

    private fun getActivityInstance(): Activity? {
        var addHabitActivity: Activity? = null
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            addHabitActivity =
                ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED)
                    .elementAtOrNull(0)
        }
        return addHabitActivity
    }


}