package com.scholaros.erp.ui.materials

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.scholaros.erp.data.local.SessionManager
import com.scholaros.erp.data.model.MaterialsData
import com.scholaros.erp.ui.BaseViewModel
import com.scholaros.erp.utils.Resource
import kotlinx.coroutines.launch

class MaterialsViewModel(session: SessionManager) : BaseViewModel(session) {
    private val _materials = MutableLiveData<Resource<MaterialsData>>()
    val materials: LiveData<Resource<MaterialsData>> = _materials

    fun load(page: Int = 1) {
        _materials.value = Resource.Loading
        viewModelScope.launch { _materials.value = repo.getMaterials(page) }
    }
}
