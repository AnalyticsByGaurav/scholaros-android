package com.scholaros.erp.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.scholaros.erp.data.local.SessionManager
import com.scholaros.erp.data.model.ProfileData
import com.scholaros.erp.ui.BaseViewModel
import com.scholaros.erp.utils.Resource
import kotlinx.coroutines.launch

class ProfileViewModel(session: SessionManager) : BaseViewModel(session) {

    private val _profile = MutableLiveData<Resource<ProfileData>>()
    val profile: LiveData<Resource<ProfileData>> = _profile

    private val _updateResult = MutableLiveData<Resource<Map<String, String>>>()
    val updateResult: LiveData<Resource<Map<String, String>>> = _updateResult

    fun load() {
        _profile.value = Resource.Loading
        viewModelScope.launch { _profile.value = repo.getProfile() }
    }

    fun updateProfile(name: String, mobile: String) {
        _updateResult.value = Resource.Loading
        viewModelScope.launch { _updateResult.value = repo.updateProfile(name, mobile) }
    }
}
