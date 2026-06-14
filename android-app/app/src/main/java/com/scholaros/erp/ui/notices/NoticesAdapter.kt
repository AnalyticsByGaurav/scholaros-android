package com.scholaros.erp.ui.notices

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.scholaros.erp.data.model.Notice
import com.scholaros.erp.databinding.ItemNoticeBinding

class NoticesAdapter : ListAdapter<Notice, NoticesAdapter.VH>(DIFF) {

    inner class VH(private val b: ItemNoticeBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: Notice) {
            b.tvTitle.text = item.title
            b.tvContent.text = item.content ?: ""
            b.tvCategory.text = item.category.replaceFirstChar { it.uppercase() }
            b.tvDate.text = item.publishDate ?: item.createdAt
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Notice>() {
            override fun areItemsTheSame(a: Notice, b: Notice) = a.id == b.id
            override fun areContentsTheSame(a: Notice, b: Notice) = a == b
        }
    }
}
