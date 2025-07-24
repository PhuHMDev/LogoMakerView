package com.mvvm.esportlogo.components.ui.preview

import android.content.Intent
import android.widget.SeekBar
import androidx.activity.viewModels
import com.mvvm.esportlogo.base.BaseActivity
import com.mvvm.esportlogo.components.ui.result.ResultActivity
import com.mvvm.esportlogo.databinding.ActivityPreviewBinding
import com.mvvm.esportlogo.extensions.observeWithLifeCycle
import com.mvvm.esportlogo.extensions.toJson

class PreviewActivity : BaseActivity<ActivityPreviewBinding>(ActivityPreviewBinding::inflate) {

    private var initTemplate: Boolean = false

    private val viewModel: PreviewViewModel by viewModels()
    override fun observeViewModels() {
        observeWithLifeCycle(viewModel.state) { state ->
            state.logoTemplate?.let { template ->
                if(!initTemplate) {
                    binding.drawView.post {
                        binding.drawView.initTemplate(template)
                        binding.drawView.isTouchEnable = true
                        initTemplate = true
                    }
                }
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
        binding.btnDone.setOnClickListener {
            binding.drawView.getLogoTemplateSaved()?.let {
                val intent = Intent(this, ResultActivity::class.java)
                intent.putExtra("json", it.toJson())
                startActivity(intent)
            }
        }
    }
}