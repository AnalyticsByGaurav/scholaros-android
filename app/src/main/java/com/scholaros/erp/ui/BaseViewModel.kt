package com.scholaros.erp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.scholaros.erp.data.local.SessionManager
import com.scholaros.erp.data.repository.MainRepository

abstract class BaseViewModel(val session: SessionManager) : ViewModel() {
    protected val repo = MainRepository(session)
}

class BaseViewModelFactory(private val session: SessionManager, private val creator: (SessionManager) -> ViewModel) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = creator(session) as T
}
