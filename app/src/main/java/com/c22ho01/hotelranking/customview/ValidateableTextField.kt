package com.c22ho01.hotelranking.customview

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.databinding.ValidateableTextFieldBinding
import com.google.android.material.textfield.TextInputLayout

class ValidateableTextField : ConstraintLayout {
    private var _binding: ValidateableTextFieldBinding? = null
    private val binding get() = _binding


    private var _hasError: Boolean = false
    val hasError: Boolean
        get() = _hasError
    private var validateType: Int? = null
    private var isRequired: Boolean = false
    private var hintText: String? = null

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

    @SuppressLint("CustomViewStyleable")
    private fun init(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) {
        inflate(context, R.layout.__validateable_text_field, this)
        _binding = ValidateableTextFieldBinding.bind(this)

        val typedArray =
                context.obtainStyledAttributes(
                        attrs,
                        R.styleable.ValidateableTextField,
                        defStyleAttr,
                        0,
                )

        hintText = typedArray.getString(R.styleable.ValidateableTextField_validFieldHint)
        val valueText = typedArray.getString(R.styleable.ValidateableTextField_validFieldValue)
        val errorText = typedArray.getString(R.styleable.ValidateableTextField_validFieldError)
        val isObscure =
                typedArray.getBoolean(R.styleable.ValidateableTextField_validFieldObscure, false)

        validateType =
                typedArray.getInt(R.styleable.ValidateableTextField_validFieldValidateType, -1)
        isRequired =
                typedArray.getBoolean(R.styleable.ValidateableTextField_validFieldRequired, false)

        if (isObscure) {
            binding?.tilValidateableViews?.run {
                endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setEndIconTintList(context.getColorStateList(R.color.md_theme_light_primary))
                }

            }
        }

        binding?.tilValidateableViews?.run {
            error = errorText
            hint = hintText
        }

        binding?.etValidateableField?.run {
            setText(valueText)
            transformationMethod = if (isObscure) PasswordTransformationMethod() else null
        }
        typedArray.recycle()
    }

    fun getText(): String? {
        return binding?.etValidateableField?.text?.toString()
    }

    fun setError(errorText: String?) {
        if (errorText != null) {
            binding?.tilValidateableViews?.run {
                error = errorText
                isErrorEnabled = true
            }
            _hasError = true
        } else {
            binding?.tilValidateableViews?.run {
                error = null
                isErrorEnabled = false
            }
            _hasError = false
        }
    }

    fun addValidateListener(matchValidateableTextFieldView: ValidateableTextField? = null, callback: (Boolean) -> Unit) {
        fun ruleHandling(rule: Boolean, errorText: String) {
            if (!rule && !_hasError) {
                setError(errorText)
            } else if (rule) {
                setError(null)
            }
            callback(rule)
        }

        binding?.etValidateableField?.addTextChangedListener(
                object : TextWatcher {
                    override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                    ) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        when (validateType) {
                            VALIDATE_TYPE_EMAIL -> {
                                ruleHandling(
                                        android.util.Patterns.EMAIL_ADDRESS.matcher(s
                                                ?: "").matches(),
                                        context.getString(R.string.error_email_invalid),
                                )
                            }
                            VALIDATE_TYPE_PASSWORD -> {
                                ruleHandling(
                                        (s?.length ?: 0) >= PASSWORD_MIN_LENGTH,
                                        context.getString(R.string.error_pass_length, PASSWORD_MIN_LENGTH)
                                )
                            }

                            VALIDATE_TYPE_PASSWORD_CONFIRMATION -> {
                                ruleHandling(
                                        s?.toString() == matchValidateableTextFieldView?.getText(),
                                        context.getString(R.string.error_pass_confirmation)
                                )
                            }
                            else -> {
                                if (isRequired && s.isNullOrEmpty()) {
                                    setError(context.getString(R.string.error_required))
                                    callback(false)
                                } else {
                                    setError(null)
                                    callback(true)
                                }
                            }
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {}
                })
    }


    companion object {
        const val VALIDATE_TYPE_EMAIL = 0
        const val VALIDATE_TYPE_PASSWORD = 1
        const val VALIDATE_TYPE_PASSWORD_CONFIRMATION = 2
        const val PASSWORD_MIN_LENGTH = 8
    }
}