package com.example.taskhep

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun addTask_showsInList() {
        val testTitle = "UI Test Task"
        val testDescription = "ADSJHSUOJDHSOJDHOUSIDHOSHDOS"

        onView(withId(R.id.fabAddTask))
            .perform(click())

        onView(withId(R.id.etTitle))
            .perform(typeText(testTitle))

        onView(withId(R.id.etDescription))
            .perform(typeText(testDescription), closeSoftKeyboard())

        onView(withId(R.id.btnSave))
            .perform(click())

        onView(withText(testTitle))
            .check(matches(isDisplayed()))
    }
}
