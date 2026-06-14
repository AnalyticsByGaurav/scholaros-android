package com.scholaros.erp.ui.complaints

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
import com.scholaros.erp.data.model.Complaint
import com.scholaros.erp.databinding.FragmentComplaintSubmitBinding
import com.scholaros.erp.databinding.FragmentComplaintsBinding
import com.scholaros.erp.databinding.FragmentLeaveListBinding
import com.scholaros.erp.databinding.ItemComplaintBinding
import com.scholaros.erp.ui.BaseViewModelFactory
import com.scholaros.erp.utils.Resource

class ComplaintsFragment : Fragment() {

    private var _binding: FragmentComplaintsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ComplaintsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentComplaintsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val session = SessionManager(requireContext())
        viewModel = ViewModelProvider(this, BaseViewModelFactory(session) { ComplaintsViewModel(it) })[ComplaintsViewModel::class.java]

        val pagerAdapter = ComplaintsPagerAdapter(this, viewModel)
        binding.viewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, pos ->
            tab.text = if (pos == 0) "My Complaints" else "Submit"
        }.attach()

        viewModel.load()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

class ComplaintsPagerAdapter(
    fragment: Fragment,
    private val viewModel: ComplaintsViewModel
) : androidx.viewpager2.adapter.FragmentStateAdapter(fragment) {
    override fun getItemCount() = 2
    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> ComplaintsListFragment(viewModel)
        else -> ComplaintSubmitFragment(viewModel)
    }
}

class ComplaintsListFragment(private val viewModel: ComplaintsViewModel) : Fragment() {

    private var _binding: FragmentLeaveListBinding? = null
    private val binding get() = _binding!!

    private val adapter = object : ListAdapter<Complaint, RecyclerView.ViewHolder>(
        object : DiffUtil.ItemCallback<Complaint>() {
            override fun areItemsTheSame(a: Complaint, b: Complaint) = a.id == b.id
            override fun areContentsTheSame(a: Complaint, b: Complaint) = a == b
        }
    ) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = object : RecyclerView.ViewHolder(
            ItemComplaintBinding.inflate(LayoutInflater.from(parent.context), parent, false).root
        ) {}

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val b = ItemComplaintBinding.bind(holder.itemView)
            val item = getItem(position)
            b.tvComplaintNo.text = item.complaintNo ?: ""
            b.tvSubject.text = item.subject
            b.tvCategory.text = item.category?.replaceFirstChar { it.uppercase() } ?: ""
            b.tvDescription.text = item.description ?: ""
            val status = item.status ?: "pending"
            b.tvStatus.text = status.replaceFirstChar { it.uppercase() }
            b.tvStatus.setTextColor(when (status.lowercase()) {
                "resolved" -> Color.parseColor("#388E3C")
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
        binding.swipeRefresh.setOnRefreshListener { viewModel.load() }
        viewModel.complaints.observe(viewLifecycleOwner) { result ->
            binding.swipeRefresh.isRefreshing = false
            when (result) {
                is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val list = result.data?.complaints ?: emptyList()
                    adapter.submitList(list)
                    binding.tvEmpty.text = "No complaints submitted"
                    binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                }
                is Resource.Error -> binding.progressBar.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

class ComplaintSubmitFragment(private val viewModel: ComplaintsViewModel) : Fragment() {

    private var _binding: FragmentComplaintSubmitBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentComplaintSubmitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val categories = listOf("academics", "facility", "transport", "staff", "other")
        val priorities = listOf("low", "medium", "high")

        binding.spinnerCategory.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, categories.map { it.replaceFirstChar { c -> c.uppercase() } })
        )
        binding.spinnerPriority.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, priorities.map { it.replaceFirstChar { c -> c.uppercase() } })
        )
        binding.spinnerCategory.setText("Other", false)
        binding.spinnerPriority.setText("Medium", false)

        viewModel.submitResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> { binding.progressBar.visibility = View.VISIBLE; binding.btnSubmit.isEnabled = false }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSubmit.isEnabled = true
                    Toast.makeText(requireContext(), "Complaint submitted", Toast.LENGTH_SHORT).show()
                    binding.etSubject.setText("")
                    binding.etDescription.setText("")
                    viewModel.load()
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
            val cat = binding.spinnerCategory.text.toString().lowercase()
            val subj = binding.etSubject.text?.toString()?.trim() ?: ""
            val desc = binding.etDescription.text?.toString()?.trim() ?: ""
            val prio = binding.spinnerPriority.text.toString().lowercase()
            if (subj.isEmpty() || desc.isEmpty()) {
                Toast.makeText(requireContext(), "Subject and description are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.submit(cat, subj, desc, prio)
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
