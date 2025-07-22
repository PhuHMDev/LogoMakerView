package com.mvvm.esportlogo.components.ui.editing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mvvm.esportlogo.data.local.model.LogoTemplate
import com.mvvm.esportlogo.data.local.repository.LogoTemplateRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditingViewModel : ViewModel() {
    private val _logo = MutableStateFlow<LogoTemplate?>(null)
    val logo = _logo.asStateFlow()

    init {
        getLogoTemplate()
    }

    private fun getLogoTemplate() {
        viewModelScope.launch {
            _logo.value = LogoTemplateRepositoryImpl.getLogoTemplateList().find { it.imageName == "esport_logo_0313.png" }
        }
    }

    fun changeTextSize(size: Float) {
        viewModelScope.launch {
            _logo.update { logo.value?.copy(fontSize = size) }
        }
    }

    fun change3DText(_3dx: Float) {
        viewModelScope.launch {
            _logo.update { logo.value?.copy(text3dX = _3dx) }
        }
    }

    fun changeLetterSpacing(value: Float) {
        viewModelScope.launch {
            _logo.update {  logo.value?.copy(letterSpacing = value/100) }
        }
    }

    fun changeCurve(value: Float) {
        viewModelScope.launch {
            _logo.update {  logo.value?.copy(textCurve = value) }
        }
    }
}