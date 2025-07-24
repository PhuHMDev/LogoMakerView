package com.mvvm.esportlogo.components.ui.result

import com.mvvm.esportlogo.base.BaseActivity
import com.mvvm.esportlogo.data.local.model.LogoTemplate
import com.mvvm.esportlogo.databinding.ActivityResultBinding
import com.mvvm.esportlogo.extensions.fromJson

class ResultActivity : BaseActivity<ActivityResultBinding>(ActivityResultBinding::inflate) {
    override fun initData() {
        intent.getStringExtra("json")?.let {
            it.fromJson<LogoTemplate>()?.let { temp ->
                binding.drawView.post {
                    binding.drawView.initTemplate(temp)
                }
            }
        }
    }
}