package com.scholaros.erp.ui.leave

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.scholaros.erp.data.local.SessionManager
import com.scholaros.erp.data.model.LeaveListData
import com.scholaros.erp.data.model.LeaveTypesData
import com.scholaros.erp.ui.BaseViewModel
import com.scholaros.erp.utils.Resource
import kotlinx.coroutines.launch

class LeaveViewModel(session: SessionManager) : BaseViewModel(session) {

    private val _leaveTypes = MutableLiveData<Resource<LeaveTypesData>>()
    val leaveTypes: LiveData<Resource<LeaveTypesData>> = _leaveTypes

    private val _applications = MutableLiveData<Resource<LeaveListData>>()
    val applications: LiveData<Resource<LeaveListData>> = _applications

    private val _applyResult = MutableLiveData<Resource<Map<String, Any>>>()
    val applyResult: LiveData<Resource<Map<String, Any>>> = _applyResult

    fun loadTypes() {
        viewModelScope.launch { _leaveTypes.value = repo.getLeaveTypes() }
    }

    fun loadApplications() {
        _applications.value = Resource.Loading
        viewModelScope.launch { _applications.value = repo.getLeaveApplications() }
    }

    fun apply(leaveTypeId: Int, fromDate: String, toDate: String, reason: String) {
        _applyResult.value = Resource.Loading
        viewModelScope.launch { _applyResult.value = repo.applyLeave(leaveTypeId, fromDate, toDate, reason) }
    }
}
