package com.scholaros.erp.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.scholaros.erp.data.local.SessionManager
import com.scholaros.erp.data.model.CalendarItem
import com.scholaros.erp.databinding.FragmentCalendarBinding
import com.scholaros.erp.databinding.ItemCalendarEventBinding
import com.scholaros.erp.ui.BaseViewModelFactory
import com.scholaros.erp.utils.Resource

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CalendarViewModel

    private val adapter = object : ListAdapter<CalendarItem, RecyclerView.ViewHolder>(
        object : DiffUtil.ItemCallback<CalendarItem>() {
            override fun areItemsTheSame(a: CalendarItem, b: CalendarItem) = a.date == b.date && a.title == b.title
            override fun areContentsTheSame(a: CalendarItem, b: CalendarItem) = a == b
        }
    ) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = object : RecyclerView.ViewHolder(
            ItemCalendarEventBinding.inflate(LayoutInflater.from(parent.context), parent, false).root
        ) {}

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val b = ItemCalendarEventBinding.bind(holder.itemView)
            val item = getItem(position)
            val parts = item.date?.split("-") ?: emptyList()
            b.tvDay.text = parts.getOrNull(2) ?: "--"
            b.tvMonth.text = when (parts.getOrNull(1)) {
                "01" -> "JAN"; "02" -> "FEB"; "03" -> "MAR"; "04" -> "APR"
                "05" -> "MAY"; "06" -> "JUN"; "07" -> "JUL"; "08" -> "AUG"
                "09" -> "SEP"; "10" -> "OCT"; "11" -> "NOV"; "12" -> "DEC"
                else -> ""
            }
            b.tvTitle.text = item.title
            b.tvType.text = item.type?.replaceFirstChar { it.uppercase() } ?: "Event"
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val session = SessionManager(requireContext())
        viewModel = ViewModelProvider(this, BaseViewModelFactory(session) { CalendarViewModel(it) })[CalendarViewModel::class.java]

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener { viewModel.load() }

        viewModel.calendar.observe(viewLifecycleOwner) { result ->
            binding.swipeRefresh.isRefreshing = false
            when (result) {
                is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val list = result.data?.items ?: emptyList()
                    adapter.submitList(list)
                    binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvError.text = result.message
                    binding.tvError.visibility = View.VISIBLE
                }
            }
        }

        viewModel.load()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
