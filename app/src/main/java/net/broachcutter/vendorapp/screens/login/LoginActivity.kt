package net.broachcutter.vendorapp.screens.login

import android.os.Bundle
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.Screens
import net.broachcutter.vendorapp.base.BaseActivity
import net.broachcutter.vendorapp.databinding.ActivityLoginBinding
import ru.terrakok.cicerone.android.support.SupportAppNavigator

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // add in "default" fragment
        if (savedInstanceState == null) {
            router.replaceScreen(Screens.LoginMain())
        }
    }

    override val navigator = object : SupportAppNavigator(this, R.id.login_container) {}
}
