package com.c22ho01.hotelranking.ui

import android.widget.DatePicker
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.ui.customview.ValidateableTextFieldTest
import com.c22ho01.hotelranking.ui.profile.ProfileCustomizationActivity
import com.c22ho01.hotelranking.utils.EspressoIdlingResource
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class ProfileCustomizeIInstrumentationTest {
    private val mockWebServer = MockWebServer()

    @get:Rule
    val activity = ActivityScenarioRule(ProfileCustomizationActivity::class.java)

    private val dummyFullName = "Awanama Wijaya"
    private val dummyIdNumber = "123456789"
    private val dummyHobby = "Hiking"

    @Before
    fun setUp() {
        Intents.init()
        mockWebServer.start(8080)
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        Intents.release()
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun whenAllFieldsAreValid_saveButtonIsEnabled() {
        onView(withId(R.id.btn_save_profile_customization)).check(matches(isNotEnabled()))
        ValidateableTextFieldTest.onTextInput(R.id.vtf_profile_custom_name)
            .perform(typeText(dummyFullName), closeSoftKeyboard())
        ValidateableTextFieldTest.onTextInput(R.id.vtf_profile_custom_nid)
            .perform(typeText(dummyIdNumber), closeSoftKeyboard())
        ValidateableTextFieldTest.onTextInput(R.id.vtf_profile_custom_birth_date).perform(click())

        onView(withClassName(equalTo(DatePicker::class.java.name))).perform(
            PickerActions.setDate(
                2022,
                5,
                24,
            ),
        )
        onView(withId(android.R.id.button1)).perform(click())
        ValidateableTextFieldTest.onEditTextLayout(R.id.vtf_profile_custom_birth_date).check(
            matches(
                hasDescendant(
                    withText(
                        "24/5/2022",
                    ),
                ),
            ),
        )
        onView(withId(R.id.rb_prefer_with_family_yes)).perform(click())
        onView(
            withText(dummyHobby)
        ).perform(click())
        onView(withId(R.id.btn_save_profile_customization)).check(matches(isEnabled()))
    }
}