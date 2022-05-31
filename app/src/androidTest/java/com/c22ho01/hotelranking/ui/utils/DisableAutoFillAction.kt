package com.c22ho01.hotelranking.ui.utils

import android.os.Build
import android.view.View
import android.view.autofill.AutofillManager
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import org.hamcrest.Matcher
import org.hamcrest.Matchers

class DisableAutoFillAction : ViewAction {
    override fun getConstraints(): Matcher<View> {
        return Matchers.any(View::class.java)
    }

    override fun getDescription(): String {
        return "Dismissing autofill picker"
    }

    override fun perform(uiController: UiController?, view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val autofillManager: AutofillManager = view.context.getSystemService(
                AutofillManager::class.java
            )
            autofillManager.cancel()
        }
    }
}
