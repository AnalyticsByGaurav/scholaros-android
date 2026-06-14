package com.scholaros.erp.ui.attendance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.scholaros.erp.data.local.SessionManager
import com.scholaros.erp.data.model.AttendanceData
import com.scholaros.erp.ui.BaseViewModel
import com.scholaros.erp.utils.Resource
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AttendanceViewModel(session: SessionManager) : BaseViewModel(session) {

    private val _attendance = MutableLiveData<Resource<AttendanceData>>()
    val attendance: LiveData<Resource<AttendanceData>> = _attendance

    fun load(month: String = currentMonth()) {
        _attendance.value = Resource.Loading
        viewModelScope.launch { _attendance.value = repo.getAttendance(month) }
    }

    private fun currentMonth() = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
}
