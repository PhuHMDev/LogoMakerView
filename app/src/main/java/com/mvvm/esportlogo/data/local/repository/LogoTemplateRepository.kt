package com.mvvm.esportlogo.data.local.repository

import com.mvvm.esportlogo.data.local.model.LogoTemplate

interface LogoTemplateRepository {
    suspend fun getLogoTemplateList() : List<LogoTemplate>
}