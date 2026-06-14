package com.scholaros.erp.ui.materials

import android.content.Intent
import android.net.Uri
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
import com.scholaros.erp.data.model.Material
import com.scholaros.erp.databinding.FragmentMaterialsBinding
import com.scholaros.erp.databinding.ItemMaterialBinding
import com.scholaros.erp.ui.BaseViewModelFactory
import com.scholaros.erp.utils.Resource

class MaterialsFragment : Fragment() {

    private var _binding: FragmentMaterialsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MaterialsViewModel

    private val adapter = object : ListAdapter<Material, RecyclerView.ViewHolder>(
        object : DiffUtil.ItemCallback<Material>() {
            override fun areItemsTheSame(a: Material, b: Material) = a.id == b.id
            override fun areContentsTheSame(a: Material, b: Material) = a == b
        }
    ) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = object : RecyclerView.ViewHolder(
            ItemMaterialBinding.inflate(LayoutInflater.from(parent.context), parent, false).root
        ) {}

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val b = ItemMaterialBinding.bind(holder.itemView)
            val item = getItem(position)
            b.tvTitle.text = item.title
            b.tvSubject.text = item.subjectName ?: ""
            b.tvDate.text = item.createdAt
            b.tvType.text = item.type.uppercase()
            holder.itemView.setOnClickListener {
                item.fileUrl?.let { url ->
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMaterialsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val session = SessionManager(requireContext())
        viewModel = ViewModelProvider(this, BaseViewModelFactory(session) { MaterialsViewModel(it) })[MaterialsViewModel::class.java]

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener { viewModel.load() }

        viewModel.materials.observe(viewLifecycleOwner) { result ->
            binding.swipeRefresh.isRefreshing = false
            when (result) {
                is Resource.Loading -> { binding.progressBar.visibility = View.VISIBLE }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val list = result.data?.materials ?: emptyList()
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
