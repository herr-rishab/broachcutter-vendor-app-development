package net.broachcutter.vendorapp.screens.cutters.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import net.broachcutter.vendorapp.base.BaseFragment
import net.broachcutter.vendorapp.databinding.FragmentCuttersHomeBinding

/**
 * A simple [Fragment] subclass.
 */
@Deprecated("Use AnnularCuttersHomeFragment")
class CuttersHomeFragment : BaseFragment() {

    lateinit var adapter: CuttersTabAdapter
    private var _binding: FragmentCuttersHomeBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        adapter = CuttersTabAdapter(
            childFragmentManager,
            context
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCuttersHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.adapter = adapter
        binding.tabLayout.viewPager = binding.viewPager
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
