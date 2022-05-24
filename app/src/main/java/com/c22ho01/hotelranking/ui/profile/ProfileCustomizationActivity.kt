package com.c22ho01.hotelranking.ui.profile

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.TypedValue
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.databinding.ActivityProfileCustomizationBinding
import com.c22ho01.hotelranking.viewmodel.ViewModelFactory
import com.c22ho01.hotelranking.viewmodel.profile.ProfileCustomizationViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class ProfileCustomizationActivity : AppCompatActivity() {
    private var _binding: ActivityProfileCustomizationBinding? = null
    private val binding get() = _binding
    private lateinit var factory: ViewModelFactory
    private val profileCustomViewModel: ProfileCustomizationViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProfileCustomizationBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        factory = ViewModelFactory.getInstance(this)

        setupFieldValidationListener()
        setupFamilyRadioValidation()
        setupHobbiesChipGroupValidation()
        setupDisabilitiesChipGroupValidation()
        setupButtonValidation()
    }

    private fun setupButtonValidation() {
        profileCustomViewModel.formValid.observe(this) {
            binding?.btnSaveProfileCustomization?.isEnabled = it
        }
    }

    private fun setupFamilyRadioValidation() {
        binding?.rgPreferWithFamily?.setOnCheckedChangeListener { _, checkedId ->
            profileCustomViewModel.setFamilyValid(true)
        }
    }

    private fun setupFieldValidationListener() {
        binding?.run {
            vtfProfileCustomName.addValidateListener {
                profileCustomViewModel.setFullnameValid(it)
            }
            vtfProfileCustomBirthDate.addValidateListener {
                profileCustomViewModel.setBirthDateValid(it)
            }
            vtfProfileCustomNid.addValidateListener {
                profileCustomViewModel.setNidValid(it)
            }
        }
    }

    private fun setupDisabilitiesChipGroupValidation() {
        val disabilityList = profileCustomViewModel.getDisabilityList()
        for (disability in disabilityList) {
            val chip = Chip(this)
            val typedValue = TypedValue()
            theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true)
            chip.apply {
                layoutParams = ChipGroup.LayoutParams(
                    ChipGroup.LayoutParams.MATCH_PARENT,
                    ChipGroup.LayoutParams.WRAP_CONTENT,
                )
                text = getString(disability.localizedLabel)
                setChipIconResource(disability.icon)
                isCloseIconVisible = false
                isCheckable = true
                setEnsureMinTouchTargetSize(false)
                chipIconTint = ColorStateList.valueOf(typedValue.data)
                setTextAppearance(R.style.TextAppearance_HotelRanking_LabelLarge)
                tag = "disability-${disability.id}"
                setOnCheckedChangeListener { _, isChecked ->
                    profileCustomViewModel.setDisabilityChecked(disability, isChecked)
                }
            }
            binding?.cgDisabilitiesGroup?.addView(chip)
        }

    }

    private fun setupHobbiesChipGroupValidation() {
        val hobbyList = profileCustomViewModel.getHobbyList()
        for (hobby in hobbyList) {
            val chip = Chip(this)
            val typedValue = TypedValue()
            theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true)
            chip.apply {
                text = getString(hobby.localizedLabel)
                setChipIconResource(hobby.icon)
                isCloseIconVisible = false
                isCheckable = true
                isClickable = true
                setEnsureMinTouchTargetSize(false)
                chipIconTint = ColorStateList.valueOf(typedValue.data)
                setTextAppearance(R.style.TextAppearance_HotelRanking_LabelLarge)
                setOnCheckedChangeListener { _, isChecked ->
                    profileCustomViewModel.setHobbyChecked(hobby, isChecked)
                }
            }
            binding?.cgHobbiesGroup?.addView(chip)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}