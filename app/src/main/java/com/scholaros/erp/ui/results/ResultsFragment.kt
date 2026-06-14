package com.scholaros.erp.ui.results

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.scholaros.erp.data.local.SessionManager
import com.scholaros.erp.data.model.ExamBasic
import com.scholaros.erp.data.model.SubjectResult
import com.scholaros.erp.databinding.FragmentResultsBinding
import com.scholaros.erp.databinding.ItemResultBinding
import com.scholaros.erp.ui.BaseViewModelFactory
import com.scholaros.erp.utils.Resource

class ResultsFragment : Fragment() {

    private var _binding: FragmentResultsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ResultsViewModel
    private var exams: List<ExamBasic> = emptyList()

    private val adapter = object : ListAdapter<SubjectResult, RecyclerView.ViewHolder>(
        object : DiffUtil.ItemCallback<SubjectResult>() {
            override fun areItemsTheSame(a: SubjectResult, b: SubjectResult) = a.subjectName == b.subjectName
            override fun areContentsTheSame(a: SubjectResult, b: SubjectResult) = a == b
        }
    ) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = object : RecyclerView.ViewHolder(
            ItemResultBinding.inflate(LayoutInflater.from(parent.context), parent, false).root
        ) {}

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val b = ItemResultBinding.bind(holder.itemView)
            val item = getItem(position)
            b.tvSubject.text = item.subjectName ?: "Subject"
            b.tvMarks.text = "${item.obtainedMarks} / ${item.maxMarks}"
            b.tvGrade.text = item.grade ?: "--"
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val session = SessionManager(requireContext())
        viewModel = ViewModelProvider(this, BaseViewModelFactory(session) { ResultsViewModel(it) })[ResultsViewModel::class.java]

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.exams.observe(viewLifecycleOwner) { result ->
            if (result is Resource.Success) {
                exams = result.data?.exams ?: emptyList()
                val names = exams.map { it.name }
                val dropdownAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, names)
                binding.spinnerExam.setAdapter(dropdownAdapter)
                binding.spinnerExam.setOnItemClickListener { _, _, pos, _ ->
                    viewModel.loadResult(exams[pos].id)
                }
                if (exams.isNotEmpty()) {
                    binding.spinnerExam.setText(exams[0].name, false)
                    viewModel.loadResult(exams[0].id)
                }
            }
        }

        viewModel.result.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> { binding.progressBar.visibility = View.VISIBLE }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    adapter.submitList(result.data?.results ?: emptyList())
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvError.text = result.message
                    binding.tvError.visibility = View.VISIBLE
                }
            }
        }

        viewModel.loadExams()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
