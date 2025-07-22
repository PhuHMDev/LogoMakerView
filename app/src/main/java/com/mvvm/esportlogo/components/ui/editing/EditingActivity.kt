package com.mvvm.esportlogo.components.ui.editing

import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import androidx.activity.viewModels
import com.mvvm.esportlogo.base.BaseActivity
import com.mvvm.esportlogo.databinding.ActivityEditingBinding
import com.mvvm.esportlogo.extensions.observeWithLifeCycle

class EditingActivity : BaseActivity<ActivityEditingBinding>(ActivityEditingBinding::inflate) {
    private val viewModel: EditingViewModel by viewModels()
    private var canChangeCurve = true

    override fun observeViewModels() {
        observeWithLifeCycle(viewModel.logo) { logoTemplate ->
            logoTemplate?.let { logo ->
                binding.templateView.setLogoTemplate(logo)

            }
        }

        binding.sbTextSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                viewModel.changeTextSize(p1.toFloat())
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        binding.sb3dx.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                viewModel.change3DText(p1.toFloat())
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        binding.sbLetterSpacing.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                viewModel.changeLetterSpacing(p1.toFloat())
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })


        binding.sbCurve.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val curveValue = progress.toFloat() / 100f
                viewModel.changeCurve(curveValue)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
}