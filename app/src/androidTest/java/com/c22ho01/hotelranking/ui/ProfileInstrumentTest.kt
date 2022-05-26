package com.c22ho01.hotelranking.ui

import android.os.Bundle
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.widget.DatePicker
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.data.remote.retrofit.APIConfig
import com.c22ho01.hotelranking.ui.customview.ValidateableTextFieldTest
import com.c22ho01.hotelranking.ui.home.ProfileFragment
import com.c22ho01.hotelranking.ui.profile.ProfileCustomizeActivity
import com.c22ho01.hotelranking.utils.EspressoIdlingResource
import com.c22ho01.hotelranking.utils.JsonConverter
import com.google.android.material.chip.Chip
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class ProfileInstrumentTest {
    @Before
    fun setUp() {
        Intents.init()
        launchFragmentInContainer<ProfileFragment>(Bundle(), R.style.Theme_HotelRanking)
        ProfileCustomizeInstrumentationTest.mockWebServer.start()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        Intents.release()
        ProfileCustomizeInstrumentationTest.mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    private val dummyEmail = "example@gmail.com"
    private val dummyFullName = "Awanama Wijaya"
    private val dummyIdNumber = "123456789"
    private val dummyHobby1 = "Selfie"
    private val dummyHobby2 = "Hiking"
    private val dummyDisability1 = "I can’t move freely"
    private val dummyDisability2 = "I can’t speak easily"

    @Test
    fun whenGoToCustomizeProfileButtonIsClicked_goToCustomizeProfileActivity() {
        onView(withId(R.id.btnGoToCustomizeProfile)).check(matches(isDisplayed())).perform(click())
        Intents.intended(hasComponent(ProfileCustomizeActivity::class.java.name))
    }

    @Test
    fun whenDataIsLoaded_showDataInProfileUI() {
        val mockResponse =
            MockResponse()
                .setResponseCode(200)
                .setBody(JsonConverter.readStringFromFile("profile_filled_success_response.json"))

        mockWebServer.enqueue(mockResponse)
        onView(withId(R.id.tvEmail)).check(matches(withText(dummyEmail)))
        onView(withId(R.id.tvName)).check(matches(withText(dummyFullName)))
        onView(withId(R.id.tvPrefer)).check(matches(withText(R.string.yes)))
        onView(withId(R.id.tvHobby)).check(matches(withText("$dummyHobby1, $dummyHobby2")))
        onView(withId(R.id.tvSpecialNeeds)).check(matches(withText("$dummyDisability1, $dummyDisability2")))
    }

    @Test
    fun whenProfileIsCustomized_updateProfileData(){
        val mockDefaultProfileResponse =
            MockResponse()
                .setResponseCode(200)
                .setBody(JsonConverter.readStringFromFile("profile_default_success_response.json"))
        mockWebServer.enqueue(mockDefaultProfileResponse)
        onView(withId(R.id.btnGoToCustomizeProfile)).check(matches(isDisplayed())).perform(click())
        mockWebServer.enqueue(mockDefaultProfileResponse)
        ValidateableTextFieldTest.run {
            onTextInput(R.id.vtf_profile_custom_name).perform(
                ViewActions.typeText(dummyFullName),
                ViewActions.closeSoftKeyboard()
            )
            onTextInput(R.id.vtf_profile_custom_nid).perform(
                ViewActions.typeText(dummyIdNumber),
                ViewActions.closeSoftKeyboard()
            )
            onTextInput(R.id.vtf_profile_custom_birth_date).perform(click())
        }
        onView(ViewMatchers.withClassName(CoreMatchers.equalTo(DatePicker::class.java.name))).perform(
            PickerActions.setDate(2022, 5, 25),
        )
        onView(ViewMatchers.withId(android.R.id.button1)).perform(click())
        onView(ViewMatchers.withId(R.id.rb_prefer_with_family_yes)).perform(click())
        onView(
            CoreMatchers.allOf(
                withText(dummyHobby1),
                ViewMatchers.withClassName(CoreMatchers.equalTo(Chip::class.java.name)),
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                isDisplayed()
            )
        ).perform(click())
        onView(
            CoreMatchers.allOf(
                withText(dummyHobby2),
                ViewMatchers.withClassName(CoreMatchers.equalTo(Chip::class.java.name)),
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                isDisplayed()
            )
        ).perform(click())
        onView(
            CoreMatchers.allOf(
                withText(dummyDisability1),
                ViewMatchers.withClassName(CoreMatchers.equalTo(Chip::class.java.name)),
            )
        ).perform(click())
        onView(
            CoreMatchers.allOf(
                withText(dummyDisability2),
                ViewMatchers.withClassName(CoreMatchers.equalTo(Chip::class.java.name)),
            )
        ).perform(click())
        val mockFilledProfileResponse =
            MockResponse()
                .setResponseCode(200)
                .setBody(JsonConverter.readStringFromFile("profile_filled_success_response.json"))
        mockWebServer.enqueue(mockFilledProfileResponse)
        onView(ViewMatchers.withId(R.id.btn_save_profile_customization)).perform(click())
        onView(withId(R.id.tvEmail)).check(matches(withText(dummyEmail)))
        onView(withId(R.id.tvName)).check(matches(withText(dummyFullName)))
        onView(withId(R.id.tvPrefer)).check(matches(withText(R.string.yes)))
        onView(withId(R.id.tvHobby)).check(matches(withText("$dummyHobby1, $dummyHobby2")))
        onView(withId(R.id.tvSpecialNeeds)).check(matches(withText("$dummyDisability1, $dummyDisability2")))
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