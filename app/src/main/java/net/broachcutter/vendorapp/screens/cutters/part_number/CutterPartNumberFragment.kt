package net.broachcutter.vendorapp.screens.cutters.part_number

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.valartech.commons.network.google.Status
import com.valartech.commons.utils.extensions.hideKeyboard
import com.valartech.commons.utils.extensions.longToast
import com.valartech.commons.utils.extensions.toast
import com.valartech.loadinglayout.LoadingLayout
import net.broachcutter.vendorapp.base.BaseFragment
import net.broachcutter.vendorapp.databinding.FragmentCutterPartNumberBinding
import net.broachcutter.vendorapp.util.SimpleTextChangeListener
import net.broachcutter.vendorapp.util.ViewModelFactory
import ru.terrakok.cicerone.Router
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 *
 */
class CutterPartNumberFragment : BaseFragment() {

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<CuttersPartNumberViewModel>

    private val model: CuttersPartNumberViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(CuttersPartNumberViewModel::class.java)
    }

    private var _binding: FragmentCutterPartNumberBinding? = null
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
        _binding = FragmentCutterPartNumberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.search.setOnClickListener {
            executeSearch()
        }
        setupNumberEntry()

        binding.cutterCardView.addToCartButton.setOnClickListener {
            binding.cutterCardView.product?.let { product ->
                val addToCartResult =
                    model.addToCart(product, binding.cutterCardView.selectedQuantity)
                addToCartResult.observe(
                    viewLifecycleOwner
                ) { resource ->
                    when (resource?.status) {
                        Status.SUCCESS -> toast("Added to cart")
                        Status.ERROR -> longToast("Error adding to cart: ${resource.message}")
                        Status.LOADING -> toast("Loading")
                        else -> {}
                    }
                }
            }
        }

        model.results.observe(
            viewLifecycleOwner
        ) { resource ->
            binding.cutterLoadingLayout.visibility = VISIBLE
            when (resource.status) {
                Status.SUCCESS -> {
                    binding.cutterLoadingLayout.setState(LoadingLayout.COMPLETE)
                    resource.data?.results?.let { binding.cutterCardView.bind(it[0]) }
                }

                Status.ERROR -> {
                    binding.cutterLoadingLayout.setState(LoadingLayout.COMPLETE)
                    resource.message?.let { context?.toast(it) }
                }

                Status.LOADING -> binding.cutterLoadingLayout.setState(LoadingLayout.LOADING)
            }
        }
    }

    private fun setupNumberEntry() {
        binding.numberEntry.addTextChangedListener(object : SimpleTextChangeListener {
            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                binding.search.isEnabled = (text != null && text.isNotEmpty())
            }
        })

        binding.numberEntry.setOnEditorActionListener { _, actionId, _ ->
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
        val query = binding.numberEntry.text.toString()
        model.setQuery(query)
        activity?.hideKeyboard()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
