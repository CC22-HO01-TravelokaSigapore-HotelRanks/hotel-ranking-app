package com.c22ho01.hotelranking.ui

import android.view.autofill.AutofillManager
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.data.remote.retrofit.APIConfig
import com.c22ho01.hotelranking.ui.auth.AuthActivity
import com.c22ho01.hotelranking.ui.customview.ValidateableTextFieldTest
import com.c22ho01.hotelranking.ui.customview.hasError
import com.c22ho01.hotelranking.ui.home.HomeLoggedInActivity
import com.c22ho01.hotelranking.ui.utils.DisableAutoFillAction
import com.c22ho01.hotelranking.utils.EspressoIdlingResource
import com.c22ho01.hotelranking.utils.JsonConverter
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class AuthInstrumentationTest {

    @get:Rule
    val activity = ActivityScenarioRule(AuthActivity::class.java)

    private val dummyUsername = "Awanama"
    private val dummyEmail = "awanama@example.com"
    private val dummyPassword = "12345678"

    private val dummyEmailInvalid = "awanama@example"
    private val dummyPasswordInvalid = "123"


    @Before
    fun setUp() {
        Intents.init()
        mockWebServer.start()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val autofillManager: AutofillManager = context.getSystemService(AutofillManager::class.java)
        autofillManager.disableAutofillServices()
        autofillManager.cancel()
    }

    @After
    fun tearDown() {
        Intents.release()
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }


    private fun fillAllRegisterFieldsValid() {
        ValidateableTextFieldTest.run {
            onTextInput(R.id.vtf_register_username).perform(
                typeText(dummyUsername),
                closeSoftKeyboard(), DisableAutoFillAction(),
            )
            onTextInput(R.id.vtf_register_email).perform(
                typeText(dummyEmail),
                closeSoftKeyboard(), DisableAutoFillAction(),
            )
            onTextInput(R.id.vtf_register_password).perform(
                typeText(dummyPassword),
                closeSoftKeyboard(), DisableAutoFillAction(),
            )
            onTextInput(R.id.vtf_register_confirm_password).perform(
                typeText(dummyPassword),
                closeSoftKeyboard(), DisableAutoFillAction(),
            )
        }
    }

    @Test
    fun loginScreenIsDisplayed_andCouldGoToRegisterScreen() {
        onView(withId(R.id.tv_login_headline)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_go_to_create_acc)).check(matches(isDisplayed())).perform(click())
        onView(withId(R.id.tv_register_headline)).check(matches(isDisplayed()))
    }

    @Test
    fun whenLoginFieldsAreValid_loginButtonIsEnabled() {
        onView(withId(R.id.btn_login)).check(matches(isNotEnabled()))
        ValidateableTextFieldTest.run {
            onTextInput(R.id.vtf_login_username).perform(
                typeText(dummyUsername),
                closeSoftKeyboard(), DisableAutoFillAction(),
            )
            onTextInput(R.id.vtf_login_password).perform(
                typeText(dummyPassword),
                closeSoftKeyboard(), DisableAutoFillAction(),
            )
        }
        onView(withId(R.id.btn_login)).check(matches(isEnabled()))
    }

    @Test
    fun whenLoginFieldsAreInvalid_errorTextIsShown() {
        onView(withId(R.id.btn_login)).check(matches(isNotEnabled()))

        ValidateableTextFieldTest.run {
            onTextInput(R.id.vtf_login_username).perform(
                typeText(dummyUsername),
                clearText(),
                closeSoftKeyboard(), DisableAutoFillAction(),
            )
            onEditTextLayout(R.id.vtf_login_username).check(matches(hasError()))
            onTextInput(R.id.vtf_login_password).perform(
                typeText(dummyPasswordInvalid),
                clearText(),
                closeSoftKeyboard(), DisableAutoFillAction(),
            )
            onEditTextLayout(R.id.vtf_login_password).check(matches(hasError()))
        }
        onView(withId(R.id.btn_login)).check(matches(isNotEnabled()))
    }

    @Test
    fun whenRegisterFieldsAreValid_registerButtonIsEnabled() {
        onView(withId(R.id.btn_go_to_create_acc)).check(matches(isDisplayed())).perform(click())
        onView(withId(R.id.btn_register)).check(matches(isNotEnabled()))
        fillAllRegisterFieldsValid()
        onView(withId(R.id.btn_register)).check(matches(isEnabled()))
    }

    @Test
    fun whenDoesNotMatchPasswordField_confirmationPasswordFieldHasError() {
        onView(withId(R.id.btn_go_to_create_acc)).check(matches(isDisplayed())).perform(click())
        ValidateableTextFieldTest.run {
            onTextInput(R.id.vtf_register_password).perform(
                typeText(dummyPassword),
                closeSoftKeyboard(), DisableAutoFillAction(),
            )
            onTextInput(R.id.vtf_register_confirm_password).perform(
                typeText(dummyPasswordInvalid),
                closeSoftKeyboard(), DisableAutoFillAction(),
            )
            onEditTextLayout(R.id.vtf_register_confirm_password).check(matches(hasError()))
        }
    }

    @Test
    fun whenRegisterFieldsAreInvalid_errorTextIsShown() {
        onView(withId(R.id.btn_go_to_create_acc)).check(matches(isDisplayed())).perform(click())
        onView(withId(R.id.btn_register)).check(matches(isNotEnabled()))

        ValidateableTextFieldTest.run {
            onTextInput(R.id.vtf_register_username).perform(
                typeText(dummyUsername),
                clearText(),
                closeSoftKeyboard(), DisableAutoFillAction(),
            )
            onTextInput(R.id.vtf_register_email).perform(
                typeText(dummyEmailInvalid),
                closeSoftKeyboard(), DisableAutoFillAction(),
            )
            onTextInput(R.id.vtf_register_password).perform(
                typeText(dummyPasswordInvalid),
                closeSoftKeyboard(), DisableAutoFillAction(),
            )
            onTextInput(R.id.vtf_register_confirm_password).perform(
                typeText(dummyPassword),
                closeSoftKeyboard(), DisableAutoFillAction(),
            )

            onEditTextLayout(R.id.vtf_register_username).check(matches(hasError()))
            onEditTextLayout(R.id.vtf_register_email).check(matches(hasError()))
            onEditTextLayout(R.id.vtf_register_password).check(matches(hasError()))
            onEditTextLayout(R.id.vtf_register_confirm_password).check(matches(hasError()))
        }
        onView(withId(R.id.btn_register)).check(matches(isNotEnabled()))
    }


    @Test
    fun whenLoginSuccess_shouldGoToHome() {
        ValidateableTextFieldTest.run {
            onTextInput(R.id.vtf_login_username).perform(
                typeText(dummyUsername),
                closeSoftKeyboard(), DisableAutoFillAction(),
            )
            onTextInput(R.id.vtf_login_password).perform(
                typeText(dummyPassword),
                closeSoftKeyboard(), DisableAutoFillAction(),
            )
        }

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JsonConverter.readStringFromFile("login_success_response.json"))
        mockWebServer.enqueue(mockResponse)
        onView(withId(R.id.btn_login)).perform(click())
        intended(hasComponent(HomeLoggedInActivity::class.java.name))
    }

    @Test
    fun whenLoginError_errorSnackBarIsShown() {
        ValidateableTextFieldTest.run {
            onTextInput(R.id.vtf_login_username).perform(
                typeText(dummyUsername),
                closeSoftKeyboard(), DisableAutoFillAction(),
            )
            onTextInput(R.id.vtf_login_password).perform(
                typeText(dummyPassword),
                closeSoftKeyboard(), DisableAutoFillAction(),
            )
        }

        val mockResponse =
            MockResponse()
                .setHttp2ErrorCode(500)
                .setResponseCode(500)
                .setBody(JsonConverter.readStringFromFile("login_error_response.json"))
        mockWebServer.enqueue(mockResponse)
        onView(withId(R.id.btn_login)).perform(click(), DisableAutoFillAction())
        onView(withId(com.google.android.material.R.id.snackbar_text)).check(matches(isDisplayed()))
    }

    @Test
    fun whenRegisterSuccess_shouldGoToLogin() {
        onView(withId(R.id.btn_go_to_create_acc)).check(matches(isDisplayed())).perform(click())
        fillAllRegisterFieldsValid()
        val mockResponse =
            MockResponse()
                .setResponseCode(200)
                .setBody(JsonConverter.readStringFromFile("register_success_response.json"))
        mockWebServer.enqueue(mockResponse)
        onView(withId(R.id.btn_register)).perform(click())
        onView(withId(R.id.tv_login_headline)).check(matches(isDisplayed()))
    }

    @Test
    fun whenRegisterError_errorSnackBarIsShown() {
        onView(withId(R.id.btn_go_to_create_acc)).check(matches(isDisplayed())).perform(click())
        fillAllRegisterFieldsValid()
        val mockResponse =
            MockResponse()
                .setResponseCode(500)
                .setBody(JsonConverter.readStringFromFile("register_error_response.json"))
        mockWebServer.enqueue(mockResponse)
        onView(withId(R.id.btn_register)).perform(click(), DisableAutoFillAction())
        onView(withId(com.google.android.material.R.id.snackbar_text)).check(matches(isDisplayed()))
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