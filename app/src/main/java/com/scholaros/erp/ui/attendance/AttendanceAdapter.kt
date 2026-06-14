package com.scholaros.erp.ui.attendance

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.scholaros.erp.data.model.AttendanceRecord
import com.scholaros.erp.databinding.ItemAttendanceBinding

class AttendanceAdapter : ListAdapter<AttendanceRecord, AttendanceAdapter.VH>(DIFF) {

    inner class VH(private val b: ItemAttendanceBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: AttendanceRecord) {
            b.tvDate.text = item.date
            b.tvStatus.text = item.status.replaceFirstChar { it.uppercase() }
            b.tvStatus.setTextColor(when (item.status.lowercase()) {
                "present" -> Color.parseColor("#388E3C")
                "absent"  -> Color.parseColor("#D32F2F")
                "late"    -> Color.parseColor("#F57C00")
                "half_day"-> Color.parseColor("#7B1FA2")
                else      -> Color.GRAY
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemAttendanceBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<AttendanceRecord>() {
            override fun areItemsTheSame(a: AttendanceRecord, b: AttendanceRecord) = a.date == b.date
            override fun areContentsTheSame(a: AttendanceRecord, b: AttendanceRecord) = a == b
        }
    }
}
