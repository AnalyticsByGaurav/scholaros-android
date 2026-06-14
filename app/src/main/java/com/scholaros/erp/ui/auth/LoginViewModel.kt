package com.scholaros.erp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scholaros.erp.data.local.SessionManager
import com.scholaros.erp.data.repository.AuthRepository
import com.scholaros.erp.utils.Resource
import kotlinx.coroutines.launch

class LoginViewModel(private val session: SessionManager) : ViewModel() {

    private val repo = AuthRepository(session)

    private val _loginResult = MutableLiveData<Resource<String>>()
    val loginResult: LiveData<Resource<String>> = _loginResult

    fun login(baseUrl: String, email: String, password: String) {
        val url = baseUrl.trimEnd('/')
        if (url.isEmpty()) { _loginResult.value = Resource.Error("Server URL is required"); return }
        if (email.isEmpty()) { _loginResult.value = Resource.Error("Email is required"); return }
        if (password.isEmpty()) { _loginResult.value = Resource.Error("Password is required"); return }

        _loginResult.value = Resource.Loading
        viewModelScope.launch {
            _loginResult.value = repo.login(url, email, password)
        }
    }
}
