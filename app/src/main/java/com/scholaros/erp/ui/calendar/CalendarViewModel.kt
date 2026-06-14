package com.scholaros.erp.ui.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.scholaros.erp.data.local.SessionManager
import com.scholaros.erp.data.model.CalendarData
import com.scholaros.erp.ui.BaseViewModel
import com.scholaros.erp.utils.Resource
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CalendarViewModel(session: SessionManager) : BaseViewModel(session) {

    private val _calendar = MutableLiveData<Resource<CalendarData>>()
    val calendar: LiveData<Resource<CalendarData>> = _calendar

    fun load(month: String = currentMonth()) {
        _calendar.value = Resource.Loading
        viewModelScope.launch { _calendar.value = repo.getCalendar(month) }
    }

    fun loadUpcoming() {
        _calendar.value = Resource.Loading
        viewModelScope.launch { _calendar.value = repo.getUpcoming() }
    }

    private fun currentMonth() = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
}
