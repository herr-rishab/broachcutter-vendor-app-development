package net.broachcutter.vendorapp.screens.cutters.pilot_pins

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.valartech.commons.utils.extensions.hideKeyboard
import net.broachcutter.vendorapp.base.BaseFragment
import net.broachcutter.vendorapp.databinding.FragmentCutterPilotPinsBinding
import net.broachcutter.vendorapp.util.ViewModelFactory
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 *
 */
class CutterPilotPinsFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<CutterPilotPinsViewModel>

    private val model: CutterPilotPinsViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(CutterPilotPinsViewModel::class.java)
    }

    private var _binding: FragmentCutterPilotPinsBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getAppComponent().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCutterPilotPinsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.search.setOnClickListener {
            executeSearch()
        }
        binding.depthOfCutEntry.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    executeSearch()
                    true
                }

                else -> false
            }
        }
    }

    private fun executeSearch() {
        model.search(binding.depthOfCutEntry.text.toString().toIntOrNull(), getDiameter())
        activity?.hideKeyboard()
    }

    private fun getDiameter(): Float? {
        return binding.diameterGroup.selectedChild?.run {
            text.toString().toFloat()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
