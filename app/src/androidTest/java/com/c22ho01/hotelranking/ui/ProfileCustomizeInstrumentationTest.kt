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
import com.c22ho01.hotelranking.data.remote.retrofit.APIConfig
import com.c22ho01.hotelranking.ui.customview.ValidateableTextFieldTest
import com.c22ho01.hotelranking.ui.profile.ProfileCustomizeActivity
import com.c22ho01.hotelranking.utils.EspressoIdlingResource
import com.c22ho01.hotelranking.utils.JsonConverter
import com.google.android.material.chip.Chip
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.equalTo
import org.junit.*
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class ProfileCustomizeInstrumentationTest {
    @get:Rule
    val activity = ActivityScenarioRule(ProfileCustomizeActivity::class.java)

    private val dummyFullName = "Awanama Wijaya"
    private val dummyIdNumber = "123456789"
    private val dummyHobby = "Hiking"

    @Before
    fun setUp() {
        Intents.init()
        mockWebServer.start()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        Intents.release()
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    private fun fillAllFieldWithValidValues() {

    }

    @Test
    fun whenAllFieldsAreValid_saveButtonIsEnabled() {
        val mockResponse =
            MockResponse()
                .setResponseCode(200)
                .setBody(JsonConverter.readStringFromFile("profile_default_success_response.json"))
        mockWebServer.enqueue(mockResponse)
        onView(withId(R.id.btn_save_profile_customization)).check(matches(isNotEnabled()))
        ValidateableTextFieldTest.run {
            onTextInput(R.id.vtf_profile_custom_name).perform(typeText(dummyFullName), closeSoftKeyboard())
            onTextInput(R.id.vtf_profile_custom_nid).perform(typeText(dummyIdNumber), closeSoftKeyboard())
            onTextInput(R.id.vtf_profile_custom_birth_date).perform(click())
        }
        onView(withClassName(equalTo(DatePicker::class.java.name))).perform(
            PickerActions.setDate(2022, 5, 24),
        )
        onView(withId(android.R.id.button1)).perform(click())
        ValidateableTextFieldTest.onEditTextLayout(R.id.vtf_profile_custom_birth_date).check(
            matches(hasDescendant(withText("24/5/2022"))),
        )
        onView(withId(R.id.rb_prefer_with_family_yes)).perform(click())
        onView(allOf(
            withText(dummyHobby),
            withClassName(equalTo(Chip::class.java.name)),
            withEffectiveVisibility(Visibility.VISIBLE),
            isDisplayed()
        )).perform(click())
        onView(withId(R.id.btn_save_profile_customization)).check(matches(isEnabled()))
    }

    @Test
    fun whenDataIsLoaded_ShowEachFieldWithRelevantData(){
        val mockResponse =
            MockResponse()
                .setResponseCode(200)
                .setBody(JsonConverter.readStringFromFile("profile_filled_success_response.json"))
        mockWebServer.enqueue(mockResponse)
        ValidateableTextFieldTest.run {
            onEditTextLayout(R.id.vtf_profile_custom_name).check(
                matches(hasDescendant(withText(dummyFullName))),
            )
            onEditTextLayout(R.id.vtf_profile_custom_nid).check(
                matches(hasDescendant(withText(dummyIdNumber))),
            )
            onEditTextLayout(R.id.vtf_profile_custom_birth_date).check(
                matches(hasDescendant(withText("25/05/2022"))),
            )
        }
        onView(withId(R.id.rb_prefer_with_family_yes)).check(matches(isChecked()))
        onView(allOf(
            withText(dummyHobby),
            withClassName(equalTo(Chip::class.java.name)),
            withEffectiveVisibility(Visibility.VISIBLE),
        )).check(matches(isChecked()))
        onView(withId(R.id.btn_save_profile_customization)).check(matches(isEnabled()))
    }



    companion object {
        val mockWebServer = MockWebServer()

        @BeforeClass
        @JvmStatic
        fun setUpBaseUrl() {
            APIConfig.BASE_URL = mockWebServer.url("/").toString()
        }
    }
}