package com.mvvm.esportlogo.components.ui.preview

import android.widget.SeekBar
import androidx.activity.viewModels
import com.mvvm.esportlogo.base.BaseActivity
import com.mvvm.esportlogo.databinding.ActivityPreviewBinding
import com.mvvm.esportlogo.extensions.observeWithLifeCycle

class PreviewActivity : BaseActivity<ActivityPreviewBinding>(ActivityPreviewBinding::inflate) {

    private val viewModel: PreviewViewModel by viewModels()
    override fun observeViewModels() {
        observeWithLifeCycle(viewModel.state) { state ->
            state.logoTemplate?.let { template ->
                binding.drawView.initTemplate(template)
            }
        }
    }

    override fun handleEvents() {
        super.handleEvents()
        binding.btnSwap.setOnClickListener {
            binding.drawView.swapLayer()
        }

        binding.sbCurve.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val curveValue = progress.toFloat() / 100f
                binding.drawView.setTextCurve(curveValue)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
}