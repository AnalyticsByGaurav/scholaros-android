package com.scholaros.erp.ui.homework

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.scholaros.erp.data.local.SessionManager
import com.scholaros.erp.data.model.HomeworkData
import com.scholaros.erp.ui.BaseViewModel
import com.scholaros.erp.utils.Resource
import kotlinx.coroutines.launch

class HomeworkViewModel(session: SessionManager) : BaseViewModel(session) {
    private val _homework = MutableLiveData<Resource<HomeworkData>>()
    val homework: LiveData<Resource<HomeworkData>> = _homework

    fun load() {
        _homework.value = Resource.Loading
        viewModelScope.launch { _homework.value = repo.getHomework() }
    }
}
