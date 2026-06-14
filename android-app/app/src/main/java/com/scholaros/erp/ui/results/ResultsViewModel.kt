package com.scholaros.erp.ui.results

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.scholaros.erp.data.local.SessionManager
import com.scholaros.erp.data.model.ExamListData
import com.scholaros.erp.data.model.ResultData
import com.scholaros.erp.ui.BaseViewModel
import com.scholaros.erp.utils.Resource
import kotlinx.coroutines.launch

class ResultsViewModel(session: SessionManager) : BaseViewModel(session) {
    private val _exams = MutableLiveData<Resource<ExamListData>>()
    val exams: LiveData<Resource<ExamListData>> = _exams

    private val _result = MutableLiveData<Resource<ResultData>>()
    val result: LiveData<Resource<ResultData>> = _result

    fun loadExams() {
        _exams.value = Resource.Loading
        viewModelScope.launch { _exams.value = repo.getExams() }
    }

    fun loadResult(examId: Int) {
        _result.value = Resource.Loading
        viewModelScope.launch { _result.value = repo.getResult(examId) }
    }
}
