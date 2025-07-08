package net.broachcutter.vendorapp.screens.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import net.broachcutter.vendorapp.Screens
import net.broachcutter.vendorapp.base.BaseActivity
import net.broachcutter.vendorapp.base.BaseFragment
import net.broachcutter.vendorapp.databinding.FragmentSettingsBinding
import net.broachcutter.vendorapp.util.Links
import ru.terrakok.cicerone.Router
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : BaseFragment() {

    @Inject
    lateinit var router: Router

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as BaseActivity)
            .getApplicationComponent()
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tos.setOnClickListener { router.navigateTo(Screens.WebView(Links.TERMS_OF_SERVICE)) }
        binding.privacyPolicy.setOnClickListener { router.navigateTo(Screens.WebView(Links.PRIVACY_POLICY)) }
        binding.openSource.setOnClickListener {
            startActivity(Intent(activity, OssLicensesMenuActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
