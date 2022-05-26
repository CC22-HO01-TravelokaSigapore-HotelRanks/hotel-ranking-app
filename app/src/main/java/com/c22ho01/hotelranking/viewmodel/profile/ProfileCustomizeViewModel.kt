package com.c22ho01.hotelranking.viewmodel.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.data.local.entity.DisabilityEntity
import com.c22ho01.hotelranking.data.local.entity.HobbyEntity
import com.c22ho01.hotelranking.data.local.entity.ProfileEntity
import com.c22ho01.hotelranking.data.repository.ProfileRepository
import java.util.*

class ProfileCustomizeViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {
    private var _formValid: MediatorLiveData<Boolean> = MediatorLiveData()
    val formValid get() = _formValid

    private var _fullName = MutableLiveData<String>()
    private var _nid = MutableLiveData<Int>()
    private var _birthDate = MutableLiveData<Date>(null)
    private var _family = MutableLiveData(false)

    private var _fullNameValid = MutableLiveData(false)
    private var _nidValid = MutableLiveData(false)
    private var _birthDateValid = MutableLiveData(false)
    private var _familyValid = MutableLiveData(false)

    private var _selectedHobbies = MutableLiveData<List<HobbyEntity?>>()
    private var _selectedDisabilities = MutableLiveData<List<DisabilityEntity?>>()

    private fun checkEveryValidationValueTrue(): Boolean {
        return (_fullNameValid.value ?: false) &&
                (_nidValid.value ?: false) &&
                (_birthDateValid.value ?: false) &&
                (_familyValid.value ?: false) &&
                (_selectedHobbies.value?.isNotEmpty() ?: false)
    }

    init {
        formValid.addSource(_fullNameValid) {
            formValid.postValue(checkEveryValidationValueTrue())
        }
        formValid.addSource(_nidValid) {
            formValid.postValue(checkEveryValidationValueTrue())
        }
        formValid.addSource(_birthDateValid) {
            formValid.postValue(checkEveryValidationValueTrue())
        }
        formValid.addSource(_familyValid) {
            formValid.postValue(checkEveryValidationValueTrue())
        }
        formValid.addSource(_selectedHobbies) {
            formValid.postValue(checkEveryValidationValueTrue())
        }
        formValid.addSource(_selectedDisabilities) {
            formValid.postValue(checkEveryValidationValueTrue())
        }
    }

    fun setFullNameValid(valid: Boolean) {
        _fullNameValid.postValue(valid)
    }

    fun setNidValid(valid: Boolean) {
        _nidValid.postValue(valid)
    }

    fun setBirthDateValid(valid: Boolean) {
        _birthDateValid.postValue(valid)
    }

    fun setFamilyValid(valid: Boolean) {
        _familyValid.postValue(valid)
    }

    fun setFullName(value: String) {
        _fullName.postValue(value)
    }

    fun setNid(value: Int) {
        _nid.postValue(value)
    }

    fun setBirthDate(value: Date) {
        _birthDate.postValue(value)
    }

    fun setFamily(value: Boolean) {
        _family.postValue(value)
    }

    fun setSelectedHobbies(value: List<HobbyEntity?>) {
        _selectedHobbies.postValue(value)
    }

    fun setSelectedDisabilities(value: List<DisabilityEntity?>) {
        _selectedDisabilities.postValue(value)
    }

    fun setHobbyChecked(hobby: HobbyEntity, checked: Boolean) {
        val list = _selectedHobbies.value?.toMutableList() ?: mutableListOf()
        if (checked) {
            list.add(hobby)
        } else {
            list.remove(hobby)
        }
        _selectedHobbies.postValue(list)
    }

    fun setDisabilityChecked(disability: DisabilityEntity, checked: Boolean) {
        val list = _selectedDisabilities.value?.toMutableList() ?: mutableListOf()
        if (checked) {
            list.add(disability)
        } else {
            list.remove(disability)
        }
        _selectedDisabilities.postValue(list)
    }

    fun customizeProfile(
        userToken: String,
        id: Int
    ): LiveData<Result<ProfileEntity>> {
        val entity = ProfileEntity(
            id = id,
            name = _fullName.value,
            nid = _nid.value,
            birthDate = _birthDate.value,
            family = _family.value,
            hobby = _selectedHobbies.value,
            specialNeeds = _selectedDisabilities.value
        )

        val result = profileRepository.updateProfile(
            userToken = userToken,
            profile = entity
        )

        return result
    }


    fun getAllHobbyList(): List<HobbyEntity> {
        return profileRepository.hobbyList
    }

    fun getAllDisabilityList(): List<DisabilityEntity> {
        return profileRepository.disabilityList
    }


}