package com.c22ho01.hotelranking.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.ui.auth.AuthActivity
import com.c22ho01.hotelranking.ui.customview.ValidateableTextFieldTest
import com.c22ho01.hotelranking.ui.customview.hasError
import com.c22ho01.hotelranking.utils.EspressoIdlingResource
import com.c22ho01.hotelranking.utils.MockAPIRule
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class AuthInstrumentationTest {
    private val mockWebServer = MockWebServer()

    @get:Rule
    val mockAPIRule = MockAPIRule(mockWebServer)

    @get:Rule
    val activity = ActivityScenarioRule(AuthActivity::class.java)

    private val dummyUsername = "Awanama"
    private val dummyEmail = "awanama@example.com"
    private val dummyPassword = "12345678"

    private val dummyEmailInvalid = "awanama@example"
    private val dummyPasswordInvalid = "123"

    @Before
    fun setUp() {
        mockWebServer.start()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun loginScreenIsDisplayedAndCouldGoToRegisterScreen() {
        onView(withId(R.id.tv_login_headline)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_go_to_create_acc)).check(matches(isDisplayed())).perform(click())
        onView(withId(R.id.tv_register_headline)).check(matches(isDisplayed()))
    }

    @Test
    fun loginButtonIsEnabledWhenLoginFieldsAreValid() {
        onView(withId(R.id.btn_login)).check(matches(isNotEnabled()))
        ValidateableTextFieldTest.onTextInput(R.id.vtf_login_username)
            .perform(typeText(dummyUsername))
        ValidateableTextFieldTest.onTextInput(R.id.vtf_login_password)
            .perform(typeText(dummyPassword))
        onView(withId(R.id.btn_login)).check(matches(isEnabled()))
    }

    @Test
    fun errorTextIsShownWhenLoginFieldsAreInvalid() {
        onView(withId(R.id.btn_login)).check(matches(isNotEnabled()))

        ValidateableTextFieldTest.onTextInput(R.id.vtf_login_username)
            .perform(typeText(dummyUsername), clearText())
        ValidateableTextFieldTest.onEditTextLayout(R.id.vtf_login_username)
            .check(matches(hasError()))

        ValidateableTextFieldTest.onTextInput(R.id.vtf_login_password)
            .perform(typeText(dummyPasswordInvalid), clearText())
        ValidateableTextFieldTest.onEditTextLayout(R.id.vtf_login_password)
            .check(matches(hasError()))

        onView(withId(R.id.btn_login)).check(matches(isNotEnabled()))
    }

    @Test
    fun registerButtonIsEnabledWhenRegisterFieldsAreValid() {
        onView(withId(R.id.btn_go_to_create_acc)).check(matches(isDisplayed())).perform(click())
        onView(withId(R.id.btn_register)).check(matches(isNotEnabled()))
        ValidateableTextFieldTest.onTextInput(R.id.vtf_register_username)
            .perform(typeText(dummyUsername))
        ValidateableTextFieldTest.onTextInput(R.id.vtf_register_email)
            .perform(typeText(dummyEmail))
        ValidateableTextFieldTest.onTextInput(R.id.vtf_register_password)
            .perform(typeText(dummyPassword))
        ValidateableTextFieldTest.onTextInput(R.id.vtf_register_confirm_password)
            .perform(typeText(dummyPassword))
        onView(withId(R.id.btn_register)).check(matches(isEnabled()))
    }

    @Test
    fun confirmationPasswordFieldHasErrorWhenDoesNotMatchPasswordField(){
        onView(withId(R.id.btn_go_to_create_acc)).check(matches(isDisplayed())).perform(click())
        ValidateableTextFieldTest.onTextInput(R.id.vtf_register_password)
            .perform(typeText(dummyPassword))
        ValidateableTextFieldTest.onTextInput(R.id.vtf_register_confirm_password)
            .perform(typeText(dummyPasswordInvalid))
        ValidateableTextFieldTest.onEditTextLayout(R.id.vtf_register_confirm_password)
            .check(matches(hasError()))
    }

    @Test
    fun errorTextIsShownWhenRegisterFieldsAreInvalid() {
        onView(withId(R.id.btn_go_to_create_acc)).check(matches(isDisplayed())).perform(click())
        onView(withId(R.id.btn_register)).check(matches(isNotEnabled()))
        ValidateableTextFieldTest.onTextInput(R.id.vtf_register_username)
            .perform(typeText(dummyUsername))
        ValidateableTextFieldTest.onEditTextLayout(R.id.vtf_register_username)
            .check(matches(hasError()))

        ValidateableTextFieldTest.onTextInput(R.id.vtf_register_email)
            .perform(typeText(dummyEmailInvalid))
        ValidateableTextFieldTest.onEditTextLayout(R.id.vtf_register_email)
            .check(matches(hasError()))

        ValidateableTextFieldTest.onTextInput(R.id.vtf_register_password)
            .perform(typeText(dummyPasswordInvalid))
        ValidateableTextFieldTest.onEditTextLayout(R.id.vtf_register_password)
            .check(matches(hasError()))

        ValidateableTextFieldTest.onTextInput(R.id.vtf_register_confirm_password)
            .perform(typeText(dummyPassword))
        ValidateableTextFieldTest.onEditTextLayout(R.id.vtf_register_confirm_password)
            .check(matches(hasError()))

        onView(withId(R.id.btn_register)).check(matches(isNotEnabled()))
    }

}