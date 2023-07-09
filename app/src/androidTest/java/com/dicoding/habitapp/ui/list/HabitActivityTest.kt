package com.dicoding.habitapp.ui.list


import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.dicoding.habitapp.R
import com.dicoding.habitapp.ui.add.AddHabitActivity
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

//TODO 16 : Write UI test to validate when user tap Add Habit (+), the AddHabitActivity displayed
@RunWith(AndroidJUnit4ClassRunner::class)
class HabitActivityTest {

    @Before
    fun setup(){
        ActivityScenario.launch(HabitListActivity::class.java)
        Intents.init()
    }

    @Test
    fun whenAddTaskClickedDisplayedAddTaskActivity(){
        Espresso.onView(withId(R.id.fab)).perform(click())
        Intents.intended(hasComponent(AddHabitActivity::class.java.name))
        Espresso.onView(withHint(R.string.title_hint))
            .check(ViewAssertions.matches(isDisplayed()))
        Espresso.onView(withHint(R.string.focus_time_duration_hint))
            .check(ViewAssertions.matches(isDisplayed()))
    }

}