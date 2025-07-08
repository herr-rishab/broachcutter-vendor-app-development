package net.broachcutter.vendorapp.screens.annular.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.broachcutter.vendorapp.base.BaseFragment
import net.broachcutter.vendorapp.databinding.FragmentAnnularCuttersHomeBinding

class AnnularCuttersHomeFragment : BaseFragment() {
    lateinit var adapter: AnnularCuttersTabAdapter
    private var _binding: FragmentAnnularCuttersHomeBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        adapter = AnnularCuttersTabAdapter(
            childFragmentManager,
            context
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnnularCuttersHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.adapter = adapter
        binding.slidingTabs.setupWithViewPager(binding.viewPager)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
