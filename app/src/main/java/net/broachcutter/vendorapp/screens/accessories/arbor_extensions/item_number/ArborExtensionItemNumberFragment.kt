package net.broachcutter.vendorapp.screens.accessories.arbor_extensions.item_number

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.broachcutter.vendorapp.R
import net.broachcutter.vendorapp.base.BaseFragment

class ArborExtensionItemNumberFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_arbor_extension_item_number, container, false)
}
