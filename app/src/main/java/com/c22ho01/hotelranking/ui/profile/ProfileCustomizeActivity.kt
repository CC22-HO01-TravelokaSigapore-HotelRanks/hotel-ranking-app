package com.c22ho01.hotelranking.ui.profile

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.data.local.entity.DisabilityEntity
import com.c22ho01.hotelranking.data.local.entity.HobbyEntity
import com.c22ho01.hotelranking.data.local.entity.ProfileEntity
import com.c22ho01.hotelranking.databinding.ActivityProfileCustomizeBinding
import com.c22ho01.hotelranking.viewmodel.ViewModelFactory
import com.c22ho01.hotelranking.viewmodel.profile.ProfileCustomizeViewModel
import com.c22ho01.hotelranking.viewmodel.profile.ProfileViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar

class ProfileCustomizeActivity : AppCompatActivity() {
    private var _binding: ActivityProfileCustomizeBinding? = null
    private val binding get() = _binding
    private lateinit var factory: ViewModelFactory
    private val profileCustomViewModel: ProfileCustomizeViewModel by viewModels { factory }
    private val profileViewModel: ProfileViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProfileCustomizeBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        factory = ViewModelFactory.getInstance(this)
        setupInitialCustomizationState()

        setupButtonValidation()
    }

    private fun setupInitialCustomizationState() {
        loadProfileData()
        profileViewModel.getCurrentProfile().observe(this) {
            setupFieldValidationListener(it)
            setupFamilyRadioValidation(it.family)
            setupHobbiesChipGroupValidation(it.hobby)
            setupDisabilitiesChipGroupValidation(it.specialNeeds)
        }
    }

    private fun loadProfileData() {
        profileViewModel.loadProfile().run {
            if (this.hasObservers()) this.removeObservers(this@ProfileCustomizeActivity)
            this.observe(this@ProfileCustomizeActivity) { result ->
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                    }
                    is Result.Error -> {
                        showLoading(false)
                        binding?.let { fragment ->
                            Snackbar.make(
                                fragment.root,
                                result.error,
                                Snackbar.LENGTH_LONG,
                            ).show()
                        }
                    }
                }
            }

        }
    }


    private fun setupButtonValidation() {
        profileCustomViewModel.formValid.observe(this) {
            binding?.btnSaveProfileCustomization?.isEnabled = it
        }
    }

    private fun setupFieldValidationListener(initialProfile: ProfileEntity) {
        binding?.run {
            vtfProfileCustomName.run {
                addValidateListener {
                    profileCustomViewModel.setFullNameValid(it)
                }
                if (initialProfile.name?.isNotEmpty() == true) {
                    setText(initialProfile.name)
                    profileCustomViewModel.setFullNameValid(true)
                }
            }
            vtfProfileCustomBirthDate.run {
                addValidateListener {
                    profileCustomViewModel.setBirthDateValid(it)
                }
                if (initialProfile.birthDate != null) {
                    setSelectedDate(initialProfile.birthDate)
                    profileCustomViewModel.setBirthDateValid(true)
                }
            }
            vtfProfileCustomNid.run {
                addValidateListener {
                    profileCustomViewModel.setNidValid(it)
                }
                if (initialProfile.nid != null) {
                    setText(initialProfile.nid.toString())
                    profileCustomViewModel.setNidValid(true)
                }
            }
        }
    }

    private fun setupFamilyRadioValidation(isYes: Boolean? = null) {
        binding?.rgPreferWithFamily?.run {
            setOnCheckedChangeListener { _, checkedId ->
                profileCustomViewModel.setFamilyValid(true)
            }
            if (isYes != null) {
                when (isYes) {
                    true -> {
                        check(R.id.rb_prefer_with_family_yes)
                        profileCustomViewModel.setFamilyValid(true)
                    }
                    false -> {
                        check(R.id.rb_prefer_with_family_no)
                        profileCustomViewModel.setFamilyValid(true)
                    }
                }
            }
        }
    }

    private fun setupHobbiesChipGroupValidation(initialSelected: List<HobbyEntity?>? = listOf()) {
        binding?.cgHobbiesGroup?.removeAllViews()
        val hobbyList = profileCustomViewModel.getAllHobbyList()
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
                if (initialSelected?.contains(hobby) == true) {
                    isChecked = true
                    profileCustomViewModel.setHobbyChecked(hobby, true)
                }
            }
            binding?.cgHobbiesGroup?.addView(chip)
        }
    }

    private fun setupDisabilitiesChipGroupValidation(initialSelected: List<DisabilityEntity?>? = listOf()) {
        binding?.cgDisabilitiesGroup?.removeAllViews()
        val disabilityList = profileCustomViewModel.getAllDisabilityList()
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
                setOnCheckedChangeListener { _, isChecked ->
                    profileCustomViewModel.setDisabilityChecked(disability, isChecked)
                }
                if (initialSelected?.contains(disability) == true) {
                    isChecked = true
                    profileCustomViewModel.setDisabilityChecked(disability, true)
                }
            }
            binding?.cgDisabilitiesGroup?.addView(chip)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding?.run {
                pbProfileCustomProgress.visibility = View.VISIBLE
                btnSaveProfileCustomization.isEnabled = false
            }
        } else {
            binding?.run {
                pbProfileCustomProgress.visibility = View.GONE
                btnSaveProfileCustomization.isEnabled =
                    profileCustomViewModel.formValid.value ?: false
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}