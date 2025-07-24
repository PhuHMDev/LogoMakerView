package com.mvvm.esportlogo.components.ui.editing

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mvvm.esportlogo.data.local.model.LogoTemplate
import com.mvvm.esportlogo.data.local.repository.LogoTemplateRepositoryImpl
import kotlinx.coroutines.Dispatchers
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
        viewModelScope.launch(Dispatchers.IO) {
            val list = LogoTemplateRepositoryImpl.getLogoTemplateList()
            _logo.value = list
        }
    }

}