package com.c22ho01.hotelranking.customview

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import androidx.constraintlayout.widget.ConstraintLayout
import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.databinding.ValidateableTextFieldBinding
import com.c22ho01.hotelranking.utils.DateUtils
import com.google.android.material.textfield.TextInputLayout
import java.util.*

class ValidateableTextField : ConstraintLayout {
    private var _binding: ValidateableTextFieldBinding? = null
    private val binding get() = _binding


    private var _hasError: Boolean = false
    val hasError: Boolean
        get() = _hasError
    private var validateType: Int? = null
    private var inputType: Int? = null
    private var isRequired: Boolean = false
    private var hintText: String? = null
    private var selectedDate: Date? = null

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

    @SuppressLint("CustomViewStyleable", "UseCompatLoadingForDrawables", "SetTextI18n")
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
        inputType = typedArray.getInt(R.styleable.ValidateableTextField_validFieldInputType, INPUT_TYPE_TEXT)
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
        setupInputType()

        typedArray.recycle()
    }

    private fun setupInputType() {
        when(inputType) {
            INPUT_TYPE_TEXT -> {
                binding?.etValidateableField?.inputType = EditorInfo.TYPE_CLASS_TEXT
            }
            INPUT_TYPE_NUMBER -> {
                binding?.etValidateableField?.inputType = EditorInfo.TYPE_CLASS_NUMBER
            }
            INPUT_TYPE_DATE -> {
                binding?.tilValidateableViews?.run {
                    startIconDrawable = context.getDrawable(R.drawable.ic_baseline_date_range_24)
                }
                binding?.etValidateableField?.run {
                    isFocusable = false
                    setOnClickListener {
                        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                            val monthOfYear = month + 1
                            binding?.etValidateableField?.setText("$dayOfMonth/$monthOfYear/$year")
                            selectedDate = Calendar.getInstance().apply {
                                set(Calendar.YEAR, year)
                                set(Calendar.MONTH, monthOfYear)
                                set(Calendar.DAY_OF_MONTH, dayOfMonth)
                            }.time
                        }
                        val calendar = Calendar.getInstance()
                        val year = calendar.get(Calendar.YEAR)
                        val month = calendar.get(Calendar.MONTH)
                        val day = calendar.get(Calendar.DAY_OF_MONTH)
                        DatePickerDialog(context, dateSetListener, year, month, day).show()
                    }
                }
            }
        }
    }

    fun getText(): String? {
        return binding?.etValidateableField?.text?.toString()
    }
    fun setText(text: String?) {
        binding?.etValidateableField?.setText(text)
    }


    fun getSelectedDate(): Date? {
        return selectedDate
    }
    fun setSelectedDate(date: Date?) {
        if(date != null) {
            selectedDate = date
            val dateStr = DateUtils.formatDateToStringSlash(date)
            setText(dateStr)
        }
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

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    fun addValidateListener(
        matchValidateableTextFieldView: ValidateableTextField? = null,
        callback: (Boolean) -> Unit
    ) {
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
                                android.util.Patterns.EMAIL_ADDRESS.matcher(
                                    s
                                        ?: ""
                                ).matches(),
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

                override fun afterTextChanged(s: Editable?) {
                    if(validateType == VALIDATE_TYPE_DATE) {
                        callback(true)
                    }
                }
            })
    }




    companion object {
        const val VALIDATE_TYPE_EMAIL = 0
        const val VALIDATE_TYPE_PASSWORD = 1
        const val VALIDATE_TYPE_PASSWORD_CONFIRMATION = 2
        const val VALIDATE_TYPE_DATE = 3

        const val INPUT_TYPE_TEXT = 0
        const val INPUT_TYPE_NUMBER = 1
        const val INPUT_TYPE_DATE = 2

        const val PASSWORD_MIN_LENGTH = 8
    }
}