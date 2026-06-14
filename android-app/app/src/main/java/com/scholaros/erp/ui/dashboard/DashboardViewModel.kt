package com.scholaros.erp.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.scholaros.erp.data.local.SessionManager
import com.scholaros.erp.data.model.DashboardData
import com.scholaros.erp.ui.BaseViewModel
import com.scholaros.erp.utils.Resource
import kotlinx.coroutines.launch

class DashboardViewModel(session: SessionManager) : BaseViewModel(session) {

    private val _dashboard = MutableLiveData<Resource<DashboardData>>()
    val dashboard: LiveData<Resource<DashboardData>> = _dashboard

    fun load() {
        _dashboard.value = Resource.Loading
        viewModelScope.launch { _dashboard.value = repo.getDashboard() }
    }
}
