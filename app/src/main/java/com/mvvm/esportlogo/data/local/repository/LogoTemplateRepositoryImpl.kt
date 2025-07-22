package com.mvvm.esportlogo.data.local.repository

import com.mvvm.esportlogo.MyApplication
import com.mvvm.esportlogo.data.local.model.LogoTemplate
import com.mvvm.esportlogo.extensions.loadJsonFromAssetsToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object LogoTemplateRepositoryImpl : LogoTemplateRepository {
    private var logos = emptyList<LogoTemplate>()

    override suspend fun getLogoTemplateList(): List<LogoTemplate> {
        val context = MyApplication.getInstance().applicationContext
        return withContext(Dispatchers.IO) {
            logos.ifEmpty { context.loadJsonFromAssetsToList("logos.json") }
        }
    }
}