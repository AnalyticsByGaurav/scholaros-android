package com.scholaros.erp.ui.complaints

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.scholaros.erp.data.local.SessionManager
import com.scholaros.erp.data.model.ComplaintsData
import com.scholaros.erp.ui.BaseViewModel
import com.scholaros.erp.utils.Resource
import kotlinx.coroutines.launch

class ComplaintsViewModel(session: SessionManager) : BaseViewModel(session) {

    private val _complaints = MutableLiveData<Resource<ComplaintsData>>()
    val complaints: LiveData<Resource<ComplaintsData>> = _complaints

    private val _submitResult = MutableLiveData<Resource<Map<String, Any>>>()
    val submitResult: LiveData<Resource<Map<String, Any>>> = _submitResult

    fun load() {
        _complaints.value = Resource.Loading
        viewModelScope.launch { _complaints.value = repo.getComplaints() }
    }

    fun submit(category: String, subject: String, description: String, priority: String) {
        _submitResult.value = Resource.Loading
        viewModelScope.launch { _submitResult.value = repo.submitComplaint(category, subject, description, priority) }
    }
}
