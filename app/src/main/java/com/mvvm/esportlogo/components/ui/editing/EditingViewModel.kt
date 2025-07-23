package com.mvvm.esportlogo.components.ui.editing

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mvvm.esportlogo.data.local.model.LogoTemplate
import com.mvvm.esportlogo.data.local.repository.LogoTemplateRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditingViewModel : ViewModel() {
    private val _logo = MutableStateFlow<List<LogoTemplate>>(emptyList())
    val logo = _logo.asStateFlow()

    init {
        getLogoTemplate()
    }

    private fun getLogoTemplate() {
        viewModelScope.launch {
            val list = LogoTemplateRepositoryImpl.getLogoTemplateList()
                .sortedBy { it.imageName } // <-- Sắp xếp theo tên

            _logo.value = list

            list.forEach {
                Log.d("1111111111111111", "getLogoTemplate: ${it.imageName}")
            }
        }
    }

}