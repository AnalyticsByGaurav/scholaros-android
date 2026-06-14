package com.scholaros.erp.ui.timetable

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
import com.scholaros.erp.data.model.TimetablePeriod
import com.scholaros.erp.databinding.FragmentTimetableBinding
import com.scholaros.erp.databinding.ItemTimetablePeriodBinding
import com.scholaros.erp.ui.BaseViewModelFactory
import com.scholaros.erp.utils.Resource
import java.util.Calendar

class TimetableFragment : Fragment() {

    private var _binding: FragmentTimetableBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: TimetableViewModel

    private val adapter = object : ListAdapter<TimetablePeriod, RecyclerView.ViewHolder>(
        object : DiffUtil.ItemCallback<TimetablePeriod>() {
            override fun areItemsTheSame(a: TimetablePeriod, b: TimetablePeriod) = a.periodId == b.periodId
            override fun areContentsTheSame(a: TimetablePeriod, b: TimetablePeriod) = a == b
        }
    ) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = object : RecyclerView.ViewHolder(
            ItemTimetablePeriodBinding.inflate(LayoutInflater.from(parent.context), parent, false).root
        ) {}

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val b = ItemTimetablePeriodBinding.bind(holder.itemView)
            val item = getItem(position)
            b.tvPeriodNum.text = if (item.isBreak == 1) "BRK" else (position + 1).toString()
            b.tvSubject.text = item.subjectName ?: "Free Period"
            b.tvTeacher.text = item.teacherName ?: ""
            b.tvTime.text = "${item.startTime ?: ""} - ${item.endTime ?: ""}"
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTimetableBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val session = SessionManager(requireContext())
        viewModel = ViewModelProvider(this, BaseViewModelFactory(session) { TimetableViewModel(it) })[TimetableViewModel::class.java]

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener { viewModel.load() }

        viewModel.timetable.observe(viewLifecycleOwner) { result ->
            binding.swipeRefresh.isRefreshing = false
            when (result) {
                is Resource.Loading -> { binding.progressBar.visibility = View.VISIBLE }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val todayDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
                    val days = result.data?.days
                    val todayPeriods = days?.find { it.day == todayDay }?.periods
                        ?: days?.firstOrNull()?.periods
                        ?: emptyList()
                    adapter.submitList(todayPeriods)
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
