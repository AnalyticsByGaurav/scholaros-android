package com.scholaros.erp.ui.notices

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.scholaros.erp.data.local.SessionManager
import com.scholaros.erp.data.model.NoticeListData
import com.scholaros.erp.ui.BaseViewModel
import com.scholaros.erp.utils.Resource
import kotlinx.coroutines.launch

class NoticesViewModel(session: SessionManager) : BaseViewModel(session) {

    private val _notices = MutableLiveData<Resource<NoticeListData>>()
    val notices: LiveData<Resource<NoticeListData>> = _notices

    fun load(page: Int = 1, category: String = "") {
        _notices.value = Resource.Loading
        viewModelScope.launch { _notices.value = repo.getNotices(page, category) }
    }
}
