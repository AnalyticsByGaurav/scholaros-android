package com.scholaros.erp.ui.fees

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.scholaros.erp.data.local.SessionManager
import com.scholaros.erp.data.model.FeesData
import com.scholaros.erp.ui.BaseViewModel
import com.scholaros.erp.utils.Resource
import kotlinx.coroutines.launch

class FeesViewModel(session: SessionManager) : BaseViewModel(session) {
    private val _fees = MutableLiveData<Resource<FeesData>>()
    val fees: LiveData<Resource<FeesData>> = _fees

    fun load() {
        _fees.value = Resource.Loading
        viewModelScope.launch { _fees.value = repo.getFees() }
    }
}
