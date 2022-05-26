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
import com.c22ho01.hotelranking.utils.wrapEspressoIdlingResource
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

    private lateinit var profileEntity: ProfileEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProfileCustomizeBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        factory = ViewModelFactory.getInstance(this)
        profileEntity = intent.getParcelableExtra<ProfileEntity>(EXTRA_PROFILE) as ProfileEntity
        setupInitialCustomizationState()
        setupButtonValidation()
    }

    private fun setupInitialCustomizationState() {
        profileEntity.let {
            setupFieldValidationListener(it)
            setupFamilyRadioValidation(it.family)
            setupHobbiesChipGroupValidation(it.hobby)
            setupDisabilitiesChipGroupValidation(it.specialNeeds)
        }
    }

    private fun setupButtonValidation() {
        profileCustomViewModel.formValid.observe(this) {
            binding?.btnSaveProfileCustomization?.isEnabled = it
        }
        binding?.btnSaveProfileCustomization?.setOnClickListener {
            customizeProfile()
        }
    }

    private fun setupFieldValidationListener(initialProfile: ProfileEntity) {
        wrapEspressoIdlingResource {
            binding?.run {
                vtfProfileCustomName.let { vtf ->
                    vtf.addValidateListener { valid ->
                        profileCustomViewModel.apply {
                            setFullNameValid(valid)
                            setFullName(vtf.getText().toString())
                        }
                    }
                    if (initialProfile.name?.isNotEmpty() == true) {
                        vtf.setText(initialProfile.name)
                        profileCustomViewModel.apply {
                            setFullNameValid(true)
                            setFullName(initialProfile.name)
                        }
                    }
                }

                vtfProfileCustomBirthDate.let { vtf ->
                    vtf.addValidateListener { valid ->
                        profileCustomViewModel.apply {
                            setBirthDateValid(valid)
                            setBirthDate(vtf.getSelectedDate())
                        }
                    }
                    if (initialProfile.birthDate != null) {
                        vtf.setSelectedDate(initialProfile.birthDate)
                        profileCustomViewModel.apply {
                            setBirthDateValid(true)
                            setBirthDate(initialProfile.birthDate)
                        }
                    }
                }
                vtfProfileCustomNid.let { vtf ->
                    vtf.addValidateListener { valid ->
                        profileCustomViewModel.apply {
                            setNidValid(valid)
                            setNid(vtf.getText()?.toIntOrNull() ?: 0)
                        }
                    }
                    if (initialProfile.nid != null) {
                        vtf.setText(initialProfile.nid.toString())
                        profileCustomViewModel.apply {
                            setNidValid(true)
                            setNid(initialProfile.nid)
                        }
                    }
                }
            }
        }
    }

    private fun setupFamilyRadioValidation(isYes: Boolean? = null) {
        binding?.rgPreferWithFamily?.run {
            setOnCheckedChangeListener { _, checkedId ->
                profileCustomViewModel.apply {
                    setFamilyValid(true)
                    setFamily(checkedId == R.id.rb_prefer_with_family_yes)
                }
            }
            if (isYes != null) {
                when (isYes) {
                    true -> {
                        check(R.id.rb_prefer_with_family_yes)
                    }
                    false -> {
                        check(R.id.rb_prefer_with_family_no)
                    }
                }
                profileCustomViewModel.apply {
                    setFamilyValid(true)
                    setFamily(isYes)
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
        wrapEspressoIdlingResource {
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

    }

    private fun customizeProfile() {
        profileCustomViewModel.customizeProfile(
            userToken = profileViewModel.userToken,
            profile = profileViewModel.getCurrentProfile().value ?: ProfileEntity(),
        ).run {
            if (this.hasObservers()) this.removeObservers(this@ProfileCustomizeActivity)
            this.observe(this@ProfileCustomizeActivity) { result ->
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        binding?.let { fragment ->
                            Snackbar.make(
                                fragment.root,
                                getString(R.string.profile_customized_successfully),
                                Snackbar.LENGTH_LONG,
                            ).show()
                        }
                        finish()
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

    companion object {
        const val EXTRA_PROFILE = "EXTRA_PROFILE"
    }
}