package com.scholaros.erp.ui.timetable

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.scholaros.erp.data.local.SessionManager
import com.scholaros.erp.data.model.TimetableData
import com.scholaros.erp.ui.BaseViewModel
import com.scholaros.erp.utils.Resource
import kotlinx.coroutines.launch

class TimetableViewModel(session: SessionManager) : BaseViewModel(session) {
    private val _timetable = MutableLiveData<Resource<TimetableData>>()
    val timetable: LiveData<Resource<TimetableData>> = _timetable

    fun load() {
        _timetable.value = Resource.Loading
        viewModelScope.launch { _timetable.value = repo.getTimetable() }
    }
}
