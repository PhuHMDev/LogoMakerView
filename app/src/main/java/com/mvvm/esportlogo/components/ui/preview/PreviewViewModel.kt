package com.mvvm.esportlogo.components.ui.preview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mvvm.esportlogo.data.local.model.LogoTemplate
import com.mvvm.esportlogo.data.local.repository.LogoTemplateRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PreviewViewModel : ViewModel() {
    private val _state = MutableStateFlow(PreviewState(null))
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                logoTemplate = LogoTemplateRepositoryImpl.getLogoTemplateList().find { it.imageName == "esport_logo_0231.png" }
            )
        }
    }

    data class PreviewState(
        val logoTemplate: LogoTemplate?
    )
}