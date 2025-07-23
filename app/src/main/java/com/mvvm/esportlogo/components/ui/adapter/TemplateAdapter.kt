package com.mvvm.esportlogo.components.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.mvvm.esportlogo.data.local.model.LogoTemplate
import com.mvvm.esportlogo.databinding.LayoutItemPreviewBinding

class TemplateAdapter  : ListAdapter<LogoTemplate, TemplateAdapter.TemplateViewHolder>(TemplateDiffCallback()) {

    inner class TemplateViewHolder(private val binding: LayoutItemPreviewBinding) : ViewHolder(binding.root) {
        fun bindData(logoTemplate: LogoTemplate) {
            binding.drawView.initTemplate(logoTemplate)
        }
    }

    class TemplateDiffCallback : DiffUtil.ItemCallback<LogoTemplate>() {
        override fun areItemsTheSame(oldItem: LogoTemplate, newItem: LogoTemplate): Boolean {
            return oldItem.logoName == newItem.logoName
        }

        override fun areContentsTheSame(oldItem: LogoTemplate, newItem: LogoTemplate): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateViewHolder {
        val binding = LayoutItemPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TemplateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TemplateViewHolder, position: Int) {
        holder.bindData(currentList[position])
    }
}