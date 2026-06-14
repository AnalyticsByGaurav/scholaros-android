package com.scholaros.erp.ui.leave

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import com.scholaros.erp.data.local.SessionManager
import com.scholaros.erp.data.model.LeaveApplication
import com.scholaros.erp.data.model.LeaveType
import com.scholaros.erp.databinding.FragmentLeaveApplyBinding
import com.scholaros.erp.databinding.FragmentLeaveBinding
import com.scholaros.erp.databinding.FragmentLeaveListBinding
import com.scholaros.erp.databinding.ItemLeaveBinding
import com.scholaros.erp.ui.BaseViewModelFactory
import com.scholaros.erp.utils.Resource

class LeaveFragment : Fragment() {

    private var _binding: FragmentLeaveBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: LeaveViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLeaveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val session = SessionManager(requireContext())
        viewModel = ViewModelProvider(this, BaseViewModelFactory(session) { LeaveViewModel(it) })[LeaveViewModel::class.java]

        val pagerAdapter = LeavePagerAdapter(this, viewModel)
        binding.viewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, pos ->
            tab.text = if (pos == 0) "My Applications" else "Apply"
        }.attach()

        viewModel.loadTypes()
        viewModel.loadApplications()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

class LeavePagerAdapter(
    private val fragment: Fragment,
    private val viewModel: LeaveViewModel
) : androidx.viewpager2.adapter.FragmentStateAdapter(fragment) {

    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> LeaveListFragment(viewModel)
        else -> LeaveApplyFragment(viewModel)
    }
}

class LeaveListFragment(private val viewModel: LeaveViewModel) : Fragment() {

    private var _binding: FragmentLeaveListBinding? = null
    private val binding get() = _binding!!

    private val adapter = object : ListAdapter<LeaveApplication, RecyclerView.ViewHolder>(
        object : DiffUtil.ItemCallback<LeaveApplication>() {
            override fun areItemsTheSame(a: LeaveApplication, b: LeaveApplication) = a.id == b.id
            override fun areContentsTheSame(a: LeaveApplication, b: LeaveApplication) = a == b
        }
    ) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = object : RecyclerView.ViewHolder(
            ItemLeaveBinding.inflate(LayoutInflater.from(parent.context), parent, false).root
        ) {}

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val b = ItemLeaveBinding.bind(holder.itemView)
            val item = getItem(position)
            b.tvLeaveType.text = item.leaveType ?: "Leave"
            b.tvDates.text = "${item.fromDate} to ${item.toDate} (${item.totalDays ?: 1} days)"
            b.tvReason.text = item.reason ?: ""
            val status = item.status ?: "pending"
            b.tvStatus.text = status.replaceFirstChar { it.uppercase() }
            b.tvStatus.setTextColor(when (status.lowercase()) {
                "approved" -> Color.parseColor("#388E3C")
                "rejected" -> Color.parseColor("#D32F2F")
                else       -> Color.parseColor("#F57C00")
            })
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLeaveListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        binding.swipeRefresh.setOnRefreshListener { viewModel.loadApplications() }

        viewModel.applications.observe(viewLifecycleOwner) { result ->
            binding.swipeRefresh.isRefreshing = false
            when (result) {
                is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val list = result.data?.applications ?: emptyList()
                    adapter.submitList(list)
                    binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                }
                is Resource.Error -> binding.progressBar.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

class LeaveApplyFragment(private val viewModel: LeaveViewModel) : Fragment() {

    private var _binding: FragmentLeaveApplyBinding? = null
    private val binding get() = _binding!!
    private var leaveTypes: List<LeaveType> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLeaveApplyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.leaveTypes.observe(viewLifecycleOwner) { result ->
            if (result is Resource.Success) {
                leaveTypes = result.data?.types ?: emptyList()
                val names = leaveTypes.map { it.name }
                binding.spinnerLeaveType.setAdapter(
                    ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, names)
                )
            }
        }

        viewModel.applyResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> { binding.progressBar.visibility = View.VISIBLE; binding.btnSubmit.isEnabled = false }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSubmit.isEnabled = true
                    Toast.makeText(requireContext(), "Leave application submitted", Toast.LENGTH_SHORT).show()
                    binding.etFromDate.setText("")
                    binding.etToDate.setText("")
                    binding.etReason.setText("")
                    viewModel.loadApplications()
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSubmit.isEnabled = true
                    binding.tvError.text = result.message
                    binding.tvError.visibility = View.VISIBLE
                }
            }
        }

        binding.btnSubmit.setOnClickListener {
            val typeIdx = leaveTypes.indexOfFirst { it.name == binding.spinnerLeaveType.text.toString() }
            if (typeIdx < 0) { Toast.makeText(requireContext(), "Select leave type", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            val from = binding.etFromDate.text?.toString()?.trim() ?: ""
            val to = binding.etToDate.text?.toString()?.trim() ?: ""
            val reason = binding.etReason.text?.toString()?.trim() ?: ""
            if (from.isEmpty() || to.isEmpty() || reason.isEmpty()) {
                Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.apply(leaveTypes[typeIdx].id, from, to, reason)
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
