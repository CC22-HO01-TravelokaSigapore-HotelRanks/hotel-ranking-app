package com.c22ho01.hotelranking.ui

import android.content.Intent
import android.widget.DatePicker
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.data.local.entity.ProfileEntity
import com.c22ho01.hotelranking.data.remote.retrofit.APIConfig
import com.c22ho01.hotelranking.ui.customview.ValidateableTextFieldTest
import com.c22ho01.hotelranking.ui.profile.ProfileCustomizeActivity
import com.c22ho01.hotelranking.utils.DataDummy
import com.c22ho01.hotelranking.utils.DateUtils
import com.c22ho01.hotelranking.utils.EspressoIdlingResource
import com.google.android.material.chip.Chip
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.equalTo
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.util.*


@RunWith(AndroidJUnit4::class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ProfileCustomizeInstrumentationTest {
    private lateinit var scenario: ActivityScenario<ProfileCustomizeActivity>

//    @get:Rule
//    val activity = ActivityScenarioRule(ProfileCustomizeActivity::class.java)

    private val dummyFullName = "Awanama Wijaya"
    private val dummyIdNumber = "123456789"
    private val dummyHobby = "Hiking"

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        Intents.init()
        mockWebServer.start()
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        scenario.close()
        Intents.release()
        mockWebServer.shutdown()
    }

    @Test
    fun whenAllFieldsAreValid_saveButtonIsEnabled() {
        val profile = ProfileEntity()
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            ProfileCustomizeActivity::class.java
        )
            .putExtra(ProfileCustomizeActivity.EXTRA_PROFILE, profile)
        scenario = ActivityScenario.launch(intent)

        onView(withId(R.id.btn_save_profile_customization)).check(matches(isNotEnabled()))
        ValidateableTextFieldTest.run {
            onTextInput(R.id.vtf_profile_custom_name).perform(
                typeText(dummyFullName),
                closeSoftKeyboard()
            )
            onTextInput(R.id.vtf_profile_custom_nid).perform(
                typeText(dummyIdNumber),
                closeSoftKeyboard()
            )
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
        onView(
            allOf(
                withText(dummyHobby),
                withClassName(equalTo(Chip::class.java.name)),
                withEffectiveVisibility(Visibility.VISIBLE),
                isDisplayed()
            )
        ).perform(click())
        onView(withId(R.id.btn_save_profile_customization)).check(matches(isEnabled()))
    }

    @Test
    fun whenDataIsLoaded_ShowEachFieldWithRelevantData() {
        val profile = DataDummy.provideProfileEntity()
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            ProfileCustomizeActivity::class.java
        )
            .putExtra(ProfileCustomizeActivity.EXTRA_PROFILE, profile)
        scenario = ActivityScenario.launch(intent)
        Thread.sleep(3000)
        ValidateableTextFieldTest.run {
            onEditTextLayout(R.id.vtf_profile_custom_name).check(
                matches(hasDescendant(withText(profile.name))),
            )
            onEditTextLayout(R.id.vtf_profile_custom_nid).check(
                matches(hasDescendant(withText(profile.nid.toString()))),
            )
            onEditTextLayout(R.id.vtf_profile_custom_birth_date).check(
                matches(
                    hasDescendant(
                        withText(
                            DateUtils.formatDateToStringSlash(
                                profile.birthDate ?: Date()
                            )
                        )
                    )
                ),
            )
        }
        onView(withId(R.id.rb_prefer_with_family_yes)).check(matches(isChecked()))
        onView(
            allOf(
                withText(dummyHobby),
                withClassName(equalTo(Chip::class.java.name)),
                withEffectiveVisibility(Visibility.VISIBLE),
            )
        ).check(matches(isChecked()))
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