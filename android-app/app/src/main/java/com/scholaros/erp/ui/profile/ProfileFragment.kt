package com.scholaros.erp.ui.profile

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.scholaros.erp.R
import com.scholaros.erp.data.local.SessionManager
import com.scholaros.erp.databinding.FragmentProfileBinding
import com.scholaros.erp.ui.BaseViewModelFactory
import com.scholaros.erp.utils.Resource

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProfileViewModel
    private lateinit var session: SessionManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        session = SessionManager(requireContext())
        viewModel = ViewModelProvider(this, BaseViewModelFactory(session) { ProfileViewModel(it) })[ProfileViewModel::class.java]

        binding.swipeRefresh.setOnRefreshListener { viewModel.load() }

        viewModel.profile.observe(viewLifecycleOwner) { result ->
            binding.swipeRefresh.isRefreshing = false
            when (result) {
                is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    result.data?.profile?.let { user ->
                        binding.tvName.text = user.name
                        binding.tvRole.text = user.roleName
                        binding.etName.setText(user.name)
                        binding.etEmail.setText(user.email)
                        binding.etMobile.setText(user.mobile ?: "")
                        user.photoUrl?.let { url ->
                            Glide.with(this).load(url).placeholder(R.mipmap.ic_launcher)
                                .circleCrop().into(binding.ivAvatar)
                        }
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

        viewModel.updateResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> { binding.progressBar.visibility = View.VISIBLE; binding.btnSave.isEnabled = false }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSave.isEnabled = true
                    binding.tvMessage.text = "Profile updated successfully"
                    binding.tvMessage.setTextColor(Color.parseColor("#388E3C"))
                    binding.tvMessage.visibility = View.VISIBLE
                    val name = binding.etName.text?.toString() ?: ""
                    if (name.isNotEmpty()) session.userName = name
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSave.isEnabled = true
                    binding.tvMessage.text = result.message
                    binding.tvMessage.setTextColor(Color.parseColor("#D32F2F"))
                    binding.tvMessage.visibility = View.VISIBLE
                }
            }
        }

        binding.btnSave.setOnClickListener {
            binding.tvMessage.visibility = View.GONE
            val name = binding.etName.text?.toString()?.trim() ?: ""
            val mobile = binding.etMobile.text?.toString()?.trim() ?: ""
            if (name.isEmpty()) { binding.tilName.error = "Name is required"; return@setOnClickListener }
            binding.tilName.error = null
            viewModel.updateProfile(name, mobile)
        }

        viewModel.load()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
