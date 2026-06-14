package com.scholaros.erp.ui.homework

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.scholaros.erp.data.model.Homework
import com.scholaros.erp.databinding.ItemHomeworkBinding

class HomeworkAdapter : ListAdapter<Homework, HomeworkAdapter.VH>(DIFF) {

    inner class VH(private val b: ItemHomeworkBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: Homework) {
            b.tvSubject.text = item.subjectName ?: "Subject"
            b.tvTitle.text = item.title
            b.tvDescription.text = item.description ?: ""
            b.tvDueDate.text = "Due: ${item.dueDate ?: "N/A"}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemHomeworkBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Homework>() {
            override fun areItemsTheSame(a: Homework, b: Homework) = a.id == b.id
            override fun areContentsTheSame(a: Homework, b: Homework) = a == b
        }
    }
}
