package net.broachcutter.vendorapp.screens.spares

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.valartech.commons.network.google.Status
import com.valartech.commons.utils.extensions.hideKeyboard
import com.valartech.commons.utils.extensions.toast
import com.valartech.loadinglayout.LoadingLayout
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.base.BaseFragment
import net.broachcutter.vendorapp.databinding.FragmentSparesBinding
import net.broachcutter.vendorapp.util.SimpleTextChangeListener
import net.broachcutter.vendorapp.util.ViewModelFactory
import net.broachcutter.vendorapp.views.FlatRadioButtonGroup
import org.jetbrains.anko.hintResource
import org.jetbrains.anko.textResource
import javax.inject.Inject

class SparesFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<SparesViewModel>

    private val model: SparesViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(SparesViewModel::class.java)
    }

    private var _binding: FragmentSparesBinding? = null
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
        _binding = FragmentSparesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearch()
        setupMachineSpinner()

        model.uiModel.observe(
            viewLifecycleOwner
        ) { resource ->
            when (resource?.status) {
                Status.SUCCESS -> {
                    binding.sparesLoadingLayout.setState(LoadingLayout.COMPLETE)
                    updateUi(resource.data)
                }

                Status.ERROR -> {
                    binding.sparesLoadingLayout.setState(LoadingLayout.COMPLETE)
                    resource.message?.let {
                        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                    }
                }

                Status.LOADING -> binding.sparesLoadingLayout.setState(LoadingLayout.LOADING)
                else -> {}
            }
        }
    }

    private fun setupMachineSpinner() {
        val machineAdapter = ArrayAdapter(
            requireContext(),
            R.layout.spares_spinner_item,
            R.id.machineName,
            arrayListOf("")
        )
        machineAdapter.setDropDownViewResource(R.layout.spares_spinner_dropdown_item)
        binding.machineSpinner.adapter = machineAdapter
        binding.machineSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    model.onMachineSelected(position)
                }
            }
    }

    private fun setupSearch() {
        binding.searchText.addTextChangedListener(object : SimpleTextChangeListener {
            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                text?.let {
                    model.onTextChanged(text.trim())
                }
            }
        })
        binding.search.setOnClickListener {
            executeSearch()
        }
        binding.searchText.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    executeSearch()
                    true
                }

                else -> false
            }
        }

        binding.searchType.buttonSelectListener =
            object : FlatRadioButtonGroup.ButtonSelectListener {
                override fun onButtonSelect(buttonId: Int?) {
                    when (buttonId) {
                        R.id.partNumberButton -> model.setSearchType(SparesSearchType.PART_NUMBER)
                        R.id.descriptionButton -> model.setSearchType(SparesSearchType.DESCRIPTION)
                    }
                }
            }
    }

    private fun executeSearch() {
        val query = binding.searchText.text.toString()
        if (query.isEmpty()) {
            toast(R.string.enter_query)
        } else {
            model.search(query)
            activity?.hideKeyboard()
        }
    }

    private fun updateUi(uiModel: SparesUiModel?) {
        uiModel?.let {
            when (uiModel.sparesSearchType) {
                SparesSearchType.PART_NUMBER -> {
                    binding.searchType.selectButton(R.id.partNumberButton)
                    binding.searchText.hintResource = SparesSearchType.PART_NUMBER.hintRes
                    binding.searchLabel.textResource = SparesSearchType.PART_NUMBER.labelRes
                }

                SparesSearchType.DESCRIPTION -> {
                    binding.searchType.selectButton(R.id.descriptionButton)
                    binding.searchText.hintResource = SparesSearchType.DESCRIPTION.hintRes
                    binding.searchLabel.textResource = SparesSearchType.DESCRIPTION.labelRes
                }
            }

            binding.search.isEnabled = uiModel.isSearchEnabled

            @Suppress("UNCHECKED_CAST")
            val adapter = binding.machineSpinner.adapter as ArrayAdapter<String>
            adapter.clear()
            adapter.addAll(uiModel.machinesList)
            binding.machineSpinner.setSelection(uiModel.selectedMachinePosition)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
