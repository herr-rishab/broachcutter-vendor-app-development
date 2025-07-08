package net.broachcutter.vendorapp.screens.pilot_pins.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.broachcutter.vendorapp.base.BaseFragment
import net.broachcutter.vendorapp.databinding.FragmentPilotPinsHomeBinding

class PilotPinsHomeFragment : BaseFragment() {
    private var _binding: FragmentPilotPinsHomeBinding? = null
    private val binding get() = _binding!!

    lateinit var adapter: PilotPinsTabAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        adapter = PilotPinsTabAdapter(
            childFragmentManager,
            context
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPilotPinsHomeBinding.inflate(inflater, container, false)
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
