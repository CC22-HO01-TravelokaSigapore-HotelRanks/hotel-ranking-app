package com.c22ho01.hotelranking.customview

import android.content.Context
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.databinding.ValidateableTextFieldBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ValidateableTextField : TextInputLayout {
    private var validateType: Int? = null
    private var isRequired: Boolean = false
    private var hasError: Boolean = false

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) {
        inflate(context, R.layout.__validateable_text_field, this)
        val childEditTextField = getChildAt(0) as TextInputEditText
        val typedArray =
            context.obtainStyledAttributes(
                attrs,
                R.styleable.ValidateableField,
                defStyleAttr,
                0,
            )
        val hintText = typedArray.getString(R.styleable.ValidateableField_validFieldHint)
        val valueText = typedArray.getString(R.styleable.ValidateableField_validFieldValue)
        val errorText = typedArray.getString(R.styleable.ValidateableField_validFieldError)
        val isObscure = typedArray.getBoolean(R.styleable.ValidateableField_validFieldObscure, false)

        validateType = typedArray.getInt(R.styleable.ValidateableField_validateType, -1)
        isRequired = typedArray.getBoolean(R.styleable.ValidateableField_validFieldRequired, false)
        hint = hintText

        childEditTextField.setText(valueText)
        if (isObscure) {
            childEditTextField.transformationMethod = PasswordTransformationMethod()
        }

        if (errorText != null) {
            error = errorText
        }
        typedArray.recycle()
    }

    fun getValue(): String {
        return (getChildAt(0) as TextInputEditText).text.toString()
    }

}