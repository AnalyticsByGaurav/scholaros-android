package com.scholaros.erp.ui.fees

import android.graphics.Color
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
import com.scholaros.erp.data.model.FeeRecord
import com.scholaros.erp.databinding.FragmentFeesBinding
import com.scholaros.erp.databinding.ItemFeeBinding
import com.scholaros.erp.ui.BaseViewModelFactory
import com.scholaros.erp.utils.Resource

class FeesFragment : Fragment() {

    private var _binding: FragmentFeesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FeesViewModel

    private val adapter = object : ListAdapter<FeeRecord, RecyclerView.ViewHolder>(
        object : DiffUtil.ItemCallback<FeeRecord>() {
            override fun areItemsTheSame(a: FeeRecord, b: FeeRecord) = a.id == b.id
            override fun areContentsTheSame(a: FeeRecord, b: FeeRecord) = a == b
        }
    ) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = object : RecyclerView.ViewHolder(
            ItemFeeBinding.inflate(LayoutInflater.from(parent.context), parent, false).root
        ) {}

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val b = ItemFeeBinding.bind(holder.itemView)
            val item = getItem(position)
            b.tvFeeName.text = item.feeHead ?: "Fee"
            b.tvAmount.text = "₹${item.totalAmount}"
            b.tvDueDate.text = item.paymentDate?.let { "Paid: $it" } ?: "Balance: ₹${item.balance}"
            val isPaid = item.status?.lowercase() == "paid"
            b.tvStatus.text = if (isPaid) "Paid" else "Pending"
            b.tvStatus.setTextColor(if (isPaid) Color.parseColor("#388E3C") else Color.parseColor("#D32F2F"))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFeesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val session = SessionManager(requireContext())
        viewModel = ViewModelProvider(this, BaseViewModelFactory(session) { FeesViewModel(it) })[FeesViewModel::class.java]

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener { viewModel.load() }

        viewModel.fees.observe(viewLifecycleOwner) { result ->
            binding.swipeRefresh.isRefreshing = false
            when (result) {
                is Resource.Loading -> { binding.progressBar.visibility = View.VISIBLE }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    result.data?.summary?.let { s ->
                        binding.tvTotalFee.text = "₹${s.totalDue}"
                        binding.tvPaid.text = "₹${s.totalPaid}"
                        binding.tvBalance.text = "₹${s.totalBalance}"
                    }
                    adapter.submitList(result.data?.history ?: emptyList())
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
