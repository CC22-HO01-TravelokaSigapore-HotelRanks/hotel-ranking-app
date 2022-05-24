package com.c22ho01.hotelranking.ui.customview

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers
import com.c22ho01.hotelranking.customview.ValidateableTextField
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.hamcrest.Description
import org.hamcrest.Matchers


object ValidateableTextFieldTest {
    fun onEditTextLayout(id: Int): ViewInteraction {
        return Espresso.onView(
            Matchers.allOf(
                ViewMatchers.isDisplayed(),
                ViewMatchers.isAssignableFrom(TextInputLayout::class.java),
                ViewMatchers.isDescendantOfA(ViewMatchers.withId(id))
            )
        )
    }

    fun onTextInput(id: Int): ViewInteraction {
        return Espresso.onView(
            Matchers.allOf(
                ViewMatchers.isDisplayed(),
                ViewMatchers.isAssignableFrom(TextInputEditText::class.java),
                ViewMatchers.isDescendantOfA(ViewMatchers.withId(id))
            )
        )
    }
}

fun validateableFieldHasError(): BoundedMatcher<View?, ValidateableTextField?> {
    return object :
        BoundedMatcher<View?, ValidateableTextField?>(ValidateableTextField::class.java) {
        override fun describeTo(description: Description) {
            description.appendText("has no error text ")
        }

        override fun matchesSafely(item: ValidateableTextField?): Boolean {
            return item?.hasError ?: false
        }
    }
}

fun hasError(): BoundedMatcher<View?, TextInputLayout?> {
    return object : BoundedMatcher<View?, TextInputLayout?>(TextInputLayout::class.java) {
        override fun describeTo(description: Description) {
            description.appendText("has no error text ")
        }

        override fun matchesSafely(item: TextInputLayout?): Boolean {
            return item?.error != null
        }
    }
}