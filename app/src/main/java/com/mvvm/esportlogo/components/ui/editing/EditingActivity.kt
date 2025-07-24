
package com.mvvm.esportlogo.components.ui.editing

import androidx.activity.viewModels
import com.mvvm.esportlogo.base.BaseActivity
import com.mvvm.esportlogo.components.ui.adapter.TemplateAdapter
import com.mvvm.esportlogo.databinding.ActivityEditingBinding
import com.mvvm.esportlogo.extensions.observeWithLifeCycle

class EditingActivity : BaseActivity<ActivityEditingBinding>(ActivityEditingBinding::inflate) {
    private val adapter by lazy { TemplateAdapter() }
    private val viewModel: EditingViewModel by viewModels()
    override fun observeViewModels() {
        observeWithLifeCycle(viewModel.logo) {
            adapter.submitList(it)
        }
        binding.rcv.adapter = adapter
        binding.rcv.setHasFixedSize(false)
    }
}